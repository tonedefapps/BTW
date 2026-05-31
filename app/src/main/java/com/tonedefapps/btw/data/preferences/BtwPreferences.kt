package com.tonedefapps.btw.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.tonedefapps.btw.domain.model.AlertPreferences
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
        val HOT_DAY_ENABLED = booleanPreferencesKey("hot_day_mode_enabled")
        val HOT_DAY_THRESHOLD = intPreferencesKey("hot_day_threshold_f")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val IS_PREMIUM = booleanPreferencesKey("is_premium")
    }

    val alertPreferences: Flow<AlertPreferences> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
        .map { prefs ->
            AlertPreferences(
                step1DelaySeconds = prefs[Keys.STEP1_DELAY] ?: 30,
                step2DelaySeconds = prefs[Keys.STEP2_DELAY] ?: 120,
                hotDayModeEnabled = prefs[Keys.HOT_DAY_ENABLED] ?: false,
                hotDayThresholdFahrenheit = prefs[Keys.HOT_DAY_THRESHOLD] ?: 80,
                onboardingComplete = prefs[Keys.ONBOARDING_COMPLETE] ?: false,
                isPremium = prefs[Keys.IS_PREMIUM] ?: false
            )
        }

    suspend fun update(prefs: AlertPreferences) {
        context.dataStore.edit { p ->
            p[Keys.STEP1_DELAY] = prefs.step1DelaySeconds
            p[Keys.STEP2_DELAY] = prefs.step2DelaySeconds
            p[Keys.HOT_DAY_ENABLED] = prefs.hotDayModeEnabled
            p[Keys.HOT_DAY_THRESHOLD] = prefs.hotDayThresholdFahrenheit
            p[Keys.ONBOARDING_COMPLETE] = prefs.onboardingComplete
            p[Keys.IS_PREMIUM] = prefs.isPremium
        }
    }

    suspend fun setPremium(premium: Boolean) {
        context.dataStore.edit { it[Keys.IS_PREMIUM] = premium }
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETE] = complete }
    }
}
