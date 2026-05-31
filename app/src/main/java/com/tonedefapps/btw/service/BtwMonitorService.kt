package com.tonedefapps.btw.service

import android.Manifest
import android.app.*
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.BatteryManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.tonedefapps.btw.R
import com.tonedefapps.btw.data.local.BtwDatabase
import com.tonedefapps.btw.data.local.entity.SavedLocationEntity
import com.tonedefapps.btw.data.preferences.BtwPreferences
import com.tonedefapps.btw.domain.model.AlertOutcome
import com.tonedefapps.btw.domain.model.HandoffOutcome
import com.tonedefapps.btw.domain.model.isActive
import com.tonedefapps.btw.domain.model.isManuallyPaused
import com.tonedefapps.btw.domain.monitor.MonitorStateHolder
import com.tonedefapps.btw.domain.repository.HandoffRepository
import com.tonedefapps.btw.domain.usecase.CheckExpectedPickupUseCase
import com.tonedefapps.btw.domain.usecase.GetNearbyLocationUseCase
import com.tonedefapps.btw.domain.usecase.RecordLocationVisitUseCase
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.sqrt

@AndroidEntryPoint
class BtwMonitorService : Service(), SensorEventListener {

    @Inject lateinit var database: BtwDatabase
    @Inject lateinit var workManager: WorkManager
    @Inject lateinit var preferences: BtwPreferences
    @Inject lateinit var handoffRepository: HandoffRepository
    @Inject lateinit var getNearbyLocation: GetNearbyLocationUseCase
    @Inject lateinit var recordLocationVisit: RecordLocationVisitUseCase
    @Inject lateinit var checkExpectedPickup: CheckExpectedPickupUseCase
    @Inject lateinit var monitorStateHolder: MonitorStateHolder

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    // ── Trip state ──────────────────────────────────────────────────────────
    private var vehicleParkedLocation: Location? = null
    private var currentLocation: Location? = null
    private var connectedVehicleAddress: String? = null
    private var connectedVehicleId: Long = -1L

    // Suppress repeat nudges for the same rider on the same calendar day
    private val nudgedDates = mutableMapOf<Long, String>()

    // ── Triple-trigger flags ─────────────────────────────────────────────────
    private var isMotionConsistentWithDriving = false
    private var btDisconnected = false
    private var movedAwayFromVehicle = false
    private var alertTriggered = false
    private val currentAlertIds: MutableList<Long> = mutableListOf()
    private var sustainedDrivingCount = 0

    // ── Location dwell tracking ──────────────────────────────────────────────
    private var currentSavedLocationId: Long = -1L
    private var lastKnownLocationId: Long = -1L
    private var dwellStartMs: Long = 0L

