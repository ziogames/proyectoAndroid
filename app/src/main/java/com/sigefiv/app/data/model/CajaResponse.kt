package com.sigefiv.app.data.model

data class CajaResponse(
    val success: Boolean,
    val message: String,
    val anio: Int,
    val anios: List<Int>,
    val resumen: CajaResumen,
    val consolidado: List<CajaMes>
)

data class CajaResumen(
    val saldo_inicial: Double,
    val ingresos: Double,
    val egresos: Double,
    val saldo_final: Double,
    val porcentaje_ingresos: Double,
    val porcentaje_egresos: Double
)

data class CajaMes(
    val mes: Int,
    val nombre_mes: String,
    val saldo_inicial: Double,
    val ingresos: Double,
    val egresos: Double,
    val saldo_final: Double
)