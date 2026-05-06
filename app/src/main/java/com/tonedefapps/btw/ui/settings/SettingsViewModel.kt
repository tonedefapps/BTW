package com.tonedefapps.btw.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.AlertEvent
import com.tonedefapps.btw.domain.model.AlertPreferences
import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.model.RiderType
import com.tonedefapps.btw.domain.repository.AlertRepository
import com.tonedefapps.btw.domain.repository.PreferencesRepository
import com.tonedefapps.btw.domain.usecase.AddRiderUseCase
import com.tonedefapps.btw.domain.usecase.GetAlertHistoryUseCase
import com.tonedefapps.btw.domain.usecase.GetRidersUseCase
import com.tonedefapps.btw.domain.repository.RiderRepository
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

    fun updateStepDelay(step: Int, seconds: Int) {
        viewModelScope.launch {
            val updated = when (step) {
                1 -> prefs.value.copy(step1DelaySeconds = seconds)
                2 -> prefs.value.copy(step2DelaySeconds = seconds)
                else -> return@launch
            }
            preferencesRepository.updateAlertPreferences(updated)
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
