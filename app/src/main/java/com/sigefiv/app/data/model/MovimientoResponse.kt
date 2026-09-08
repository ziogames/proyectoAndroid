package com.sigefiv.app.data.model

data class MovimientoResponse(
    val success: Boolean,
    val message: String,
    val movimiento: Movimiento
)