package com.sigefiv.app.data.repository

import android.content.Context
import android.net.Uri
import com.sigefiv.app.data.api.EmprendimientoApi
import com.sigefiv.app.data.api.EmprendimientoCategoriaDto
import com.sigefiv.app.data.model.EmprendimientoDto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class EmprendimientoRepository(
    private val api: EmprendimientoApi,
    private val context: Context
) {

    suspend fun obtenerCategorias(): Result<List<EmprendimientoCategoriaDto>> {
        return try {
            val categorias = api.getCategorias()
            Result.success(categorias) // 👈 Pasamos la lista directamente
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 👈 Actualizado a getMisEmprendimientos para coincidir con el ViewModel
    suspend fun getMisEmprendimientos(): Result<List<EmprendimientoDto>> {
        return try {
            val lista = api.getMisEmprendimientos()
            Result.success(lista)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generarDescripcionZoe(nombre: String): Result<String> {
        return try {
            val response = api.generarDescripcionZoe(mapOf("name" to nombre))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.description)
            } else {
                Result.failure(Exception("Error al generar descripción con ZOE"))
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
}