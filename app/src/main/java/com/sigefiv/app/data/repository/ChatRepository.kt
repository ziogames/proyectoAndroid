package com.sigefiv.app.data.repository

import com.google.gson.Gson
import com.sigefiv.app.data.api.ChatApi
import com.sigefiv.app.data.model.ChatEscribiendoRequest
import com.sigefiv.app.data.model.ChatEnviarResponse
import com.sigefiv.app.data.model.ChatNuevosResponse
import com.sigefiv.app.data.model.ChatPresenciaResponse
import com.sigefiv.app.data.model.ChatReaccionResponse
import com.sigefiv.app.data.model.ChatResponse
import retrofit2.Response
import com.sigefiv.app.data.model.ChatSimpleResponse

// 🚨 Excepción personalizada para transportar los datos de baneo de ZOE al ViewModel
class ZoeException(
    message: String,
    val esBloqueoPermanente: Boolean,
    val detalle: String?
) : Exception(message)

// Modelo para capturar la estructura de error enviada por Laravel/ZOE
data class ZoeErrorResponse(
    val success: Boolean?,
    val message: String?,
    val detalle: String?,
    val es_bloqueo_permanente: Boolean?
)

class ChatRepository(
    private val api: ChatApi
) {

    suspend fun obtenerChat(): Result<ChatResponse> {
        return ejecutar {
            api.obtenerChat()
        }
    }

    suspend fun enviarMensaje(
        mensaje: String,
        replyToId: Int? = null // 💬 Soporte opcional para enviar el ID del mensaje citado
    ): Result<ChatEnviarResponse> {
        return ejecutar {
            val mapa = mutableMapOf("mensaje" to mensaje)

            if (replyToId != null) {
                mapa["reply_to_id"] = replyToId.toString()
            }

            api.enviarMensaje(mapa)
        }
    }

    suspend fun actualizarPresencia(): Result<ChatPresenciaResponse> {
        return ejecutar {
            api.actualizarPresencia()
        }
    }

    suspend fun actualizarEscribiendo(
        escribiendo: Boolean
    ): Result<ChatPresenciaResponse> {
        return ejecutar {
            api.actualizarEscribiendo(
                ChatEscribiendoRequest(
                    escribiendo = escribiendo
                )
            )
        }
    }

    suspend fun obtenerMensajesNuevos(
        afterId: Int
    ): Result<ChatNuevosResponse> {
        return ejecutar {
            api.obtenerMensajesNuevos(
                afterId = afterId
            )
        }
    }

    /**
     * ❤️ Agregar o quitar una reacción de un mensaje.
     *
     * Si el usuario ya tiene ese emoji:
     *      → Laravel elimina la reacción.
     *
     * Si no la tiene:
     *      → Laravel agrega la reacción.
     */
    suspend fun reaccionar(
        chatMessageId: Int,
        emoji: String
    ): Result<ChatReaccionResponse> {
        return ejecutar {
            api.reaccionar(
                chatMessageId = chatMessageId,
                request = mapOf(
                    "emoji" to emoji
                )
            )
        }
    }

    /**
     * ❤️ Obtener las reacciones de un mensaje.
     */
    suspend fun obtenerReacciones(
        chatMessageId: Int
    ): Result<ChatReaccionResponse> {
        return ejecutar {
            api.obtenerReacciones(
                chatMessageId = chatMessageId
            )
        }
    }

    private suspend fun <T> ejecutar(
        llamada: suspend () -> Response<T>
    ): Result<T> {

        return try {

            val response = llamada()

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(
                            "El servidor no devolvió datos."
                        )
                    )
                }

            } else {

                val mensaje =
                    when (response.code()) {

                        401 ->
                            "La sesión ha expirado. Inicia sesión nuevamente."

                        403 ->
                            "No tienes permiso para acceder al Chat Vecinal."

                        404 ->
                            "El Chat Vecinal no fue encontrado."

                        422 -> {

                            // 🚨 CAPTURAMOS EL JSON DE ERROR ENVIADO POR ZOE
                            val errorBody =
                                response.errorBody()?.string()

                            val zoeError = try {

                                if (!errorBody.isNullOrEmpty()) {

                                    Gson().fromJson(
                                        errorBody,
                                        ZoeErrorResponse::class.java
                                    )

                                } else {
                                    null
                                }

                            } catch (e: Exception) {
                                null
                            }

                            val mensajeZoe =
                                zoeError?.message
                                    ?: "Tu mensaje ha sido bloqueado por ZOE."

                            val esBloqueo =
                                zoeError?.es_bloqueo_permanente == true ||
                                        zoeError?.detalle == "bloqueo"

                            // 🚨 Devolvemos la excepción con la bandera real de baneo de Laravel
                            return Result.failure(
                                ZoeException(
                                    mensajeZoe,
                                    esBloqueo,
                                    zoeError?.detalle
                                )
                            )
                        }

                        else ->
                            "Error del servidor (${response.code()})."
                    }

                Result.failure(
                    Exception(mensaje)
                )
            }

        } catch (e: Exception) {

            Result.failure(
                Exception(
                    e.message
                        ?: "No se pudo conectar con el servidor."
                )
            )
        }
    }
    suspend fun marcarLeido(mensajeId: Int): Result<ChatSimpleResponse> {
        return ejecutar {
            api.marcarLeido(
                request = mapOf(
                    "mensaje_id" to mensajeId
                )
            )
        }
    }
}