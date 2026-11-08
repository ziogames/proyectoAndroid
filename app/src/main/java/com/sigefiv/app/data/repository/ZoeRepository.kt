package com.sigefiv.app.data.repository

import android.content.Context
import android.util.Log
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.api.ConfirmarAsistenciaRequest
import com.sigefiv.app.data.api.ConfirmarAsistenciaResponse
import com.sigefiv.app.data.api.EstadisticasAsambleaActivaDto // 👈 Importación agregada
import com.sigefiv.app.data.api.SimpleSuccessResponse
import com.sigefiv.app.data.model.DetalleConversacionResponse
import com.sigefiv.app.data.model.HistorialResponse
import com.sigefiv.app.data.model.ZoeConsultaRequest
import com.sigefiv.app.data.model.ZoeConsultaResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class ZoeRepository(
    context: Context
) {

    private val api = ApiClient.create(context)
    private val sessionManager = SessionManager(context)
    private val zoeApi = ApiClient.zoeApi(context)

    suspend fun consultar(
        mensaje: String
    ): ZoeConsultaResponse {

        val usuarioId = sessionManager.userId.first()
            ?: throw IllegalStateException("No hay usuario autenticado")

        val seudonimo = sessionManager.seudonimo.first()
        val nombre = sessionManager.nombre.first()

        return api.consultarZoeN8n(
            ZoeConsultaRequest(
                usuario_id = usuarioId,
                seudonimo = seudonimo?.takeIf { it.isNotBlank() },
                nombre_usuario = nombre ?: "vecino",
                mensaje = mensaje
            )
        )
    }

    /*
    |--------------------------------------------------------------------------
    | OBTENER HISTORIAL DE CONVERSACIONES DE ZOE
    |--------------------------------------------------------------------------
    */
    suspend fun obtenerHistorial(): HistorialResponse {
        return zoeApi.obtenerHistorialZoe()
    }

    suspend fun obtenerDetalle(conversacionId: String): DetalleConversacionResponse {
        return zoeApi.obtenerDetalleConversacion(conversacionId)
    }

    suspend fun enviarMensajeConArchivo(
        archivo: MultipartBody.Part?,
        texto: RequestBody?
    ): HistorialResponse? {
        return withContext(Dispatchers.IO) {
            try {
                api.enviarMensajeConArchivo(archivo, texto)
            } catch (e: Exception) {
                Log.e("ZOE_REPO", "Error en enviarMensajeConArchivo: ${e.message}", e)
                null
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | ASAMBLEAS VECINALES (NOTIFICACIONES Y RESPUESTAS)
    |--------------------------------------------------------------------------
    */
    suspend fun verificarAvisosPendientes(): Response<SimpleSuccessResponse> {
        return withContext(Dispatchers.IO) {
            zoeApi.verificarAvisosPendientes()
        }
    }

    suspend fun confirmarAsistencia(
        asambleaId: Long,
        opcion: String
    ): Response<ConfirmarAsistenciaResponse> {
        return withContext(Dispatchers.IO) {
            zoeApi.confirmarAsistencia(
                ConfirmarAsistenciaRequest(asamblea_id = asambleaId, opcion = opcion)
            )
        }
    }

    suspend fun obtenerEstadisticasAsambleaActiva(): Response<EstadisticasAsambleaActivaDto> {
        return withContext(Dispatchers.IO) {
            zoeApi.obtenerEstadisticasAsambleaActiva() // 👈 Usa zoeApi con Dispatchers.IO
        }
    }
}