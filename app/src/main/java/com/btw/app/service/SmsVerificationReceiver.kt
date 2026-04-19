package com.btw.app.service

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.WorkManager
import com.btw.app.data.local.BtwDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class SmsVerificationReceiver : BroadcastReceiver() {

    @Inject lateinit var database: BtwDatabase

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        messages.forEach { message ->
            val body = message.messageBody?.trim()?.uppercase() ?: return@forEach
            val senderPhone = message.originatingAddress ?: return@forEach

            if (body.startsWith("YES")) {
                handleConfirmation(context, senderPhone)
            }
        }
    }

    private fun handleConfirmation(context: Context, senderPhone: String) {
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        scope.launch {
            val confirmed = database.handoffDao().getPendingEventForContactPhone(senderPhone)
            if (confirmed != null) {
                database.handoffDao().updateOutcome(
                    confirmed.id,
                    com.btw.app.domain.model.HandoffOutcome.VERIFIED_SMS.name,
                    senderPhone
                )
                // Cancel the pending escalation for this rider
                WorkManager.getInstance(context).cancelUniqueWork(
                    HandoffMonitorWorker.handoffWorkName(confirmed.riderId)
                )
                // Dismiss the handoff notification
                val nm = context.getSystemService(NotificationManager::class.java)
                nm.cancel(HandoffMonitorWorker.NOTIFICATION_ID_HANDOFF)
                nm.cancel(HandoffMonitorWorker.NOTIFICATION_ID_HANDOFF + 1)
            }
        }
    }
}
