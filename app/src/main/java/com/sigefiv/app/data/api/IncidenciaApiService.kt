package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.ActualizarEstadoRequest
import com.sigefiv.app.data.model.IncidenciasResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface IncidenciaApiService {

    // 1. Listado general para la directiva (/zoe-incidencias)
    @GET("zoe-incidencias")
    suspend fun obtenerIncidencias(
        @Query("user_id") userId: Long? = null
    ): Response<IncidenciasResponse>

    // 2. Listado exclusivo para "Mis Reportes" del vecino (/mis-reportes)
    @GET("mis-reportes")
    suspend fun obtenerMisReportes(): Response<IncidenciasResponse>

    // 3. Crear incidencia con soporte para archivos multimedia (Multipart)
    @Multipart
    @POST("zoe-incidencias")
    suspend fun crearIncidencia(
        @Part("titulo") titulo: RequestBody,
        @Part("categoria") categoria: RequestBody,
        @Part("mensaje") mensaje: RequestBody,
        @Part imagen: MultipartBody.Part?
    ): Response<Any>

    // 4. Actualizar estado (Directiva)
    @PATCH("zoe-incidencias/{id}/estado")
    suspend fun actualizarEstadoIncidencia(
        @Path("id") id: Long,
        @Body request: ActualizarEstadoRequest
    ): Response<Any>
}