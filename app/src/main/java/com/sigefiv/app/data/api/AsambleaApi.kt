package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.AsambleaDetalleResponse
import com.sigefiv.app.data.model.AsambleaResponse
import com.sigefiv.app.data.model.CrearAsambleaRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import com.sigefiv.app.data.model.AvisoSinAsambleaResponse

interface AsambleaApi {

    @GET("asambleas")
    suspend fun obtenerAsambleas(): Response<AsambleaResponse>

    @GET("asambleas/{id}")
    suspend fun obtenerAsamblea(
        @Path("id") id: Int
    ): Response<AsambleaDetalleResponse>

    // Consulta pública de una asamblea publicada
    @GET("asambleas/convocatoria/{id}")
    suspend fun obtenerAsambleaPublica(
        @Path("id") id: Int
    ): Response<AsambleaDetalleResponse>

    @GET("asambleas/convocatoria/proxima")
    suspend fun obtenerProximaAsamblea(): Response<AsambleaDetalleResponse>

    @GET("asambleas/aviso-sin-asamblea")
    suspend fun obtenerAvisoSinAsamblea(): Response<AvisoSinAsambleaResponse>

    @POST("asambleas")
    suspend fun crearAsamblea(
        @Body request: CrearAsambleaRequest
    ): Response<AsambleaDetalleResponse>

    @PUT("asambleas/{id}")
    suspend fun actualizarAsamblea(
        @Path("id") id: Int,
        @Body request: CrearAsambleaRequest
    ): Response<AsambleaDetalleResponse>

    @DELETE("asambleas/{id}")
    suspend fun eliminarAsamblea(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("asambleas/{id}/publicar")
    suspend fun publicarAsamblea(
        @Path("id") id: Int
    ): Response<AsambleaDetalleResponse>
}