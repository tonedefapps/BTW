package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.model.AlertEvent
import com.tonedefapps.btw.domain.repository.AlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAlertHistoryUseCase @Inject constructor(
    private val repository: AlertRepository
) {
    operator fun invoke(): Flow<List<AlertEvent>> = repository.getAlertHistory()
}
