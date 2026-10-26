package com.example.demov2026.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Gray200,
    onPrimary = Gray900,
    primaryContainer = Gray700,
    onPrimaryContainer = Gray50,
    secondary = Gray300,
    onSecondary = Gray900,
    secondaryContainer = Gray800,
    onSecondaryContainer = Gray100,
    tertiary = Gray400,
    onTertiary = Gray900,
    tertiaryContainer = Gray700,
    onTertiaryContainer = Gray100,
    background = Gray950,
    onBackground = Gray100,
    surface = Gray900,
    onSurface = Gray100,
    surfaceVariant = Gray800,
    onSurfaceVariant = Gray300,
    surfaceTint = Gray200,
    inverseSurface = Gray100,
    inverseOnSurface = Gray900,
    inversePrimary = Gray800,
    outline = Gray500,
    outlineVariant = Gray700,
    error = ErrorDark,
    onError = Gray950,
    errorContainer = ErrorContainerDark,
    onErrorContainer = ErrorDark,
    surfaceDim = Gray950,
    surfaceBright = Gray800,
    surfaceContainerLowest = Gray950,
    surfaceContainerLow = Gray900,
    surfaceContainer = Gray850,
    surfaceContainerHigh = Gray800,
    surfaceContainerHighest = Gray700
)

private val LightColorScheme = lightColorScheme(
    primary = Gray800,
    onPrimary = Color.White,
    primaryContainer = Gray100,
    onPrimaryContainer = Gray900,
    secondary = Gray600,
    onSecondary = Color.White,
    secondaryContainer = Gray200,
    onSecondaryContainer = Gray900,
    tertiary = Gray500,
    onTertiary = Color.White,
    tertiaryContainer = Gray150,
    onTertiaryContainer = Gray900,
    background = Gray50,
    onBackground = Gray900,
    surface = Color.White,
    onSurface = Gray900,
    surfaceVariant = Gray100,
    onSurfaceVariant = Gray600,
    surfaceTint = Gray800,
    inverseSurface = Gray800,
    inverseOnSurface = Gray50,
    inversePrimary = Gray200,
    outline = Gray500,
    outlineVariant = Gray200,
    error = ErrorLight,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = ErrorLight,
    surfaceDim = Gray150,
    surfaceBright = Color.White,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Gray50,
    surfaceContainer = Gray100,
    surfaceContainerHigh = Gray150,
    surfaceContainerHighest = Gray200
)

@Composable
fun Demov2026Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
