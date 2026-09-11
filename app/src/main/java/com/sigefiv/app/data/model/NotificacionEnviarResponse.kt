package com.sigefiv.app.data.model

data class NotificacionEnviarResponse(
    val success: Boolean,
    val message: String? = null,
    val data: NotificacionEnviarData? = null
)

data class NotificacionEnviarData(
    val destinatario: String? = null,
    val usuario_id: Int? = null,
    val enviados: Int = 0
)