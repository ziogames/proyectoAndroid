package com.sigefiv.app.data.model

data class PeriodoMovimientosResponse(
    val success: Boolean,
    val periodo: PeriodoResumen?,
    val movimientos: List<Movimiento>
)

data class PeriodoResumen(
    val id: Int,
    val nombre: String,
    val nombre_completo: String,
    val anio: Int,
    val mes: Int,
    val estado: String
)