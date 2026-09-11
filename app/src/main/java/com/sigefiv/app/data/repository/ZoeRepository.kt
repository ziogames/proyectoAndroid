package com.sigefiv.app.data.repository

import android.content.Context
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ZoeConsultaRequest
import com.sigefiv.app.data.model.ZoeConsultaResponse

class ZoeRepository(
    context: Context
) {

    private val api = ApiClient.create(context)

    suspend fun consultar(
        mensaje: String
    ): ZoeConsultaResponse {

        return api.consultarZoeN8n(
            ZoeConsultaRequest(
                mensaje = mensaje
            )
        )
    }
}