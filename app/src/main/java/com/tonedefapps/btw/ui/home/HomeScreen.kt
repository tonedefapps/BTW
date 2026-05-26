package com.tonedefapps.btw.ui.home

import android.Manifest
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.tonedefapps.btw.domain.model.AlertOutcome
import com.tonedefapps.btw.domain.model.Rider
import com.tonedefapps.btw.domain.model.TripState
import com.tonedefapps.btw.ui.onboarding.Wordmark
import com.tonedefapps.btw.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    onNavigateToRiders: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val requiredPermissions = remember {
        buildList {
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) add(Manifest.permission.POST_NOTIFICATIONS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
    }

    val permissionsState = rememberMultiplePermissionsState(requiredPermissions)
    val hasLocation = permissionsState.permissions.any {
        it.permission == Manifest.permission.ACCESS_FINE_LOCATION && it.status.isGranted
    }
    val hasBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissionsState.permissions.any {
            it.permission == Manifest.permission.BLUETOOTH_CONNECT && it.status.isGranted
        }
    } else true

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) permissionsState.launchMultiplePermissionRequest()
    }
    LaunchedEffect(hasLocation, hasBluetooth) {
        if (hasLocation && hasBluetooth) viewModel.startMonitor()
    }

    val bgColor = if (uiState.tripState == TripState.ALERT_TRIGGERED) AlertRed.copy(alpha = 0.08f) else Ink

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Wordmark()
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                when (uiState.tripState) {
                    TripState.IDLE -> IdleState(uiState, onNavigateToRiders, onNavigateToSettings)
                    TripState.IN_VEHICLE -> WatchingState(
                        uiState = uiState,
                        onNavigateToRiders = onNavigateToRiders,
                        onUnpauseRider = { viewModel.unpauseRider(it) }
                    )
                    TripState.ALERT_TRIGGERED -> AlertState(
                        uiState = uiState,
                        onSafe = { viewModel.acknowledgeSafe(uiState.activeAlertId) },
                        onGoingBack = { viewModel.acknowledgeGoingBack(uiState.activeAlertId) }
                    )
                    TripState.PARKED_SAFE -> SafeState(uiState)
                }
            }
        }
    }
}

