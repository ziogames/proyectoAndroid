package com.sigefiv.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ActualizarPerfilRequest
import com.sigefiv.app.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import com.sigefiv.app.data.SessionManager
class PerfilViewModel(
    private val context: Context
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    private val _subiendoFoto = MutableStateFlow(false)
    val subiendoFoto: StateFlow<Boolean> = _subiendoFoto.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /**
     * Carga los datos del usuario autenticado.
     */
    fun cargarPerfil() {
        viewModelScope.launch {

            _cargando.value = true
            _error.value = null

            try {
                val api = ApiClient.create(context)

                val response = api.user()

                if (response.success && response.usuario != null) {

                    _usuario.value = response.usuario

                } else {

                    _error.value =
                        response.message.ifBlank {
                            "No se pudo obtener la información del usuario."
                        }
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Error de conexión con el servidor."

            } finally {

                _cargando.value = false
            }
        }
    }

    /**
     * Actualiza los datos editables del perfil.
     *
     * El nombre real no se modifica.
     */
    fun actualizarPerfil(
        seudonimo: String?,
        telefono: String?,
        dni: String?,
        direccion: String?
    ) {
        viewModelScope.launch {

            _guardando.value = true
            _error.value = null
            _mensaje.value = null

            try {

                val api = ApiClient.create(context)

                val request = ActualizarPerfilRequest(
                    seudonimo = seudonimo
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() },

                    telefono = telefono
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() },

                    dni = dni
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() },

                    direccion = direccion
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }
                )

                val response = api.actualizarPerfil(request)

                if (
                    response.success &&
                    response.usuario != null
                ) {

                    _usuario.value = response.usuario

                    // Actualizar inmediatamente el seudónimo de la sesión.
                    val sessionManager = SessionManager(context)

                    sessionManager.actualizarSeudonimo(
                        response.usuario.seudonimo
                    )

                    _mensaje.value =
                        response.message.ifBlank {
                            "Perfil actualizado correctamente."
                        }

                } else {

                    _error.value =
                        response.message.ifBlank {
                            "No se pudo actualizar el perfil."
                        }
                }

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Error al actualizar el perfil."

            } finally {

                _guardando.value = false
            }
        }
    }

    /**
     * Sube una nueva foto de perfil.
     *
     * La foto se guarda en SIGEFIV y reemplaza la foto
     * anterior de SIGEFIV.
     *
     * Si anteriormente tenía una URL de Google,
     * esa URL simplemente deja de utilizarse para el perfil
     * de SIGEFIV.
     */
    fun actualizarFoto(uri: Uri) {
        viewModelScope.launch {

            _subiendoFoto.value = true
            _error.value = null
            _mensaje.value = null

            try {

                val archivo = crearArchivoTemporal(uri)

                if (archivo == null) {
                    _error.value =
                        "No se pudo leer la imagen seleccionada."
                    return@launch
                }

                val requestBody =
                    archivo
                        .asRequestBody(
                            "image/*".toMediaTypeOrNull()
                        )

                val fotoPart =
                    MultipartBody.Part.createFormData(
                        "foto",
                        archivo.name,
                        requestBody
                    )

                val api = ApiClient.create(context)

                val response =
                    api.actualizarFoto(fotoPart)

                if (response.isSuccessful) {

                    /*
                     * Volvemos a consultar el perfil para obtener
                     * la nueva URL generada por Laravel.
                     */
                    val perfil = api.user()

                    if (
                        perfil.success &&
                        perfil.usuario != null
                    ) {
                        _usuario.value = perfil.usuario
                    }

                    _mensaje.value =
                        "Foto de perfil actualizada correctamente."

                } else {

                    _error.value =
                        "No se pudo actualizar la foto."
                }

                archivo.delete()

            } catch (e: Exception) {

                _error.value =
                    e.message ?: "Error al subir la foto."

            } finally {

                _subiendoFoto.value = false
            }
        }
    }

    /**
     * Copia el Uri seleccionado por el usuario a un archivo temporal.
     */
    private fun crearArchivoTemporal(uri: Uri): File? {

        return try {

            val resolver = context.contentResolver

            val extension =
                resolver.getType(uri)
                    ?.substringAfterLast(
                        '/',
                        "jpg"
                    )
                    ?: "jpg"

            val archivo = File.createTempFile(
                "perfil_",
                ".$extension",
                context.cacheDir
            )

            resolver.openInputStream(uri)?.use { input ->

                archivo.outputStream().use { output ->

                    input.copyTo(output)
                }
            } ?: return null

            archivo

        } catch (e: Exception) {

            null
        }
    }

    /**
     * Limpia el mensaje de error.
     */
    fun limpiarError() {
        _error.value = null
    }

    /**
     * Limpia el mensaje de éxito.
     */
    fun limpiarMensaje() {
        _mensaje.value = null
    }
}