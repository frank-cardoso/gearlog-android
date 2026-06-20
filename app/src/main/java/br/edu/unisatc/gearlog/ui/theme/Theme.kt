package br.edu.unisatc.gearlog.ui.theme

import android.app.Activity
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

private val DarkColorScheme = darkColorScheme(
    primary = PremiumPrimary,
    onPrimary = PremiumOnPrimary,
    secondary = PremiumMuted,
    background = PremiumBackground,
    onBackground = PremiumOnBackground,
    surface = PremiumCard,
    onSurface = PremiumOnSurface,
    surfaceVariant = PremiumCard,
    onSurfaceVariant = PremiumOnSurface,
    outline = PremiumMuted
)

private val LightColorScheme = lightColorScheme(
    primary = PremiumPrimary,
    onPrimary = PremiumOnPrimary,
    secondary = PremiumMuted,
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFF8F6F0),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = PremiumOnSurface,
    onSurfaceVariant = PremiumCard,

    outline = PremiumMuted
)

@Composable
fun GearLogTheme(
    themeMode: ThemeMode = ThemeMode.LIGHT,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}