@Composable
private fun IdleState(
    uiState: HomeUiState,
    onNavigateToRiders: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val hasVehicle = uiState.connectedVehicle != null
    val needsParkingSpot = uiState.hasLocationOnlyVehicle && !uiState.hasSavedLocations
    val setupComplete = uiState.riders.isNotEmpty() && hasVehicle && !needsParkingSpot
    val timeFmt = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    val statusLabel = when {
        uiState.passiveWatchActive -> "watching"
        setupComplete -> "ready"
        else -> "setup needed"
    }
    val readySubtext = when {
        uiState.hasLocationOnlyVehicle ->
            "monitoring starts automatically when you leave a saved parking spot"
        else ->
            "activates automatically when your car's bluetooth connects"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        BtwStatusPill(statusLabel, if (uiState.passiveWatchActive) SafeGreen else Sky)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when {
                    uiState.passiveWatchActive -> "watching for departure"
                    setupComplete -> "ready to watch"
                    else -> "finish setup to start"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = Air,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (setupComplete) readySubtext else "complete the steps below to enable monitoring",
                style = MaterialTheme.typography.bodyMedium,
                color = Sky,
                textAlign = TextAlign.Center
            )
        }

        if (uiState.riders.isEmpty() || !hasVehicle || needsParkingSpot) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.riders.isEmpty()) {
                    BtwCard {
                        BtwCardRow(
                            label = "add a rider",
                            sublabel = "who are you watching for?",
                            onClick = onNavigateToRiders
                        )
                    }
                }
                if (!hasVehicle) {
                    BtwCard {
                        BtwCardRow(
                            label = "add a vehicle",
                            sublabel = "pair a bluetooth device or add a location-only vehicle",
                            onClick = onNavigateToSettings
                        )
                    }
                }
                if (needsParkingSpot) {
                    BtwCard {
                        BtwCardRow(
                            label = "add a parking spot",
                            sublabel = "btw watches for when you leave — add where you usually park",
                            onClick = onNavigateToSettings
                        )
                    }
                }
            }
        }

        if (uiState.recentAlerts.isNotEmpty()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                BtwSectionHeader("today")
                BtwCard {
                    uiState.recentAlerts.forEachIndexed { index, alert ->
                        if (index > 0) BtwRowDivider()
                        val (outcomeLabel, outcomeColor) = when (alert.outcome) {
                            AlertOutcome.SAFE -> "safe" to SafeGreen
                            AlertOutcome.WENT_BACK -> "went back" to WarnAmber
                            AlertOutcome.ESCALATED_SMS -> "sms sent" to AlertRed
                            else -> "resolved" to Sky
                        }
                        BtwCardRow(
                            label = alert.riderName,
                            sublabel = "${alert.vehicleName} · ${timeFmt.format(Date(alert.triggeredAt))}",
                            trailing = { BtwStatusPill(outcomeLabel, outcomeColor) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WatchingState(
    uiState: HomeUiState,
    onNavigateToRiders: () -> Unit,
    onUnpauseRider: (Long) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(1400, easing = EaseInOut), RepeatMode.Reverse),
        label = "pulse_alpha"
    )

    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(60_000)
            now = System.currentTimeMillis()
        }
    }

    val pauseDateFmt = remember { SimpleDateFormat("MMM d", Locale.getDefault()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        BtwStatusPill("monitoring", SafeGreen)

        Box(contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(140.dp).scale(pulseScale).alpha(pulseAlpha).background(SafeGreen, CircleShape))
            Box(modifier = Modifier.size(88.dp).background(SafeGreen.copy(alpha = 0.3f), CircleShape))
            Box(modifier = Modifier.size(56.dp).background(SafeGreen, CircleShape))
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            uiState.activeRiders.forEach { rider ->
                ActiveRiderCard(rider = rider, uiState = uiState, now = now)
            }

            if (uiState.pausedRiders.isNotEmpty()) {
                uiState.pausedRiders.forEach { rider ->
                    PausedRiderCard(
                        rider = rider,
                        pauseDateFmt = pauseDateFmt,
                        onResume = { onUnpauseRider(rider.id) },
                        onManage = onNavigateToRiders
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveRiderCard(rider: Rider, uiState: HomeUiState, now: Long) {
    BtwCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "${rider.emoji.ifBlank { "" }} ${rider.name}".trim(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Air
                )
                uiState.connectedVehicle?.let {
                    Text(text = "via ${it.name}", style = MaterialTheme.typography.bodySmall, color = Sky)
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                BtwStatusPill("in vehicle", SafeGreen)
                uiState.tripStartedAt?.let { startedAt ->
                    val elapsedMin = ((now - startedAt) / 60_000).toInt()
                    Text(
                        text = if (elapsedMin < 1) "just started" else "${elapsedMin}m",
                        style = MaterialTheme.typography.bodySmall,
                        color = Sky.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PausedRiderCard(
    rider: Rider,
    pauseDateFmt: SimpleDateFormat,
    onResume: () -> Unit,
    onManage: () -> Unit
) {
    BtwCard(
        modifier = Modifier.clickable { onManage() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "${rider.emoji.ifBlank { "" }} ${rider.name}".trim(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Air.copy(alpha = 0.6f)
                )
                val untilLabel = rider.pausedUntil?.let { "paused · until ${pauseDateFmt.format(Date(it)).lowercase()}" }
                    ?: "paused"
                Text(text = untilLabel, style = MaterialTheme.typography.bodySmall, color = Sky.copy(alpha = 0.6f))
            }
            TextButton(onClick = onResume) {
                Text("resume", color = Sky, fontSize = 13.sp, fontFamily = DmSans)
            }
        }
    }
}

@Composable
private fun AlertState(uiState: HomeUiState, onSafe: () -> Unit, onGoingBack: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        BtwStatusPill("check on them now", AlertRed)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = uiState.activeRiderName,
                fontFamily = DmSans,
                fontWeight = FontWeight.Bold,
                fontSize = 38.sp,
                color = Air,
                textAlign = TextAlign.Center
            )
            Text(
                text = "may still be in the vehicle.",
                style = MaterialTheme.typography.headlineSmall,
                color = Air.copy(alpha = 0.85f),
                textAlign = TextAlign.Center
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onSafe,
                colors = ButtonDefaults.buttonColors(containerColor = SafeGreen, contentColor = Air),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text("they're safe", fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
            Button(
                onClick = onGoingBack,
                colors = ButtonDefaults.buttonColors(containerColor = WarnAmber, contentColor = Ink),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text("going back now", fontFamily = DmSans, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
        }
    }
}

@Composable
private fun SafeState(uiState: HomeUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        BtwStatusPill("all clear", SafeGreen)
        Text(
            text = "${uiState.activeRiderName} is safe.",
            style = MaterialTheme.typography.headlineMedium,
            color = Air,
            textAlign = TextAlign.Center
        )
    }
}
