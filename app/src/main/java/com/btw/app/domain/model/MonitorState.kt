package com.btw.app.domain.model

data class MonitorState(
    val isVehicleConnected: Boolean = false,
    val connectedVehicle: Vehicle? = null,
    val activeRiders: List<Rider> = emptyList(),
    val tripState: TripState = TripState.IDLE
)

enum class TripState { IDLE, IN_VEHICLE, PARKED_SAFE, ALERT_TRIGGERED }
