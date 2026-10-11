package com.sigefiv.app.data.repository

import android.content.Context
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ZoeConsultaRequest
import com.sigefiv.app.data.model.ZoeConsultaResponse
import kotlinx.coroutines.flow.first

class ZoeRepository(
    context: Context
) {

    private val api = ApiClient.create(context)
    private val sessionManager = SessionManager(context)

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
}