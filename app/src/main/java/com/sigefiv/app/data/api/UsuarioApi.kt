package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.CambiarEstadoResponse
import com.sigefiv.app.data.model.UsuariosResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UsuarioApi {

    @GET("usuarios")
    suspend fun obtenerUsuarios(
        @Query("buscar") buscar: String? = null
    ): Response<UsuariosResponse>

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
    suspend fun marcarBienvenidaVista(): Response<Map<String, Any>>
}