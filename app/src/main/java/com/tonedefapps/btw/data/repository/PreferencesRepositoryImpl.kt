package com.tonedefapps.btw.data.repository

import com.tonedefapps.btw.data.preferences.BtwPreferences
import com.tonedefapps.btw.domain.model.AlertPreferences
import com.tonedefapps.btw.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PreferencesRepositoryImpl @Inject constructor(
    private val prefs: BtwPreferences
) : PreferencesRepository {

    override fun getAlertPreferences(): Flow<AlertPreferences> = prefs.alertPreferences

    override suspend fun updateAlertPreferences(alertPrefs: AlertPreferences) =
        prefs.update(alertPrefs)

    override suspend fun setOnboardingComplete(complete: Boolean) =
        prefs.setOnboardingComplete(complete)

    override suspend fun isOnboardingComplete(): Boolean =
        prefs.alertPreferences.first().onboardingComplete
}
