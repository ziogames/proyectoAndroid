package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.UsuarioApi
import com.sigefiv.app.data.model.CambiarEstadoResponse
import com.sigefiv.app.data.model.CrearUsuarioRequest
import com.sigefiv.app.data.model.UsuariosResponse
import org.json.JSONObject
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

    suspend fun crearUsuario(
        name: String,
        email: String,
        role: String,
        cargoId: Int?,
        password: String,
        passwordConfirmation: String
    ): Result<CambiarEstadoResponse> {
        return ejecutar {
            api.crearUsuario(
                CrearUsuarioRequest(
                    name = name,
                    email = email,
                    role = role,
                    cargoId = cargoId,
                    password = password,
                    passwordConfirmation = passwordConfirmation
                )
            )
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
        rol: String,
        cargoId: Int? = null
    ): Result<UsuariosResponse> {
        val datos = buildMap<String, String> {
            put("rol", rol)
            if (cargoId != null) {
                put("cargo_id", cargoId.toString())
            }
        }

        val resultado = ejecutar {
            api.cambiarRol(
                usuarioId = usuarioId,
                datos = datos
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

    suspend fun marcarBienvenidaVista(): Result<Unit> {
        return ejecutar {
            api.marcarBienvenidaVista()
        }.map { }
    }

    suspend fun actualizarPassword(
        currentPassword: String,
        newPassword: String,
        newPasswordConfirmation: String
    ): Result<String> {
        val parametros = mapOf(
            "current_password" to currentPassword,
            "new_password" to newPassword,
            "new_password_confirmation" to newPasswordConfirmation
        )
        return ejecutar {
            api.actualizarPassword(parametros)
        }.map { body ->
            try {
                JSONObject(body.string()).optString("message", "Contraseña actualizada correctamente.")
            } catch (_: Exception) {
                "Contraseña actualizada correctamente."
            }
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
                val errorBody = response.errorBody()?.string()
                val mensajeServidor = extraerMensajeError(errorBody)

                when (response.code()) {
                    401 -> Result.failure(
                        Exception("La sesión ha expirado.")
                    )

                    403 -> Result.failure(
                        Exception(
                            mensajeServidor ?: "No tienes permiso para realizar esta acción."
                        )
                    )

                    404 -> Result.failure(
                        Exception(
                            mensajeServidor ?: "No se encontró el usuario solicitado."
                        )
                    )

                    422 -> Result.failure(
                        Exception(
                            mensajeServidor ?: "Verifica los datos ingresados e inténtalo nuevamente."
                        )
                    )

                    else -> Result.failure(
                        Exception(
                            mensajeServidor ?: "Error del servidor: ${response.code()}"
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: "No se pudo conectar con el servidor."
                )
            )
        }
    }

    private fun extraerMensajeError(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null
        return try {
            val json = JSONObject(errorBody)
            if (json.has("errors")) {
                val errors = json.getJSONObject("errors")
                val firstKey = errors.keys().asSequence().firstOrNull()
                if (firstKey != null) {
                    val mensajes = errors.getJSONArray(firstKey)
                    if (mensajes.length() > 0) return mensajes.getString(0)
                }
            }
            json.optString("message").takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}