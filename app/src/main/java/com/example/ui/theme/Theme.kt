package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

enum class AppThemeStyle(val displayName: String) {
    GREEN("أخضر (إسلامي)"),
    GOLD("ذهبي (روحاني)"),
    WHITE("أبيض (بسيط)"),
    BLUE("أزرق هادئ"),
    DARK("وضع ليلي")
}

private val GreenColorScheme = lightColorScheme(
    primary = GreenPrimary,
    secondary = GreenSecondary,
    tertiary = GreenTertiary,
    background = GreenBackground,
    surface = GreenSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1B5E20),
    onSurface = Color(0xFF1B5E20)
)

private val GoldColorScheme = lightColorScheme(
    primary = GoldPrimary,
    secondary = GoldSecondary,
    tertiary = GoldTertiary,
    background = GoldBackground,
    surface = GoldSurface,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF3E2723),
    onSurface = Color(0xFF3E2723)
)

private val WhiteColorScheme = lightColorScheme(
    primary = WhitePrimary,
    secondary = WhiteSecondary,
    tertiary = WhiteTertiary,
    background = WhiteBackground,
    surface = WhiteSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF263238),
    onSurface = Color(0xFF263238)
)

private val BlueColorScheme = lightColorScheme(
    primary = BluePrimary,
    secondary = BlueSecondary,
    tertiary = BlueTertiary,
    background = BlueBackground,
    surface = BlueSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0D47A1),
    onSurface = Color(0xFF0D47A1)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4CAF50),       // Radiant green for action buttons
    secondary = Color(0xFFFFD54F),     // Warm gold for spiritual accents
    tertiary = Color(0xFF81C784),      // Light emerald green
    background = Color(0xFF0F110F),    // Deep dark forest background
    surface = Color(0xFF161916),       // Soft dark green-surface card color
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFFE8F5E9),  // Light minty white for maximum text readability
    onSurface = Color(0xFFE8F5E9),     // Light minty white
    primaryContainer = Color(0xFF1B5E20), // Dark green container
    onPrimaryContainer = Color(0xFFC8E6C9),
    secondaryContainer = Color(0xFF3E2723), // Warm dark container
    onSecondaryContainer = Color(0xFFFFECB3),
    surfaceVariant = Color(0xFF252925),
    onSurfaceVariant = Color(0xFFC8E6C9),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun MyApplicationTheme(
    themeStyle: AppThemeStyle = AppThemeStyle.GREEN,
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val baseScheme = when (themeStyle) {
        AppThemeStyle.GREEN -> GreenColorScheme
        AppThemeStyle.GOLD -> GoldColorScheme
        AppThemeStyle.WHITE -> WhiteColorScheme
        AppThemeStyle.BLUE -> BlueColorScheme
        AppThemeStyle.DARK -> DarkColorScheme
    }

    val colorScheme = if (highContrast) {
        if (themeStyle == AppThemeStyle.DARK) {
            baseScheme.copy(
                background = Color.Black,
                surface = Color(0xFF121212),
                onBackground = Color.White,
                onSurface = Color.White,
                primary = Color(0xFF00FF66), // Extremely high visibility fluorescent green
                secondary = Color(0xFFFFEB3B), // High visibility solid gold/yellow
                onPrimary = Color.Black,
                onSecondary = Color.Black
            )
        } else {
            baseScheme.copy(
                background = Color.White,
                surface = Color(0xFFF0F0F0),
                onBackground = Color.Black,
                onSurface = Color.Black,
                primary = Color(0xFF0D3211), // High contrast very deep green
                secondary = Color(0xFFB71C1C), // High contrast blood red/amber
                onPrimary = Color.White,
                onSecondary = Color.White
            )
        }
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
