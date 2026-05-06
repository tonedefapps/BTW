package com.tonedefapps.btw.data.local.dao

import androidx.room.*
import com.tonedefapps.btw.data.local.entity.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY isPrimary DESC, lastSeenAt DESC")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles ORDER BY isPrimary DESC, lastSeenAt DESC")
    suspend fun getAllVehiclesList(): List<VehicleEntity>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: Long): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE bluetoothAddress = :address LIMIT 1")
    suspend fun getByBluetoothAddress(address: String): VehicleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: VehicleEntity): Long

    @Query("UPDATE vehicles SET lastLatitude = :lat, lastLongitude = :lng, lastSeenAt = :now WHERE id = :id")
    suspend fun updateLocation(id: Long, lat: Double, lng: Double, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM vehicles WHERE id = :id")
    suspend fun deleteById(id: Long)
}
