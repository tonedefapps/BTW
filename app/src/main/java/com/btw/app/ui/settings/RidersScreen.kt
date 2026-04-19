package com.btw.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.domain.model.Rider
import com.btw.app.domain.model.RiderType
import com.btw.app.ui.theme.DmSans
import com.btw.app.ui.theme.*

@Composable
fun RidersScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val riders by viewModel.riders.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
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
            Text("riders", style = MaterialTheme.typography.headlineSmall, color = Air)
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "add rider", tint = Sky)
            }
        }

        if (riders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("no riders yet", style = MaterialTheme.typography.bodyMedium, color = Sky)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(riders) { rider ->
                    RiderRow(rider = rider, onDelete = { viewModel.deleteRider(rider.id) })
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
private fun RiderRow(rider: Rider, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Depth.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (rider.emoji.isNotBlank()) {
                Text(rider.emoji, fontSize = 24.sp)
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
            Icon(Icons.Outlined.Delete, contentDescription = "remove rider", tint = Sky)
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
        title = { Text("add rider", color = Air, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("name", color = Sky) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Sand,
                        unfocusedTextColor = Sand,
                        focusedBorderColor = Sky,
                        unfocusedBorderColor = Air
                    )
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RiderType.entries.forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(t.name.lowercase(), color = if (type == t) Ink else Sky) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Sky,
                                containerColor = Ink
                            )
                        )
                    }
                }
                OutlinedTextField(
                    value = emoji,
                    onValueChange = { if (it.length <= 2) emoji = it },
                    label = { Text("emoji (optional)", color = Sky) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Air,
                        unfocusedTextColor = Air,
                        focusedBorderColor = Sky,
                        unfocusedBorderColor = Air
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onAdd(name.trim(), type, emoji) }) {
                Text("add", color = Air)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("cancel", color = Sky)
            }
        }
    )
}
