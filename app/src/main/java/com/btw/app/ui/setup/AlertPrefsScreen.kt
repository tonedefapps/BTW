package com.btw.app.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.ui.onboarding.BtwButton
import com.btw.app.ui.onboarding.Wordmark
import com.btw.app.ui.theme.*

@Composable
fun AlertPrefsScreen(
    onContinue: () -> Unit,
    viewModel: AlertPrefsViewModel = hiltViewModel()
) {
    val prefs by viewModel.prefs.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .padding(horizontal = 32.dp, vertical = 56.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Wordmark()

            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "how btw checks in",
                    style = MaterialTheme.typography.displaySmall,
                    color = Air
                )

                EscalationRow(label = "gentle reminder", value = "30 seconds after leaving")
                EscalationRow(label = "persistent nudge", value = "2 minutes unacknowledged")
                EscalationRow(label = "sms your contact", value = "5 minutes unacknowledged")
                EscalationRow(label = "one-tap 911", value = "10 minutes unacknowledged")

                Divider(color = Depth)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("hot day mode", style = MaterialTheme.typography.bodyMedium, color = Air)
                        Text("compresses all timers 50% when > 85°f", style = MaterialTheme.typography.bodySmall, color = Sky)
                    }
                    Switch(
                        checked = prefs.hotDayModeEnabled,
                        onCheckedChange = { viewModel.toggleHotDayMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Air,
                            checkedTrackColor = Depth,
                            uncheckedThumbColor = Sky,
                            uncheckedTrackColor = Ink
                        )
                    )
                }
            }

            BtwButton(text = "looks good", onClick = onContinue)
        }
    }
}

@Composable
private fun EscalationRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = Air)
        Text(value, style = MaterialTheme.typography.bodySmall, color = Sky)
    }
}
