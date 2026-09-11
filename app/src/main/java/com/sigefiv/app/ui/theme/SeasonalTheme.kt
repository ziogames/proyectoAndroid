package com.sigefiv.app.ui.theme

import java.util.Calendar

enum class AppSeason {
    NORMAL,
    FIESTAS_PATRIAS,
    NAVIDAD
}

object SeasonalTheme {

    fun getSeason(): AppSeason {

        val calendar = Calendar.getInstance()

        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // 🇵🇪 Fiestas Patrias
        // Del 15 al 31 de julio
        if (month == 7 && day in 15..31) {
            return AppSeason.FIESTAS_PATRIAS
        }

        // 🎄 Navidad
        // Del 1 de diciembre al 6 de enero
        if (month == 12 || (month == 1 && day in 1..6)) {
            return AppSeason.NAVIDAD
        }

        return AppSeason.NORMAL
    }
}