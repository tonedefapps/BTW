package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.model.SavedLocation
import com.tonedefapps.btw.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): Flow<List<SavedLocation>> = repository.getLocations()
}
