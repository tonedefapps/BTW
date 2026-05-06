package com.tonedefapps.btw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.tonedefapps.btw.domain.repository.PreferencesRepository
import com.tonedefapps.btw.ui.navigation.BtwNavGraph
import com.tonedefapps.btw.ui.navigation.Screen
import com.tonedefapps.btw.ui.theme.BtwTheme
import com.tonedefapps.btw.ui.theme.Ink
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BtwTheme {
                val navController = rememberNavController()
                val startDestination by produceState<String?>(initialValue = null) {
                    value = if (preferencesRepository.getAlertPreferences().first().onboardingComplete)
                        Screen.Home.route
                    else
                        Screen.Onboarding.route
                }
                Surface(modifier = Modifier.fillMaxSize(), color = Ink) {
                    startDestination?.let { dest ->
                        BtwNavGraph(navController = navController, startDestination = dest)
                    }
                }
            }
        }
    }
}
