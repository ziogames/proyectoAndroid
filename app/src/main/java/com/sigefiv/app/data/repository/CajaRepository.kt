package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.CajaApi
import com.sigefiv.app.data.model.CajaResponse

class CajaRepository(
    private val api: CajaApi
) {

    suspend fun obtenerCaja(
        anio: Int? = null
    ): Result<CajaResponse> {

        return try {

            val response = api.obtenerCaja(anio)

            if (response.isSuccessful) {

                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception(
                            "La respuesta de Caja está vacía."
                        )
                    )
                }

            } else {

                Result.failure(
                    Exception(
                        "Error al obtener Caja: ${response.code()}"
                    )
                )
            }

        } catch (e: Exception) {

            Result.failure(e)
        }
    }
}