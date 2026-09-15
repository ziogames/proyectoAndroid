package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.FcmPreferenciaRequest
import com.sigefiv.app.notifications.FcmTokenProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FcmPreferenciaRepository(
    private val authApi: AuthApi
) {

    // =========================================================
    // OBTENER PREFERENCIAS ACTUALES
    // =========================================================

    suspend fun obtener(
        token: String
    ): Result<PreferenciasFcm> {

        return withContext(Dispatchers.IO) {

            try {

                val response =
                    authApi.obtenerPreferenciaFcm(
                        token = token
                    )

                if (response.isSuccessful) {

                    val body =
                        response.body()

                    if (body?.success == true && body.data != null) {

                        Result.success(
                            PreferenciasFcm(
                                activo = body.data.activo,
                                ingresos = body.data.ingresos,
                                egresos = body.data.egresos,
                                zoe = body.data.zoe,
                                avisos = body.data.avisos
                            )
                        )

                    } else {

                        Result.failure(
                            Exception(
                                body?.message
                                    ?: "No se pudieron obtener las preferencias."
                            )
                        )
                    }

                } else {

                    Result.failure(
                        Exception(
                            "No se pudieron obtener las preferencias. " +
                                    "Código: ${response.code()}"
                        )
                    )
                }

            } catch (e: Exception) {

                Result.failure(e)
            }
        }
    }

    // =========================================================
    // ACTUALIZAR PREFERENCIAS
    // =========================================================

    suspend fun actualizar(
        token: String,
        activo: Boolean,
        ingresos: Boolean,
        egresos: Boolean,
        zoe: Boolean,
        avisos: Boolean
    ): Result<Boolean> {

        return withContext(Dispatchers.IO) {

            try {

                val datos =
                    FcmPreferenciaRequest(
                        token = token,
                        activo = activo,
                        ingresos = ingresos,
                        egresos = egresos,
                        zoe = zoe,
                        avisos = avisos
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

    // =========================================================
    // MODELO INTERNO DE PREFERENCIAS
    // =========================================================

    data class PreferenciasFcm(
        val activo: Boolean,
        val ingresos: Boolean,
        val egresos: Boolean,
        val zoe: Boolean,
        val avisos: Boolean
    )
}