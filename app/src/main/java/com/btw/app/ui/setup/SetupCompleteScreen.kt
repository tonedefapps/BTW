package com.btw.app.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.btw.app.ui.onboarding.BtwButton
import com.btw.app.ui.onboarding.Wordmark
import com.btw.app.ui.theme.*

@Composable
fun SetupCompleteScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .padding(horizontal = 32.dp, vertical = 56.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(40.dp)
        ) {
            Wordmark()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "you're all set.",
                    style = MaterialTheme.typography.displaySmall,
                    color = Air
                )
                Text(
                    text = "btw will quietly watch in the background. you'll only hear from us if something needs your attention.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Sky
                )
            }

            BtwButton(text = "start watching", onClick = onStart)
        }
    }
}
