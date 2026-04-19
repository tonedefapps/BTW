package com.btw.app.domain.usecase

import com.btw.app.domain.model.AlertEvent
import com.btw.app.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlertHistoryUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    operator fun invoke(): Flow<List<AlertEvent>> = repository.getAlertHistory()
}
