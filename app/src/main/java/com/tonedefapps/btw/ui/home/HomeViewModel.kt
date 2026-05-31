package com.tonedefapps.btw.ui.home

import android.app.Application
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.*
import com.tonedefapps.btw.domain.model.isLocationOnly
import com.tonedefapps.btw.domain.monitor.MonitorStateHolder
import com.tonedefapps.btw.domain.repository.AlertRepository
import com.tonedefapps.btw.domain.repository.PreferencesRepository
import com.tonedefapps.btw.domain.repository.RiderRepository
import com.tonedefapps.btw.domain.repository.RiderScheduleRepository
import com.tonedefapps.btw.domain.usecase.GetAlertHistoryUseCase
import com.tonedefapps.btw.domain.usecase.GetLocationsUseCase
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
    val activeRiders: List<Rider> = emptyList(),
    val pausedRiders: List<Rider> = emptyList(),
    val activeAlertId: Long = -1L,
    val activeRiderName: String = "",
    val recentAlerts: List<AlertEvent> = emptyList(),
    val tripStartedAt: Long? = null,
    val hasLocationOnlyVehicle: Boolean = false,
    val hasBtVehicle: Boolean = false,
    val passiveWatchActive: Boolean = false,
    val hasSavedLocations: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    getRidersUseCase: GetRidersUseCase,
    getVehiclesUseCase: GetVehiclesUseCase,
    getLocationsUseCase: GetLocationsUseCase,
    private val alertRepository: AlertRepository,
    private val preferencesRepository: PreferencesRepository,
    private val monitorStateHolder: MonitorStateHolder,
    private val riderRepository: RiderRepository,
    private val riderScheduleRepository: RiderScheduleRepository,
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

    private val ridersAndSchedules = combine(
        getRidersUseCase(),
        riderScheduleRepository.getSchedules()
    ) { riders, schedules -> riders to schedules }

    val uiState: StateFlow<HomeUiState> = combine(
        ridersAndSchedules,
        getVehiclesUseCase(),
        monitorStateHolder.tripState,
        monitorStateHolder.activeAlertId,
        combine(
            monitorStateHolder.tripStartedAt,
            monitorStateHolder.passiveWatchActive,
            getLocationsUseCase(),
            getAlertHistoryUseCase()
        ) { tripStart, passiveWatch, locations, history -> object {
            val tripStart = tripStart; val passiveWatch = passiveWatch
            val locations = locations; val history = history
        }}
    ) { (riders, schedules), vehicles, tripState, activeAlertId, extra ->
        val now = System.currentTimeMillis()
        val activeRiders = riders.filter { it.isActive(schedules.firstOrNull { s -> s.riderId == it.id }, now) }
        val pausedRiders = riders.filter { !it.isActive(schedules.firstOrNull { s -> s.riderId == it.id }, now) }
        val todayAlerts = extra.history
            .filter { it.triggeredAt >= startOfToday && it.outcome != AlertOutcome.PENDING }
            .sortedByDescending { it.triggeredAt }
            .take(3)
        HomeUiState(
            tripState = tripState,
            connectedVehicle = vehicles.firstOrNull { it.isPrimary },
            riders = riders,
            activeRiders = activeRiders,
            pausedRiders = pausedRiders,
            activeAlertId = activeAlertId,
            activeRiderName = activeRiders.map { it.name }.let { names ->
                if (names.isEmpty()) riders.firstOrNull()?.name ?: "" else names.joinToString(" & ")
            },
            recentAlerts = todayAlerts,
            tripStartedAt = extra.tripStart,
            hasLocationOnlyVehicle = vehicles.any { it.isLocationOnly },
            hasBtVehicle = vehicles.any { !it.isLocationOnly },
            passiveWatchActive = extra.passiveWatch,
            hasSavedLocations = extra.locations.isNotEmpty()
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

    fun acknowledgeSafe(@Suppress("UNUSED_PARAMETER") alertId: Long) {
        val intent = Intent(getApplication(), BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_SAFE
        }
        getApplication<Application>().startService(intent)
    }

    fun acknowledgeGoingBack(@Suppress("UNUSED_PARAMETER") alertId: Long) {
        val intent = Intent(getApplication(), BtwMonitorService::class.java).apply {
            action = BtwMonitorService.ACTION_ACKNOWLEDGE_GOING_BACK
        }
        getApplication<Application>().startService(intent)
    }

    fun unpauseRider(riderId: Long) {
        viewModelScope.launch { riderRepository.unpauseRider(riderId) }
    }
}
