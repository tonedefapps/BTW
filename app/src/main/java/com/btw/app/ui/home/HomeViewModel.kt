package com.btw.app.ui.home

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.btw.app.domain.model.*
import com.btw.app.domain.repository.AlertRepository
import com.btw.app.domain.repository.PreferencesRepository
import com.btw.app.domain.usecase.GetRidersUseCase
import com.btw.app.domain.usecase.GetVehiclesUseCase
import com.btw.app.service.BtwMonitorService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val tripState: TripState = TripState.IDLE,
    val connectedVehicle: Vehicle? = null,
    val riders: List<Rider> = emptyList(),
    val activeAlertId: Long = -1L,
    val activeRiderName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    getRidersUseCase: GetRidersUseCase,
    getVehiclesUseCase: GetVehiclesUseCase,
    private val alertRepository: AlertRepository,
    private val preferencesRepository: PreferencesRepository
) : AndroidViewModel(application) {

    private val _tripState = MutableStateFlow(TripState.IDLE)
    private val _connectedVehicle = MutableStateFlow<Vehicle?>(null)

    val uiState: StateFlow<HomeUiState> = combine(
        getRidersUseCase(),
        getVehiclesUseCase(),
        _tripState,
        _connectedVehicle
    ) { riders, vehicles, tripState, vehicle ->
        HomeUiState(
            tripState = tripState,
            connectedVehicle = vehicle ?: vehicles.firstOrNull { it.isPrimary },
            riders = riders,
            activeRiderName = riders.firstOrNull()?.name ?: ""
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun startMonitor() {
        val intent = Intent(getApplication(), BtwMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getApplication<Application>().startForegroundService(intent)
        } else {
            getApplication<Application>().startService(intent)
        }
    }

    fun acknowledgeSafe(alertId: Long) {
        viewModelScope.launch {
            alertRepository.updateOutcome(alertId, AlertOutcome.SAFE)
        }
        val intent = Intent(getApplication(), BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_SAFE
        }
        getApplication<Application>().startService(intent)
        _tripState.value = TripState.PARKED_SAFE
    }

    fun acknowledgeGoingBack(alertId: Long) {
        viewModelScope.launch {
            alertRepository.updateOutcome(alertId, AlertOutcome.WENT_BACK)
        }
        val intent = Intent(getApplication(), BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_GOING_BACK
        }
        getApplication<Application>().startService(intent)
        _tripState.value = TripState.IDLE
    }
}
