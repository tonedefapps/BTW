package com.tonedefapps.btw.domain.repository

import com.tonedefapps.btw.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun getVehicles(): Flow<List<Vehicle>>
    suspend fun getVehicleById(id: Long): Vehicle?
    suspend fun getVehicleByBluetoothAddress(address: String): Vehicle?
    suspend fun addVehicle(vehicle: Vehicle): Long
    suspend fun updateVehicleLocation(id: Long, latitude: Double, longitude: Double)
    suspend fun deleteVehicle(id: Long)
}
