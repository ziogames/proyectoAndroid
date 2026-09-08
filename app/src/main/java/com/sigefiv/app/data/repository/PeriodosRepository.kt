package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.PeriodoDetalleResponse
import com.sigefiv.app.data.model.PeriodoMovimientosResponse
import com.sigefiv.app.data.model.PeriodosResponse

class PeriodosRepository(
    private val authApi: AuthApi
) {

    suspend fun obtenerPeriodos(): PeriodosResponse {
        return authApi.periodos()
    }

    suspend fun obtenerPeriodo(
        id: Int
    ): PeriodoDetalleResponse {
        return authApi.periodoDetalle(id)
    }

    suspend fun obtenerMovimientosPeriodo(
        id: Int
    ): PeriodoMovimientosResponse {
        return authApi.periodoMovimientos(id)
    }
}