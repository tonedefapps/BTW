package com.btw.app.domain.repository

import com.btw.app.domain.model.AlertEvent
import com.btw.app.domain.model.AlertOutcome
import kotlinx.coroutines.flow.Flow

interface AlertRepository {
    fun getAlertHistory(): Flow<List<AlertEvent>>
    suspend fun insertAlert(alert: AlertEvent): Long
    suspend fun updateOutcome(id: Long, outcome: AlertOutcome, acknowledgedAt: Long = System.currentTimeMillis())
    suspend fun deleteAlert(id: Long)
}
