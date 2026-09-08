package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.FcmTokenRequest
import com.sigefiv.app.data.model.FcmTokenResponse

class FcmTokenRepository(
    private val authApi: AuthApi
) {

    /**
     * Registra o actualiza el token FCM del dispositivo
     * en el backend de SIGEFIV.
     */
    suspend fun registrarToken(
        token: String
    ): Result<FcmTokenResponse> {

        return try {

            if (token.isBlank()) {
                return Result.failure(
                    IllegalArgumentException(
                        "El token FCM no puede estar vacío."
                    )
                )
            }

            val respuesta =
                authApi.registrarTokenFcm(
                    FcmTokenRequest(
                        token = token,
                        plataforma = "android"
                    )
                )

            if (respuesta.success) {

                Result.success(respuesta)

            } else {

                Result.failure(
                    Exception(
                        respuesta.message
                            ?: "El servidor no pudo registrar el token FCM."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    /**
     * Desactiva un token FCM del usuario autenticado.
     */
    suspend fun desactivarToken(
        token: String
    ): Result<FcmTokenResponse> {

        return try {

            if (token.isBlank()) {
                return Result.failure(
                    IllegalArgumentException(
                        "El token FCM no puede estar vacío."
                    )
                )
            }

            val respuesta =
                authApi.desactivarTokenFcm(
                    FcmTokenRequest(
                        token = token,
                        plataforma = "android"
                    )
                )

            if (respuesta.success) {

                Result.success(respuesta)

            } else {

                Result.failure(
                    Exception(
                        respuesta.message
                            ?: "El servidor no pudo desactivar el token FCM."
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}