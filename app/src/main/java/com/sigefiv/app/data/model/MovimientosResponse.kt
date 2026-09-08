package com.sigefiv.app.data.model

data class MovimientosResponse(
    val success: Boolean,
    val movimientos: List<Movimiento>
)