package com.tonedefapps.btw.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.model.RiderSchedule
import com.tonedefapps.btw.domain.model.ScheduleType
import com.tonedefapps.btw.domain.repository.RiderScheduleRepository
import com.tonedefapps.btw.domain.usecase.GetRidersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RiderScheduleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getRidersUseCase: GetRidersUseCase,
    private val riderScheduleRepository: RiderScheduleRepository
) : ViewModel() {

    val riderId: Long = checkNotNull(savedStateHandle["riderId"])

    val rider: StateFlow<Rider?> = getRidersUseCase()
        .map { riders -> riders.firstOrNull { it.id == riderId } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val schedule: StateFlow<RiderSchedule?> = riderScheduleRepository.getScheduleForRider(riderId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun saveWeekly(bitmask: Int) {
        viewModelScope.launch {
            riderScheduleRepository.upsertSchedule(
                RiderSchedule(riderId = riderId, type = ScheduleType.WEEKLY, daysOfWeekBitmask = bitmask)
            )
        }
    }

    fun saveAlternating(referenceDate: Long) {
        viewModelScope.launch {
            riderScheduleRepository.upsertSchedule(
                RiderSchedule(riderId = riderId, type = ScheduleType.ALTERNATING_WEEKS, referenceDate = referenceDate)
            )
        }
    }

    fun clearSchedule() {
        viewModelScope.launch { riderScheduleRepository.deleteSchedule(riderId) }
    }
}
