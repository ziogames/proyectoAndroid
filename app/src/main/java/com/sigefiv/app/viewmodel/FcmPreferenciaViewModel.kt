package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.repository.FcmPreferenciaRepository
import com.sigefiv.app.notifications.FcmTokenProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FcmPreferenciaViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        FcmPreferenciaRepository(
            ApiClient.create(context)
        )

    // ------------------------------------------------------------
    // ESTADO DEL SWITCH
    // ------------------------------------------------------------

    private val _notificacionesActivadas =
        MutableStateFlow(true)

    val notificacionesActivadas: StateFlow<Boolean> =
        _notificacionesActivadas

    // ------------------------------------------------------------
    // ESTADO DE CARGA
    // ------------------------------------------------------------

    private val _guardando =
        MutableStateFlow(false)

    val guardando: StateFlow<Boolean> =
        _guardando

    // ------------------------------------------------------------
    // MENSAJE
    // ------------------------------------------------------------

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje

    // ------------------------------------------------------------
    // CAMBIAR PREFERENCIA
    // ------------------------------------------------------------

    fun cambiarEstado(
        activo: Boolean
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            try {

                // Obtener el token FCM del dispositivo.
                val token =
                    FcmTokenProvider.obtenerToken()

                val resultado =
                    repository.actualizar(
                        token = token,
                        activo = activo
                    )

                resultado
                    .onSuccess {

                        _notificacionesActivadas.value =
                            activo

                        _mensaje.value =
                            if (activo) {
                                "Notificaciones activadas."
                            } else {
                                "Notificaciones desactivadas."
                            }
                    }
                    .onFailure { error ->

                        _mensaje.value =
                            error.message
                                ?: "No se pudo actualizar la preferencia."
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudo actualizar la preferencia."

            } finally {

                _guardando.value = false
            }
        }
    }

    // ------------------------------------------------------------
    // LIMPIAR MENSAJE
    // ------------------------------------------------------------

    fun limpiarMensaje() {

        _mensaje.value = null
    }
}