    // ── Power mode ───────────────────────────────────────────────────────────
    private enum class LocationMode { OFF, PASSIVE_WATCH, IN_VEHICLE, ALERT_ACTIVE }
    private var locationMode = LocationMode.OFF
    private var accelerometerJob: Job? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { handleLocationUpdate(it) }
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.bluetoothDevice() ?: return
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> onVehicleConnected(device.address)
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> onVehicleDisconnected(device.address)
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun Intent.bluetoothDevice(): BluetoothDevice? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        else
            getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        createNotificationChannels()
        registerBtReceiver()
        startLocationUpdates()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ACKNOWLEDGE_SAFE -> handleAcknowledgeSafe()
            ACTION_ACKNOWLEDGE_GOING_BACK -> handleAcknowledgeGoingBack()
            ACTION_UNPAUSE_RIDER -> {
                val riderId = intent.getLongExtra(EXTRA_RIDER_ID, -1L)
                if (riderId >= 0) {
                    serviceScope.launch {
                        database.riderDao().unpauseRider(riderId)
                        getSystemService(NotificationManager::class.java)
                            .cancel(NOTIFICATION_ID_NUDGE_BASE + riderId.toInt())
                    }
                }
            }
            ACTION_HANDOFF_CONFIRMED -> {
                val eventId = intent.getLongExtra(EXTRA_HANDOFF_EVENT_ID, -1L)
                val riderId = intent.getLongExtra(EXTRA_HANDOFF_RIDER_ID, -1L)
                if (eventId >= 0) {
                    serviceScope.launch {
                        handoffRepository.updateHandoffOutcome(eventId, HandoffOutcome.COMPLETED)
                        if (riderId >= 0) workManager.cancelUniqueWork(HandoffMonitorWorker.handoffWorkName(riderId))
                        getSystemService(NotificationManager::class.java).cancel(HandoffMonitorWorker.NOTIFICATION_ID_HANDOFF)
                    }
                }
            }
            ACTION_STOP -> stopSelf()
            else -> {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    stopSelf()
                    return START_NOT_STICKY
                }
                startForeground(NOTIFICATION_ID_MONITOR, buildMonitorNotification())
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopAccelerometer()
        try { unregisterReceiver(bluetoothReceiver) } catch (_: Exception) {}
    }

    // ── Bluetooth ────────────────────────────────────────────────────────────

    private fun registerBtReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(bluetoothReceiver, filter)
    }

    private fun onVehicleConnected(address: String) {
        // Set synchronously — onVehicleDisconnected's guard reads this on the main thread.
        // If this stays inside the IO coroutine, a BT flicker causes the disconnect to be
        // silently ignored (guard sees null), then the coroutine finishes setting IN_VEHICLE
        // with BT already gone.
        connectedVehicleAddress = address

        serviceScope.launch {
            val vehicle = database.vehicleDao().getByBluetoothAddress(address) ?: run {
                // Unknown device — clear the optimistic address so the guard resets cleanly
                if (connectedVehicleAddress == address) connectedVehicleAddress = null
                return@launch
            }

            // BT flickered: disconnect fired and ran its logic while the DB query was in flight
            if (btDisconnected) return@launch

            connectedVehicleId = vehicle.id
            btDisconnected = false
            movedAwayFromVehicle = false
            alertTriggered = false
            vehicleParkedLocation = null
            sustainedDrivingCount = 0
            stopAccelerometer()
            setLocationMode(LocationMode.IN_VEHICLE)
            monitorStateHolder.onVehicleConnected()

            // Nudge for any manually-paused riders — once per rider per calendar day
            val now = System.currentTimeMillis()
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date(now))
            database.riderDao().getAllRidersList()
                .filter { it.pausedUntil != null && it.pausedUntil > now }
                .filter { nudgedDates[it.id] != today }
                .forEach {
                    nudgedDates[it.id] = today
                    showPausedRiderNudge(it.id, it.name)
                }
        }
    }

    private fun onVehicleDisconnected(address: String) {
        if (address != connectedVehicleAddress) return
        monitorStateHolder.onPassiveWatchStopped()
        btDisconnected = true
        vehicleParkedLocation = currentLocation
        setLocationMode(LocationMode.ALERT_ACTIVE)
        startAccelerometerForAlertWindow()
        checkTripleTrigger()
        checkHandoffWindows(currentSavedLocationId)
    }

    // Called on main thread when user exits a saved parking location (no-BT vehicle mode)
    private fun onDepartureSensed(parkedAt: SavedLocationEntity) {
        monitorStateHolder.onPassiveWatchStopped()
        sustainedDrivingCount = 0
        btDisconnected = true
        vehicleParkedLocation = Location("saved-location").apply {
            latitude = parkedAt.lat
            longitude = parkedAt.lng
        }
        setLocationMode(LocationMode.ALERT_ACTIVE)
        startAccelerometerForAlertWindow()
        checkTripleTrigger()
        checkHandoffWindows(parkedAt.id)
    }

    // ── Location ─────────────────────────────────────────────────────────────

    private fun setLocationMode(mode: LocationMode) {
        if (locationMode == mode) return
        locationMode = mode
        fusedLocationClient.removeLocationUpdates(locationCallback)
        val request = when (mode) {
            LocationMode.OFF -> return
            LocationMode.PASSIVE_WATCH -> LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60_000L)
                .setMinUpdateDistanceMeters(20f)
                .build()
            LocationMode.IN_VEHICLE -> LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 60_000L)
                .setMinUpdateDistanceMeters(20f)
                .build()
            LocationMode.ALERT_ACTIVE -> LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 15_000L)
                .setMinUpdateDistanceMeters(5f)
                .build()
        }
        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (_: SecurityException) {}
    }

    private fun startLocationUpdates() {
        serviceScope.launch {
            val hasNoBtVehicle = database.vehicleDao().getAllVehiclesList().any { it.bluetoothAddress == null }
            val btPermissionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
            } else true
            if (hasNoBtVehicle || !btPermissionGranted) {
                monitorStateHolder.onPassiveWatchStarted()
                setLocationMode(LocationMode.PASSIVE_WATCH)
            }
        }
    }

    private fun startAccelerometerForAlertWindow() {
        accelerometer ?: return
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        accelerometerJob?.cancel()
        accelerometerJob = serviceScope.launch {
            delay(10 * 60 * 1000L)
            sensorManager.unregisterListener(this@BtwMonitorService)
            isMotionConsistentWithDriving = false  // stale data must not block a trigger after sensor stops
            setLocationMode(LocationMode.OFF)
        }
    }

    private fun stopAccelerometer() {
        accelerometerJob?.cancel()
        accelerometerJob = null
        sensorManager.unregisterListener(this)
    }

    private fun handleLocationUpdate(location: Location) {
        currentLocation = location

        if (!btDisconnected && connectedVehicleId >= 0) {
            vehicleParkedLocation = location
            serviceScope.launch {
                database.vehicleDao().updateLocation(connectedVehicleId, location.latitude, location.longitude)
            }
        }

        val prevLocationId = currentSavedLocationId
        serviceScope.launch {
            val nearby = getNearbyLocation(location.latitude, location.longitude, radiusMeters = 80f)
            if (nearby != null) {
                if (nearby.id != currentSavedLocationId) {
                    currentSavedLocationId = nearby.id
                    dwellStartMs = System.currentTimeMillis()
                    recordLocationVisit(nearby.id)
                }
            } else {
                currentSavedLocationId = -1L
            }

            // Departure detection for no-BT vehicles: user was at a known spot and just left it
            if (prevLocationId >= 0 && currentSavedLocationId == -1L && !btDisconnected) {
                val hasNoBtVehicle = database.vehicleDao().getAllVehiclesList().any { it.bluetoothAddress == null }
                if (hasNoBtVehicle) {
                    val departed = database.locationDao().getById(prevLocationId)
                    withContext(Dispatchers.Main) {
                        if (departed != null && !btDisconnected) onDepartureSensed(departed)
                    }
                }
            }
        }

        val parked = vehicleParkedLocation
        if (btDisconnected && parked != null) {
            movedAwayFromVehicle = location.distanceTo(parked) > DISTANCE_THRESHOLD_METERS
            if (movedAwayFromVehicle) checkTripleTrigger()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return
        val mag = sqrt(
            event.values[0] * event.values[0] +
            event.values[1] * event.values[1] +
            event.values[2] * event.values[2].toDouble()
        ).toFloat()
        isMotionConsistentWithDriving = mag > DRIVING_MOTION_THRESHOLD

        // No-BT mode: sustained driving after departure means user re-entered the vehicle.
        // Reset departure watch to prevent false trigger at next stop.
        if (btDisconnected && !alertTriggered && connectedVehicleAddress == null) {
            if (isMotionConsistentWithDriving) {
                if (++sustainedDrivingCount >= DRIVING_RESET_COUNT) resetNoBtDeparture()
            } else {
                sustainedDrivingCount = 0
            }
        }
    }

    private fun resetNoBtDeparture() {
        btDisconnected = false
        movedAwayFromVehicle = false
        alertTriggered = false
        vehicleParkedLocation = null
        sustainedDrivingCount = 0
        stopAccelerometer()
        serviceScope.launch {
            monitorStateHolder.onPassiveWatchStarted()
            setLocationMode(LocationMode.PASSIVE_WATCH)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // ── Triple-trigger ───────────────────────────────────────────────────────

    private fun checkTripleTrigger() {
        if (alertTriggered) return
        if (btDisconnected && movedAwayFromVehicle && !isMotionConsistentWithDriving) {
            alertTriggered = true
            triggerChildPetAlert()
        }
    }

    private fun triggerChildPetAlert() {
        serviceScope.launch {
            val allRiders = database.riderDao().getAllRidersList()
            if (allRiders.isEmpty()) return@launch
            val schedules = database.riderScheduleDao().getAllSchedulesList()
            val now = System.currentTimeMillis()
            val activeRiders = allRiders.filter { riderEntity ->
                val rider = riderEntity.toDomain()
                rider.isActive(schedules.firstOrNull { it.riderId == riderEntity.id }?.toDomain(), now)
                    && !rider.isManuallyPaused(now)
            }
            if (activeRiders.isEmpty()) return@launch

            val address = connectedVehicleAddress
            val vehicle = if (address != null)
                database.vehicleDao().getByBluetoothAddress(address)
            else
                database.vehicleDao().getAllVehiclesList().firstOrNull { it.bluetoothAddress == null }

            currentAlertIds.clear()
            activeRiders.forEach { rider ->
                val alertId = database.alertDao().insert(
                    com.tonedefapps.btw.data.local.entity.AlertEntity(
                        riderId = rider.id,
                        riderName = rider.name,
                        vehicleId = vehicle?.id ?: -1L,
                        vehicleName = vehicle?.name ?: "vehicle",
                        triggeredAt = now,
                        acknowledgedAt = null,
                        outcome = AlertOutcome.PENDING.name,
                        latitude = vehicleParkedLocation?.latitude ?: 0.0,
                        longitude = vehicleParkedLocation?.longitude ?: 0.0
                    )
                )
                currentAlertIds.add(alertId)
            }

            val firstAlertId = currentAlertIds.firstOrNull() ?: return@launch
            monitorStateHolder.onAlertTriggered(firstAlertId)
            scheduleEscalation(firstAlertId)
        }
    }

    private suspend fun scheduleEscalation(alertId: Long) {
        val prefs = preferences.alertPreferences.first()
        val autoHotDay = readBatteryTempCelsius() >= HOT_BATTERY_TEMP_C
        val multiplier = if (prefs.hotDayModeEnabled || autoHotDay) HOT_DAY_MULTIPLIER else 1f
        val step1Delay = (prefs.step1DelaySeconds * multiplier).toLong().coerceAtLeast(10L)
        val work = OneTimeWorkRequestBuilder<AlertEscalationWorker>()
            .setInputData(workDataOf(
                AlertEscalationWorker.KEY_STEP to 1,
                AlertEscalationWorker.KEY_ALERT_ID to alertId,
                AlertEscalationWorker.KEY_AUTO_HOT_DAY to autoHotDay
            ))
            .setInitialDelay(step1Delay, TimeUnit.SECONDS)
            .addTag(TAG_ESCALATION)
            .build()
        workManager.enqueueUniqueWork(ESCALATION_WORK_NAME, ExistingWorkPolicy.REPLACE, work)
    }

    private fun readBatteryTempCelsius(): Float {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val tenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: return -1f
        return tenths / 10f
    }

    // ── Paused-rider nudge ───────────────────────────────────────────────────

    private fun showPausedRiderNudge(riderId: Long, riderName: String) {
        val unpauseIntent = Intent(this, BtwMonitorService::class.java).apply {
            action = ACTION_UNPAUSE_RIDER
            putExtra(EXTRA_RIDER_ID, riderId)
        }
        val unpausePi = PendingIntent.getService(
            this, riderId.toInt(), unpauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_NUDGE)
            .setContentTitle("$riderName is paused")
            .setContentText("is $riderName with you today?")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .addAction(0, "with me", unpausePi)
            .build()
        getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID_NUDGE_BASE + riderId.toInt(), notification)
    }

    // ── Handoff detection ────────────────────────────────────────────────────

    private fun checkHandoffWindows(locationId: Long) {
        if (locationId < 0) return

        serviceScope.launch {
            val activeWindows = checkExpectedPickup(locationId)
            activeWindows.forEach { window ->
                val riderExists = database.riderDao().getRiderById(window.riderId) != null
                if (riderExists) {
                    HandoffMonitorWorker.scheduleFor(
                        context = this@BtwMonitorService,
                        riderId = window.riderId,
                        locationId = locationId
                    )
                }
            }
        }
    }

    // ── Acknowledgement ──────────────────────────────────────────────────────

    private fun handleAcknowledgeSafe() {
        workManager.cancelAllWorkByTag(TAG_ESCALATION)
        getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID_ALERT)
        alertTriggered = false
        btDisconnected = false
        movedAwayFromVehicle = false
        vehicleParkedLocation = null
        stopAccelerometer()
        monitorStateHolder.onAcknowledged()
        serviceScope.launch {
            val now = System.currentTimeMillis()
            currentAlertIds.forEach { id ->
                database.alertDao().updateOutcome(id, AlertOutcome.SAFE.name, now)
            }
            currentAlertIds.clear()
            val hasNoBtVehicle = database.vehicleDao().getAllVehiclesList().any { it.bluetoothAddress == null }
            if (hasNoBtVehicle) {
                monitorStateHolder.onPassiveWatchStarted()
                setLocationMode(LocationMode.PASSIVE_WATCH)
            } else {
                setLocationMode(LocationMode.OFF)
            }
        }
    }

    private fun handleAcknowledgeGoingBack() {
        workManager.cancelAllWorkByTag(TAG_ESCALATION)
        getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID_ALERT)
        alertTriggered = false
        btDisconnected = false
        movedAwayFromVehicle = false
        vehicleParkedLocation = null
        stopAccelerometer()
        monitorStateHolder.onGoingBack()
        serviceScope.launch {
            val now = System.currentTimeMillis()
            currentAlertIds.forEach { id ->
                database.alertDao().updateOutcome(id, AlertOutcome.WENT_BACK.name, now)
            }
            currentAlertIds.clear()
            val hasNoBtVehicle = database.vehicleDao().getAllVehiclesList().any { it.bluetoothAddress == null }
            if (hasNoBtVehicle) {
                monitorStateHolder.onPassiveWatchStarted()
                setLocationMode(LocationMode.PASSIVE_WATCH)
            } else {
                setLocationMode(LocationMode.OFF)
            }
        }
    }

    // ── Notification ─────────────────────────────────────────────────────────

    private fun buildMonitorNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_MONITOR)
            .setContentTitle(getString(R.string.notification_monitor_title))
            .setContentText(getString(R.string.notification_monitor_text))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_MONITOR, getString(R.string.notification_channel_monitor), NotificationManager.IMPORTANCE_MIN)
                .apply { setShowBadge(false) }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ALERT, getString(R.string.notification_channel_alert), NotificationManager.IMPORTANCE_HIGH)
                .apply { enableVibration(true) }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_NUDGE, getString(R.string.notification_channel_nudge), NotificationManager.IMPORTANCE_DEFAULT)
                .apply { setShowBadge(false) }
        )
    }

    companion object {
        const val CHANNEL_MONITOR = "btw_monitor"
        const val CHANNEL_ALERT = "btw_alert"
        const val CHANNEL_NUDGE = "btw_nudge"
        const val NOTIFICATION_ID_MONITOR = 1001
        const val NOTIFICATION_ID_ALERT = 1002
        const val NOTIFICATION_ID_NUDGE_BASE = 3000
        const val ACTION_ACKNOWLEDGE_SAFE = "com.tonedefapps.btw.ACTION_SAFE"
        const val ACTION_ACKNOWLEDGE_GOING_BACK = "com.tonedefapps.btw.ACTION_GOING_BACK"
        const val ACTION_UNPAUSE_RIDER = "com.tonedefapps.btw.ACTION_UNPAUSE_RIDER"
        const val ACTION_HANDOFF_CONFIRMED = "com.tonedefapps.btw.ACTION_HANDOFF_CONFIRMED"
        const val ACTION_STOP = "com.tonedefapps.btw.ACTION_STOP"
        const val EXTRA_RIDER_ID = "rider_id"
        const val EXTRA_HANDOFF_EVENT_ID = "handoff_event_id"
        const val EXTRA_HANDOFF_RIDER_ID = "handoff_rider_id"
        const val TAG_ESCALATION = "btw_escalation"
        const val ESCALATION_WORK_NAME = "btw_escalation_ladder"
        private const val DISTANCE_THRESHOLD_METERS = 15.24f
        private const val DRIVING_MOTION_THRESHOLD = 12f
        private const val HOT_BATTERY_TEMP_C = 36f
        private const val HOT_DAY_MULTIPLIER = 0.5f
        private const val DRIVING_RESET_COUNT = 5
    }
}
