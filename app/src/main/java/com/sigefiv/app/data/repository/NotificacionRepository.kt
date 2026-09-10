package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.Notificacion
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
}