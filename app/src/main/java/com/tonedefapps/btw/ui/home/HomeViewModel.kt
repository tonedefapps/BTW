package com.tonedefapps.btw.ui.home

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.*
import com.tonedefapps.btw.domain.monitor.MonitorStateHolder
import com.tonedefapps.btw.domain.repository.AlertRepository
import com.tonedefapps.btw.domain.repository.PreferencesRepository
import com.tonedefapps.btw.domain.usecase.GetAlertHistoryUseCase
import com.tonedefapps.btw.domain.usecase.GetRidersUseCase
import com.tonedefapps.btw.domain.usecase.GetVehiclesUseCase
import com.tonedefapps.btw.service.BtwMonitorService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val tripState: TripState = TripState.IDLE,
    val connectedVehicle: Vehicle? = null,
    val riders: List<Rider> = emptyList(),
    val activeAlertId: Long = -1L,
    val activeRiderName: String = "",
    val recentAlerts: List<AlertEvent> = emptyList(),
    val tripStartedAt: Long? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    getRidersUseCase: GetRidersUseCase,
    getVehiclesUseCase: GetVehiclesUseCase,
    private val alertRepository: AlertRepository,
    private val preferencesRepository: PreferencesRepository,
    private val monitorStateHolder: MonitorStateHolder,
    getAlertHistoryUseCase: GetAlertHistoryUseCase
) : AndroidViewModel(application) {

    private val startOfToday: Long get() {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    val uiState: StateFlow<HomeUiState> = combine(
        getRidersUseCase(),
        getVehiclesUseCase(),
        monitorStateHolder.tripState,
        monitorStateHolder.activeAlertId,
        combine(monitorStateHolder.tripStartedAt, getAlertHistoryUseCase()) { tripStart, history -> tripStart to history }
    ) { riders, vehicles, tripState, activeAlertId, (tripStart, history) ->
        val todayAlerts = history
            .filter { it.triggeredAt >= startOfToday && it.outcome != AlertOutcome.PENDING }
            .sortedByDescending { it.triggeredAt }
            .take(3)
        HomeUiState(
            tripState = tripState,
            connectedVehicle = vehicles.firstOrNull { it.isPrimary },
            riders = riders,
            activeAlertId = activeAlertId,
            activeRiderName = riders.firstOrNull()?.name ?: "",
            recentAlerts = todayAlerts,
            tripStartedAt = tripStart
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
        val intent = Intent(getApplication(), BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_SAFE
        }
        getApplication<Application>().startService(intent)
    }

    fun acknowledgeGoingBack(alertId: Long) {
        val intent = Intent(getApplication(), BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_GOING_BACK
        }
        getApplication<Application>().startService(intent)
    }
}
