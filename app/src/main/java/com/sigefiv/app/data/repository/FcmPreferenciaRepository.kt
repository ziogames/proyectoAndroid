package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.FcmPreferenciaRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FcmPreferenciaRepository(
    private val authApi: AuthApi
) {

    suspend fun actualizar(
        token: String,
        activo: Boolean
    ): Result<Boolean> {

        return withContext(Dispatchers.IO) {

            try {

                val datos = FcmPreferenciaRequest(
                    token = token,
                    activo = activo
                )

                val response =
                    authApi.actualizarPreferenciaFcm(datos)

                if (response.isSuccessful) {

                    Result.success(true)

                } else {

                    Result.failure(
                        Exception(
                            "No se pudo actualizar la preferencia. " +
                                    "Código: ${response.code()}"
                        )
                    )
                }

            } catch (e: Exception) {

                Result.failure(e)
            }
        }
    }
}