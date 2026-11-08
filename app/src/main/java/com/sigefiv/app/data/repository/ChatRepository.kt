package com.sigefiv.app.data.repository

import com.google.gson.Gson
import com.sigefiv.app.data.api.ChatApi
import com.sigefiv.app.data.model.ChatEscribiendoRequest
import com.sigefiv.app.data.model.ChatEnviarResponse
import com.sigefiv.app.data.model.ChatMensajeOperacionResponse
import com.sigefiv.app.data.model.ChatNuevosResponse
import com.sigefiv.app.data.model.ChatPresenciaResponse
import com.sigefiv.app.data.model.ChatReaccionResponse
import com.sigefiv.app.data.model.ChatResponse
import com.sigefiv.app.data.model.ChatSimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

// 🚨 Excepción personalizada para transportar advertencias y bloqueos de ZOE al ViewModel
class ZoeException(
    message: String,
    val esBloqueoPermanente: Boolean,
    val detalle: String?,
    val strikes: Int = 0
) : Exception(message)

// Modelo para capturar la estructura de error enviada por Laravel/ZOE
data class ZoeErrorResponse(
    val success: Boolean?,
    val message: String?,
    val detalle: String?,
    val strikes: Int?,
    val bloqueado: Boolean?,
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
        replyToId: Int? = null
    ): Result<ChatEnviarResponse> {
        return ejecutar {
            val mapa = mutableMapOf("mensaje" to mensaje)

            if (replyToId != null) {
                mapa["reply_to_id"] = replyToId.toString()
            }

            api.enviarMensaje(mapa)
        }
    }

    /**
     * ✏️ Editar un mensaje propio (límite 15 minutos en el backend).
     */
    suspend fun editarMensaje(
        mensajeId: Int,
        nuevoTexto: String
    ): Result<ChatMensajeOperacionResponse> {
        return ejecutar {
            api.editarMensaje(
                mensajeId = mensajeId,
                request = mapOf("mensaje" to nuevoTexto)
            )
        }
    }

    /**
     * 🗑️ Eliminar un mensaje (Soft Delete estilo WhatsApp o moderación de directiva).
     */
    suspend fun eliminarMensaje(
        mensajeId: Int
    ): Result<ChatMensajeOperacionResponse> {
        return ejecutar {
            api.eliminarMensaje(
                mensajeId = mensajeId
            )
        }
    }

    /**
     * 📎 Enviar archivo multimedia (foto o video) exclusivo para el Chat Vecinal
     */
    suspend fun enviarChatVecinalConArchivo(
        archivo: MultipartBody.Part,
        mensaje: RequestBody?,
        replyToId: RequestBody?
    ): Result<ChatEnviarResponse> {
        return ejecutar {
            api.enviarChatVecinalConArchivo(archivo, mensaje, replyToId)
        }
    }

    /**
     * 🤖 Invocar directamente al Webhook de ZOE en n8n cuando se menciona a @Zoe en el chat vecinal
     */
    suspend fun consultarWebhookZoe(usuarioId: Int, mensaje: String): Result<Unit> {
        return try {
            val payload = mapOf(
                "usuario_id" to usuarioId,
                "mensaje" to mensaje,
                "origen" to "chat_vecinal"
            )

            val response = api.invocarWebhookZoe(payload)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al conectar con ZOE (${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "No se pudo invocar el webhook de ZOE"))
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

    suspend fun marcarLeido(mensajeId: Int): Result<ChatSimpleResponse> {
        return ejecutar {
            api.marcarLeido(
                request = mapOf(
                    "mensaje_id" to mensajeId
                )
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
                    Result.failure(Exception("El servidor no devolvió datos."))
                }
            } else {
                // 🚨 Parsear respuesta JSON de error (ZOE moderación / bloqueo)
                val errorBody = response.errorBody()?.string()
                val zoeError = try {
                    if (!errorBody.isNullOrEmpty()) {
                        Gson().fromJson(errorBody, ZoeErrorResponse::class.java)
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }

                val mensajeServidor = zoeError?.message

                when (response.code()) {
                    401 -> Result.failure(Exception("La sesión ha expirado. Inicia sesión nuevamente."))

                    // 🚨 403: Puede ser falta de permisos o la 3.ª FALTA (BLOQUEO PERMANENTE)
                    403 -> {
                        val esBloqueo = zoeError?.es_bloqueo_permanente == true ||
                                zoeError?.bloqueado == true ||
                                zoeError?.detalle == "bloqueo"

                        if (esBloqueo) {
                            Result.failure(
                                ZoeException(
                                    message = mensajeServidor ?: "Tu cuenta ha sido bloqueada por infringir las normas de convivencia.",
                                    esBloqueoPermanente = true,
                                    detalle = zoeError?.detalle ?: "bloqueo",
                                    strikes = zoeError?.strikes ?: 3
                                )
                            )
                        } else {
                            Result.failure(Exception(mensajeServidor ?: "No tienes permiso para acceder al Chat Vecinal."))
                        }
                    }

                    404 -> Result.failure(Exception("El recurso solicitado no fue encontrado."))

                    // ⚠️ 422: ADVERTENCIAS 1 Y 2 DE ZOE O VALIDACIÓN
                    422 -> {
                        if (zoeError != null && (!zoeError.detalle.isNullEmptyOrBlank() || zoeError.strikes != null)) {
                            val esBloqueo = zoeError.es_bloqueo_permanente == true || zoeError.detalle == "bloqueo"

                            Result.failure(
                                ZoeException(
                                    message = mensajeServidor ?: "Tu mensaje ha sido bloqueado por ZOE.",
                                    esBloqueoPermanente = esBloqueo,
                                    detalle = zoeError.detalle,
                                    strikes = zoeError.strikes ?: 0
                                )
                            )
                        } else {
                            Result.failure(Exception(mensajeServidor ?: "Los datos enviados no son válidos."))
                        }
                    }

                    else -> Result.failure(Exception(mensajeServidor ?: "Error del servidor (${response.code()})."))
                }
            }
        } catch (e: ZoeException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: "No se pudo conectar con el servidor."))
        }
    }

    private fun String?.isNullEmptyOrBlank(): Boolean {
        return this.isNullOrEmpty() || this.isBlank()
    }
}