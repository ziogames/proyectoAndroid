package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.MovimientoRequest
import com.sigefiv.app.data.model.MovimientoResponse
import com.sigefiv.app.data.model.MovimientosResponse
import retrofit2.Response

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

    suspend fun actualizarMovimiento(
        id: Int,
        request: MovimientoRequest
    ): MovimientoResponse {
        return authApi.actualizarMovimiento(id, request)
    }

    suspend fun eliminarMovimiento(
        id: Int
    ): Response<Unit> {
        return authApi.eliminarMovimiento(id)
    }
}
