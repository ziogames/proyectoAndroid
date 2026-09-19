
package com.sigefiv.app.data.api

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NetworkErrorManager {

    private val _error =
        MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    private const val MENSAJE_ERROR =
        "En este momento no podemos conectarnos con SIGEFIV.\n\n" +
                "Por favor, inténtalo nuevamente en unos momentos. " +
                "Si el problema continúa, comunícate con la administración."

    fun mostrarErrorConexion() {

        // Evita mostrar el mismo error varias veces
        if (_error.value == null) {
            _error.value = MENSAJE_ERROR
        }
    }

    fun limpiarError() {
        _error.value = null
    }
    fun mostrarErrorServidor() {

        if (_error.value == null) {
            _error.value =
                "Estamos teniendo dificultades para procesar tu solicitud.\n\n" +
                        "Por favor, inténtalo nuevamente en unos momentos. " +
                        "Si el problema continúa, comunícate con la administración."
        }
    }
}