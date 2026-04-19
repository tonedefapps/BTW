package com.btw.app.domain.model

data class AlertPreferences(
    val step1DelaySeconds: Int = 30,
    val step2DelaySeconds: Int = 120,
    val step3DelaySeconds: Int = 300,
    val step4DelaySeconds: Int = 600,
    val hotDayModeEnabled: Boolean = true,
    val hotDayThresholdFahrenheit: Int = 85,
    val emergencyContactName: String = "",
    val emergencyContactPhone: String = "",
    val onboardingComplete: Boolean = false
)
