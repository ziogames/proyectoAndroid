package com.sigefiv.app.data.repository

import android.content.Context
import android.net.Uri
import com.sigefiv.app.data.api.EmprendimientoApi
import com.sigefiv.app.data.model.EmprendimientoDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import retrofit2.Response

class EmprendimientoRepository(
    private val api: EmprendimientoApi,
    private val context: Context
) {

    // 🚀 OBTENER CATEGORÍAS ELIMINADO (Ya no se usa en el formulario de emprendimientos)

    suspend fun getMisEmprendimientos(): Result<List<EmprendimientoDto>> {
        return try {
            val lista = api.getMisEmprendimientos()
            Result.success(lista)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 🔍 NUEVO: Obtener un emprendimiento específico por su ID para rellenar el formulario de edición
    suspend fun obtenerEmprendimientoPorId(id: Long): Result<EmprendimientoDto> {
        return try {
            val listaMisEmprendimientos = api.getMisEmprendimientos()
            // Buscamos en la lista local o puedes implementar un endpoint dedicado si lo tienes en Laravel
            val encontrado = listaMisEmprendimientos.find { it.id == id }
            if (encontrado != null) {
                Result.success(encontrado)
            } else {
                Result.failure(Exception("Emprendimiento no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generarDescripcionZoe(
        nombre: String,
        borradorDescripcion: String = ""
    ): Result<String> {
        return try {
            val response = api.generarDescripcionZoe(
                mapOf(
                    "name" to nombre,
                    "description" to borradorDescripcion
                )
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.description)
            } else {
                Result.failure(Exception("Error al generar descripción: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun crearEmprendimiento(
        campos: Map<String, String>,
        urisFotos: List<Uri>
    ): Result<EmprendimientoDto> {
        return try {
            val partMap = mutableMapOf<String, RequestBody>()
            campos.forEach { (key, value) ->
                partMap[key] = value.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            val fotosParts = urisFotos.mapNotNull { uri ->
                uriToFile(context, uri)?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photos[]", file.name, requestFile)
                }
            }

            val response = api.crearEmprendimiento(partMap, fotosParts)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error al guardar en el servidor: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✏️ NUEVO: Actualizar un emprendimiento existente enviando los campos al servidor
    suspend fun actualizarEmprendimiento(
        id: Long,
        campos: Map<String, String>,
        urisFotos: List<Uri>
    ): Result<EmprendimientoDto> {
        return try {
            val partMap = mutableMapOf<String, RequestBody>()
            campos.forEach { (key, value) ->
                partMap[key] = value.toRequestBody("text/plain".toMediaTypeOrNull())
            }

            val fotosParts = urisFotos.mapNotNull { uri ->
                uriToFile(context, uri)?.let { file ->
                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("photos[]", file.name, requestFile)
                }
            }

            // Nota: Asegúrate de tener definido el método actualizar/put en tu EmprendimientoApi si usas PUT/POST con _method
            val response = api.actualizarEmprendimiento(id, partMap, fotosParts)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error al actualizar en el servidor: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateStatus(id: Long, body: Map<String, String>): Result<EmprendimientoDto> {
        return try {
            val response = api.updateStatus(id, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error al actualizar estado: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun eliminarEmprendimiento(id: Long): Result<Unit> {
        return try {
            val response = api.eliminarEmprendimiento(id)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar en el servidor: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // 🌐 NUEVO: Obtener el directorio público de emprendimientos de todos los vecinos
    suspend fun obtenerEmprendimientosPublicos(categoria: String? = null): List<EmprendimientoDto> {
        return try {
            val response = api.getEmprendimientosPublicos(categoria)
            response.emprendimientos // 👈 Extraemos la lista interna
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun toggleFavorito(id: Int): Response<com.sigefiv.app.data.api.FavoritoResponseWrapper> {
        return api.toggleFavorito(id)
    }

    suspend fun getMisFavoritos(): com.sigefiv.app.data.api.EmprendimientosPublicosResponse {
        return api.getMisFavoritos()
    }

}