package com.tonedefapps.btw.ui.handoff

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.HandoffContact
import com.tonedefapps.btw.domain.model.HandoffEvent
import com.tonedefapps.btw.domain.model.PickupWindow
import com.tonedefapps.btw.domain.model.SavedLocation
import com.tonedefapps.btw.domain.repository.HandoffRepository
import com.tonedefapps.btw.domain.usecase.GetLocationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HandoffViewModel @Inject constructor(
    private val handoffRepository: HandoffRepository,
    getLocationsUseCase: GetLocationsUseCase
) : ViewModel() {

    // riderId selected in the UI — drives the contact/window flows
    private val _selectedRiderId = MutableStateFlow<Long>(-1L)
    val selectedRiderId: StateFlow<Long> = _selectedRiderId.asStateFlow()

    val locations: StateFlow<List<SavedLocation>> = getLocationsUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val handoffHistory: StateFlow<List<HandoffEvent>> = handoffRepository.getHandoffHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val contacts: StateFlow<List<HandoffContact>> = _selectedRiderId
        .flatMapLatest { id ->
            if (id < 0) flowOf(emptyList())
            else handoffRepository.getContactsForRider(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val pickupWindows: StateFlow<List<PickupWindow>> = _selectedRiderId
        .flatMapLatest { id ->
            if (id < 0) flowOf(emptyList())
            else handoffRepository.getWindowsForRider(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectRider(riderId: Long) { _selectedRiderId.value = riderId }

    fun addContact(riderId: Long, name: String, phone: String) {
        viewModelScope.launch {
            handoffRepository.addContact(HandoffContact(riderId = riderId, name = name, phone = phone))
        }
    }

    fun deleteContact(id: Long) {
        viewModelScope.launch { handoffRepository.deleteContact(id) }
    }

    fun addPickupWindow(
        riderId: Long,
        locationId: Long,
        daysOfWeek: Set<Int>,
        hour: Int,
        minute: Int,
        windowMinutes: Int
    ) {
        viewModelScope.launch {
            handoffRepository.addWindow(
                PickupWindow(
                    riderId = riderId,
                    locationId = locationId,
                    daysOfWeek = daysOfWeek,
                    startHour = hour,
                    startMinute = minute,
                    windowMinutes = windowMinutes
                )
            )
        }
    }

    fun toggleWindow(window: PickupWindow) {
        viewModelScope.launch {
            handoffRepository.updateWindow(window.copy(isActive = !window.isActive))
        }
    }

    fun deleteWindow(id: Long) {
        viewModelScope.launch { handoffRepository.deleteWindow(id) }
    }

    companion object {
        val DAY_LABELS = mapOf(
            Calendar.SUNDAY to "sun",
            Calendar.MONDAY to "mon",
            Calendar.TUESDAY to "tue",
            Calendar.WEDNESDAY to "wed",
            Calendar.THURSDAY to "thu",
            Calendar.FRIDAY to "fri",
            Calendar.SATURDAY to "sat"
        )
    }
}
