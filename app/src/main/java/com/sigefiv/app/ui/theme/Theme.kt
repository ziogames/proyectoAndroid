package com.sigefiv.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun SIGEFIVTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
            tertiary = seasonalPrimary
        )

    } else {

        lightColorScheme(
            primary = seasonalPrimary,
            secondary = seasonalPrimary,
            tertiary = seasonalPrimary
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}