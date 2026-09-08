package com.sigefiv.app.data.model

data class PeriodoDetalleResponse(
    val success: Boolean,
    val periodo: PeriodoDetalle?
)

data class PeriodoDetalle(
    val id: Int,
    val nombre: String,
    val nombre_completo: String,
    val anio: Int,
    val mes: Int,
    val estado: String,

    val fecha_cierre: String?,

    val saldo_anterior: Double,
    val total_ingresos: Double,
    val saldo_disponible: Double,
    val total_egresos: Double,
    val saldo_caja: Double,
    val saldo_final: Double,
    val total_movimientos: Int = 0
)