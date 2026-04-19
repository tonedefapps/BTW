package com.btw.app.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.domain.model.TripState
import com.btw.app.ui.onboarding.BtwButton
import com.btw.app.ui.onboarding.Wordmark
import com.btw.app.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToRiders: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.startMonitor()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Wordmark()
            Row {
                IconButton(onClick = onNavigateToRiders) {
                    Icon(Icons.Outlined.Person, contentDescription = "riders", tint = Sky)
                }
                IconButton(onClick = onNavigateToHistory) {
                    Icon(Icons.Outlined.History, contentDescription = "history", tint = Sky)
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(Icons.Outlined.Settings, contentDescription = "settings", tint = Sky)
                }
            }
        }

        // Center state content
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when (uiState.tripState) {
                TripState.IN_VEHICLE -> WatchingState(uiState)
                TripState.IDLE -> IdleState(uiState)
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

@Composable
private fun WatchingState(uiState: HomeUiState) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
                    .alpha(pulseAlpha)
                    .background(Sky, CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Depth, CircleShape)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("watching for", style = MaterialTheme.typography.bodyMedium, color = Sky)
            Text(
                text = uiState.riders.joinToString(", ") { it.name },
                fontSize = 28.sp,
                fontFamily = DmSans,
                fontWeight = FontWeight.Normal,
                color = Sand
            )
        }

        uiState.connectedVehicle?.let { vehicle ->
            Text(
                text = "connected to ${vehicle.name}",
                style = MaterialTheme.typography.bodySmall,
                color = Sky
            )
        }
    }
}

@Composable
private fun IdleState(uiState: HomeUiState) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Text(
            text = "btw...",
            style = MaterialTheme.typography.displayMedium,
            color = Air
        )
        Text(
            text = "no vehicle connected. btw is ready when you are.",
            style = MaterialTheme.typography.bodyMedium,
            color = Sky
        )
        if (uiState.riders.isEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "add a rider to get started",
                style = MaterialTheme.typography.bodySmall,
                color = Sky
            )
        }
    }
}

@Composable
private fun AlertState(
    uiState: HomeUiState,
    onSafe: () -> Unit,
    onGoingBack: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp),
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        Text(
            text = "btw...",
            style = MaterialTheme.typography.displaySmall,
            color = Air
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = uiState.activeRiderName,
                fontSize = 36.sp,
                fontFamily = DmSans,
                fontWeight = FontWeight.Normal,
                color = Sand
            )
            Text(
                text = "still in the car?",
                style = MaterialTheme.typography.headlineSmall,
                color = Air
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            BtwButton(text = "we're safe", onClick = onSafe)
            OutlinedButton(
                onClick = onGoingBack,
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Sky)
                ),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("going back", color = Sky, style = MaterialTheme.typography.labelLarge)
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
        Text(
            text = "glad everyone's safe.",
            style = MaterialTheme.typography.displaySmall,
            color = Air
        )
        Text(
            text = "btw will keep watching.",
            style = MaterialTheme.typography.bodyMedium,
            color = Sky
        )
    }
}
