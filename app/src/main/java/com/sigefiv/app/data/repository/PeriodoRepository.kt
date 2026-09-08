package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.PeriodoResponse

class PeriodoRepository(
    private val authApi: AuthApi
) {

    suspend fun obtenerPeriodoAbierto(): PeriodoResponse {
        return authApi.periodoAbierto()
    }
}