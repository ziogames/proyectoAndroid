package com.sigefiv.app.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.api.IncidenciaApiService
import com.sigefiv.app.data.model.ActualizarEstadoRequest
import com.sigefiv.app.data.model.IncidenciaItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class IncidenciasViewModel(application: Application) : AndroidViewModel(application) {

    // 🛠️ Usamos el cliente centralizado correcto de ApiClient para incidencias con su token de Sanctum
    private val apiService: IncidenciaApiService by lazy {
        ApiClient.incidenciaApi(application)
    }

    private val _incidenciasDirectiva = MutableStateFlow<List<IncidenciaItem>>(emptyList())
    val incidenciasDirectiva: StateFlow<List<IncidenciaItem>> = _incidenciasDirectiva.asStateFlow()

    private val _incidenciasVecino = MutableStateFlow<List<IncidenciaItem>>(emptyList())
    val incidenciasVecino: StateFlow<List<IncidenciaItem>> = _incidenciasVecino.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // 1. Cargar todas las incidencias (Para la vista global de la Directiva / Secretarios)
    fun cargarIncidenciasDirectiva() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = apiService.obtenerIncidencias()
                if (response.isSuccessful) {
                    val body = response.body()
                    val lista = body?.incidencias ?: emptyList()
                    Log.d("INCIDENCIAS_DEBUG", "Directiva - Incidencias cargadas: ${lista.size}")
                    _incidenciasDirectiva.value = lista
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""
                    Log.e("INCIDENCIAS_DEBUG", "Directiva Error HTTP: ${response.code()} $errorMsg")
                }
            } catch (e: Exception) {
                Log.e("INCIDENCIAS_DEBUG", "Directiva Excepción: ${e.message}", e)
            } finally {
                _cargando.value = false
            }
        }
    }

    // 2. Cargar los reportes personales del vecino autenticado (Rol Consulta usando /mis-reportes)
    fun cargarMisReportesParaVecino() {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val response = apiService.obtenerMisReportes()
                if (response.isSuccessful) {
                    val body = response.body()
                    val lista = body?.incidencias ?: emptyList()
                    Log.d("INCIDENCIAS_DEBUG", "Vecino - Mis Reportes cargados: ${lista.size}")
                    _incidenciasVecino.value = lista
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""
                    Log.e("INCIDENCIAS_DEBUG", "Vecino Error HTTP: ${response.code()} $errorMsg")
                }
            } catch (e: Exception) {
                Log.e("INCIDENCIAS_DEBUG", "Vecino Excepción: ${e.message}", e)
            } finally {
                _cargando.value = false
            }
        }
    }

    // 3. Crear una nueva incidencia por Multipart (Incluyendo la imagen adjunta)
    fun crearIncidencia(
        titulo: String,
        categoria: String,
        mensaje: String,
        imagenUri: Uri? = null,
        onResultado: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                // Convertimos los campos de texto a RequestBody para peticiones Multipart
                val tituloBody = titulo.toRequestBody("text/plain".toMediaTypeOrNull())
                val categoriaBody = categoria.toRequestBody("text/plain".toMediaTypeOrNull())
                val mensajeBody = mensaje.toRequestBody("text/plain".toMediaTypeOrNull())

                var imagenPart: MultipartBody.Part? = null
                imagenUri?.let { uri ->
                    val context = getApplication<Application>().applicationContext
                    val file = uriToFile(context, uri)
                    file?.let {
                        val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                        imagenPart = MultipartBody.Part.createFormData("imagen", it.name, requestFile)
                    }
                }

                val response = apiService.crearIncidencia(
                    titulo = tituloBody,
                    categoria = categoriaBody,
                    mensaje = mensajeBody,
                    imagen = imagenPart
                )

                if (response.isSuccessful) {
                    Log.d("INCIDENCIAS_DEBUG", "Incidencia creada exitosamente con imagen")
                    cargarMisReportesParaVecino()
                    onResultado(true, "¡Reporte enviado con éxito!")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
                    Log.e("INCIDENCIAS_DEBUG", "Error al crear incidencia: ${response.code()} $errorMsg")
                    onResultado(false, "No se pudo enviar el reporte.")
                }
            } catch (e: Exception) {
                Log.e("INCIDENCIAS_DEBUG", "Excepción al crear incidencia: ${e.message}", e)
                onResultado(false, "Error de conexión: ${e.message}")
            } finally {
                _cargando.value = false
            }
        }
    }

    // 4. Cambiar el estado de la incidencia (Usado por la directiva)
    fun cambiarEstadoIncidencia(id: Long, nuevoEstado: String, esVistaDirectiva: Boolean = true) {
        viewModelScope.launch {
            try {
                val response = apiService.actualizarEstadoIncidencia(id, ActualizarEstadoRequest(estado = nuevoEstado))
                if (response.isSuccessful) {
                    Log.d("INCIDENCIAS_DEBUG", "Estado de incidencia #$id actualizado a $nuevoEstado")
                    if (esVistaDirectiva) {
                        cargarIncidenciasDirectiva()
                    }
                } else {
                    Log.e("INCIDENCIAS_DEBUG", "Error al actualizar estado: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("INCIDENCIAS_DEBUG", "Excepción al actualizar estado: ${e.message}", e)
            }
        }
    }

    // Función auxiliar privada para transformar el Uri de Android a un archivo temporal de caché
    private fun uriToFile(context: Context, uri: Uri): File? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "incidencia_${System.currentTimeMillis()}.jpg")
            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            Log.e("INCIDENCIAS_DEBUG", "Error al convertir Uri a File: ${e.message}", e)
            null
        }
    }
}