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

    // 🌱🇵🇪🎄 Detectar temporada automáticamente
    val season = SeasonalTheme.getSeason()

    // Color principal según la temporada
    val seasonalPrimary = SeasonalColors.primary(season)

    val colorScheme = if (darkTheme) {

        darkColorScheme(
            primary = seasonalPrimary,
            secondary = seasonalPrimary,
            tertiary = seasonalPrimary,

            background = Color(0xFF0D1117),
            surface = Color(0xFF161B22),
            surfaceVariant = Color(0xFF21262D),

            onBackground = Color(0xFFE6EDF3),
            onSurface = Color(0xFFE6EDF3),
            onSurfaceVariant = Color(0xFFB0BAC5),

            onPrimary = Color.White
        )

    } else {

        lightColorScheme(
            primary = seasonalPrimary,
            secondary = seasonalPrimary,
            tertiary = seasonalPrimary,

            background = Color(0xFFF8F9FC),
            surface = Color.White,
            surfaceVariant = Color(0xFFF0F2F5),

            onBackground = Color(0xFF1A2333),
            onSurface = Color(0xFF1A2333),
            onSurfaceVariant = Color(0xFF64748B),

            onPrimary = Color.White
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}