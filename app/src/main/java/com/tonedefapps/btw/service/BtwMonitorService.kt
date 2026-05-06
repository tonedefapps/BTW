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
import com.tonedefapps.btw.domain.model.AlertEvent
import com.tonedefapps.btw.domain.model.AlertOutcome
import com.tonedefapps.btw.domain.model.HandoffEvent
import com.tonedefapps.btw.domain.monitor.MonitorStateHolder
import com.tonedefapps.btw.domain.usecase.CheckExpectedPickupUseCase
import com.tonedefapps.btw.domain.usecase.GetNearbyLocationUseCase
import com.tonedefapps.btw.domain.usecase.RecordLocationVisitUseCase
import com.google.android.gms.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.sqrt

@AndroidEntryPoint
class BtwMonitorService : Service(), SensorEventListener {

    @Inject lateinit var database: BtwDatabase
    @Inject lateinit var workManager: WorkManager
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

    // ── Triple-trigger flags ─────────────────────────────────────────────────
    private var isMotionConsistentWithDriving = false
    private var btDisconnected = false
    private var movedAwayFromVehicle = false
    private var alertTriggered = false
    private var currentAlertId: Long = -1L

    // ── Location dwell tracking ──────────────────────────────────────────────
    private var currentSavedLocationId: Long = -1L
    private var dwellStartMs: Long = 0L

