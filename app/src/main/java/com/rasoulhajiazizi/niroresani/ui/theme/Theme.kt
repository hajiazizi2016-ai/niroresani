package com.rasoulhajiazizi.niroresani.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

private val LightColors = lightColorScheme(
    primary = NavyPrimary,
    onPrimary = SurfaceLight,
    secondary = AccentOrange,
    background = SurfaceLight,
    surface = SurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = ErrorRed
)

private val DarkColors = darkColorScheme(
    primary = AccentOrange,
    onPrimary = SurfaceDark,
    secondary = NavyPrimary,
    background = SurfaceDark,
    surface = SurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = ErrorRed
)

@Composable
fun NiroResaniTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = colors, typography = AppTypography, content = content)
    }
}
