package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.ConfiguracionApi
import com.sigefiv.app.data.model.Configuracion

class ConfiguracionRepository(
    private val api: ConfiguracionApi
) {
    suspend fun obtenerConfiguracion(): Result<Configuracion> {
        return try {
            val response = api.obtenerConfiguracion()
            if (response.isSuccessful && response.body()?.data != null) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Error al obtener configuración"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun guardarConfiguracion(configuracion: Configuracion): Result<Configuracion> {
        return try {
            val response = api.guardarConfiguracion(configuracion)
            if (response.isSuccessful && response.body()?.data != null) {
                android.util.Log.d("CONFIG_DEBUG", "Configuración guardada exitosamente en el servidor")
                Result.success(response.body()!!.data!!)
            } else {
                val errorRaw = response.errorBody()?.string()
                android.util.Log.e("CONFIG_DEBUG", "Error HTTP ${response.code()}: $errorRaw")
                Result.failure(Exception("Error ${response.code()}: $errorRaw"))
            }
        } catch (e: Exception) {
            android.util.Log.e("CONFIG_DEBUG", "Excepción al enviar PUT:", e)
            Result.failure(e)
        }
    }
}