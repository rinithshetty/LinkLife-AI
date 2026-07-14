package com.lifelink.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    primaryContainer = TealContainer,
    error = EmergencyRed,
    errorContainer = EmergencyRedContainer,
    background = SurfaceLight,
    surface = SurfaceLight,
    outline = NeutralOutline,
)

private val DarkColors = darkColorScheme(
    primary = TealPrimaryDark,
    primaryContainer = TealPrimary,
    error = EmergencyRed,
    errorContainer = EmergencyRedContainer,
    background = SurfaceDark,
    surface = SurfaceDark,
    outline = NeutralOutline,
)

/**
 * Root theme composable for the whole app. Accessibility toggles (dark mode override,
 * large text) are first-class parameters here from day one rather than bolted on later —
 * every screen in every feature module composes under this.
 *
 * @param useLargeText when true, scales all text sizes up ~1.3x (Accessibility Mode).
 */
@Composable
fun LifeLinkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    useLargeText: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColors else LightColors
    val typography = if (useLargeText) LifeLinkTypography.scaled(1.3f) else LifeLinkTypography

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = LifeLinkShapes,
        content = content,
    )
}

private fun androidx.compose.material3.Typography.scaled(factor: Float): androidx.compose.material3.Typography {
    fun TextStyle.scale() = copy(fontSize = fontSize * factor, lineHeight = lineHeight * factor)
    return copy(
        displayLarge = displayLarge.scale(),
        headlineMedium = headlineMedium.scale(),
        titleLarge = titleLarge.scale(),
        titleMedium = titleMedium.scale(),
        bodyLarge = bodyLarge.scale(),
        bodyMedium = bodyMedium.scale(),
        labelLarge = labelLarge.scale(),
    )
}
