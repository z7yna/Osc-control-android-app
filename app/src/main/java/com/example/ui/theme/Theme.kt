package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonRed,
    onPrimary = Color.White,
    primaryContainer = NeonRedDim,
    onPrimaryContainer = Color.White,
    secondary = AccentOrange,
    onSecondary = Color.White,
    tertiary = LaserCyan,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextWhitePrimary,
    surface = DarkSurface,
    onSurface = TextWhitePrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextGraySecondary,
    outline = GlassBorderRed,
    outlineVariant = GlassBorderWhite
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
