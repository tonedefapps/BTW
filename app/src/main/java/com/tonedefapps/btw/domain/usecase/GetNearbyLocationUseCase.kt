package com.tonedefapps.btw.domain.usecase

import com.tonedefapps.btw.domain.model.SavedLocation
import com.tonedefapps.btw.domain.repository.LocationRepository
import javax.inject.Inject

class GetNearbyLocationUseCase @Inject constructor(
    private val repository: LocationRepository
) {
    suspend operator fun invoke(lat: Double, lng: Double, radiusMeters: Float = 100f): SavedLocation? =
        repository.getNearestWithin(lat, lng, radiusMeters)
}
