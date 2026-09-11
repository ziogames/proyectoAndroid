package com.sigefiv.app.ui.theme

import androidx.compose.ui.graphics.Color

object SeasonalColors {

    // 🌱 Tema normal
    val VerdeSIGEFIV = Color(0xFF188544)
    val VerdeSIGEFIVDark = Color(0xFF126B35)

    // 🇵🇪 Fiestas Patrias
    val RojoPatrio = Color(0xFFD91023)
    val RojoPatrioDark = Color(0xFFB71C1C)

    // 🎄 Navidad
    val VerdeNavidad = Color(0xFF146B3A)
    val VerdeNavidadDark = Color(0xFF0B4F2A)

    /**
     * Color principal de la aplicación
     */
    fun primary(season: AppSeason): Color {

        return when (season) {

            AppSeason.NORMAL ->
                VerdeSIGEFIV

            AppSeason.FIESTAS_PATRIAS ->
                RojoPatrio

            AppSeason.NAVIDAD ->
                VerdeNavidad
        }
    }

    /**
     * Color utilizado para Snackbar
     */
    fun snackbar(season: AppSeason): Color {

        return when (season) {

            AppSeason.NORMAL ->
                VerdeSIGEFIVDark

            AppSeason.FIESTAS_PATRIAS ->
                RojoPatrioDark

            AppSeason.NAVIDAD ->
                VerdeNavidadDark
        }
    }
}