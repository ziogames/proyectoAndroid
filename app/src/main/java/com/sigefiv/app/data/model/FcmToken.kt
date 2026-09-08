package com.sigefiv.app.data.model

/**
 * Solicitud para registrar o desactivar
 * un token de Firebase Cloud Messaging.
 */
data class FcmTokenRequest(
    val token: String,
    val plataforma: String = "android"
)

/**
 * Respuesta del backend al registrar
 * o desactivar un token FCM.
 */
data class FcmTokenResponse(
    val success: Boolean,
    val message: String? = null,
    val fcm_token: FcmTokenInfo? = null
)

/**
 * Información del token FCM registrada
 * por el backend.
 */
data class FcmTokenInfo(
    val id: Int,
    val plataforma: String? = null,
    val activo: Boolean = false,
    val ultimo_acceso: String? = null
)