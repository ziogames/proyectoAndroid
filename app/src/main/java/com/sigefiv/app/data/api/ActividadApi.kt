package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.ActividadResponse
import retrofit2.Response
import retrofit2.http.GET

interface ActividadApi {

    @GET("actividad")
    suspend fun obtenerActividades(): Response<ActividadResponse>
}