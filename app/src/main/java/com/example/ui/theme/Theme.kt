package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = TacticalCyanGlow,
        onPrimary = TacticalObsidianBlack,
        primaryContainer = TacticalCyanContainer,
        onPrimaryContainer = TacticalCyanOnContainer,
        secondary = TacticalAmberSecondary,
        onSecondary = TacticalObsidianBlack,
        secondaryContainer = TacticalAmberContainer,
        onSecondaryContainer = TacticalAmberOnContainer,
        tertiary = TacticalCyanLight,
        background = TacticalObsidianBlack,
        surface = TacticalNavySurface,
        onBackground = Color(0xFFF1F5F9),
        onSurface = Color(0xFFF1F5F9),
        surfaceVariant = TacticalNavyCard,
        onSurfaceVariant = Color(0xFF94A3B8),
        outline = TacticalBorderCyan,
        error = TacticalError,
        errorContainer = TacticalErrorContainer,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = TacticalCyanPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0F2FE),
        onPrimaryContainer = Color(0xFF03446A),
        secondary = TacticalAmberSecondary,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFFEF3C7),
        onSecondaryContainer = Color(0xFF78350F),
        tertiary = TacticalCyanDark,
        background = TacticalLightBackground,
        surface = TacticalLightSurface,
        onBackground = TacticalTextNavy,
        onSurface = TacticalTextNavy,
        surfaceVariant = TacticalLightCard,
        onSurfaceVariant = TacticalTextMuted,
        outline = Color(0xFFCBD5E1),
        error = TacticalError,
        errorContainer = Color(0xFFFEE2E2),
    )

@Composable
fun ShikkhaAITheme(
    darkTheme: Boolean = true, // Default to true to honor the AIS Army IBA Specialist tactical dark aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    ShikkhaAITheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
