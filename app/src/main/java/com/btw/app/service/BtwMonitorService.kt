package com.btw.app.service

import android.app.*
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.btw.app.R
import com.btw.app.data.local.BtwDatabase
import com.btw.app.domain.model.AlertEvent
import com.btw.app.domain.model.AlertOutcome
import com.btw.app.domain.model.Vehicle
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

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null

    private var vehicleLastLocation: Location? = null
    private var connectedVehicleAddress: String? = null
    private var isMotionConsistentWithDriving = false
    private var btDisconnected = false
    private var movedAwayFromVehicle = false
    private var alertTriggered = false
    private var currentAlertId: Long = -1

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { handleLocationUpdate(it) }
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.address?.let { onVehicleConnected(it) }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    device?.address?.let { onVehicleDisconnected(it) }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        createNotificationChannels()
        registerBtReceiver()
        startLocationUpdates()
        startAccelerometer()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_ACKNOWLEDGE_SAFE -> handleAcknowledgeSafe()
            ACTION_ACKNOWLEDGE_GOING_BACK -> handleAcknowledgeGoingBack()
            ACTION_STOP -> stopSelf()
            else -> startForeground(NOTIFICATION_ID_MONITOR, buildMonitorNotification())
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        sensorManager.unregisterListener(this)
        unregisterReceiver(bluetoothReceiver)
    }

    private fun registerBtReceiver() {
        val filter = IntentFilter().apply {
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(bluetoothReceiver, filter)
    }

    private fun startLocationUpdates() {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
            .setMinUpdateDistanceMeters(5f)
            .build()
        try {
            fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
        } catch (_: SecurityException) {}
    }

    private fun startAccelerometer() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    private fun onVehicleConnected(address: String) {
        serviceScope.launch {
            val vehicle = database.vehicleDao().getByBluetoothAddress(address)
            if (vehicle != null) {
                connectedVehicleAddress = address
                btDisconnected = false
                movedAwayFromVehicle = false
                alertTriggered = false
            }
        }
    }

    private fun onVehicleDisconnected(address: String) {
        if (address == connectedVehicleAddress) {
            btDisconnected = true
            checkTripleTrigger()
        }
    }

    private fun handleLocationUpdate(location: Location) {
        val vehicleLoc = vehicleLastLocation
        if (connectedVehicleAddress != null && !btDisconnected) {
            vehicleLastLocation = location
            serviceScope.launch {
                val vehicle = database.vehicleDao().getByBluetoothAddress(connectedVehicleAddress!!)
                vehicle?.let {
                    database.vehicleDao().updateLocation(it.id, location.latitude, location.longitude)
                }
            }
        } else if (btDisconnected && vehicleLoc != null) {
            val distanceMeters = location.distanceTo(vehicleLoc)
            movedAwayFromVehicle = distanceMeters > DISTANCE_THRESHOLD_METERS
            if (movedAwayFromVehicle) checkTripleTrigger()
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val magnitude = sqrt(x * x + y * y + z * z.toDouble()).toFloat()
            isMotionConsistentWithDriving = magnitude > DRIVING_MOTION_THRESHOLD
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun checkTripleTrigger() {
        if (alertTriggered) return
        if (btDisconnected && movedAwayFromVehicle && !isMotionConsistentWithDriving) {
            alertTriggered = true
            triggerAlert()
        }
    }

    private fun triggerAlert() {
        serviceScope.launch {
            val vehicles = database.vehicleDao().getAllVehicles()
            // Get the first vehicle that matches the disconnected address
            val riders = database.riderDao().getAllRiders()
            // We'll create an alert for the first active rider (simplified — real impl manages active riders per trip)
        }
        scheduleEscalation()
    }

    fun scheduleEscalation() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .build()
        val step1Work = OneTimeWorkRequestBuilder<AlertEscalationWorker>()
            .setInputData(workDataOf(AlertEscalationWorker.KEY_STEP to 1, AlertEscalationWorker.KEY_ALERT_ID to currentAlertId))
            .setInitialDelay(30, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .addTag(TAG_ESCALATION)
            .build()
        workManager.enqueueUniqueWork(ESCALATION_WORK_NAME, ExistingWorkPolicy.REPLACE, step1Work)
    }

    private fun handleAcknowledgeSafe() {
        workManager.cancelAllWorkByTag(TAG_ESCALATION)
        alertTriggered = false
        if (currentAlertId >= 0) {
            serviceScope.launch {
                database.alertDao().updateOutcome(
                    currentAlertId,
                    AlertOutcome.SAFE.name,
                    System.currentTimeMillis()
                )
            }
        }
    }

    private fun handleAcknowledgeGoingBack() {
        workManager.cancelAllWorkByTag(TAG_ESCALATION)
        alertTriggered = false
        if (currentAlertId >= 0) {
            serviceScope.launch {
                database.alertDao().updateOutcome(
                    currentAlertId,
                    AlertOutcome.WENT_BACK.name,
                    System.currentTimeMillis()
                )
            }
        }
    }

    private fun buildMonitorNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_MONITOR)
            .setContentTitle(getString(R.string.notification_monitor_title))
            .setContentText(getString(R.string.notification_monitor_text))
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannels() {
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_MONITOR, getString(R.string.notification_channel_monitor), NotificationManager.IMPORTANCE_MIN).apply {
                setShowBadge(false)
            }
        )
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ALERT, getString(R.string.notification_channel_alert), NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
            }
        )
    }

    companion object {
        const val CHANNEL_MONITOR = "btw_monitor"
        const val CHANNEL_ALERT = "btw_alert"
        const val NOTIFICATION_ID_MONITOR = 1001
        const val NOTIFICATION_ID_ALERT = 1002
        const val ACTION_ACKNOWLEDGE_SAFE = "com.btw.app.ACTION_SAFE"
        const val ACTION_ACKNOWLEDGE_GOING_BACK = "com.btw.app.ACTION_GOING_BACK"
        const val ACTION_STOP = "com.btw.app.ACTION_STOP"
        const val TAG_ESCALATION = "btw_escalation"
        const val ESCALATION_WORK_NAME = "btw_escalation_ladder"
        private const val DISTANCE_THRESHOLD_METERS = 15.24f  // ~50 feet
        private const val DRIVING_MOTION_THRESHOLD = 12f
    }
}
