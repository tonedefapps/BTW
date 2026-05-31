package com.tonedefapps.btw.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.domain.model.ScheduleType
import com.tonedefapps.btw.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderScheduleScreen(
    onBack: () -> Unit,
    viewModel: RiderScheduleViewModel = hiltViewModel()
) {
    val rider by viewModel.rider.collectAsState()
    val existingSchedule by viewModel.schedule.collectAsState()

    // Mode: null = no selection yet (derive from existing), WEEKLY, ALTERNATING_WEEKS
    var selectedMode by remember(existingSchedule) {
        mutableStateOf(existingSchedule?.type ?: ScheduleType.WEEKLY)
    }

    // Weekly state — bitmask, Calendar DAY_OF_WEEK bits: bit 1=Sun … bit 7=Sat
    var weeklyBitmask by remember(existingSchedule) {
        mutableIntStateOf(
            if (existingSchedule?.type == ScheduleType.WEEKLY) existingSchedule!!.daysOfWeekBitmask else 0
        )
    }

    // Alternating state — referenceDate
    var referenceDate by remember(existingSchedule) {
        mutableLongStateOf(
            if (existingSchedule?.type == ScheduleType.ALTERNATING_WEEKS) existingSchedule!!.referenceDate
            else System.currentTimeMillis()
        )
    }

    var showAltDatePicker by remember { mutableStateOf(false) }
    val altDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = referenceDate
    )

    val dateFmt = remember { SimpleDateFormat("MMM d, yyyy", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        BtwTopBar(
            title = "schedule for ${rider?.name?.lowercase() ?: "rider"}",
            onBack = onBack
        )

        Text(
            "btw will only track ${rider?.name?.lowercase() ?: "this rider"} when they're scheduled to be with you",
            style = MaterialTheme.typography.bodyMedium,
            color = Sky,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Mode selector
        BtwSectionHeader("schedule type")
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            FilterChip(
                selected = selectedMode == ScheduleType.WEEKLY,
                onClick = { selectedMode = ScheduleType.WEEKLY },
                label = { Text("weekly") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Sky,
                    selectedLabelColor = Ink,
                    containerColor = Ink,
                    labelColor = Sky
                )
            )
            FilterChip(
                selected = selectedMode == ScheduleType.ALTERNATING_WEEKS,
                onClick = { selectedMode = ScheduleType.ALTERNATING_WEEKS },
                label = { Text("alternating weeks") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Sky,
                    selectedLabelColor = Ink,
                    containerColor = Ink,
                    labelColor = Sky
                )
            )
        }

        if (selectedMode == ScheduleType.WEEKLY) {
            WeeklyScheduleSection(
                bitmask = weeklyBitmask,
                onBitmaskChange = { weeklyBitmask = it }
            )
            Spacer(Modifier.height(32.dp))
            BtwPrimaryButton(
                text = "save schedule",
                onClick = { viewModel.saveWeekly(weeklyBitmask); onBack() },
                enabled = weeklyBitmask != 0
            )
        } else {
            AlternatingScheduleSection(
                referenceDate = referenceDate,
                dateFmt = dateFmt,
                onPickDate = { showAltDatePicker = true }
            )
            Spacer(Modifier.height(32.dp))
            BtwPrimaryButton(
                text = "save schedule",
                onClick = { viewModel.saveAlternating(referenceDate); onBack() }
            )
        }

        if (existingSchedule != null) {
            Spacer(Modifier.height(16.dp))
            BtwDestructiveButton(
                text = "clear schedule",
                onClick = { viewModel.clearSchedule(); onBack() }
            )
        }

        Spacer(Modifier.height(32.dp))
    }

    if (showAltDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showAltDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    altDatePickerState.selectedDateMillis?.let { referenceDate = it }
                    showAltDatePicker = false
                }) { Text("ok", color = Air) }
            },
            dismissButton = {
                TextButton(onClick = { showAltDatePicker = false }) { Text("cancel", color = Sky) }
            },
            colors = DatePickerDefaults.colors(containerColor = Depth)
        ) {
            DatePicker(
                state = altDatePickerState,
                title = {
                    Text(
                        "pick any day in your 'on' week",
                        style = MaterialTheme.typography.labelLarge,
                        color = Sky,
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp)
                    )
                },
                colors = DatePickerDefaults.colors(
                    containerColor = Depth,
                    titleContentColor = Sky,
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
}

@Composable
private fun WeeklyScheduleSection(bitmask: Int, onBitmaskChange: (Int) -> Unit) {
    // Calendar.DAY_OF_WEEK: 1=Sun, 2=Mon, ... 7=Sat
    val days = listOf("S", "M", "T", "W", "T", "F", "S")

    BtwSectionHeader("active days")
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        days.forEachIndexed { index, label ->
            val calDay = index + 1  // 1=Sun .. 7=Sat
            val selected = (bitmask shr calDay) and 1 == 1
            FilterChip(
                selected = selected,
                onClick = { onBitmaskChange(bitmask xor (1 shl calDay)) },
                label = { Text(label) },
                modifier = Modifier.weight(1f),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Sky,
                    selectedLabelColor = Ink,
                    containerColor = Ink,
                    labelColor = Sky.copy(alpha = 0.6f)
                )
            )
        }
    }
    if (bitmask == 0) {
        Text(
            "select at least one day",
            style = MaterialTheme.typography.bodySmall,
            color = Sky.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun AlternatingScheduleSection(
    referenceDate: Long,
    dateFmt: SimpleDateFormat,
    onPickDate: () -> Unit
) {
    BtwSectionHeader("on-week reference")
    Text(
        "pick any day in an active-custody week. btw will alternate weeks from there.",
        style = MaterialTheme.typography.bodySmall,
        color = Sky,
        modifier = Modifier.padding(bottom = 16.dp)
    )
    BtwCard {
        BtwCardRow(
            label = dateFmt.format(Date(referenceDate)).lowercase(),
            sublabel = "tap to change",
            onClick = onPickDate
        )
    }
}
