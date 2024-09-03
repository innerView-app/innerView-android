package com.dev.innerview.core.designsystem.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Black,
    onPrimary = LightBlue,
    primaryContainer = Black,
    onPrimaryContainer = Aero,
    background = Black,
    onBackground = Alabaster,
    onSurface = DarkGray,
    surfaceContainer = DuskGray,
    secondary = Black,
    onSecondary = Khaki,
    secondaryContainer = Black,
    onSecondaryContainer = Dun,
    tertiary = White,
    onTertiary = DimGray,
    error = Red02,
    outline = White,
    scrim = Black.copy(alpha = 0.30f)
)

private val LightColorScheme = lightColorScheme(
    primary = LightBlue,
    onPrimary = Black,
    primaryContainer = Aero,
    onPrimaryContainer = Black,
    background = Alabaster,
    onBackground = Black,
    onSurface = DarkGray,
    surfaceContainer = DuskGray,
    secondary = Khaki,
    onSecondary = Black,
    secondaryContainer = Dun,
    onSecondaryContainer = Black,
    tertiary = DimGray,
    onTertiary = White,
    error = Red02,
    outline = White,
    scrim = Black.copy(alpha = 0.30f)
)

@Composable
fun InnerViewTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}