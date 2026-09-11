package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.FcmEnviarNotificacionRequest
import com.sigefiv.app.data.model.Notificacion
import com.sigefiv.app.data.model.NotificacionEnviarResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificacionRepository(
    private val authApi: AuthApi
) {

    /**
     * Obtiene todas las notificaciones del usuario.
     */
    suspend fun obtenerNotificaciones(): Result<List<Notificacion>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.notificaciones()

                if (response.success) {
                    Result.success(response.data)
                } else {
                    Result.failure(
                        Exception("No se pudieron obtener las notificaciones")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Obtiene solamente las notificaciones no leídas.
     */
    suspend fun obtenerNoLeidas(): Result<List<Notificacion>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.notificacionesNoLeidas()

                if (response.success) {
                    Result.success(response.data)
                } else {
                    Result.failure(
                        Exception("No se pudieron obtener las notificaciones no leídas")
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Marca una notificación como leída.
     */
    suspend fun marcarComoLeida(
        id: Int
    ): Result<Notificacion?> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.marcarNotificacionLeida(id)

                if (response.success) {
                    Result.success(response.data)
                } else {
                    Result.failure(
                        Exception(
                            response.message
                                ?: "No se pudo marcar la notificación como leída"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Marca todas las notificaciones como leídas.
     */
    suspend fun marcarTodasComoLeidas(): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val response = authApi.marcarTodasNotificacionesLeidas()

                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    Result.failure(
                        Exception(
                            "Error al marcar las notificaciones como leídas: " +
                                    response.code()
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // ============================================================
    // ENVIAR NOTIFICACIÓN
    // ============================================================

    /**
     * Envía una notificación a todos los vecinos
     * o a un usuario específico.
     */
    suspend fun enviarNotificacion(
        titulo: String,
        mensaje: String,
        tipo: String,
        destinatario: String,
        usuarioIds: List<Int>? = null
    ): Result<NotificacionEnviarResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val request =
                    FcmEnviarNotificacionRequest(
                        titulo = titulo,
                        mensaje = mensaje,
                        tipo = tipo,
                        destinatario = destinatario,
                        usuario_ids = usuarioIds
                    )

                val response =
                    authApi.enviarNotificacion(request)

                if (response.success) {
                    Result.success(response)
                } else {
                    Result.failure(
                        Exception(
                            response.message
                                ?: "No se pudo enviar la notificación"
                        )
                    )
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}