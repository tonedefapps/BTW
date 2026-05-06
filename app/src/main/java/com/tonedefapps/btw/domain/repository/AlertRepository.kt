package com.tonedefapps.btw.domain.repository

import com.tonedefapps.btw.domain.model.AlertEvent
import com.tonedefapps.btw.domain.model.AlertOutcome
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun getAlertHistory(): Flow<List<AlertEvent>>
    suspend fun insertAlert(alert: AlertEvent): Long
    suspend fun updateOutcome(id: Long, outcome: AlertOutcome, acknowledgedAt: Long = System.currentTimeMillis())
    suspend fun deleteAlert(id: Long)
}
