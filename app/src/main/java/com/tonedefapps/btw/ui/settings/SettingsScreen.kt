package com.tonedefapps.btw.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.ui.theme.*

@Composable
fun SettingsScreen(
    onNavigateToVehicles: () -> Unit = {},
    onNavigateToLocations: () -> Unit = {},
    onNavigateToHandoff: (Long) -> Unit = {},
    onNavigateToPaywall: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val prefs by viewModel.prefs.collectAsState()
    val riders by viewModel.riders.collectAsState()
    val isPremium = prefs.isPremium

    var editingStep by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        BtwTopBar(title = "settings")

        // ── Premium upsell (free users only) ──────────────────────────────
        if (!isPremium) {
            BtwCard {
                BtwCardRow(
                    label = "unlock btw premium",
                    sublabel = "handoff alerts · configurable timers · $0.99/mo",
                    onClick = onNavigateToPaywall,
                    trailing = { PremiumBadge() }
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        // ── Vehicles ───────────────────────────────────────────────────────
        BtwSectionHeader("vehicles")
        BtwCard {
            BtwCardRow(label = "manage paired vehicles", sublabel = "add or remove bluetooth pairings", onClick = onNavigateToVehicles)
        }

        Spacer(Modifier.height(24.dp))

        // ── Escalation ladder ──────────────────────────────────────────────
        BtwSectionHeader("alert escalation")
        BtwCard {
            BtwCardRow(
                label = "1 · gentle notification",
                sublabel = formatSeconds(prefs.step1DelaySeconds) + " after leaving vehicle",
                onClick = { editingStep = 1 }
            )
            BtwRowDivider()
            BtwCardRow(
                label = "2 · persistent alert",
                sublabel = formatSeconds(prefs.step2DelaySeconds) + " unacknowledged",
                onClick = { editingStep = 2 }
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Hot day mode ───────────────────────────────────────────────────
        BtwSectionHeader("hot day mode")
        BtwCard {
            BtwCardValueRow(
                label = "auto-detect via battery temperature",
                value = "on"
            )
            BtwRowDivider()
            BtwCardToggleRow(
                label = "always on",
                sublabel = "force faster timers regardless of temperature",
                checked = prefs.hotDayModeEnabled,
                onCheckedChange = { viewModel.toggleHotDayMode(it) }
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── Locations ──────────────────────────────────────────────────────
        BtwSectionHeader("locations")
        BtwCard {
            BtwCardRow(label = "manage saved locations", onClick = onNavigateToLocations)
        }

        Spacer(Modifier.height(24.dp))

        // ── Handoff ────────────────────────────────────────────────────────
        BtwSectionHeader("handoff contacts")
        if (riders.isEmpty()) {
            BtwCard {
                BtwCardRow(label = "add a rider first to configure handoff")
            }
        } else {
            BtwCard {
                riders.forEachIndexed { index, rider ->
                    if (index > 0) BtwRowDivider()
                    BtwCardRow(
                        label = "${rider.emoji.ifBlank { "" }} ${rider.name}".trim(),
                        sublabel = "contacts & pickup windows",
                        onClick = { onNavigateToHandoff(rider.id) }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Alert history ──────────────────────────────────────────────────
        BtwSectionHeader("history")
        BtwCard {
            BtwCardRow(label = "alert history", sublabel = "view past alerts and outcomes", onClick = onNavigateToHistory)
        }

        Spacer(Modifier.height(32.dp))
        Text(
            text = "no cloud · no data · no agenda · just btw",
            style = MaterialTheme.typography.bodySmall,
            color = Sky.copy(alpha = 0.5f)
        )
        Spacer(Modifier.height(32.dp))
    }

    editingStep?.let { step ->
        val current = when (step) {
            1 -> prefs.step1DelaySeconds
            else -> prefs.step2DelaySeconds
        }
        val label = when (step) {
            1 -> "gentle notification"
            else -> "persistent alert"
        }
        DelayPickerDialog(
            stepLabel = label,
            currentSeconds = current,
            onDismiss = { editingStep = null },
            onConfirm = { seconds ->
                viewModel.updateStepDelay(step, seconds)
                editingStep = null
            }
        )
    }
}

@Composable
private fun DelayPickerDialog(
    stepLabel: String,
    currentSeconds: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    val options = listOf(15, 30, 45, 60, 120, 180, 300, 420, 600, 900)
    var selected by remember { mutableIntStateOf(
        options.minByOrNull { kotlin.math.abs(it - currentSeconds) } ?: currentSeconds
    ) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Depth,
        title = { Text(stepLabel, color = Air, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("delay after previous step", style = MaterialTheme.typography.bodySmall, color = Sky)
                Spacer(Modifier.height(8.dp))
                options.forEach { seconds ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selected == seconds,
                            onClick = { selected = seconds },
                            colors = RadioButtonDefaults.colors(selectedColor = Sky)
                        )
                        Text(
                            text = formatSeconds(seconds),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selected == seconds) Air else Sky
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(selected) }) { Text("save", color = Air) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("cancel", color = Sky) }
        }
    )
}

private fun formatSeconds(s: Int): String = when {
    s < 60 -> "${s}s"
    s % 60 == 0 -> "${s / 60}m"
    else -> "${s / 60}m ${s % 60}s"
}
