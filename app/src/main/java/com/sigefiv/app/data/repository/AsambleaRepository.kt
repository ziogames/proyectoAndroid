package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AsambleaApi
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.data.model.CrearAsambleaRequest

class AsambleaRepository(
    private val api: AsambleaApi
) {

    suspend fun obtenerAsambleas(): Result<List<Asamblea>> {
        return try {
            val response = api.obtenerAsambleas()

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true) {
                    Result.success(body.asambleas)
                } else {
                    Result.failure(
                        Exception(
                            body?.message
                                ?: "No se pudieron obtener las asambleas."
                        )
                    )
                }
            } else {
                Result.failure(
                    Exception(
                        "Error del servidor: ${response.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerAsamblea(
        id: Int
    ): Result<Asamblea> {
        return try {
            val response = api.obtenerAsamblea(id)

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.asamblea != null) {
                    Result.success(body.asamblea)
                } else {
                    Result.failure(
                        Exception(
                            body?.message
                                ?: "No se pudo obtener la asamblea."
                        )
                    )
                }
            } else {
                Result.failure(
                    Exception(
                        "Error del servidor: ${response.code()}"
                    )
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearAsamblea(
        request: CrearAsambleaRequest
    ): Result<Asamblea> {
        return try {
            val response = api.crearAsamblea(request)

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.asamblea != null) {
                    Result.success(body.asamblea)
                } else {
                    Result.failure(
                        Exception(
                            body?.message
                                ?: "No se pudo crear la asamblea."
                        )
                    )
                }
            } else {
                val mensaje = when (response.code()) {
                    401 -> "La sesión ha expirado."
                    403 -> "No tienes permiso para crear asambleas."
                    422 -> "Los datos enviados no son válidos."
                    else -> "Error del servidor: ${response.code()}"
                }

                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun actualizarAsamblea(
        id: Int,
        request: CrearAsambleaRequest
    ): Result<Asamblea> {
        return try {
            val response = api.actualizarAsamblea(
                id = id,
                request = request
            )

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.asamblea != null) {
                    Result.success(body.asamblea)
                } else {
                    Result.failure(
                        Exception(
                            body?.message
                                ?: "No se pudo actualizar la asamblea."
                        )
                    )
                }
            } else {
                val mensaje = when (response.code()) {
                    401 -> "La sesión ha expirado."
                    403 -> "No tienes permiso para editar esta asamblea."
                    404 -> "La asamblea no existe."
                    422 -> "Los datos enviados no son válidos."
                    else -> "Error del servidor: ${response.code()}"
                }

                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarAsamblea(
        id: Int
    ): Result<Boolean> {
        return try {
            val response = api.eliminarAsamblea(id)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val mensaje = when (response.code()) {
                    401 -> "La sesión ha expirado."
                    403 -> "No tienes permiso para eliminar esta asamblea."
                    404 -> "La asamblea no existe."
                    422 -> "La asamblea no puede eliminarse."
                    else -> "No se pudo eliminar la asamblea. Código: ${response.code()}"
                }

                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun publicarAsamblea(
        id: Int
    ): Result<Asamblea> {
        return try {
            val response = api.publicarAsamblea(id)

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true && body.asamblea != null) {
                    Result.success(body.asamblea)
                } else {
                    Result.failure(
                        Exception(
                            body?.message
                                ?: "No se pudo publicar la asamblea."
                        )
                    )
                }
            } else {
                val mensaje = when (response.code()) {
                    401 -> "La sesión ha expirado."
                    403 -> "No tienes permiso para publicar asambleas."
                    404 -> "La asamblea no existe."
                    422 -> "Solo se pueden publicar asambleas que estén en borrador."
                    else -> "Error del servidor: ${response.code()}"
                }

                Result.failure(Exception(mensaje))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
