package com.sigefiv.app.data.api

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
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatApi {

    @GET("chat")
    suspend fun obtenerChat(): Response<ChatResponse>

    @POST("chat")
    suspend fun enviarMensaje(
        @Body request: Map<String, String>
    ): Response<ChatEnviarResponse>

    @Multipart
    @POST("chat")
    suspend fun enviarMensajeConArchivo(
        @Part("mensaje") mensaje: RequestBody?,
        @Part archivo: MultipartBody.Part
    ): Response<ChatEnviarResponse>

    @Multipart
    @POST("chat")
    suspend fun enviarChatVecinalConArchivo(
        @Part archivo: MultipartBody.Part,
        @Part("mensaje") mensaje: RequestBody?,
        @Part("reply_to_id") replyToId: RequestBody?
    ): Response<ChatEnviarResponse>

    /**
     * ✏️ Editar un mensaje propio (límite 15 minutos en el backend).
     */
    @PUT("chat/mensajes/{id}")
    suspend fun editarMensaje(
        @Path("id") mensajeId: Int,
        @Body request: Map<String, String>
    ): Response<ChatMensajeOperacionResponse>

    /**
     * 🗑️ Eliminar un mensaje (Soft Delete estilo WhatsApp o moderación de directiva).
     */
    @DELETE("chat/mensajes/{id}")
    suspend fun eliminarMensaje(
        @Path("id") mensajeId: Int
    ): Response<ChatMensajeOperacionResponse>

    /**
     * 🤖 Invocar directamente al Webhook de ZOE en n8n
     */
    @POST("http://10.0.2.2:5678/webhook/zoe-sigefiv-v2")
    suspend fun invocarWebhookZoe(
        @Body request: Map<String, Any>
    ): Response<Any>

    @POST("chat/presencia")
    suspend fun actualizarPresencia(): Response<ChatPresenciaResponse>

    @POST("chat/escribiendo")
    suspend fun actualizarEscribiendo(
        @Body request: ChatEscribiendoRequest
    ): Response<ChatPresenciaResponse>

    @GET("chat/nuevos")
    suspend fun obtenerMensajesNuevos(
        @Query("after_id") afterId: Int
    ): Response<ChatNuevosResponse>

    @POST("chat/leido")
    suspend fun marcarLeido(
        @Body request: Map<String, Int>
    ): Response<ChatSimpleResponse>

    /**
     * ❤️ Agregar o quitar una reacción.
     * - Si el usuario ya tiene ese emoji -> Laravel lo elimina.
     * - Si no lo tiene -> Laravel lo agrega.
     */
    @POST("chat/{chatMessage}/reaccion")
    suspend fun reaccionar(
        @Path("chatMessage") chatMessageId: Int,
        @Body request: Map<String, String>
    ): Response<ChatReaccionResponse>

    /**
     * ❤️ Obtener las reacciones de un mensaje.
     */
    @GET("chat/{chatMessage}/reacciones")
    suspend fun obtenerReacciones(
        @Path("chatMessage") chatMessageId: Int
    ): Response<ChatReaccionResponse>
}