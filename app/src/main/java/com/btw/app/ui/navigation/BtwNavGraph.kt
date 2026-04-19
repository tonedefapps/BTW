package com.btw.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.btw.app.ui.handoff.HandoffContactsScreen
import com.btw.app.ui.home.HomeScreen
import com.btw.app.ui.locations.LocationsScreen
import com.btw.app.ui.onboarding.OnboardingScreen
import com.btw.app.ui.settings.HistoryScreen
import com.btw.app.ui.settings.RidersScreen
import com.btw.app.ui.settings.SettingsScreen
import com.btw.app.ui.setup.AddRiderScreen
import com.btw.app.ui.setup.AlertPrefsScreen
import com.btw.app.ui.setup.PairVehicleScreen
import com.btw.app.ui.setup.SetupCompleteScreen

@Composable
fun BtwNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.PairVehicle.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PairVehicle.route) {
            PairVehicleScreen(
                onVehiclePaired = { navController.navigate(Screen.AddRider.route) },
                onSkip = { navController.navigate(Screen.AddRider.route) }
            )
        }

        composable(Screen.AddRider.route) {
            AddRiderScreen(
                onRiderAdded = { navController.navigate(Screen.AlertPrefs.route) },
                onSkip = { navController.navigate(Screen.AlertPrefs.route) }
            )
        }

        composable(Screen.AlertPrefs.route) {
            AlertPrefsScreen(onContinue = { navController.navigate(Screen.SetupComplete.route) })
        }

        composable(Screen.SetupComplete.route) {
            SetupCompleteScreen(
                onStart = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.PairVehicle.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToRiders = { navController.navigate(Screen.Riders.route) },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        composable(Screen.Riders.route) {
            RidersScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.History.route) {
            HistoryScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLocations = { navController.navigate(Screen.Locations.route) },
                onNavigateToHandoff = { riderId -> navController.navigate(Screen.Handoff.route(riderId)) }
            )
        }

        composable(Screen.Locations.route) {
            LocationsScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.Handoff.route,
            arguments = listOf(navArgument("riderId") { type = NavType.LongType })
        ) { backStackEntry ->
            val riderId = backStackEntry.arguments?.getLong("riderId") ?: -1L
            HandoffContactsScreen(
                riderId = riderId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
