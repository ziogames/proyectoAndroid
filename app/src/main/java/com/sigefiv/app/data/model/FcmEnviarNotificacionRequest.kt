package com.sigefiv.app.data.model

data class FcmEnviarNotificacionRequest(
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val destinatario: String,
    val usuario_ids: List<Int>? = null
)