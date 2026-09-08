package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.RolApi
import com.sigefiv.app.data.model.ActualizarRolRequest
import com.sigefiv.app.data.model.RolDetalle

class RolRepository(private val api: RolApi) {

    suspend fun obtenerRoles(): Result<List<RolDetalle>> {
        return try {
            val response = api.obtenerRoles()
            if (response.success) {
                Result.success(response.roles)
            } else {
                Result.failure(Exception(response.message ?: "Error al obtener roles"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun obtenerRolDetalle(id: Int): Result<RolDetalle> {
        return try {
            val response = api.obtenerRolDetalle(id)
            if (response.success && response.rol != null) {
                Result.success(response.rol)
            } else {
                Result.failure(Exception(response.message ?: "No se pudo cargar el rol"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    suspend fun actualizarRol(id: Int, nombre: String, permisos: List<String>): Result<Unit> {
        return try {
            val response = api.actualizarRol(id, ActualizarRolRequest(nombre, permisos))
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Error al actualizar rol"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }
}