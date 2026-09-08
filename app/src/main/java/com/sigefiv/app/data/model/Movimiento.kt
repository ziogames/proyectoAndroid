package com.sigefiv.app.data.model

data class Movimiento(
    val id: Int,
    val numero: String,
    val fecha: String?,
    val tipo: String,
    val categoria: String?,
    val concepto: String,
    val persona: String?,
    val forma_pago: String?,
    val monto: Double,
    val comprobante: String?,
    val referencia: String?,
    val observaciones: String?,
    val estado: String
)