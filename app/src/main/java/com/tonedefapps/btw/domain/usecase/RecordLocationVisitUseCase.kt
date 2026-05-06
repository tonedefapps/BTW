package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.repository.LocationRepository
import javax.inject.Inject

class RecordLocationVisitUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(locationId: Long) = repository.recordVisit(locationId)
}
