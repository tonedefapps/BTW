package com.tonedefapps.btw.domain.model

data class AlertPreferences(
    val step1DelaySeconds: Int = 30,
    val step2DelaySeconds: Int = 120,
    val hotDayModeEnabled: Boolean = true,
    val hotDayThresholdFahrenheit: Int = 80,
    val onboardingComplete: Boolean = false,
    val isPremium: Boolean = false
)
