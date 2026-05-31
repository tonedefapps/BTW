package com.tonedefapps.btw.ui.handoff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.domain.model.HandoffContact
import com.tonedefapps.btw.domain.model.PickupWindow
import com.tonedefapps.btw.domain.model.SavedLocation
import com.tonedefapps.btw.ui.theme.*
import java.util.Calendar

@Composable
fun HandoffContactsScreen(
    riderId: Long,
    onBack: () -> Unit,
    viewModel: HandoffViewModel = hiltViewModel()
) {
    LaunchedEffect(riderId) {
        if (riderId >= 0) viewModel.selectRider(riderId)
    }

    val contacts by viewModel.contacts.collectAsState()
    val windows by viewModel.pickupWindows.collectAsState()
    val locations by viewModel.locations.collectAsState()

    var showAddContactDialog by remember { mutableStateOf(false) }
    var showAddWindowDialog by remember { mutableStateOf(false) }
    var showNoLocationsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        BtwTopBar(title = "handoff", onBack = onBack)

        // ── Contacts ───────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BtwSectionHeader("handoff contacts")
            IconButton(onClick = { showAddContactDialog = true }) {
                Icon(Icons.Outlined.Add, contentDescription = "add contact", tint = Sky)
            }
        }

        if (contacts.isEmpty()) {
            BtwCard {
                BtwCardRow(label = "no contacts yet", sublabel = "add people who share this rider's pickups")
            }
        } else {
            BtwCard {
                contacts.forEachIndexed { index, contact ->
                    if (index > 0) BtwRowDivider()
                    ContactRow(contact = contact, onDelete = { viewModel.deleteContact(contact.id) })
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Pickup windows ─────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BtwSectionHeader("pickup windows")
            IconButton(onClick = {
                if (locations.isEmpty()) showNoLocationsDialog = true else showAddWindowDialog = true
            }) {
                Icon(Icons.Outlined.Add, contentDescription = "add window", tint = Sky)
            }
        }

        if (windows.isEmpty()) {
            BtwCard {
                BtwCardRow(label = "no windows yet", sublabel = "btw won't send handoff checks without one")
            }
        } else {
            BtwCard {
                windows.forEachIndexed { index, window ->
                    if (index > 0) BtwRowDivider()
                    val locationLabel = locations.find { it.id == window.locationId }?.label ?: "unknown location"
                    PickupWindowRow(
                        window = window,
                        locationLabel = locationLabel,
                        onToggle = { viewModel.toggleWindow(window) },
                        onDelete = { viewModel.deleteWindow(window.id) }
                    )
                }
            }
        }

        Spacer(Modifier.height(32.dp))
    }

    if (showAddContactDialog) {
        AddContactDialog(
            onDismiss = { showAddContactDialog = false },
            onAdd = { name, phone ->
                viewModel.addContact(riderId, name, phone)
                showAddContactDialog = false
            }
        )
    }

    if (showAddWindowDialog) {
        AddWindowDialog(
            locations = locations,
            onDismiss = { showAddWindowDialog = false },
            onAdd = { locationId, days, hour, minute, windowMin ->
                viewModel.addPickupWindow(riderId, locationId, days, hour, minute, windowMin)
                showAddWindowDialog = false
            }
        )
    }

    if (showNoLocationsDialog) {
        AlertDialog(
            onDismissRequest = { showNoLocationsDialog = false },
            containerColor = Depth,
            title = { Text("add a known location first", color = Air, style = MaterialTheme.typography.titleLarge) },
            text = {
                Text(
                    "pickup windows need a location to watch. add one in settings → known locations, then come back here.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Sky
                )
            },
            confirmButton = {
                TextButton(onClick = { showNoLocationsDialog = false }) { Text("ok", color = Air) }
            }
        )
    }
}

