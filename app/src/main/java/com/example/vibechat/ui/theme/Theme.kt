package com.example.vibechat.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = VibeGreen,
    onPrimary = Color.White,
    primaryContainer = VibeGreenDark,
    onPrimaryContainer = Color.White,
    secondary = VibeGreenLight,
    onSecondary = Color.Black,
    background = VibeDarkBg,
    onBackground = VibeDarkTextPrimary,
    surface = VibeDarkPanel,
    onSurface = VibeDarkTextPrimary,
    surfaceVariant = VibeDarkSurface,
    onSurfaceVariant = VibeDarkTextSecondary,
    outline = VibeDarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = VibeTealDark,
    onPrimary = Color.White,
    primaryContainer = VibeGreen,
    onPrimaryContainer = Color.White,
    secondary = VibeGreenLight,
    onSecondary = Color.Black,
    background = VibeLightBg,
    onBackground = VibeLightTextPrimary,
    surface = VibeLightSurface,
    onSurface = VibeLightTextPrimary,
    surfaceVariant = VibeLightSurface,
    onSurfaceVariant = VibeLightTextSecondary,
    outline = VibeLightDivider
)

@Composable
fun VibeChatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
