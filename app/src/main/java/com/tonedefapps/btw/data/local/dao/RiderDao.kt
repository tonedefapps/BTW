package com.tonedefapps.btw.data.local.dao

import androidx.room.*
import com.tonedefapps.btw.data.local.entity.RiderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiderDao {
    @Query("SELECT * FROM riders ORDER BY createdAt DESC")
    fun getAllRiders(): Flow<List<RiderEntity>>

    @Query("SELECT * FROM riders ORDER BY createdAt DESC")
    suspend fun getAllRidersList(): List<RiderEntity>

    @Query("SELECT * FROM riders WHERE id = :id")
    suspend fun getRiderById(id: Long): RiderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rider: RiderEntity): Long

    @Update
    suspend fun update(rider: RiderEntity)

    @Query("DELETE FROM riders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE riders SET pausedUntil = :until WHERE id = :id")
    suspend fun pauseRider(id: Long, until: Long)

    @Query("UPDATE riders SET pausedUntil = NULL WHERE id = :id")
    suspend fun unpauseRider(id: Long)
}
