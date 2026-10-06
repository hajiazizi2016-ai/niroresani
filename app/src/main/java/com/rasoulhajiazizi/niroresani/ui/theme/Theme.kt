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
    primary = SkyBlue,
    onPrimary = White,
    secondary = AccentOrange,
    background = White,
    surface = SurfaceLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = ErrorRed
)

// حتی در تم تاریک سیستم، به‌جای پس‌زمینهٔ تقریبا سیاه قبلی، از آبی آسمانی تیره استفاده می‌شود
// تا صفحه اصلی هیچ‌وقت حس «مشکی و ناخوشایند» ندهد (بخش ۱ درخواست اصلاحات).
private val DarkColors = darkColorScheme(
    primary = SkyBlue,
    onPrimary = White,
    secondary = AccentOrange,
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
