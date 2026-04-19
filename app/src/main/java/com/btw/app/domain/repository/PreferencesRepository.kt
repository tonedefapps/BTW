package com.btw.app.domain.repository

import com.btw.app.domain.model.AlertPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getAlertPreferences(): Flow<AlertPreferences>
    suspend fun updateAlertPreferences(prefs: AlertPreferences)
    suspend fun setOnboardingComplete(complete: Boolean)
    suspend fun isOnboardingComplete(): Boolean
}
