package com.sigefiv.app.data.model

data class MovimientoRequest(
    val fecha: String,
    val categoria_id: Int,
    val concepto: String,
    val persona: String? = null,
    val forma_pago: String,
    val monto: Double,
    val referencia: String? = null,
    val observaciones: String? = null
)