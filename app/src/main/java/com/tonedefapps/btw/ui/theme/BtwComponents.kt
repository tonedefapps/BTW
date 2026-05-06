package com.tonedefapps.btw.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
fun BtwTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "back",
                    tint = Air
                )
            }
            Spacer(Modifier.width(4.dp))
        }
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Air,
            modifier = Modifier.weight(1f)
        )
        trailing?.invoke()
    }
    HorizontalDivider(color = Sky.copy(alpha = 0.15f), thickness = 1.dp)
    Spacer(Modifier.height(16.dp))
}

// ── Section header ────────────────────────────────────────────────────────────

@Composable
fun BtwSectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Sky,
        letterSpacing = 1.2.sp,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

// ── Card ──────────────────────────────────────────────────────────────────────

@Composable
fun BtwCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Depth.copy(alpha = 0.2f))
            .border(1.dp, Sky.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        content = content
    )
}

// ── Divider ───────────────────────────────────────────────────────────────────

@Composable
fun BtwRowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = Sky.copy(alpha = 0.15f),
        thickness = 1.dp
    )
}

// ── Card rows ─────────────────────────────────────────────────────────────────

@Composable
fun BtwCardRow(
    label: String,
    sublabel: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = Air, fontWeight = FontWeight.Medium)
            if (sublabel != null) {
                Text(sublabel, style = MaterialTheme.typography.bodySmall, color = Sky)
            }
        }
        if (trailing != null) {
            Spacer(Modifier.width(8.dp))
            trailing()
        } else if (onClick != null) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Sky,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun BtwCardToggleRow(
    label: String,
    sublabel: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(label, style = MaterialTheme.typography.bodyLarge, color = Air, fontWeight = FontWeight.Medium)
            if (sublabel != null) {
                Text(sublabel, style = MaterialTheme.typography.bodySmall, color = Sky)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Ink,
                checkedTrackColor = Sky,
                uncheckedThumbColor = Sky.copy(alpha = 0.5f),
                uncheckedTrackColor = Depth.copy(alpha = 0.4f)
            )
        )
    }
}

@Composable
fun BtwCardValueRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge, color = Air, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Sky, fontWeight = FontWeight.Medium)
    }
}

// ── Buttons ───────────────────────────────────────────────────────────────────

@Composable
fun BtwPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = Air,
            contentColor = Ink,
            disabledContainerColor = Sky.copy(alpha = 0.2f),
            disabledContentColor = Sky
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(text, fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
fun BtwSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Air),
        border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = androidx.compose.ui.graphics.SolidColor(Sky)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(text, fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

@Composable
fun BtwDestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = AlertRed,
            contentColor = Air
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(text, fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

// ── Text field ────────────────────────────────────────────────────────────────

@Composable
fun BtwTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, style = MaterialTheme.typography.bodySmall, color = Sky) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Air,
            unfocusedTextColor = Air,
            focusedBorderColor = Sky,
            unfocusedBorderColor = Sky.copy(alpha = 0.35f),
            cursorColor = Sky
        ),
        modifier = modifier.fillMaxWidth()
    )
}

// ── Status pill ───────────────────────────────────────────────────────────────

@Composable
fun BtwStatusPill(text: String, color: androidx.compose.ui.graphics.Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Box(modifier = Modifier.size(8.dp).background(color, RoundedCornerShape(4.dp)))
        Text(text, fontFamily = DmSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = color)
    }
}

// ── Premium badge ─────────────────────────────────────────────────────────────

@Composable
fun PremiumBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(Sky.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .border(1.dp, Sky.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(imageVector = Icons.Outlined.Star, contentDescription = null, tint = Sky, modifier = Modifier.size(11.dp))
        Text("premium", fontFamily = DmSans, fontWeight = FontWeight.Medium, fontSize = 11.sp, color = Sky)
    }
}
