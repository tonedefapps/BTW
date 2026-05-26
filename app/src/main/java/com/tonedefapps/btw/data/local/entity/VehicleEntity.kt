package com.tonedefapps.btw.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tonedefapps.btw.domain.model.Vehicle

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val bluetoothAddress: String?,
    val lastLatitude: Double,
    val lastLongitude: Double,
    val lastSeenAt: Long,
    val isPrimary: Boolean
) {
    fun toDomain() = Vehicle(
        id = id,
        name = name,
        bluetoothAddress = bluetoothAddress,
        lastLatitude = lastLatitude,
        lastLongitude = lastLongitude,
        lastSeenAt = lastSeenAt,
        isPrimary = isPrimary
    )

    companion object {
        fun fromDomain(vehicle: Vehicle) = VehicleEntity(
            id = vehicle.id,
            name = vehicle.name,
            bluetoothAddress = vehicle.bluetoothAddress,
            lastLatitude = vehicle.lastLatitude,
            lastLongitude = vehicle.lastLongitude,
            lastSeenAt = vehicle.lastSeenAt,
            isPrimary = vehicle.isPrimary
        )
    }
}
