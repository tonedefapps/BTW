package com.btw.app.data.local.dao

import androidx.room.*
import com.btw.app.data.local.entity.RiderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RiderDao {
    @Query("SELECT * FROM riders ORDER BY createdAt DESC")
    fun getAllRiders(): Flow<List<RiderEntity>>

    @Query("SELECT * FROM riders WHERE id = :id")
    suspend fun getRiderById(id: Long): RiderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rider: RiderEntity): Long

    @Update
    suspend fun update(rider: RiderEntity)

    @Query("DELETE FROM riders WHERE id = :id")
    suspend fun deleteById(id: Long)
}
