package com.tonedefapps.btw.domain.monitor

import com.tonedefapps.btw.domain.model.TripState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MonitorStateHolder @Inject constructor() {
    private val _tripState = MutableStateFlow(TripState.IDLE)
    val tripState: StateFlow<TripState> = _tripState

    private val _tripStartedAt = MutableStateFlow<Long?>(null)
    val tripStartedAt: StateFlow<Long?> = _tripStartedAt

    private val _activeAlertId = MutableStateFlow(-1L)
    val activeAlertId: StateFlow<Long> = _activeAlertId

    fun onVehicleConnected() {
        _tripState.value = TripState.IN_VEHICLE
        _tripStartedAt.value = System.currentTimeMillis()
    }

    fun onAlertTriggered(alertId: Long) {
        _activeAlertId.value = alertId
        _tripState.value = TripState.ALERT_TRIGGERED
    }

    fun onAcknowledged() {
        _tripState.value = TripState.PARKED_SAFE
        _tripStartedAt.value = null
        _activeAlertId.value = -1L
    }

    fun onGoingBack() {
        _tripState.value = TripState.IDLE
        _tripStartedAt.value = null
        _activeAlertId.value = -1L
    }

    fun onIdle() {
        _tripState.value = TripState.IDLE
        _tripStartedAt.value = null
    }
}
