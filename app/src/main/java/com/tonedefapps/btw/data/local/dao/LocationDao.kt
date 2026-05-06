package com.tonedefapps.btw.data.local.dao

import androidx.room.*
import com.tonedefapps.btw.data.local.entity.RiderLocationStatsEntity
import com.tonedefapps.btw.data.local.entity.SavedLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {

    @Query("SELECT * FROM saved_locations ORDER BY confidence DESC, visitCount DESC")
    fun getAllLocations(): Flow<List<SavedLocationEntity>>

    @Query("SELECT * FROM saved_locations WHERE id = :id")
    suspend fun getById(id: Long): SavedLocationEntity?

    // Bounding-box pre-filter; exact distance check happens in the repo
    @Query("""
        SELECT * FROM saved_locations
        WHERE lat BETWEEN :minLat AND :maxLat
          AND lng BETWEEN :minLng AND :maxLng
        ORDER BY confidence DESC
    """)
    suspend fun getWithinBoundingBox(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double
    ): List<SavedLocationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: SavedLocationEntity): Long

    @Update
    suspend fun update(location: SavedLocationEntity)

    @Query("DELETE FROM saved_locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("""
        UPDATE saved_locations
        SET visitCount = visitCount + 1,
            confidence = CASE source
                WHEN 'MANUAL' THEN 1.0
                ELSE MIN(1.0, (visitCount + 1) * 0.1)
            END,
            lastVisited = :now
        WHERE id = :id
    """)
    suspend fun recordVisit(id: Long, now: Long = System.currentTimeMillis())

    // Rider-location stats
    @Query("SELECT * FROM rider_location_stats WHERE riderId = :riderId")
    fun getStatsForRider(riderId: Long): Flow<List<RiderLocationStatsEntity>>

    @Query("SELECT * FROM rider_location_stats WHERE riderId = :riderId AND locationId = :locationId LIMIT 1")
    suspend fun getStat(riderId: Long, locationId: Long): RiderLocationStatsEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertStat(stat: RiderLocationStatsEntity): Long

    @Query("UPDATE rider_location_stats SET presentCount = presentCount + 1 WHERE riderId = :riderId AND locationId = :locationId")
    suspend fun incrementPresent(riderId: Long, locationId: Long)

    @Query("UPDATE rider_location_stats SET absentCount = absentCount + 1 WHERE riderId = :riderId AND locationId = :locationId")
    suspend fun incrementAbsent(riderId: Long, locationId: Long)
}
