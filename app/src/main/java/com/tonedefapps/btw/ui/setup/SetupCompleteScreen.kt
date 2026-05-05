package com.tonedefapps.btw.ui.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tonedefapps.btw.ui.onboarding.BtwButton
import com.tonedefapps.btw.ui.onboarding.Wordmark
import com.tonedefapps.btw.ui.theme.*

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
                    text = "btw is running in the background. it'll only speak up if something needs checking.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Sky
                )
            }

            BtwButton(text = "start", onClick = onStart)
        }
    }
}
