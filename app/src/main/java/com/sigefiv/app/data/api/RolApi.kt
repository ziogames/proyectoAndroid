
package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.ActualizarRolRequest
import com.sigefiv.app.data.model.BaseResponse
import com.sigefiv.app.data.model.CrearRolRequest
import com.sigefiv.app.data.model.PermisosResponse
import com.sigefiv.app.data.model.RolDetalleResponse
import com.sigefiv.app.data.model.RolesResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RolApi {

    @GET("roles")
    suspend fun obtenerRoles(): RolesResponse

    @GET("roles/permisos")
    suspend fun obtenerPermisos(): PermisosResponse

    @GET("roles/{id}")
    suspend fun obtenerRolDetalle(
        @Path("id") id: Int
    ): RolDetalleResponse

    @POST("roles")
    suspend fun crearRol(
        @Body request: CrearRolRequest
    ): BaseResponse

    @PUT("roles/{id}")
    suspend fun actualizarRol(
        @Path("id") id: Int,
        @Body request: ActualizarRolRequest
    ): BaseResponse

    @DELETE("roles/{id}")
    suspend fun eliminarRol(
        @Path("id") id: Int
    ): BaseResponse
}
