package com.sigefiv.app.data.model

data class NotificacionResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Notificacion? = null
)