package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.DashboardResponse
import com.sigefiv.app.data.model.GoogleLoginRequest
import com.sigefiv.app.data.model.LoginRequest
import com.sigefiv.app.data.model.LoginResponse
import com.sigefiv.app.data.model.MovimientosResponse

class AuthRepository(
    private val authApi: AuthApi
) {

    suspend fun login(
        email: String,
        password: String
    ): LoginResponse {

        return authApi.login(
            LoginRequest(
                email = email,
                password = password
            )
        )
    }

    suspend fun loginConGoogle(
        idToken: String
    ): LoginResponse {

        return authApi.loginConGoogle(
            GoogleLoginRequest(
                id_token = idToken
            )
        )
    }

    suspend fun obtenerUsuario(): LoginResponse {

        return authApi.user()
    }

    suspend fun obtenerDashboard(): DashboardResponse {

        return authApi.dashboard()
    }

    suspend fun obtenerMovimientos(): MovimientosResponse {

        return authApi.movimientos()
    }

    suspend fun cerrarSesion() {

        authApi.logout()
    }
}