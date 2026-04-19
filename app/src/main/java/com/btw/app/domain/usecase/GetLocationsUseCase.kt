package com.btw.app.domain.usecase

import com.btw.app.domain.model.SavedLocation
import com.btw.app.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationsUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    operator fun invoke(): Flow<List<SavedLocation>> = repository.getLocations()
}