    // ── Power mode ───────────────────────────────────────────────────────────
    private enum class LocationMode { OFF, IN_VEHICLE, ALERT_ACTIVE }
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
        serviceScope.launch {
            val vehicle = database.vehicleDao().getByBluetoothAddress(address) ?: return@launch
            connectedVehicleAddress = address
            connectedVehicleId = vehicle.id
            btDisconnected = false
            movedAwayFromVehicle = false
            alertTriggered = false
            vehicleParkedLocation = null
            stopAccelerometer()
            setLocationMode(LocationMode.IN_VEHICLE)
            monitorStateHolder.onVehicleConnected()
        }
    }

    private fun onVehicleDisconnected(address: String) {
        if (address != connectedVehicleAddress) return
        btDisconnected = true
        vehicleParkedLocation = currentLocation
        setLocationMode(LocationMode.ALERT_ACTIVE)
        startAccelerometerForAlertWindow()
        checkTripleTrigger()
        checkHandoffWindows()
    }

    // ── Location ─────────────────────────────────────────────────────────────

    private fun setLocationMode(mode: LocationMode) {
        if (locationMode == mode) return
        locationMode = mode
        fusedLocationClient.removeLocationUpdates(locationCallback)
        val request = when (mode) {
            LocationMode.OFF -> {
                // Already removed updates above; nothing to schedule
                return
            }
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
        // Don't poll until a vehicle connects — setLocationMode called from onVehicleConnected
    }

    private fun startAccelerometerForAlertWindow() {
        accelerometer ?: return
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        // Auto-unregister after 10 minutes — if alert hasn't fired by then, phone hasn't moved
        accelerometerJob?.cancel()
        accelerometerJob = serviceScope.launch {
            delay(10 * 60 * 1000L)
            sensorManager.unregisterListener(this@BtwMonitorService)
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

        // Update the parked-location and vehicle DB record while BT is connected
        if (!btDisconnected && connectedVehicleId >= 0) {
            vehicleParkedLocation = location
            serviceScope.launch {
                database.vehicleDao().updateLocation(connectedVehicleId, location.latitude, location.longitude)
            }
        }

        // Check proximity to named saved locations for visit tracking
        serviceScope.launch {
            val nearby = getNearbyLocation(location.latitude, location.longitude, radiusMeters = 80f)
            if (nearby != null) {
                if (nearby.id != currentSavedLocationId) {
                    // Entered a new saved location
                    currentSavedLocationId = nearby.id
                    dwellStartMs = System.currentTimeMillis()
                    recordLocationVisit(nearby.id)
                }
            } else {
                currentSavedLocationId = -1L
            }
        }

        // Check displacement from parked vehicle (50ft ≈ 15.24m)
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
            val riders = database.riderDao().getAllRidersList()
            if (riders.isEmpty()) return@launch
            val vehicle = database.vehicleDao().getByBluetoothAddress(connectedVehicleAddress ?: return@launch)

            val alertId = database.alertDao().insert(
                com.tonedefapps.btw.data.local.entity.AlertEntity(
                    riderId = riders.first().id,
                    riderName = riders.first().name,
                    vehicleId = vehicle?.id ?: -1L,
                    vehicleName = vehicle?.name ?: "vehicle",
                    triggeredAt = System.currentTimeMillis(),
                    acknowledgedAt = null,
                    outcome = AlertOutcome.PENDING.name,
                    latitude = vehicleParkedLocation?.latitude ?: 0.0,
                    longitude = vehicleParkedLocation?.longitude ?: 0.0
                )
            )
            currentAlertId = alertId
            monitorStateHolder.onAlertTriggered(alertId)
            scheduleEscalation(alertId)
        }
    }

    fun scheduleEscalation(alertId: Long) {
        val autoHotDay = readBatteryTempCelsius() >= HOT_BATTERY_TEMP_C
        val work = OneTimeWorkRequestBuilder<AlertEscalationWorker>()
            .setInputData(workDataOf(
                AlertEscalationWorker.KEY_STEP to 1,
                AlertEscalationWorker.KEY_ALERT_ID to alertId,
                AlertEscalationWorker.KEY_AUTO_HOT_DAY to autoHotDay
            ))
            .setInitialDelay(30, TimeUnit.SECONDS)
            .addTag(TAG_ESCALATION)
            .build()
        workManager.enqueueUniqueWork(ESCALATION_WORK_NAME, ExistingWorkPolicy.REPLACE, work)
    }

    private fun readBatteryTempCelsius(): Float {
        val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val tenths = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: return -1f
        return tenths / 10f
    }

    // ── Handoff detection ────────────────────────────────────────────────────

    private fun checkHandoffWindows() {
        val locationId = currentSavedLocationId
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
        alertTriggered = false
        stopAccelerometer()
        setLocationMode(LocationMode.OFF)
        monitorStateHolder.onAcknowledged()
        if (currentAlertId >= 0) {
            serviceScope.launch {
                database.alertDao().updateOutcome(currentAlertId, AlertOutcome.SAFE.name, System.currentTimeMillis())
            }
        }
    }

    private fun handleAcknowledgeGoingBack() {
        workManager.cancelAllWorkByTag(TAG_ESCALATION)
        alertTriggered = false
        stopAccelerometer()
        setLocationMode(LocationMode.OFF)
        monitorStateHolder.onGoingBack()
        if (currentAlertId >= 0) {
            serviceScope.launch {
                database.alertDao().updateOutcome(currentAlertId, AlertOutcome.WENT_BACK.name, System.currentTimeMillis())
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
    }

    companion object {
        const val CHANNEL_MONITOR = "btw_monitor"
        const val CHANNEL_ALERT = "btw_alert"
        const val NOTIFICATION_ID_MONITOR = 1001
        const val NOTIFICATION_ID_ALERT = 1002
        const val ACTION_ACKNOWLEDGE_SAFE = "com.tonedefapps.btw.ACTION_SAFE"
        const val ACTION_ACKNOWLEDGE_GOING_BACK = "com.tonedefapps.btw.ACTION_GOING_BACK"
        const val ACTION_STOP = "com.tonedefapps.btw.ACTION_STOP"
        const val TAG_ESCALATION = "btw_escalation"
        const val ESCALATION_WORK_NAME = "btw_escalation_ladder"
        private const val DISTANCE_THRESHOLD_METERS = 15.24f  // 50 feet
        private const val DRIVING_MOTION_THRESHOLD = 12f
        private const val HOT_BATTERY_TEMP_C = 36f  // ~97°F battery temp reliably indicates hot environment
    }
}
