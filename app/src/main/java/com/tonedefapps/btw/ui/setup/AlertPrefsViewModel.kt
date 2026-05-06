package com.tonedefapps.btw.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tonedefapps.btw.domain.model.AlertPreferences
import com.tonedefapps.btw.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertPrefsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    val prefs: StateFlow<AlertPreferences> = preferencesRepository.getAlertPreferences()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AlertPreferences())

    fun toggleHotDayMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesRepository.updateAlertPreferences(prefs.value.copy(hotDayModeEnabled = enabled))
        }
    }
}
