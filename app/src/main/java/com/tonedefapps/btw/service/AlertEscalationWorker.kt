package com.tonedefapps.btw.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.tonedefapps.btw.MainActivity
import com.tonedefapps.btw.R
import com.tonedefapps.btw.data.local.BtwDatabase
import com.tonedefapps.btw.data.preferences.BtwPreferences
import com.tonedefapps.btw.domain.model.AlertOutcome
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

@HiltWorker
class AlertEscalationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val database: BtwDatabase,
    private val preferences: BtwPreferences
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val step = inputData.getInt(KEY_STEP, 1)
        val alertId = inputData.getLong(KEY_ALERT_ID, -1L)
        val autoHotDay = inputData.getBoolean(KEY_AUTO_HOT_DAY, false)
        val prefs = preferences.alertPreferences.first()
        val multiplier = if (prefs.hotDayModeEnabled || autoHotDay) HOT_DAY_MULTIPLIER else 1f

        val alert = database.alertDao().getById(alertId)
        val riderName = alert?.riderName ?: ""
        val lat = alert?.latitude ?: 0.0
        val lng = alert?.longitude ?: 0.0
        val address = resolveAddress(lat, lng)

        when (step) {
            1 -> {
                showGentleNotification(alertId, riderName, lat, lng, address)
                scheduleNextStep(2, alertId, (prefs.step2DelaySeconds * multiplier).toLong(), autoHotDay)
            }
            2 -> {
                showPersistentAlert(alertId, riderName, lat, lng, address)
            }
        }
        return Result.success()
    }

    private fun showGentleNotification(alertId: Long, riderName: String, lat: Double, lng: Double, address: String) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val contentText = buildString {
            if (riderName.isNotBlank()) append("$riderName · ")
            append("still in the car?")
            if (address.isNotBlank()) append("\n$address")
        }
        val builder = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("btw...")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openAppIntent(alertId))
            .addAction(0, "we're safe", safeIntent())
            .addAction(0, "going back", goingBackIntent())
            .setAutoCancel(false)
        directionsIntent(lat, lng)?.let { builder.addAction(0, "directions", it) }
        nm.notify(BtwMonitorService.NOTIFICATION_ID_ALERT, builder.build())
    }

    private fun showPersistentAlert(alertId: Long, riderName: String, lat: Double, lng: Double, address: String) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val contentText = buildString {
            if (riderName.isNotBlank()) append("$riderName · ")
            append("still in the car?")
            if (address.isNotBlank()) append("\n$address")
        }
        val builder = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("hey...")
            .setContentText(contentText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(contentText))
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(openAppIntent(alertId))
            .addAction(0, "we're safe", safeIntent())
            .addAction(0, "going back", goingBackIntent())
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(openAppIntent(alertId), true)
        directionsIntent(lat, lng)?.let { builder.addAction(0, "directions", it) }
        nm.notify(BtwMonitorService.NOTIFICATION_ID_ALERT, builder.build())
    }

    private suspend fun resolveAddress(lat: Double, lng: Double): String {
        if (lat == 0.0 && lng == 0.0) return ""
        if (!Geocoder.isPresent()) return ""
        return try {
            withTimeout(3_000L) {
                val geocoder = Geocoder(applicationContext, Locale.getDefault())
                val addresses = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    suspendCancellableCoroutine { cont ->
                        geocoder.getFromLocation(lat, lng, 1) { list -> cont.resume(list) }
                    }
                } else {
                    @Suppress("DEPRECATION")
                    withContext(Dispatchers.IO) { geocoder.getFromLocation(lat, lng, 1) ?: emptyList() }
                }
                addresses.firstOrNull()?.let { addr ->
                    listOfNotNull(
                        listOfNotNull(addr.subThoroughfare, addr.thoroughfare)
                            .joinToString(" ").ifBlank { null },
                        addr.locality
                    ).joinToString(", ")
                } ?: ""
            }
        } catch (_: Exception) { "" }
    }

    private fun directionsIntent(lat: Double, lng: Double): PendingIntent? {
        if (lat == 0.0 && lng == 0.0) return null
        val geoUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        val mapIntent = Intent(Intent.ACTION_VIEW, geoUri)
        if (mapIntent.resolveActivity(applicationContext.packageManager) == null) return null
        return PendingIntent.getActivity(
            applicationContext, 3, mapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun openAppIntent(alertId: Long) = PendingIntent.getActivity(
        applicationContext, 0,
        Intent(applicationContext, MainActivity::class.java).apply {
            putExtra(EXTRA_ALERT_ID, alertId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun safeIntent() = PendingIntent.getService(
        applicationContext, 1,
        Intent(applicationContext, BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_SAFE
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun goingBackIntent() = PendingIntent.getService(
        applicationContext, 2,
        Intent(applicationContext, BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_GOING_BACK
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun scheduleNextStep(step: Int, alertId: Long, delaySeconds: Long, autoHotDay: Boolean) {
        val work = OneTimeWorkRequestBuilder<AlertEscalationWorker>()
            .setInputData(workDataOf(
                KEY_STEP to step,
                KEY_ALERT_ID to alertId,
                KEY_AUTO_HOT_DAY to autoHotDay
            ))
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .addTag(BtwMonitorService.TAG_ESCALATION)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(BtwMonitorService.ESCALATION_WORK_NAME, ExistingWorkPolicy.REPLACE, work)
    }

    companion object {
        const val KEY_STEP = "step"
        const val KEY_ALERT_ID = "alert_id"
        const val KEY_AUTO_HOT_DAY = "auto_hot_day"
        const val EXTRA_ALERT_ID = "extra_alert_id"
        private const val HOT_DAY_MULTIPLIER = 0.5f
    }
}
