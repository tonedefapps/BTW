package com.btw.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.btw.app.domain.model.AlertPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "btw_prefs")

@Singleton
class BtwPreferences @Inject constructor(private val context: Context) {

    private object Keys {
        val STEP1_DELAY = intPreferencesKey("step1_delay_seconds")
        val STEP2_DELAY = intPreferencesKey("step2_delay_seconds")
        val STEP3_DELAY = intPreferencesKey("step3_delay_seconds")
        val STEP4_DELAY = intPreferencesKey("step4_delay_seconds")
        val HOT_DAY_ENABLED = booleanPreferencesKey("hot_day_mode_enabled")
        val HOT_DAY_THRESHOLD = intPreferencesKey("hot_day_threshold_f")
        val EMERGENCY_NAME = stringPreferencesKey("emergency_contact_name")
        val EMERGENCY_PHONE = stringPreferencesKey("emergency_contact_phone")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    val alertPreferences: Flow<AlertPreferences> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            AlertPreferences(
                step1DelaySeconds = prefs[Keys.STEP1_DELAY] ?: 30,
                step2DelaySeconds = prefs[Keys.STEP2_DELAY] ?: 120,
                step3DelaySeconds = prefs[Keys.STEP3_DELAY] ?: 300,
                step4DelaySeconds = prefs[Keys.STEP4_DELAY] ?: 600,
                hotDayModeEnabled = prefs[Keys.HOT_DAY_ENABLED] ?: true,
                hotDayThresholdFahrenheit = prefs[Keys.HOT_DAY_THRESHOLD] ?: 85,
                emergencyContactName = prefs[Keys.EMERGENCY_NAME] ?: "",
                emergencyContactPhone = prefs[Keys.EMERGENCY_PHONE] ?: "",
                onboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false
            )
        }

    suspend fun update(prefs: AlertPreferences) {
        context.dataStore.edit { p ->
            p[Keys.STEP1_DELAY] = prefs.step1DelaySeconds
            p[Keys.STEP2_DELAY] = prefs.step2DelaySeconds
            p[Keys.STEP3_DELAY] = prefs.step3DelaySeconds
            p[Keys.STEP4_DELAY] = prefs.step4DelaySeconds
            p[Keys.HOT_DAY_ENABLED] = prefs.hotDayModeEnabled
            p[Keys.HOT_DAY_THRESHOLD] = prefs.hotDayThresholdFahrenheit
            p[Keys.EMERGENCY_NAME] = prefs.emergencyContactName
            p[Keys.EMERGENCY_PHONE] = prefs.emergencyContactPhone
            p[Keys.ONBOARDING_COMPLETE] = prefs.onboardingComplete
        }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }
}
