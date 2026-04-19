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
    object Locations : Screen("locations")

    // riderId is a required nav arg
    object Handoff : Screen("handoff/{riderId}") {
        fun route(riderId: Long) = "handoff/$riderId"
    }
}
