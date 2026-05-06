package com.tonedefapps.btw.data.repository

import com.tonedefapps.btw.data.local.dao.VehicleDao
import com.tonedefapps.btw.data.local.entity.VehicleEntity
import com.tonedefapps.btw.domain.model.Vehicle
import com.tonedefapps.btw.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VehicleRepositoryImpl @Inject constructor(
    private val dao: VehicleDao
) : VehicleRepository {

    override fun getVehicles(): Flow<List<Vehicle>> =
        dao.getAllVehicles().map { list -> list.map { it.toDomain() } }

    override suspend fun getVehicleById(id: Long): Vehicle? =
        dao.getVehicleById(id)?.toDomain()

    override suspend fun getVehicleByBluetoothAddress(address: String): Vehicle? =
        dao.getByBluetoothAddress(address)?.toDomain()

    override suspend fun addVehicle(vehicle: Vehicle): Long =
        dao.insert(VehicleEntity.fromDomain(vehicle))

    override suspend fun updateVehicleLocation(id: Long, latitude: Double, longitude: Double) =
        dao.updateLocation(id, latitude, longitude)

    override suspend fun deleteVehicle(id: Long) =
        dao.deleteById(id)
}
