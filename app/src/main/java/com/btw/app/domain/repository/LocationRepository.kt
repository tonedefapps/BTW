package com.btw.app.domain.repository

import com.btw.app.domain.model.RiderLocationStats
import com.btw.app.domain.model.SavedLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getLocations(): Flow<List<SavedLocation>>
    suspend fun getLocationById(id: Long): SavedLocation?
    suspend fun getNearestWithin(lat: Double, lng: Double, radiusMeters: Float): SavedLocation?
    suspend fun addLocation(location: SavedLocation): Long
    suspend fun updateLocation(location: SavedLocation)
    suspend fun deleteLocation(id: Long)

    suspend fun recordVisit(locationId: Long)

    fun getStatsForRider(riderId: Long): Flow<List<RiderLocationStats>>
    suspend fun incrementPresent(riderId: Long, locationId: Long)
    suspend fun incrementAbsent(riderId: Long, locationId: Long)
}
