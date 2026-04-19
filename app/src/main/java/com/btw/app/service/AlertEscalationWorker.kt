package com.btw.app.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.btw.app.MainActivity
import com.btw.app.R
import com.btw.app.data.local.BtwDatabase
import com.btw.app.data.preferences.BtwPreferences
import com.btw.app.domain.model.AlertOutcome
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

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
        val prefs = preferences.alertPreferences.first()

        val multiplier = if (prefs.hotDayModeEnabled) HOT_DAY_MULTIPLIER else 1f

        when (step) {
            1 -> {
                showGentleNotification(alertId)
                scheduleNextStep(step + 1, alertId, (prefs.step2DelaySeconds * multiplier).toLong())
            }
            2 -> {
                showPersistentAlert(alertId)
                scheduleNextStep(step + 1, alertId, (prefs.step3DelaySeconds * multiplier).toLong())
            }
            3 -> {
                sendEmergencySms(prefs.emergencyContactPhone, prefs.emergencyContactName, alertId)
                database.alertDao().updateOutcome(alertId, AlertOutcome.ESCALATED_SMS.name, System.currentTimeMillis())
                scheduleNextStep(step + 1, alertId, (prefs.step4DelaySeconds * multiplier).toLong())
            }
            4 -> {
                showOneTouch911(alertId)
                database.alertDao().updateOutcome(alertId, AlertOutcome.ESCALATED_911.name, System.currentTimeMillis())
            }
        }
        return Result.success()
    }

    private fun showGentleNotification(alertId: Long) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val openIntent = PendingIntent.getActivity(
            applicationContext, 0,
            Intent(applicationContext, MainActivity::class.java).apply {
                putExtra(EXTRA_ALERT_ID, alertId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val safeIntent = PendingIntent.getService(
            applicationContext, 1,
            Intent(applicationContext, BtwMonitorService::class.java).apply {
                action = BtwMonitorService.ACTION_ACKNOWLEDGE_SAFE
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val goBackIntent = PendingIntent.getService(
            applicationContext, 2,
            Intent(applicationContext, BtwMonitorService::class.java).apply {
                action = BtwMonitorService.ACTION_ACKNOWLEDGE_GOING_BACK
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("btw...")
            .setContentText("still in the car?")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openIntent)
            .addAction(0, "we're safe", safeIntent)
            .addAction(0, "going back", goBackIntent)
            .setAutoCancel(false)
            .build()
        nm.notify(BtwMonitorService.NOTIFICATION_ID_ALERT, notification)
    }

    private fun showPersistentAlert(alertId: Long) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val openIntent = PendingIntent.getActivity(
            applicationContext, 0,
            Intent(applicationContext, MainActivity::class.java).apply {
                putExtra(EXTRA_ALERT_ID, alertId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("hey...")
            .setContentText("they would remind you if they could.")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(openIntent, true)
            .build()
        nm.notify(BtwMonitorService.NOTIFICATION_ID_ALERT, notification)
    }

    private fun sendEmergencySms(phone: String, contactName: String, alertId: Long) {
        if (phone.isBlank()) return
        try {
            val sms = SmsManager.getDefault()
            val message = "btw — someone may still be in the vehicle. Please check immediately."
            sms.sendTextMessage(phone, null, message, null, null)
        } catch (_: Exception) {}
    }

    private fun showOneTouch911(alertId: Long) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val dialIntent = PendingIntent.getActivity(
            applicationContext, 0,
            Intent(Intent.ACTION_DIAL).apply { data = android.net.Uri.parse("tel:911") },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("btw")
            .setContentText("tap to call 911 — location ready")
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(dialIntent)
            .addAction(0, "call 911", dialIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(dialIntent, true)
            .build()
        nm.notify(BtwMonitorService.NOTIFICATION_ID_ALERT + 1, notification)
    }

    private fun scheduleNextStep(step: Int, alertId: Long, delaySeconds: Long) {
        val work = OneTimeWorkRequestBuilder<AlertEscalationWorker>()
            .setInputData(workDataOf(KEY_STEP to step, KEY_ALERT_ID to alertId))
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .addTag(BtwMonitorService.TAG_ESCALATION)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(BtwMonitorService.ESCALATION_WORK_NAME, ExistingWorkPolicy.REPLACE, work)
    }

    companion object {
        const val KEY_STEP = "step"
        const val KEY_ALERT_ID = "alert_id"
        const val EXTRA_ALERT_ID = "extra_alert_id"
        private const val HOT_DAY_MULTIPLIER = 0.5f
    }
}
