package com.cyberfusion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Gold = Color(0xFFD4AF37)
private val DarkGold = Color(0xFFB8960C)
private val LightGold = Color(0xFFF5E6B8)
private val SurfaceWhite = Color(0xFFFAFAFA)
private val BackgroundWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1A1A1A)
private val TextSecondary = Color(0xFF666666)
private val BorderColor = Color(0xFFE0E0E0)

private val DarkColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Color.Black,
    secondary = DarkGold,
    onSecondary = Color.Black,
    tertiary = LightGold,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White,
    outline = BorderColor
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Color.White,
    secondary = DarkGold,
    onSecondary = Color.White,
    tertiary = LightGold,
    background = BackgroundWhite,
    surface = SurfaceWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = BorderColor
)

@Composable
fun CyberFusionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
