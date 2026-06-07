package com.lovecare.cycle.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = TextPrimaryLight,
    primaryContainer = PinkPrimaryVariant,
    onPrimaryContainer = TextPrimaryLight,
    secondary = PurplePrimary,
    onSecondary = TextPrimaryLight,
    secondaryContainer = PurplePrimaryVariant,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = AccentLavender,
    onTertiary = TextPrimaryLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = PinkPrimaryVariant,
    onSurfaceVariant = TextSecondaryLight,
    error = Error,
    onError = SurfaceLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkDark,
    onPrimary = TextPrimaryDark,
    primaryContainer = PinkPrimary,
    onPrimaryContainer = TextPrimaryDark,
    secondary = PurpleDark,
    onSecondary = TextPrimaryDark,
    secondaryContainer = PurplePrimary,
    onSecondaryContainer = TextPrimaryDark,
    tertiary = AccentLavender,
    onTertiary = TextPrimaryDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondaryDark,
    error = Error,
    onError = SurfaceDark
)

@Composable
fun LoveCycleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

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
