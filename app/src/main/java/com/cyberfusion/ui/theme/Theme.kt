package com.cyberfusion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val CyberRed = Color(0xFFFF1744)
val CyberRedDark = Color(0xFFD50000)
val CyberBlue = Color(0xFF2979FF)
val CyberBlueDark = Color(0xFF0062E0)
val CyberSurfaceRed = Color(0xFF1A0508)
val CyberSurfaceBlue = Color(0xFF050A1A)

private val DarkColorScheme = darkColorScheme(
    primary = CyberRed,
    onPrimary = Color.White,
    secondary = CyberBlue,
    onSecondary = Color.White,
    tertiary = Color(0xFF64FFDA),
    background = Color(0xFF0A0A0A),
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color(0xFF333333),
    error = CyberRed
)

private val LightColorScheme = lightColorScheme(
    primary = CyberRed,
    onPrimary = Color.White,
    secondary = CyberBlue,
    onSecondary = Color.White,
    tertiary = Color(0xFF00BFA5),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onBackground = Color.Black,
    onSurface = Color.Black,
    outline = Color(0xFFE0E0E0),
    error = CyberRed
)

@Composable
fun CyberFusionTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
