package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PerfilViewModel(
    private val context: Context
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

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
     * Limpia el mensaje de error.
     */
    fun limpiarError() {
        _error.value = null
    }
}