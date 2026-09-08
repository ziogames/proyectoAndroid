package com.sigefiv.app.data.model

data class DashboardResponse(
    val success: Boolean,
    val periodo: PeriodoDashboard?
)

data class PeriodoDashboard(
    val nombre: String,
    val anio: Int,
    val mes: Int,
    val estado: String,
    val saldo_inicial: Double,
    val ingresos: Double,
    val egresos: Double,
    val saldo_final: Double
)