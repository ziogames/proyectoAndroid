package com.sigefiv.app.data.repository

import android.content.Context
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ActividadResponse

class ActividadRepository(
    context: Context
) {

    private val api = ApiClient.actividadApi(context)

    suspend fun obtenerActividades(): Result<ActividadResponse> {
        return try {

            val respuesta = api.obtenerActividades()

            if (respuesta.isSuccessful && respuesta.body() != null) {
                Result.success(respuesta.body()!!)
            } else {
                Result.failure(
                    Exception(
                        "Error HTTP: ${respuesta.code()}"
                    )
                )
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}