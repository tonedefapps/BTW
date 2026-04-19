package com.btw.app.ui.navigation

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object PairVehicle : Screen("pair_vehicle")
    object AddRider : Screen("add_rider")
    object AlertPrefs : Screen("alert_prefs")
    object SetupComplete : Screen("setup_complete")
    object Home : Screen("home")
    object Riders : Screen("riders")
    object History : Screen("history")
    object Settings : Screen("settings")
}
