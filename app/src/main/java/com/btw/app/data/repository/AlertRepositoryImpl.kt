package com.btw.app.data.repository

import com.btw.app.data.local.dao.AlertDao
import com.btw.app.data.local.entity.AlertEntity
import com.btw.app.domain.model.AlertEvent
import com.btw.app.domain.model.AlertOutcome
import com.btw.app.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlertRepositoryImpl @Inject constructor(
    private val dao: AlertDao
) : AlertRepository {

    override fun getAlertHistory(): Flow<List<AlertEvent>> =
        dao.getAllAlerts().map { list -> list.map { it.toDomain() } }

    override suspend fun insertAlert(alert: AlertEvent): Long =
        dao.insert(AlertEntity.fromDomain(alert))

    override suspend fun updateOutcome(id: Long, outcome: AlertOutcome, acknowledgedAt: Long) =
        dao.updateOutcome(id, outcome.name, acknowledgedAt)

    override suspend fun deleteAlert(id: Long) =
        dao.deleteById(id)
}
