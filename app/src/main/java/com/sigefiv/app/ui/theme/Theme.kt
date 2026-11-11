package com.sigefiv.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun SIGEFIVTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    // 🌱 Detectar temporada automáticamente
    val season = SeasonalTheme.getSeason()

    // Color principal según la temporada
    val seasonalPrimary = SeasonalColors.primary(season)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = seasonalPrimary,
            secondary = seasonalPrimary,
            tertiary = seasonalPrimary,

            background = DarkBackground,
            surface = DarkSurface,
            surfaceVariant = DarkSurfaceVariant,

            onBackground = DarkOnBackground,
            onSurface = DarkOnSurface,
            onSurfaceVariant = DarkOnSurfaceVariant,

            outline = DarkOutline,
            outlineVariant = DarkOutline.copy(alpha = 0.5f),

            onPrimary = Color.White
        )
    } else {
        lightColorScheme(
            primary = seasonalPrimary,
            secondary = seasonalPrimary,
            tertiary = seasonalPrimary,

            background = LightBackground,
            surface = LightSurface,
            surfaceVariant = LightSurfaceVariant,

            onBackground = LightOnBackground,
            onSurface = LightOnSurface,
            onSurfaceVariant = LightOnSurfaceVariant,

            outline = LightOutline,
            outlineVariant = LightOutline.copy(alpha = 0.6f),

            onPrimary = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}