package com.btw.app.ui.handoff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.domain.model.HandoffContact
import com.btw.app.domain.model.PickupWindow
import com.btw.app.domain.model.SavedLocation
import com.btw.app.ui.theme.*
import java.util.Calendar

/**
 * Screen for managing handoff contacts and pickup windows for a specific rider.
 * riderId is passed via nav args; -1 means no rider resolved (nav will not reach here).
 */
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
                .padding(top = 32.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Outlined.ArrowBack, contentDescription = "back", tint = Sky)
            }
            Text("handoff", style = MaterialTheme.typography.headlineSmall, color = Air)
            Spacer(Modifier.size(48.dp))
        }

        SectionHeader(title = "handoff contacts", onAdd = { showAddContactDialog = true })
        if (contacts.isEmpty()) {
            EmptyHint("no contacts — they'll receive a pickup verification sms")
        } else {
            contacts.forEach { contact ->
                ContactRow(contact = contact, onDelete = { viewModel.deleteContact(contact.id) })
                Spacer(Modifier.height(8.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        SectionHeader(title = "pickup windows", onAdd = { showAddWindowDialog = true })
        if (windows.isEmpty()) {
            EmptyHint("no windows — btw won't send handoff checks without one")
        } else {
            windows.forEach { window ->
                val locationLabel = locations.find { it.id == window.locationId }?.label ?: "unknown location"
                PickupWindowRow(
                    window = window,
                    locationLabel = locationLabel,
                    onToggle = { viewModel.toggleWindow(window) },
                    onDelete = { viewModel.deleteWindow(window.id) }
                )
                Spacer(Modifier.height(8.dp))
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

    if (showAddWindowDialog && locations.isNotEmpty()) {
        AddWindowDialog(
            locations = locations,
            onDismiss = { showAddWindowDialog = false },
            onAdd = { locationId, days, hour, minute, windowMin ->
                viewModel.addPickupWindow(riderId, locationId, days, hour, minute, windowMin)
                showAddWindowDialog = false
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.labelLarge, color = Sky)
        IconButton(onClick = onAdd, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Outlined.Add, contentDescription = "add", tint = Sky)
        }
    }
}

@Composable
private fun EmptyHint(text: String) {
    Text(text, style = MaterialTheme.typography.bodySmall, color = Depth, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
private fun ContactRow(contact: HandoffContact, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Depth.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(contact.name, style = MaterialTheme.typography.bodyMedium, color = Air)
            Text(contact.phone, style = MaterialTheme.typography.bodySmall, color = Sky)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "remove contact", tint = Sky)
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
            .background(Depth.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(locationLabel, style = MaterialTheme.typography.bodyMedium, color = Air)
            Text(daysLabel, style = MaterialTheme.typography.bodySmall, color = Sky)
            Text(timeLabel, style = MaterialTheme.typography.labelMedium, color = Depth)
        }
        Switch(
            checked = window.isActive,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedThumbColor = Air, checkedTrackColor = Depth)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = "delete window", tint = Sky)
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
        title = { Text("add handoff contact", color = Air, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                HandoffTextField(value = name, onValueChange = { name = it }, label = "name")
                HandoffTextField(value = phone, onValueChange = { phone = it }, label = "phone number")
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

    val allDays = listOf(Calendar.SUNDAY, Calendar.MONDAY, Calendar.TUESDAY,
        Calendar.WEDNESDAY, Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Depth,
        title = { Text("pickup window", color = Air, style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text("location", style = MaterialTheme.typography.labelLarge, color = Sky)
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

                Text("days", style = MaterialTheme.typography.labelLarge, color = Sky)
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
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (day in selectedDays) Ink else Sky
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Sky,
                                containerColor = Ink
                            )
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    NumberStepper(label = "hour", value = hour, range = 0..23, onValueChange = { hour = it })
                    Text(":", color = Air)
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
        Text(label, style = MaterialTheme.typography.labelMedium, color = Sky)
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextButton(onClick = { if (value - step >= range.first) onValueChange(value - step) }) {
                Text("−", color = Sky)
            }
            Text("%02d".format(value), style = MaterialTheme.typography.bodyMedium, color = Air)
            TextButton(onClick = { if (value + step <= range.last) onValueChange(value + step) }) {
                Text("+", color = Sky)
            }
        }
    }
}

@Composable
private fun HandoffTextField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Sky) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Air,
            unfocusedTextColor = Air,
            focusedBorderColor = Sky,
            unfocusedBorderColor = Air.copy(alpha = 0.4f)
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
