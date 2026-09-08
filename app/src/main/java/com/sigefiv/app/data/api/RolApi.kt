package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.ActualizarRolRequest
import com.sigefiv.app.data.model.BaseResponse
import com.sigefiv.app.data.model.RolDetalleResponse
import com.sigefiv.app.data.model.RolesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface RolApi {
    @GET("roles")
    suspend fun obtenerRoles(): RolesResponse

    @GET("roles/{id}")
    suspend fun obtenerRolDetalle(@Path("id") id: Int): RolDetalleResponse

    @PUT("roles/{id}")
    suspend fun actualizarRol(
        @Path("id") id: Int,
        @Body request: ActualizarRolRequest
    ): BaseResponse
}