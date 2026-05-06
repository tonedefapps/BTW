package com.tonedefapps.btw.ui.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.ui.onboarding.BtwButton
import com.tonedefapps.btw.ui.onboarding.Wordmark
import com.tonedefapps.btw.ui.theme.*

@Composable
fun AlertPrefsScreen(
    onContinue: () -> Unit,
    onNavigateToPaywall: () -> Unit = {},
    viewModel: AlertPrefsViewModel = hiltViewModel()
) {
    val prefs by viewModel.prefs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))
        Wordmark()
        Spacer(Modifier.height(40.dp))

        Text("how btw checks in", style = MaterialTheme.typography.displaySmall, color = Air)
        Spacer(Modifier.height(24.dp))

        BtwSectionHeader("always free")
        BtwCard {
            BtwCardValueRow(label = "1 · gentle notification", value = "30s after leaving")
            BtwRowDivider()
            BtwCardValueRow(label = "2 · persistent alarm", value = "2 min unacknowledged")
            BtwRowDivider()
            BtwCardToggleRow(
                label = "hot day mode",
                sublabel = "compresses all timers 50% above 80°f",
                checked = prefs.hotDayModeEnabled,
                onCheckedChange = { viewModel.toggleHotDayMode(it) }
            )
        }

        Spacer(Modifier.height(20.dp))

        BtwSectionHeader("btw premium — $0.99/mo")
        BtwCard {
            BtwCardRow(
                label = "handoff alerts",
                sublabel = "coordinate backup pickups",
                onClick = onNavigateToPaywall,
                trailing = { PremiumBadge() }
            )
            BtwRowDivider()
            BtwCardRow(
                label = "configurable timers",
                sublabel = "adjust step 1 and step 2 delays",
                onClick = onNavigateToPaywall,
                trailing = { PremiumBadge() }
            )
        }

        Spacer(Modifier.height(40.dp))
        BtwButton(text = "looks good", onClick = onContinue)
        Spacer(Modifier.height(32.dp))
    }
}