@Composable
private fun ContactRow(contact: HandoffContact, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(contact.name, style = MaterialTheme.typography.bodyMedium, color = Air)
            Text(contact.phone, style = MaterialTheme.typography.bodySmall, color = Sky)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "remove", tint = Sky.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun PickupWindowRow(
    window: PickupWindow,
    locationLabel: String,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val daysLabel = window.daysOfWeek.sortedBy { it }
        .joinToString(" ") { HandoffViewModel.DAY_LABELS[it] ?: "" }
    val timeLabel = "%02d:%02d  ±%dm".format(window.startHour, window.startMinute, window.windowMinutes)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(locationLabel, style = MaterialTheme.typography.bodyMedium, color = Air)
            Text(daysLabel, style = MaterialTheme.typography.bodySmall, color = Sky)
            Text(timeLabel, style = MaterialTheme.typography.labelSmall, color = Sky.copy(alpha = 0.5f))
        }
        Switch(
            checked = window.isActive,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Ink,
                checkedTrackColor = Sky,
                uncheckedThumbColor = Sky.copy(alpha = 0.6f),
                uncheckedTrackColor = Depth
            )
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "delete", tint = Sky.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun AddContactDialog(onDismiss: () -> Unit, onAdd: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Depth,
        title = { Text("add handoff contact", color = Air, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BtwTextField(value = name, onValueChange = { name = it }, label = "name")
                BtwTextField(value = phone, onValueChange = { phone = it }, label = "phone number")
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank() && phone.isNotBlank()) onAdd(name.trim(), phone.trim())
            }) { Text("add", color = Air) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("cancel", color = Sky) } }
    )
}

@Composable
private fun AddWindowDialog(
    locations: List<SavedLocation>,
    onDismiss: () -> Unit,
    onAdd: (Long, Set<Int>, Int, Int, Int) -> Unit
) {
    var selectedLocationId by remember { mutableStateOf(locations.first().id) }
    var selectedDays by remember { mutableStateOf(setOf(Calendar.MONDAY)) }
    var hour by remember { mutableIntStateOf(15) }
    var minute by remember { mutableIntStateOf(0) }
    var windowMin by remember { mutableIntStateOf(30) }

    val allDays = listOf(
        Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
        Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Depth,
        title = { Text("pickup window", color = Air, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("location", style = MaterialTheme.typography.labelSmall, color = Sky, letterSpacing = 1.sp)
                    locations.forEach { loc ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = loc.id == selectedLocationId,
                                onClick = { selectedLocationId = loc.id },
                                colors = RadioButtonDefaults.colors(selectedColor = Sky)
                            )
                            Text("${loc.emoji} ${loc.label}".trim(), style = MaterialTheme.typography.bodyMedium, color = Air)
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("days", style = MaterialTheme.typography.labelSmall, color = Sky, letterSpacing = 1.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        allDays.forEach { day ->
                            FilterChip(
                                selected = day in selectedDays,
                                onClick = {
                                    selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                                },
                                label = {
                                    Text(
                                        HandoffViewModel.DAY_LABELS[day] ?: "",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    NumberStepper(label = "hour", value = hour, range = 0..23, onValueChange = { hour = it })
                    Text(":", color = Air, style = MaterialTheme.typography.bodyLarge)
                    NumberStepper(label = "min", value = minute, range = 0..59, step = 5, onValueChange = { minute = it })
                }
                NumberStepper(label = "window (min)", value = windowMin, range = 5..120, step = 5, onValueChange = { windowMin = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedDays.isNotEmpty()) onAdd(selectedLocationId, selectedDays, hour, minute, windowMin)
            }) { Text("add", color = Air) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("cancel", color = Sky) } }
    )
}

@Composable
private fun NumberStepper(label: String, value: Int, range: IntRange, step: Int = 1, onValueChange: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Sky)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { if (value - step >= range.first) onValueChange(value - step) }) {
                Text("−", color = Sky, style = MaterialTheme.typography.bodyLarge)
            }
            Text("%02d".format(value), style = MaterialTheme.typography.bodyMedium, color = Air)
            TextButton(onClick = { if (value + step <= range.last) onValueChange(value + step) }) {
                Text("+", color = Sky, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
