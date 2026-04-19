package com.btw.app.domain.usecase

import com.btw.app.domain.repository.LocationRepository
import javax.inject.Inject

class RecordLocationVisitUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(locationId: Long) = repository.recordVisit(locationId)
}
