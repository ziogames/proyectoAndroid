package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.CategoriasResponse
import com.sigefiv.app.data.model.DashboardResponse
import com.sigefiv.app.data.model.FcmTokenRequest
import com.sigefiv.app.data.model.FcmTokenResponse
import com.sigefiv.app.data.model.GoogleLoginRequest
import com.sigefiv.app.data.model.LoginRequest
import com.sigefiv.app.data.model.LoginResponse
import com.sigefiv.app.data.model.MovimientoRequest
import com.sigefiv.app.data.model.MovimientoResponse
import com.sigefiv.app.data.model.MovimientosResponse
import com.sigefiv.app.data.model.PeriodoDetalleResponse
import com.sigefiv.app.data.model.PeriodoMovimientosResponse
import com.sigefiv.app.data.model.PeriodoResponse
import com.sigefiv.app.data.model.PeriodosResponse
import com.sigefiv.app.data.model.ZoeConsultaRequest
import com.sigefiv.app.data.model.ZoeConsultaResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApi {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @POST("auth/google")
    suspend fun loginConGoogle(
        @Body request: GoogleLoginRequest
    ): LoginResponse

    @GET("user")
    suspend fun user(): LoginResponse

    @POST("logout")
    suspend fun logout()

    @GET("dashboard")
    suspend fun dashboard(): DashboardResponse

    @GET("movimientos")
    suspend fun movimientos(
        @Query("limite") limite: Int? = null
    ): MovimientosResponse

    @POST("movimientos")
    suspend fun crearMovimiento(
        @Body request: MovimientoRequest
    ): MovimientoResponse

    @GET("categorias")
    suspend fun categorias(): CategoriasResponse

    @GET("periodo/abierto")
    suspend fun periodoAbierto(): PeriodoResponse

    @GET("periodos")
    suspend fun periodos(): PeriodosResponse

    @GET("periodos/{id}")
    suspend fun periodoDetalle(
        @Path("id") id: Int
    ): PeriodoDetalleResponse

    @GET("periodos/{id}/movimientos")
    suspend fun periodoMovimientos(
        @Path("id") id: Int
    ): PeriodoMovimientosResponse

    @POST("consulta-inteligente")
    suspend fun consultarZoe(
        @Body request: ZoeConsultaRequest
    ): ZoeConsultaResponse

    @POST("fcm/token")
    suspend fun registrarTokenFcm(
        @Body request: FcmTokenRequest
    ): FcmTokenResponse

    @POST("fcm/token/desactivar")
    suspend fun desactivarTokenFcm(
        @Body request: FcmTokenRequest
    ): FcmTokenResponse
}