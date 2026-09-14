package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BrandCrimsonLight,
    onPrimary = Color.White,
    primaryContainer = BrandPeachDark,
    onPrimaryContainer = Color(0xFFFFDAD9),
    secondary = BrandAmber,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF422C00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = BrandSparkle,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF322254),
    onTertiaryContainer = Color(0xFFE9DDFF),
    background = BgDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = OutlineDark,
    outlineVariant = Color(0xFF3E4150)
)

private val LightColorScheme = lightColorScheme(
    primary = BrandCrimson,
    onPrimary = Color.White,
    primaryContainer = BrandPeach,
    onPrimaryContainer = Color(0xFF410007),
    secondary = BrandAmber,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE082),
    onSecondaryContainer = Color(0xFF261900),
    tertiary = BrandSparkle,
    onTertiary = Color.White,
    tertiaryContainer = BrandSparkleContainer,
    onTertiaryContainer = Color(0xFF260A66),
    background = BgLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = OutlineLight,
    outlineVariant = Color(0xFFD1D5DB)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
