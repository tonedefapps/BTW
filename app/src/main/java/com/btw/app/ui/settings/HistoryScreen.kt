package com.btw.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.domain.model.AlertEvent
import com.btw.app.domain.model.AlertOutcome
import com.btw.app.ui.onboarding.DmSans
import com.btw.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val history by viewModel.alertHistory.collectAsState()

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
            Text("history", style = MaterialTheme.typography.headlineSmall, color = Air)
            Spacer(modifier = Modifier.size(48.dp))
        }

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("no alerts yet", style = MaterialTheme.typography.bodyMedium, color = Sky)
                    Text("that's a good thing.", style = MaterialTheme.typography.bodySmall, color = Depth)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { event ->
                    AlertHistoryRow(event = event)
                }
            }
        }
    }
}

@Composable
private fun AlertHistoryRow(event: AlertEvent) {
    val fmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val outcomeColor = when (event.outcome) {
        AlertOutcome.SAFE, AlertOutcome.WENT_BACK -> Sky
        AlertOutcome.ESCALATED_SMS, AlertOutcome.ESCALATED_911 -> Sand
        else -> Depth
    }
    val outcomeLabel = when (event.outcome) {
        AlertOutcome.SAFE -> "safe"
        AlertOutcome.WENT_BACK -> "went back"
        AlertOutcome.ESCALATED_SMS -> "sms sent"
        AlertOutcome.ESCALATED_911 -> "911 prompted"
        AlertOutcome.DISMISSED -> "dismissed"
        AlertOutcome.PENDING -> "pending"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Depth.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = event.riderName,
                fontSize = 16.sp,
                fontFamily = DmSans,
                fontWeight = FontWeight.Normal,
                color = Sand
            )
            Text(
                text = event.vehicleName,
                style = MaterialTheme.typography.bodySmall,
                color = Sky
            )
            Text(
                text = fmt.format(Date(event.triggeredAt)),
                style = MaterialTheme.typography.labelMedium,
                color = Depth
            )
        }
        Text(
            text = outcomeLabel,
            style = MaterialTheme.typography.labelMedium,
            color = outcomeColor
        )
    }
}
