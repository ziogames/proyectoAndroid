package com.sigefiv.app.data.repository

import android.content.Context
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ZoeConsultaRequest
import com.sigefiv.app.data.model.ZoeConsultaResponse
import com.sigefiv.app.data.model.HistorialResponse
import kotlinx.coroutines.flow.first
import com.sigefiv.app.data.model.DetalleConversacionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import android.util.Log


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

        val nombreUsuario =
            when {
                !seudonimo.isNullOrBlank() -> seudonimo
                !nombre.isNullOrBlank() -> nombre
                else -> "vecino"
            }

        return api.consultarZoeN8n(
            ZoeConsultaRequest(
                usuario_id = usuarioId,
                nombre_usuario = nombreUsuario,
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
}