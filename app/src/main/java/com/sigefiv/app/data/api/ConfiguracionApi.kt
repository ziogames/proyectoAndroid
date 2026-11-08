package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.Configuracion
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface ConfiguracionApi {
    @GET("configuracion")
    suspend fun obtenerConfiguracion(): Response<ConfiguracionResponse>

    // Cambiado a @PUT para coincidir exactamente con Route::put(...)
    @PUT("configuracion")
    suspend fun guardarConfiguracion(@Body configuracion: Configuracion): Response<ConfiguracionResponse>
}

data class ConfiguracionResponse(
    val success: Boolean,
    val message: String? = null,
    val data: Configuracion? = null
)