package com.tonedefapps.btw.data.repository

import com.tonedefapps.btw.data.local.dao.LocationDao
import com.tonedefapps.btw.data.local.entity.RiderLocationStatsEntity
import com.tonedefapps.btw.data.local.entity.SavedLocationEntity
import com.tonedefapps.btw.domain.model.RiderLocationStats
import com.tonedefapps.btw.domain.model.SavedLocation
import com.tonedefapps.btw.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.*

class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao
) : LocationRepository {

    override fun getLocations(): Flow<List<SavedLocation>> =
        dao.getAllLocations().map { it.map(SavedLocationEntity::toDomain) }

    override suspend fun getLocationById(id: Long): SavedLocation? =
        dao.getById(id)?.toDomain()

    override suspend fun getNearestWithin(lat: Double, lng: Double, radiusMeters: Float): SavedLocation? {
        // 1 degree ≈ 111_000 metres — bounding box slightly larger than search radius
        val degreePad = (radiusMeters / 111_000.0) * 1.5
        val candidates = dao.getWithinBoundingBox(
            minLat = lat - degreePad, maxLat = lat + degreePad,
            minLng = lng - degreePad, maxLng = lng + degreePad
        )
        return candidates
            .map { it.toDomain() }
            .filter { haversineMeters(lat, lng, it.lat, it.lng) <= it.radiusMeters }
            .minByOrNull { haversineMeters(lat, lng, it.lat, it.lng) }
    }

    override suspend fun addLocation(location: SavedLocation): Long =
        dao.insert(SavedLocationEntity.fromDomain(location))

    override suspend fun updateLocation(location: SavedLocation) =
        dao.update(SavedLocationEntity.fromDomain(location))

    override suspend fun deleteLocation(id: Long) = dao.deleteById(id)

    override suspend fun recordVisit(locationId: Long) = dao.recordVisit(locationId)

    override fun getStatsForRider(riderId: Long): Flow<List<RiderLocationStats>> =
        dao.getStatsForRider(riderId).map { it.map(RiderLocationStatsEntity::toDomain) }

    override suspend fun incrementPresent(riderId: Long, locationId: Long) {
        ensureStatRow(riderId, locationId)
        dao.incrementPresent(riderId, locationId)
    }

    override suspend fun incrementAbsent(riderId: Long, locationId: Long) {
        ensureStatRow(riderId, locationId)
        dao.incrementAbsent(riderId, locationId)
    }

    private suspend fun ensureStatRow(riderId: Long, locationId: Long) {
        if (dao.getStat(riderId, locationId) == null) {
            dao.insertStat(RiderLocationStatsEntity(riderId = riderId, locationId = locationId))
        }
    }

    companion object {
        private fun haversineMeters(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
            val r = 6_371_000.0
            val dLat = Math.toRadians(lat2 - lat1)
            val dLng = Math.toRadians(lng2 - lng1)
            val a = sin(dLat / 2).pow(2) +
                    cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
            return r * 2 * asin(sqrt(a))
        }
    }
}
