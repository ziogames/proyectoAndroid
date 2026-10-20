package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.HistorialResponse
import com.sigefiv.app.data.model.DetalleConversacionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Part
// Si decides usar retrofit2.Response, asegúrate de tener este import:
// import retrofit2.Response

interface ZoeApi {
    @GET("zoe/historial")
    suspend fun obtenerHistorialZoe(): HistorialResponse

    @GET("zoe/historial/{id}")
    suspend fun obtenerDetalleConversacion(@Path("id") conversacionId: String): DetalleConversacionResponse

    // 👈 AÑADE ESTO EXACTAMENTE AQUÍ:
    @Multipart
    @POST("zoe/mensaje-con-adjunto")
    suspend fun enviarMensajeConArchivo(
        @Part archivo: MultipartBody.Part?,
        @Part("mensaje") mensaje: RequestBody?
    ): HistorialResponse
}