package com.sigefiv.app.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response
import com.sigefiv.app.data.model.BitacoraResponse

interface ApiService {
    @GET("bitacora") // 👈 Cambiado de "api/bitacora" a "bitacora"
    suspend fun obtenerBitacora(
        @Query("buscar") buscar: String? = null,
        @Query("usuario") usuario: Long? = null,
        @Query("modulo") modulo: String? = null,
        @Query("desde") desde: String? = null,
        @Query("hasta") hasta: String? = null
    ): Response<BitacoraResponse>
}