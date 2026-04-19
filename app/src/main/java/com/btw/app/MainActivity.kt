package com.btw.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.btw.app.domain.repository.PreferencesRepository
import com.btw.app.ui.navigation.BtwNavGraph
import com.btw.app.ui.navigation.Screen
import com.btw.app.ui.theme.BtwTheme
import com.btw.app.ui.theme.Ink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val onboardingComplete = runBlocking {
            preferencesRepository.getAlertPreferences().first().onboardingComplete
        }
        val startDestination = if (onboardingComplete) Screen.Home.route else Screen.Onboarding.route

        setContent {
            BtwTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Ink
                ) {
                    val navController = rememberNavController()
                    BtwNavGraph(
                        navController = navController,
                        startDestination = startDestination
                    )
                }
            }
        }
    }
}
