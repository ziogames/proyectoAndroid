package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.CajaResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CajaApi {

    @GET("caja")
    suspend fun obtenerCaja(
        @Query("anio") anio: Int? = null
    ): Response<CajaResponse>
}