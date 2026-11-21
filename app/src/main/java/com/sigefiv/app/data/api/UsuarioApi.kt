package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.CambiarEstadoResponse
import com.sigefiv.app.data.model.CrearUsuarioRequest
import com.sigefiv.app.data.model.UsuariosResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import okhttp3.ResponseBody

interface UsuarioApi {

    @GET("usuarios")
    suspend fun obtenerUsuarios(
        @Query("buscar") buscar: String? = null
    ): Response<UsuariosResponse>

    @POST("usuarios")
    suspend fun crearUsuario(
        @Body datos: CrearUsuarioRequest
    ): Response<CambiarEstadoResponse>

    @PATCH("usuarios/{usuario}/estado")
    suspend fun cambiarEstado(
        @Path("usuario") usuarioId: Int,
        @Body datos: Map<String, String>
    ): Response<CambiarEstadoResponse>

    @PATCH("usuarios/{usuario}/rol")
    suspend fun cambiarRol(
        @Path("usuario") usuarioId: Int,
        @Body datos: Map<String, String>
    ): Response<CambiarEstadoResponse>

    @DELETE("usuarios/{usuario}")
    suspend fun eliminarUsuario(
        @Path("usuario") usuarioId: Int
    ): Response<Unit>

    @POST("usuario/marcar-bienvenida")
    suspend fun marcarBienvenidaVista(): Response<ResponseBody>

    @PUT("mi-cuenta/password")
    suspend fun actualizarPassword(
        @Body datos: Map<String, String>
    ): Response<ResponseBody>
}