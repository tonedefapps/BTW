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
import com.tonedefapps.btw.data.local.BtwDatabase
import com.tonedefapps.btw.domain.model.HandoffEvent
import com.tonedefapps.btw.domain.model.HandoffOutcome
import com.tonedefapps.btw.data.preferences.BtwPreferences
import com.tonedefapps.btw.domain.repository.HandoffRepository
import com.tonedefapps.btw.domain.repository.LocationRepository
import com.tonedefapps.btw.domain.repository.RiderRepository
import kotlinx.coroutines.flow.first
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class HandoffMonitorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val handoffRepository: HandoffRepository,
    private val riderRepository: RiderRepository,
    private val locationRepository: LocationRepository,
    private val preferences: BtwPreferences
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val step = inputData.getInt(KEY_STEP, STEP_NOTIFY)
        val riderId = inputData.getLong(KEY_RIDER_ID, -1L)
        val locationId = inputData.getLong(KEY_LOCATION_ID, -1L)
        val handoffEventId = inputData.getLong(KEY_HANDOFF_EVENT_ID, -1L)

        if (riderId < 0 || locationId < 0) return Result.failure()

        return when (step) {
            STEP_NOTIFY -> handleInitialNotify(riderId, locationId, handoffEventId)
            STEP_ESCALATE -> handleEscalation(riderId, locationId, handoffEventId)
            else -> Result.failure()
        }
    }

    private suspend fun handleInitialNotify(riderId: Long, locationId: Long, existingEventId: Long): Result {
        val rider = riderRepository.getRiderById(riderId) ?: return Result.failure()
        val location = locationRepository.getLocationById(locationId) ?: return Result.failure()

        val eventId = if (existingEventId >= 0) {
            existingEventId
        } else {
            handoffRepository.insertHandoffEvent(
                HandoffEvent(
                    riderId = riderId,
                    locationId = locationId,
                    riderName = rider.name,
                    locationLabel = location.label,
                    expectedAt = System.currentTimeMillis()
                )
            )
        }

        // Notify the primary user
        showHandoffNotification(rider.name, location.label, eventId)

        // SMS handoff contacts — requires premium
        val isPremium = preferences.alertPreferences.first().isPremium
        if (isPremium) {
            val contacts = handoffRepository.getAllContactsForRider(riderId)
            contacts.forEach { contact ->
                sendVerificationSms(phone = contact.phone, riderName = rider.name, locationLabel = location.label)
            }
            if (contacts.isNotEmpty()) {
                handoffRepository.updateHandoffOutcome(eventId, HandoffOutcome.PENDING, null)
            }
        }

        // Schedule the escalation check in 20 minutes
        val escalationWork = OneTimeWorkRequestBuilder<HandoffMonitorWorker>()
            .setInputData(
                workDataOf(
                    KEY_STEP to STEP_ESCALATE,
                    KEY_RIDER_ID to riderId,
                    KEY_LOCATION_ID to locationId,
                    KEY_HANDOFF_EVENT_ID to eventId
                )
            )
            .setInitialDelay(ESCALATION_DELAY_MINUTES, TimeUnit.MINUTES)
            .addTag(TAG_HANDOFF)
            .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(handoffWorkName(riderId), ExistingWorkPolicy.REPLACE, escalationWork)

        return Result.success()
    }

    private suspend fun handleEscalation(riderId: Long, locationId: Long, eventId: Long): Result {
        // Check if the handoff was already confirmed via SMS reply
        val event = handoffRepository.getPendingHandoffForRider(riderId) ?: return Result.success()
        if (event.outcome != HandoffOutcome.PENDING) return Result.success()

        val rider = riderRepository.getRiderById(riderId) ?: return Result.failure()
        val location = locationRepository.getLocationById(locationId) ?: return Result.failure()

        handoffRepository.updateHandoffOutcome(eventId, HandoffOutcome.ESCALATED)
        showEscalationNotification(rider.name, location.label, eventId)

        return Result.success()
    }

    private fun showHandoffNotification(riderName: String, locationLabel: String, eventId: Long) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val openIntent = PendingIntent.getActivity(
            applicationContext, 0,
            Intent(applicationContext, MainActivity::class.java).apply {
                putExtra(AlertEscalationWorker.EXTRA_ALERT_ID, eventId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("btw...")
            .setContentText("$riderName pickup expected at $locationLabel")
            .setSmallIcon(com.tonedefapps.btw.R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(openIntent)
            .setAutoCancel(true)
            .build()
        nm.notify(NOTIFICATION_ID_HANDOFF, notification)
    }

    private fun showEscalationNotification(riderName: String, locationLabel: String, eventId: Long) {
        val nm = applicationContext.getSystemService(NotificationManager::class.java)
        val openIntent = PendingIntent.getActivity(
            applicationContext, 0,
            Intent(applicationContext, MainActivity::class.java).apply {
                putExtra(AlertEscalationWorker.EXTRA_ALERT_ID, eventId)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, BtwMonitorService.CHANNEL_ALERT)
            .setContentTitle("hey...")
            .setContentText("$riderName pickup at $locationLabel unconfirmed after 20 minutes")
            .setSmallIcon(com.tonedefapps.btw.R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openIntent)
            .setOngoing(true)
            .build()
        nm.notify(NOTIFICATION_ID_HANDOFF + 1, notification)
    }

    private fun sendVerificationSms(phone: String, riderName: String, locationLabel: String) {
        if (phone.isBlank()) return
        try {
            val message = "hey — were you able to grab $riderName from $locationLabel?"
            val sms = applicationContext.getSystemService(SmsManager::class.java)
            sms?.sendTextMessage(phone, null, message, null, null)
        } catch (_: Exception) {}
    }

    companion object {
        const val KEY_STEP = "step"
        const val KEY_RIDER_ID = "rider_id"
        const val KEY_LOCATION_ID = "location_id"
        const val KEY_HANDOFF_EVENT_ID = "handoff_event_id"

        const val STEP_NOTIFY = 1
        const val STEP_ESCALATE = 2

        const val TAG_HANDOFF = "btw_handoff"
        const val ESCALATION_DELAY_MINUTES = 20L
        const val NOTIFICATION_ID_HANDOFF = 2001

        fun handoffWorkName(riderId: Long) = "btw_handoff_$riderId"

        fun scheduleFor(
            context: Context,
            riderId: Long,
            locationId: Long,
            existingEventId: Long = -1L,
            delayMinutes: Long = 0L
        ) {
            val work = OneTimeWorkRequestBuilder<HandoffMonitorWorker>()
                .setInputData(
                    workDataOf(
                        KEY_STEP to STEP_NOTIFY,
                        KEY_RIDER_ID to riderId,
                        KEY_LOCATION_ID to locationId,
                        KEY_HANDOFF_EVENT_ID to existingEventId
                    )
                )
                .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
                .addTag(TAG_HANDOFF)
                .build()
            WorkManager.getInstance(context)
                .enqueueUniqueWork(handoffWorkName(riderId), ExistingWorkPolicy.REPLACE, work)
        }
    }
}
