package com.sigefiv.app.data.model

data class FcmPreferenciaResponse(
    val success: Boolean,
    val message: String,
    val data: FcmPreferenciaData?
)

data class FcmPreferenciaData(
    val activo: Boolean,
    val ingresos: Boolean,
    val egresos: Boolean,
    val zoe: Boolean,
    val avisos: Boolean,
    val plataforma: String
)