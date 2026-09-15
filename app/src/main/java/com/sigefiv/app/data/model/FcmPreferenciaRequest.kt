package com.sigefiv.app.data.model

data class FcmPreferenciaRequest(
    val token: String,
    val activo: Boolean,
    val ingresos: Boolean,
    val egresos: Boolean,
    val zoe: Boolean,
    val avisos: Boolean
)