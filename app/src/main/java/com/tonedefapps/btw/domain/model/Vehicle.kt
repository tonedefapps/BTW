package com.tonedefapps.btw.domain.model

data class Vehicle(
    val id: Long = 0,
    val name: String,
    val bluetoothAddress: String?,  // null = location-only (no-BT) vehicle
    val lastLatitude: Double = 0.0,
    val lastLongitude: Double = 0.0,
    val lastSeenAt: Long = 0L,
    val isPrimary: Boolean = false
)

val Vehicle.isLocationOnly get() = bluetoothAddress == null
