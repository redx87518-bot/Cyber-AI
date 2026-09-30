package com.cyberfusion.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Shapes

// ── Palette: deep-ink console with electric cyan accent ─────────────────────────
val Ink0 = Color(0xFF070B14) // deepest background
val Ink1 = Color(0xFF0B1120) // app background
val Ink2 = Color(0xFF101A30) // surface
val Ink3 = Color(0xFF16223E) // raised surface / inputs
val Ink4 = Color(0xFF1E2C4C) // outline-ish

val Cyan = Color(0xFF22D3EE)
val CyanDeep = Color(0xFF0E7490)
val Mint = Color(0xFF34D399)
val Amber = Color(0xFFFBBF24)
val Coral = Color(0xFFFB7185)
val Violet = Color(0xFFA78BFA)
val Blue = Color(0xFF60A5FA)

val TextHi = Color(0xFFE8EFFA)
val TextLo = Color(0xFF8FA3C2)
val TextFaint = Color(0xFF5C6F8F)

val Success = Mint
val Warning = Amber
val Danger = Coral

// ── Semantic helpers ────────────────────────────────────────────────────────────
fun severityColor(severity: String): Color = when (severity.lowercase().trim()) {
    "critical" -> Coral
    "high" -> Amber
    "medium", "moderate" -> Cyan
    "low", "info" -> Mint
    "malicious" -> Coral
    "clean", "benign" -> Mint
    else -> TextLo
}

fun statusColor(status: String): Color = when (status.lowercase().trim()) {
    "active", "open", "in progress", "running", "connected" -> Cyan
    "contained", "resolved", "closed", "completed", "mitigated" -> Mint
    "failed", "critical", "invalid" -> Coral
    "pending", "not configured", "saved" -> TextLo
    else -> TextLo
}

// ── Material scheme (dark-first product) ───────────────────────────────────────
private val CyberColorScheme = darkColorScheme(
    primary = Cyan,
    onPrimary = Ink0,
    primaryContainer = Color(0xFF0F3546),
    onPrimaryContainer = Color(0xFFB8F1FF),
    inversePrimary = CyanDeep,
    secondary = Violet,
    onSecondary = Ink0,
    secondaryContainer = Color(0xFF2A2450),
    onSecondaryContainer = Color(0xFFDDD2FF),
    tertiary = Mint,
    onTertiary = Ink0,
    tertiaryContainer = Color(0xFF0E3A2E),
    onTertiaryContainer = Color(0xFFB8F5DE),
    background = Ink1,
    onBackground = TextHi,
    surface = Ink1,
    onSurface = TextHi,
    surfaceVariant = Ink2,
    onSurfaceVariant = TextLo,
    surfaceTint = Cyan,
    inverseSurface = Color(0xFFE8EFFA),
    inverseOnSurface = Ink1,
    error = Coral,
    onError = Ink0,
    errorContainer = Color(0xFF3D1526),
    onErrorContainer = Color(0xFFFFC9D4),
    outline = Ink4,
    outlineVariant = Color(0xFF182536),
    scrim = Color(0xCC05080F)
)

// ── Typography: tight neo-grotesque headings, mono console labels ──────────────
private val CyberTypography = Typography(
    headlineLarge = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, lineHeight = 36.sp, letterSpacing = (-0.5).sp),
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 25.sp, lineHeight = 31.sp, letterSpacing = (-0.4).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Bold, fontSize = 21.sp, lineHeight = 27.sp, letterSpacing = (-0.2).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 25.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 19.sp),
    bodyLarge = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.1.sp),
    bodyMedium = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 21.sp, letterSpacing = 0.1.sp),
    bodySmall = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 17.sp, letterSpacing = 0.2.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 18.sp, letterSpacing = 0.2.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp),
    labelSmall = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 10.5.sp, lineHeight = 14.sp, letterSpacing = 0.6.sp)
)

private val CyberShapes = Shapes(
    extraSmall = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    small = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(18.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(26.dp),
    extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(32.dp)
)

@Composable
fun CyberFusionTheme(
    darkTheme: Boolean = true, // dark-first product: the console look is the brand
    content: @Composable () -> Unit
) {
    // Kept parameter for API compatibility; the design system is intentionally dark.
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = CyberTypography,
        shapes = CyberShapes,
        content = content
    )
}
