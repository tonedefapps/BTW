package com.btw.app.domain.model

data class Vehicle(
    val id: Long = 0,
    val name: String,
    val bluetoothAddress: String,
    val lastLatitude: Double = 0.0,
    val lastLongitude: Double = 0.0,
    val lastSeenAt: Long = 0L,
    val isPrimary: Boolean = false
)
