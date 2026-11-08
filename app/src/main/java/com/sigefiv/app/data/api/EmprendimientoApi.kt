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

    // ✏️ NUEVO: Método para actualizar el emprendimiento (envía los campos y nuevas fotos)
    @Multipart
    @POST("emprendimientos/{id}") // Usamos POST con _method = PUT o PUT directo según tu backend
    suspend fun actualizarEmprendimiento(
        @Path("id") id: Long,
        @PartMap parts: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part fotos: List<MultipartBody.Part>?
    ): Response<EmprendimientoResponseWrapper>

    // Método para pausar o publicar (cambio rápido de estado)
    @PUT("emprendimientos/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Long,
        @Body body: Map<String, String>
    ): Response<EmprendimientoResponseWrapper>

    @DELETE("emprendimientos/{id}")
    suspend fun eliminarEmprendimiento(
        @Path("id") id: Long
    ): Response<Unit>

    @POST("zoe/generate-description")
    suspend fun generarDescripcionZoe(
        @Body body: Map<String, String>
    ): Response<ZoeDescriptionResponse>

    // 🌐 Ruta pública corregida para apuntar al endpoint que creamos en Laravel
    @GET("emprendimientos/publicos")
    suspend fun getEmprendimientosPublicos(
        @Query("categoria") categoria: String? = null
    ): EmprendimientosPublicosResponse

    @GET("emprendimientos/favoritos")
    suspend fun getMisFavoritos(): EmprendimientosPublicosResponse

    @POST("emprendimientos/{id}/favorito")
    suspend fun toggleFavorito(
        @Path("id") id: Int
    ): Response<FavoritoResponseWrapper>

    // ====================================================================
    // ⭐ NUEVOS MÉTODOS PARA OPINIONES Y CALIFICACIONES
    // ====================================================================
    @GET("emprendimientos/{id}/opiniones")
    suspend fun getOpiniones(
        @Path("id") emprendimientoId: Int
    ): Response<OpinionesDataResponse>

    @POST("emprendimientos/{id}/opiniones")
    suspend fun enviarOpinion(
        @Path("id") emprendimientoId: Int,
        @Body request: OpinionRequest
    ): Response<Any>
}

// 📦 Clase contenedora para mapear la respuesta JSON del directorio público
data class EmprendimientosPublicosResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("emprendimientos") val emprendimientos: List<EmprendimientoDto>
)

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

data class FavoritoResponseWrapper(
    @SerializedName("success") val success: Boolean,
    @SerializedName("es_favorito") val esFavorito: Boolean,
    @SerializedName("message") val message: String
)

// ========================================================================
// 📊 CLASES DE DATOS PARA OPINIONES
// ========================================================================
data class OpinionRequest(
    @SerializedName("calificacion") val calificacion: Int,
    @SerializedName("comentario") val comentario: String?
)

data class OpinionesDataResponse(
    @SerializedName("promedio") val promedio: Float,
    @SerializedName("total") val total: Int,
    @SerializedName("opiniones") val opiniones: List<OpinionResponseModel>,
    @SerializedName("user_opinion") val userOpinion: OpinionResponseModel? // 👈 Nuevo campo
)

data class OpinionResponseModel(
    @SerializedName("id") val id: Long,
    @SerializedName("user") val user: UsuarioSimpleModel?,
    @SerializedName("calificacion") val calificacion: Int,
    @SerializedName("comentario") val comentario: String?,
    @SerializedName("created_at") val createdAt: String?
)

data class UsuarioSimpleModel(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String
)