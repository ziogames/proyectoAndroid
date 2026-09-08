package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.UsuarioApi
import com.sigefiv.app.data.model.UsuariosResponse
import retrofit2.Response

class UsuarioRepository(
    private val api: UsuarioApi
) {

    suspend fun obtenerUsuarios(
        buscar: String? = null
    ): Result<UsuariosResponse> {
        return ejecutar {
            api.obtenerUsuarios(buscar)
        }
    }

    suspend fun cambiarEstado(
        usuarioId: Int,
        estado: String
    ): Result<UsuariosResponse> {
        val resultado = ejecutar {
            api.cambiarEstado(
                usuarioId = usuarioId,
                datos = mapOf(
                    "estado" to estado
                )
            )
        }

        return resultado.map { respuesta ->
            UsuariosResponse(
                success = respuesta.success,
                message = respuesta.message,
                usuarios = respuesta.usuario?.let {
                    listOf(it)
                } ?: emptyList()
            )
        }
    }

    suspend fun cambiarRol(
        usuarioId: Int,
        rol: String
    ): Result<UsuariosResponse> {
        val resultado = ejecutar {
            api.cambiarRol(
                usuarioId = usuarioId,
                datos = mapOf(
                    "rol" to rol
                )
            )
        }

        return resultado.map { respuesta ->
            UsuariosResponse(
                success = respuesta.success,
                message = respuesta.message,
                usuarios = respuesta.usuario?.let {
                    listOf(it)
                } ?: emptyList()
            )
        }
    }

    suspend fun eliminarUsuario(
        usuarioId: Int
    ): Result<Unit> {
        return ejecutar {
            api.eliminarUsuario(usuarioId)
        }
    }

    suspend fun marcarBienvenidaVista(): Result<Map<String, Any>> {
        return ejecutar {
            api.marcarBienvenidaVista()
        }
    }

    private suspend fun <T> ejecutar(
        llamada: suspend () -> Response<T>
    ): Result<T> {
        return try {
            val response = llamada()

            if (response.isSuccessful) {
                val body = response.body()

                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(
                        Exception("El servidor no devolvió datos.")
                    )
                }
            } else {
                when (response.code()) {
                    401 -> Result.failure(
                        Exception("La sesión ha expirado.")
                    )

                    403 -> Result.failure(
                        Exception(
                            "No tienes permiso para realizar esta acción."
                        )
                    )

                    404 -> Result.failure(
                        Exception(
                            "No se encontró el usuario solicitado."
                        )
                    )

                    422 -> Result.failure(
                        Exception(
                            "No se pudo realizar la acción sobre el usuario."
                        )
                    )

                    else -> Result.failure(
                        Exception(
                            "Error del servidor: ${response.code()}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message
                        ?: "No se pudo conectar con el servidor."
                )
            )
        }
    }
}