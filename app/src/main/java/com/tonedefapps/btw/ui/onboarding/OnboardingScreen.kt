package com.tonedefapps.btw.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
            .verticalScroll(rememberScrollState())
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Wordmark()
        Spacer(Modifier.height(32.dp))

        Text(
            text = "because they would.",
            style = MaterialTheme.typography.displaySmall,
            color = Air,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "no account  ·  no data  ·  no ads",
            style = MaterialTheme.typography.bodySmall,
            color = Sky,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(36.dp))

        BtwSectionHeader("always free")
        BtwCard {
            BtwCardValueRow(label = "bluetooth + motion detection", value = "✓", valueColor = SafeGreen)
            BtwRowDivider()
            BtwCardValueRow(label = "gentle check-in notification", value = "✓", valueColor = SafeGreen)
            BtwRowDivider()
            BtwCardValueRow(label = "persistent alarm",             value = "✓", valueColor = SafeGreen)
            BtwRowDivider()
            BtwCardValueRow(label = "hot day mode",                 value = "✓", valueColor = SafeGreen)
        }
        Spacer(Modifier.height(20.dp))

        BtwSectionHeader("premium — when you need backup")
        BtwCard {
            BtwCardValueRow(label = "sms emergency contact",          value = "★", valueColor = WarnAmber)
            BtwRowDivider()
            BtwCardValueRow(label = "backup pickup coordination",     value = "★", valueColor = WarnAmber)
            BtwRowDivider()
            BtwCardValueRow(label = "configurable escalation timers", value = "★", valueColor = WarnAmber)
        }
        Spacer(Modifier.height(12.dp))

        Text(
            text = "no account required  ·  managed by Google Play",
            style = MaterialTheme.typography.bodySmall,
            color = Sky.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        BtwButton(text = "get started", onClick = {
            viewModel.markOnboardingComplete()
            onComplete()
        })
        Spacer(Modifier.height(12.dp))
        Text(
            text = "unlock premium anytime in settings",
            style = MaterialTheme.typography.bodySmall,
            color = Sky.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun Wordmark() {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Air)) { append("btw") }
            withStyle(SpanStyle(color = Sky)) { append("...") }
        },
        fontSize = 36.sp,
        fontFamily = DmSans,
        letterSpacing = (-0.5).sp
    )
}

@Composable
fun BtwButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Air,
            contentColor = Ink
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}
