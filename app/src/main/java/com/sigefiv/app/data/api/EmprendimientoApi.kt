package com.sigefiv.app.data.api

import com.google.gson.annotations.SerializedName
import com.sigefiv.app.data.model.EmprendimientoDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface EmprendimientoApi {


    @GET("emprendimientos/categorias")
    suspend fun getCategorias(): List<EmprendimientoCategoriaDto>
    @GET("user/emprendimientos")
    suspend fun getMisEmprendimientos(): List<EmprendimientoDto>

    @Multipart
    @POST("emprendimientos")
    suspend fun crearEmprendimiento(
        @PartMap parts: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part fotos: List<MultipartBody.Part>?
    ): Response<EmprendimientoResponseWrapper>

    // 👈 Añade este método para pausar o publicar
    @PUT("emprendimientos/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    ): Response<EmprendimientoResponseWrapper>

    @POST("zoe/generate-description")
    suspend fun generarDescripcionZoe(
        @Body body: Map<String, String>
    ): Response<ZoeDescriptionResponse>
}
data class EmprendimientoCategoriaDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String
)

data class EmprendimientoResponseWrapper(
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: EmprendimientoDto
)

data class ZoeDescriptionResponse(
    @SerializedName("description") val description: String
)
