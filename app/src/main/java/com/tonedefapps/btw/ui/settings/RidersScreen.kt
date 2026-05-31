package com.tonedefapps.btw.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PauseCircle
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.model.RiderSchedule
import com.tonedefapps.btw.domain.model.RiderType
import com.tonedefapps.btw.domain.model.isManuallyPaused
import com.tonedefapps.btw.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val DAY_MS = 24L * 60 * 60 * 1000

private fun endOfDay(offsetDays: Int): Long {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_YEAR, offsetDays)
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    return cal.timeInMillis
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RidersScreen(
    onNavigateToPaywall: () -> Unit = {},
    onNavigateToSchedule: (Long) -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val riders by viewModel.riders.collectAsState()
    val schedules by viewModel.schedules.collectAsState()
    val isPremium by viewModel.isPremium.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var pausingRider by remember { mutableStateOf<Rider?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis() + 7 * DAY_MS,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val now = System.currentTimeMillis()
                return utcTimeMillis > now && utcTimeMillis <= now + 14 * DAY_MS
            }
        }
    )

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
                    val hasSchedule = schedules.any { it.riderId == rider.id }
                    RiderCard(
                        rider = rider,
                        hasSchedule = hasSchedule,
                        isPremium = isPremium,
                        onDelete = { viewModel.deleteRider(rider.id) },
                        onPause = {
                            if (isPremium) pausingRider = rider else onNavigateToPaywall()
                        },
                        onResume = { viewModel.unpauseRider(rider.id) },
                        onSchedule = {
                            if (isPremium) onNavigateToSchedule(rider.id) else onNavigateToPaywall()
                        }
                    )
                }
            }
        }
    }

    // Pause duration bottom sheet
    pausingRider?.let { rider ->
        ModalBottomSheet(
            onDismissRequest = { pausingRider = null },
            containerColor = Depth
        ) {
            PauseDurationSheetContent(
                riderName = rider.name,
                onDismiss = { pausingRider = null },
                onPause = { until ->
                    viewModel.pauseRider(rider.id, until)
                    pausingRider = null
                },
                onCustomDate = { showDatePicker = true }
            )
        }
    }

    // Custom date picker (14-day cap enforced by selectableDates above)
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false; pausingRider = null },
            confirmButton = {
                TextButton(onClick = {
                    val selected = datePickerState.selectedDateMillis
                    if (selected != null && pausingRider != null) {
                        // Use end-of-selected-day
                        val cal = Calendar.getInstance().apply { timeInMillis = selected }
                        cal.set(Calendar.HOUR_OF_DAY, 23)
                        cal.set(Calendar.MINUTE, 59)
                        cal.set(Calendar.SECOND, 59)
                        viewModel.pauseRider(pausingRider!!.id, cal.timeInMillis)
                    }
                    showDatePicker = false
                    pausingRider = null
                }) { Text("set", color = Air) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("cancel", color = Sky) }
            },
            colors = DatePickerDefaults.colors(containerColor = Depth)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Depth,
                    titleContentColor = Air,
                    headlineContentColor = Air,
                    weekdayContentColor = Sky,
                    dayContentColor = Air,
                    selectedDayContainerColor = Sky,
                    selectedDayContentColor = Ink,
                    todayDateBorderColor = Sky,
                    todayContentColor = Sky
                )
            )
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
private fun PauseDurationSheetContent(
    riderName: String,
    onDismiss: () -> Unit,
    onPause: (Long) -> Unit,
    onCustomDate: () -> Unit
) {
    val now = System.currentTimeMillis()
    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            "pause ${riderName.lowercase()}",
            style = MaterialTheme.typography.titleMedium,
            color = Air,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "btw won't alert you for ${riderName.lowercase()} until the pause ends",
            style = MaterialTheme.typography.bodySmall,
            color = Sky,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        listOf(
            "rest of today" to endOfDay(0),
            "tomorrow" to endOfDay(1),
            "3 days" to (now + 3 * DAY_MS),
            "1 week" to (now + 7 * DAY_MS),
            "2 weeks" to (now + 14 * DAY_MS)
        ).forEach { (label, until) ->
            TextButton(
                onClick = { onPause(until); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Text(label, color = Air, style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth())
            }
        }
        TextButton(
            onClick = onCustomDate,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            Text("custom date...", color = Sky, style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth())
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun RiderCard(
    rider: Rider,
    hasSchedule: Boolean,
    @Suppress("UNUSED_PARAMETER") isPremium: Boolean,
    onDelete: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSchedule: () -> Unit
) {
    val now = System.currentTimeMillis()
    val paused = rider.isManuallyPaused(now)
    val pauseDateFmt = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    BtwCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
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
                    if (paused && rider.pausedUntil != null) {
                        Text(
                            text = "paused · until ${pauseDateFmt.format(Date(rider.pausedUntil)).lowercase()}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Sky.copy(alpha = 0.7f)
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = rider.type.name.lowercase(),
                                style = MaterialTheme.typography.labelMedium,
                                color = Sky
                            )
                            if (hasSchedule) {
                                BtwStatusPill("scheduled", Sky)
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (paused) {
                    IconButton(onClick = onResume) {
                        Icon(Icons.Outlined.PlayCircle, contentDescription = "resume", tint = SafeGreen)
                    }
                } else {
                    IconButton(onClick = onSchedule) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = "schedule",
                            tint = if (hasSchedule) Sky else Sky.copy(alpha = 0.45f)
                        )
                    }
                    IconButton(onClick = onPause) {
                        Icon(Icons.Outlined.PauseCircle, contentDescription = "pause", tint = Sky.copy(alpha = 0.7f))
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "remove", tint = Sky.copy(alpha = 0.45f))
                }
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
