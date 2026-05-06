package com.tonedefapps.btw.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.model.RiderType
import com.tonedefapps.btw.ui.theme.*

@Composable
fun RidersScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val riders by viewModel.riders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp)
    ) {
        BtwTopBar(
            title = "riders",
            trailing = {
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = "add rider", tint = Sky)
                }
            }
        )

        if (riders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("no riders yet", style = MaterialTheme.typography.bodyMedium, color = Air)
                    Text("tap + to add someone to watch for", style = MaterialTheme.typography.bodySmall, color = Sky)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(riders) { rider ->
                    RiderCard(rider = rider, onDelete = { viewModel.deleteRider(rider.id) })
                }
            }
        }
    }

    if (showAddDialog) {
        AddRiderDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { name, type, emoji ->
                viewModel.addRider(name, type, emoji)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun RiderCard(rider: Rider, onDelete: () -> Unit) {
    BtwCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (rider.emoji.isNotBlank()) {
                    Text(rider.emoji, fontSize = 26.sp)
                }
                Column {
                    Text(
                        text = rider.name,
                        fontSize = 18.sp,
                        fontFamily = DmSans,
                        fontWeight = FontWeight.Normal,
                        color = Sand
                    )
                    Text(
                        text = rider.type.name.lowercase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Sky
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, contentDescription = "remove", tint = Sky.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
private fun AddRiderDialog(onDismiss: () -> Unit, onAdd: (String, RiderType, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(RiderType.CHILD) }
    var emoji by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Depth,
        title = { Text("add rider", color = Air, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                BtwTextField(value = name, onValueChange = { name = it }, label = "name")
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("type", style = MaterialTheme.typography.labelSmall, color = Sky)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RiderType.entries.forEach { t ->
                            FilterChip(
                                selected = type == t,
                                onClick = { type = t },
                                label = { Text(t.name.lowercase()) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Sky,
                                    selectedLabelColor = Ink,
                                    containerColor = Ink,
                                    labelColor = Sky
                                )
                            )
                        }
                    }
                }
                BtwTextField(
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    label = "emoji (optional)"
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onAdd(name.trim(), type, emoji) }) {
                Text("add", color = Air)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("cancel", color = Sky) }
        }
    )
}
