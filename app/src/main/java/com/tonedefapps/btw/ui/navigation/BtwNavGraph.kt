package com.tonedefapps.btw.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.tonedefapps.btw.ui.handoff.HandoffContactsScreen
import com.tonedefapps.btw.ui.vehicles.VehiclesScreen
import com.tonedefapps.btw.ui.home.HomeScreen
import com.tonedefapps.btw.ui.locations.LocationsScreen
import com.tonedefapps.btw.ui.onboarding.OnboardingScreen
import com.tonedefapps.btw.ui.paywall.PaywallScreen
import com.tonedefapps.btw.ui.settings.HistoryScreen
import com.tonedefapps.btw.ui.settings.RidersScreen
import com.tonedefapps.btw.ui.settings.SettingsScreen
import com.tonedefapps.btw.ui.setup.AddRiderScreen
import com.tonedefapps.btw.ui.setup.AlertPrefsScreen
import com.tonedefapps.btw.ui.setup.PairVehicleScreen
import com.tonedefapps.btw.ui.setup.SetupCompleteScreen
import com.tonedefapps.btw.ui.theme.Depth
import com.tonedefapps.btw.ui.theme.Ink
import com.tonedefapps.btw.ui.theme.Sky

private data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home.route, "home", Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem(Screen.Riders.route, "riders", Icons.Filled.People, Icons.Outlined.People),
    BottomNavItem(Screen.Settings.route, "settings", Icons.Filled.Settings, Icons.Outlined.Settings),
)

private val bottomNavRoutes = setOf(Screen.Home.route, Screen.Riders.route, Screen.Settings.route)

@Composable
fun BtwNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomNav = currentRoute in bottomNavRoutes

    Scaffold(
        containerColor = Ink,
        bottomBar = {
            if (showBottomNav) {
                NavigationBar(
                    containerColor = Depth.copy(alpha = 0.95f),
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Ink,
                                selectedTextColor = Sky,
                                indicatorColor = Sky,
                                unselectedIconColor = Sky.copy(alpha = 0.6f),
                                unselectedTextColor = Sky.copy(alpha = 0.6f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = androidx.compose.ui.Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
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
                AlertPrefsScreen(
                    onContinue = { navController.navigate(Screen.SetupComplete.route) },
                    onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) }
                )
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
                    onNavigateToRiders = {
                        navController.navigate(Screen.Riders.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToSettings = {
                        navController.navigate(Screen.Settings.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(Screen.Riders.route) {
                RidersScreen()
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToVehicles = { navController.navigate(Screen.Vehicles.route) },
                    onNavigateToLocations = { navController.navigate(Screen.Locations.route) },
                    onNavigateToHandoff = { riderId -> navController.navigate(Screen.Handoff.route(riderId)) },
                    onNavigateToPaywall = { navController.navigate(Screen.Paywall.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) }
                )
            }

            composable(Screen.Vehicles.route) {
                VehiclesScreen(onBack = { navController.popBackStack() })
            }

            composable(Screen.History.route) {
                HistoryScreen(onBack = { navController.popBackStack() })
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

            composable(Screen.Paywall.route) {
                PaywallScreen(
                    onBack = { navController.popBackStack() },
                    onPurchased = { navController.popBackStack() }
                )
            }
        }
    }
}
