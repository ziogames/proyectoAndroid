package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.HistorialResponse
import com.sigefiv.app.data.model.DetalleConversacionResponse
import com.sigefiv.app.screens.chat.model.AsambleaEstadisticasDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Part

// Modelos para la petición y respuesta de confirmación de asamblea
data class ConfirmarAsistenciaRequest(
    val asamblea_id: Long,
    val opcion: String // "asistira", "no_asistira", "en_duda"
)

data class ConfirmarAsistenciaResponse(
    val status: String,
    val mensaje_zoe: String,
    val estadisticas: AsambleaEstadisticasDto
)

data class SimpleSuccessResponse(
    val success: Boolean,
    val tiene_asamblea: Boolean = false,
    val asamblea_id: Long? = null,
    val texto: String? = null
)

data class EstadisticasAsambleaActivaDto(
    val tiene_asamblea: Boolean,
    val asamblea_id: Long? = null,
    val titulo: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val lugar: String? = null,
    val mi_voto: String? = null,
    val estadisticas: AsambleaEstadisticasDto? = null
)

interface ZoeApi {
    @GET("zoe/historial")
    suspend fun obtenerHistorialZoe(): HistorialResponse

    @GET("zoe/historial/{id}")
    suspend fun obtenerDetalleConversacion(@Path("id") conversacionId: String): DetalleConversacionResponse

    @Multipart
    @POST("zoe/mensaje-con-adjunto")
    suspend fun enviarMensajeConArchivo(
        @Part archivo: MultipartBody.Part?,
        @Part("mensaje") mensaje: RequestBody?
    ): HistorialResponse

    // 1. Dispara la revisión de recordatorios pendientes al entrar al chat
    @POST("zoe/asambleas/verificar-pendientes")
    suspend fun verificarAvisosPendientes(): Response<SimpleSuccessResponse>

    // 2. Registra la opción elegida (asistira, no_asistira, en_duda)
    @POST("zoe/asambleas/confirmar")
    suspend fun confirmarAsistencia(
        @Body request: ConfirmarAsistenciaRequest
    ): Response<ConfirmarAsistenciaResponse>

    // 3. Obtiene el reporte y estadísticas de la asamblea activa para el chip y bottom sheet
    @GET("zoe/asambleas/estadisticas-activa")
    suspend fun obtenerEstadisticasAsambleaActiva(): Response<EstadisticasAsambleaActivaDto>
}