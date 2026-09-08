package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.MovimientoRequest
import com.sigefiv.app.data.model.MovimientoResponse
import com.sigefiv.app.data.model.MovimientosResponse

class MovimientosRepository(
    private val authApi: AuthApi
) {

    suspend fun obtenerMovimientos(
        limite: Int? = null
    ): MovimientosResponse {
        return authApi.movimientos(limite)
    }

    suspend fun crearMovimiento(
        request: MovimientoRequest
    ): MovimientoResponse {
        return authApi.crearMovimiento(request)
    }
}

