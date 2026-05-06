package com.tonedefapps.btw.ui.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tonedefapps.btw.data.billing.BillingManager
import com.tonedefapps.btw.ui.onboarding.Wordmark
import com.tonedefapps.btw.ui.theme.*

@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    onPurchased: () -> Unit,
    viewModel: PaywallViewModel = hiltViewModel()
) {
    val isPremium by viewModel.isPremium.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    val activity = context as? android.app.Activity

    var selectedProduct by remember { mutableStateOf(BillingManager.PRODUCT_YEARLY) }

    LaunchedEffect(isPremium) {
        if (isPremium) onPurchased()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(56.dp))
        Wordmark()
        Spacer(Modifier.height(32.dp))

        Text(
            text = "the full safety net.",
            style = MaterialTheme.typography.displaySmall,
            color = Air,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))

        // ── What's included ────────────────────────────────────────────────
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

        BtwSectionHeader("premium — when you need backup")
        BtwCard {
            BtwCardValueRow(label = "backup pickup coordination", value = "★")
            BtwRowDivider()
            BtwCardValueRow(label = "configurable escalation timers", value = "★")
        }

        Spacer(Modifier.height(28.dp))

        // ── Pricing options ────────────────────────────────────────────────
        BtwSectionHeader("choose a plan")

        PricingOption(
            label = "monthly",
            price = "$0.99 / month",
            sublabel = null,
            selected = selectedProduct == BillingManager.PRODUCT_MONTHLY,
            onClick = { selectedProduct = BillingManager.PRODUCT_MONTHLY }
        )
        Spacer(Modifier.height(10.dp))
        PricingOption(
            label = "yearly",
            price = "$9.99 / year",
            sublabel = "save 16% vs monthly",
            selected = selectedProduct == BillingManager.PRODUCT_YEARLY,
            onClick = { selectedProduct = BillingManager.PRODUCT_YEARLY }
        )
        Spacer(Modifier.height(10.dp))
        PricingOption(
            label = "lifetime",
            price = "$14.99 once",
            sublabel = "pay once, protected forever",
            selected = selectedProduct == BillingManager.PRODUCT_LIFETIME,
            onClick = { selectedProduct = BillingManager.PRODUCT_LIFETIME }
        )

        Spacer(Modifier.height(28.dp))

        errorMessage?.let { msg ->
            Text(
                text = msg,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
        }

        BtwPrimaryButton(
            text = when (selectedProduct) {
                BillingManager.PRODUCT_MONTHLY  -> "unlock premium — $0.99/mo"
                BillingManager.PRODUCT_YEARLY   -> "unlock premium — $9.99/yr"
                else                            -> "unlock premium — $14.99 forever"
            },
            onClick = { activity?.let { viewModel.startPurchase(it, selectedProduct) } }
        )
        Spacer(Modifier.height(12.dp))
        BtwSecondaryButton(
            text = "restore purchase",
            onClick = { viewModel.restore() }
        )
        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onBack) {
            Text("not now", color = Sky.copy(alpha = 0.6f), style = MaterialTheme.typography.bodyMedium)
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "no account required · managed by Google Play\ncancel anytime",
            style = MaterialTheme.typography.bodySmall,
            color = Sky.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun PricingOption(
    label: String,
    price: String,
    sublabel: String?,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) Sky else Sky.copy(alpha = 0.2f)
    val bgColor = if (selected) Sky.copy(alpha = 0.1f) else Depth.copy(alpha = 0.45f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(if (selected) 1.5.dp else 0.5.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Sky)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = Air)
            sublabel?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = Sky.copy(alpha = 0.7f))
            }
        }
        Text(
            text = price,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (selected) Air else Sky
        )
    }
}
