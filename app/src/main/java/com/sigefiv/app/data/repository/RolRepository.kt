
package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.RolApi
import com.sigefiv.app.data.model.ActualizarRolRequest
import com.sigefiv.app.data.model.CrearRolRequest
import com.sigefiv.app.data.model.Permiso
import com.sigefiv.app.data.model.RolDetalle

class RolRepository(
    private val api: RolApi
) {

    suspend fun obtenerRoles(): Result<List<RolDetalle>> {
        return try {
            val response = api.obtenerRoles()

            if (response.success) {
                Result.success(response.roles)
            } else {
                Result.failure(
                    Exception(response.message ?: "Error al obtener roles")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Error al obtener roles: ${e.message}")
            )
        }
    }

    suspend fun obtenerPermisos(): Result<Map<String, List<Permiso>>> {
        return try {
            val response = api.obtenerPermisos()

            if (response.success) {
                Result.success(response.permisosAgrupados)
            } else {
                Result.failure(
                    Exception(response.message ?: "Error al obtener permisos")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Error al obtener permisos: ${e.message}")
            )
        }
    }

    suspend fun obtenerRolDetalle(id: Int): Result<RolDetalle> {
        return try {
            val response = api.obtenerRolDetalle(id)

            if (response.success && response.rol != null) {
                Result.success(response.rol)
            } else {
                Result.failure(
                    Exception(response.message ?: "No se pudo cargar el rol")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Error al cargar el rol: ${e.message}")
            )
        }
    }

    suspend fun crearRol(
        nombre: String,
        permisos: List<String>
    ): Result<Unit> {
        return try {
            val response = api.crearRol(
                CrearRolRequest(
                    name = nombre.trim(),
                    permissions = permisos
                )
            )

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(response.message ?: "Error al crear el rol")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Error al crear el rol: ${e.message}")
            )
        }
    }

    suspend fun actualizarRol(
        id: Int,
        nombre: String,
        permisos: List<String>
    ): Result<Unit> {
        return try {
            val response = api.actualizarRol(
                id,
                ActualizarRolRequest(nombre.trim(), permisos)
            )

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(response.message ?: "Error al actualizar rol")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Error al actualizar rol: ${e.message}")
            )
        }
    }

    suspend fun eliminarRol(id: Int): Result<Unit> {
        return try {
            val response = api.eliminarRol(id)

            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(
                    Exception(response.message ?: "Error al eliminar rol")
                )
            }
        } catch (e: Exception) {
            Result.failure(
                Exception("Error al eliminar rol: ${e.message}")
            )
        }
    }
}
