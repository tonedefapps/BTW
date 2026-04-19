package com.btw.app.ui.onboarding

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.btw.app.ui.theme.*

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            body = "because this world is distracting.",
            cta = null
        ),
        OnboardingPage(
            body = "because they're worth the reminder.",
            cta = null
        ),
        OnboardingPage(
            body = "because they would.",
            cta = "get started",
            footnote = "no cloud · no data · no agenda · just btw"
        )
    )

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

            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "onboarding_page"
            ) { page ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = pages[page].body,
                        style = MaterialTheme.typography.displaySmall,
                        color = Air
                    )
                    pages[page].footnote?.let { note ->
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            color = Sky
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PageIndicator(total = pages.size, current = currentPage)

            if (pages[currentPage].cta != null) {
                BtwButton(
                    text = pages[currentPage].cta!!,
                    onClick = {
                        viewModel.markOnboardingComplete()
                        onComplete()
                    }
                )
            } else {
                BtwButton(
                    text = "next",
                    onClick = { currentPage++ }
                )
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
            containerColor = Depth,
            contentColor = Air
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Air
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

private data class OnboardingPage(
    val body: String,
    val cta: String?,
    val footnote: String? = null
)
