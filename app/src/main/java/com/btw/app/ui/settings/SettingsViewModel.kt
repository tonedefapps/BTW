package com.btw.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btw.app.domain.model.AlertEvent
import com.btw.app.domain.model.AlertPreferences
import com.btw.app.domain.model.Rider
import com.btw.app.domain.model.RiderType
import com.btw.app.domain.repository.AlertRepository
import com.btw.app.domain.repository.PreferencesRepository
import com.btw.app.domain.usecase.AddRiderUseCase
import com.btw.app.domain.usecase.GetAlertHistoryUseCase
import com.btw.app.domain.usecase.GetRidersUseCase
import com.btw.app.domain.repository.RiderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getRidersUseCase: GetRidersUseCase,
    getAlertHistoryUseCase: GetAlertHistoryUseCase,
    private val preferencesRepository: PreferencesRepository,
    private val riderRepository: RiderRepository,
    private val addRiderUseCase: AddRiderUseCase
) : ViewModel() {

    val riders: StateFlow<List<Rider>> = getRidersUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val alertHistory: StateFlow<List<AlertEvent>> = getAlertHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val prefs: StateFlow<AlertPreferences> = preferencesRepository.getAlertPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AlertPreferences())

    fun toggleHotDayMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAlertPreferences(prefs.value.copy(hotDayModeEnabled = enabled))
        }
    }

    fun saveEmergencyContact(name: String, phone: String) {
        viewModelScope.launch {
            preferencesRepository.updateAlertPreferences(
                prefs.value.copy(emergencyContactName = name, emergencyContactPhone = phone)
            )
        }
    }

    fun addRider(name: String, type: RiderType, emoji: String) {
        viewModelScope.launch {
            addRiderUseCase(Rider(name = name, type = type, emoji = emoji))
        }
    }

    fun deleteRider(id: Long) {
        viewModelScope.launch {
            riderRepository.deleteRider(id)
        }
    }
}
