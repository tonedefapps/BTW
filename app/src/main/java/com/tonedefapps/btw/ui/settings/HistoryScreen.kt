package com.tonedefapps.btw.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.domain.model.AlertEvent
import com.tonedefapps.btw.domain.model.AlertOutcome
import com.tonedefapps.btw.ui.theme.*
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
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp)
    ) {
        BtwTopBar(title = "history", onBack = onBack)

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("no alerts yet", style = MaterialTheme.typography.bodyMedium, color = Air)
                    Text("that's a good thing.", style = MaterialTheme.typography.bodySmall, color = Sky)
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(history) { event ->
                    AlertHistoryCard(event = event)
                }
            }
        }
    }
}

@Composable
private fun AlertHistoryCard(event: AlertEvent) {
    val fmt = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
    val (outcomeColor, outcomeLabel) = when (event.outcome) {
        AlertOutcome.SAFE -> SafeGreen to "safe"
        AlertOutcome.WENT_BACK -> WarnAmber to "went back"
        AlertOutcome.ESCALATED_SMS -> Sand to "sms sent"
        AlertOutcome.DISMISSED -> Sky to "dismissed"
        AlertOutcome.PENDING -> Sky to "no response"
    }

    BtwCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = event.riderName,
                    fontSize = 16.sp,
                    fontFamily = DmSans,
                    fontWeight = FontWeight.Normal,
                    color = Sand
                )
                Text(text = event.vehicleName, style = MaterialTheme.typography.bodySmall, color = Sky)
                Text(
                    text = fmt.format(Date(event.triggeredAt)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Sky.copy(alpha = 0.5f)
                )
            }
            Surface(
                color = outcomeColor.copy(alpha = 0.15f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = outcomeLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = outcomeColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
