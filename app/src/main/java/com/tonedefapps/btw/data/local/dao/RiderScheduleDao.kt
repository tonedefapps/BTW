package com.tonedefapps.btw.data.local.dao

import androidx.room.*
import com.tonedefapps.btw.data.local.entity.RiderScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiderScheduleDao {
    @Query("SELECT * FROM rider_schedules WHERE riderId = :riderId")
    fun getScheduleForRider(riderId: Long): Flow<RiderScheduleEntity?>

    @Query("SELECT * FROM rider_schedules WHERE riderId = :riderId")
    suspend fun getScheduleForRiderOnce(riderId: Long): RiderScheduleEntity?

    @Query("SELECT * FROM rider_schedules")
    fun getAllSchedules(): Flow<List<RiderScheduleEntity>>

    @Query("SELECT * FROM rider_schedules")
    suspend fun getAllSchedulesList(): List<RiderScheduleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(schedule: RiderScheduleEntity)

    @Query("DELETE FROM rider_schedules WHERE riderId = :riderId")
    suspend fun deleteForRider(riderId: Long)
}
