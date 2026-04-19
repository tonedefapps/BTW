package com.btw.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val prefs by viewModel.prefs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "back", tint = Sky)
            }
            Text("settings", style = MaterialTheme.typography.headlineSmall, color = Air)
            Spacer(modifier = Modifier.size(48.dp))
        }

        SectionLabel("hot day mode")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("compress timers when > 85°f", style = MaterialTheme.typography.bodyMedium, color = Air)
                Text("all escalation steps fire at 50% speed", style = MaterialTheme.typography.bodySmall, color = Sky)
            }
            Switch(
                checked = prefs.hotDayModeEnabled,
                onCheckedChange = { viewModel.toggleHotDayMode(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Air,
                    checkedTrackColor = Depth
                )
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        SectionLabel("escalation ladder")
        EscalationDetail(step = 1, label = "gentle notification", seconds = prefs.step1DelaySeconds)
        EscalationDetail(step = 2, label = "persistent alert", seconds = prefs.step2DelaySeconds)
        EscalationDetail(step = 3, label = "sms emergency contact", seconds = prefs.step3DelaySeconds)
        EscalationDetail(step = 4, label = "one-tap 911", seconds = prefs.step4DelaySeconds)

        Spacer(modifier = Modifier.height(24.dp))
        SectionLabel("emergency contact")
        var emergencyName by remember(prefs) { mutableStateOf(prefs.emergencyContactName) }
        var emergencyPhone by remember(prefs) { mutableStateOf(prefs.emergencyContactPhone) }

        OutlinedTextField(
            value = emergencyName,
            onValueChange = { emergencyName = it },
            label = { Text("name", color = Sky) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Air,
                unfocusedTextColor = Air,
                focusedBorderColor = Sky,
                unfocusedBorderColor = Depth
            )
        )
        OutlinedTextField(
            value = emergencyPhone,
            onValueChange = { emergencyPhone = it },
            label = { Text("phone", color = Sky) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Air,
                unfocusedTextColor = Air,
                focusedBorderColor = Sky,
                unfocusedBorderColor = Depth
            )
        )
        TextButton(
            onClick = {
                viewModel.saveEmergencyContact(emergencyName.trim(), emergencyPhone.trim())
            }
        ) {
            Text("save contact", color = Sky, style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "no cloud · no data · no agenda · just btw",
            style = MaterialTheme.typography.bodySmall,
            color = Depth
        )
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = Sky,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun EscalationDetail(step: Int, label: String, seconds: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("$step. $label", style = MaterialTheme.typography.bodyMedium, color = Air)
        Text(
            formatSeconds(seconds),
            style = MaterialTheme.typography.bodySmall,
            color = Sky
        )
    }
}

private fun formatSeconds(s: Int): String {
    return if (s < 60) "${s}s" else "${s / 60}m"
}
