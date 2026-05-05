package com.tonedefapps.btw.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.tonedefapps.btw.MainActivity
import com.tonedefapps.btw.R
import com.tonedefapps.btw.data.local.BtwDatabase
import com.tonedefapps.btw.data.preferences.BtwPreferences
import com.tonedefapps.btw.domain.model.AlertOutcome
import com.tonedefapps.btw.domain.model.FinalEscalationAction
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
        val autoHotDay = inputData.getBoolean(KEY_AUTO_HOT_DAY, false)
        val prefs = preferences.alertPreferences.first()
        val multiplier = if (prefs.hotDayModeEnabled || autoHotDay) HOT_DAY_MULTIPLIER else 1f

        // Step 3 (SMS) requires premium — stop escalation silently if not subscribed
        if (step >= 3 && !prefs.isPremium) return Result.success()

        when (step) {
            1 -> {
                showGentleNotification(alertId)
                scheduleNextStep(2, alertId, (prefs.step2DelaySeconds * multiplier).toLong(), autoHotDay)
            }
            2 -> {
                showPersistentAlert(alertId)
                scheduleNextStep(3, alertId, (prefs.step3DelaySeconds * multiplier).toLong(), autoHotDay)
            }
            3 -> {
                sendEmergencySms(prefs.emergencyContactPhone, alertId)
                database.alertDao().updateOutcome(alertId, AlertOutcome.ESCALATED_SMS.name, System.currentTimeMillis())
                when (prefs.finalEscalationAction) {
                    FinalEscalationAction.RESEND_SMS ->
                        scheduleNextStep(3, alertId, (prefs.step3DelaySeconds * multiplier).toLong(), autoHotDay)
                    FinalEscalationAction.REPEAT_ALERT ->
                        scheduleNextStep(2, alertId, (prefs.step2DelaySeconds * multiplier).toLong(), autoHotDay)
                }
            }
        }
        return Result.success()
    }

    private fun showGentleNotification(alertId: Long) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val openIntent = openAppIntent(alertId)
        val notification = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("btw...")
            .setContentText("still in the car?")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openIntent)
            .addAction(0, "we're safe", safeIntent())
            .addAction(0, "going back", goingBackIntent())
            .setAutoCancel(false)
            .build()
        nm.notify(BtwMonitorService.NOTIFICATION_ID_ALERT, notification)
    }

    private fun showPersistentAlert(alertId: Long) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val openIntent = openAppIntent(alertId)
        val notification = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("hey...")
            .setContentText("still in the car?")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(openIntent)
            .addAction(0, "we're safe", safeIntent())
            .addAction(0, "going back", goingBackIntent())
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(openIntent, true)
            .build()
        nm.notify(BtwMonitorService.NOTIFICATION_ID_ALERT, notification)
    }

    private fun sendEmergencySms(phone: String, alertId: Long) {
        if (phone.isBlank()) return
        try {
            val sms = applicationContext.getSystemService(SmsManager::class.java)
            sms?.sendTextMessage(
                phone, null,
                "btw — someone may still be in the vehicle. Please check immediately.",
                null, null
            )
        } catch (_: Exception) {}
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
