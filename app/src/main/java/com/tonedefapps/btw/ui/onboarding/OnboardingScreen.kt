package com.tonedefapps.btw.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = 4

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Ink)
    ) {
        AnimatedContent(
            targetState = currentPage,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "onboarding_page"
        ) { page ->
            when (page) {
                3 -> PremiumExplainerPage(
                    onContinue = {
                        viewModel.markOnboardingComplete()
                        onComplete()
                    }
                )
                else -> {
                    val bodies = listOf(
                        "because this world is distracting.",
                        "because brains have a lot going on.",
                        "because they would."
                    )
                    val isLast = page == 2
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
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
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                Text(
                                    text = bodies[page],
                                    style = MaterialTheme.typography.displaySmall,
                                    color = Air
                                )
                                if (isLast) {
                                    Text(
                                        text = "no account · no password · no data · just btw",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Sky
                                    )
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                            PageIndicator(total = totalPages, current = page)
                            BtwButton(
                                text = if (isLast) "see what's included" else "next",
                                onClick = { currentPage++ }
                            )
                        }
                    }
                }
            }
        }
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

@Composable
fun PageIndicator(total: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { i ->
            Box(
                modifier = Modifier
                    .size(if (i == current) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(if (i == current) Sky else Depth)
            )
        }
    }
}

@Composable
private fun PremiumExplainerPage(onContinue: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))
        Wordmark()
        Spacer(Modifier.height(40.dp))

        Text(
            text = "here's what you get.",
            style = MaterialTheme.typography.displaySmall,
            color = Air,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        BtwSectionHeader("always free — keeps you informed")
        BtwCard {
            BtwCardValueRow(label = "bluetooth + motion detection", value = "✓")
            BtwRowDivider()
            BtwCardValueRow(label = "gentle notification", value = "✓")
            BtwRowDivider()
            BtwCardValueRow(label = "persistent alarm", value = "✓")
            BtwRowDivider()
            BtwCardValueRow(label = "hot day mode", value = "✓")
        }

        Spacer(Modifier.height(20.dp))

        BtwSectionHeader("premium — notifies others too")
        BtwCard {
            BtwCardValueRow(label = "sms your emergency contact", value = "★")
            BtwRowDivider()
            BtwCardValueRow(label = "handoff / proxy pickup alerts", value = "★")
            BtwRowDivider()
            BtwCardValueRow(label = "configurable escalation timers", value = "★")
        }

        Spacer(Modifier.height(16.dp))
        BtwCard {
            BtwCardValueRow(label = "monthly", value = "$0.99 / mo")
            BtwRowDivider()
            BtwCardValueRow(label = "yearly", value = "$9.99 / yr")
            BtwRowDivider()
            BtwCardValueRow(label = "lifetime", value = "$14.99 once")
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = "no account required · managed by Google Play",
            style = MaterialTheme.typography.bodySmall,
            color = Sky.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(32.dp))
        BtwButton(text = "get started", onClick = onContinue)
        Spacer(Modifier.height(12.dp))
        Text(
            text = "you can unlock premium anytime in settings",
            style = MaterialTheme.typography.bodySmall,
            color = Sky.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
    }
}
