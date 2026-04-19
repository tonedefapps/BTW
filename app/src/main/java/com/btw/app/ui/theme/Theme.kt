package com.btw.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BtwColorScheme = darkColorScheme(
    primary = Depth,
    onPrimary = Air,
    primaryContainer = Ink,
    onPrimaryContainer = Air,
    secondary = Sky,
    onSecondary = Ink,
    secondaryContainer = Depth,
    onSecondaryContainer = Air,
    tertiary = Sand,
    onTertiary = Ink,
    background = Ink,
    onBackground = Air,
    surface = Ink,
    onSurface = Air,
    surfaceVariant = Depth,
    onSurfaceVariant = Air,
    outline = Sky,
    outlineVariant = Depth,
    error = Sky,
    onError = Ink,
)

@Composable
fun BtwTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BtwColorScheme,
        typography = BtwTypography,
        content = content
    )
}
