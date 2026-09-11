package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.repository.NotificacionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnviarNotificacionViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        NotificacionRepository(
            ApiClient.create(context)
        )

    // ============================================================
    // ESTADO DEL FORMULARIO
    // ============================================================

    private val _enviando =
        MutableStateFlow(false)

    val enviando: StateFlow<Boolean> =
        _enviando

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje

    private val _enviado =
        MutableStateFlow(false)

    val enviado: StateFlow<Boolean> =
        _enviado

    // ============================================================
    // ENVIAR NOTIFICACIÓN
    // ============================================================

    fun enviar(
        titulo: String,
        mensaje: String,
        tipo: String,
        destinatario: String,
        usuarioIds: List<Int> = emptyList()
    ) {
        if (titulo.isBlank()) {
            _mensaje.value = "Ingresa un título."
            return
        }

        if (mensaje.isBlank()) {
            _mensaje.value = "Ingresa el mensaje."
            return
        }

        if (
            destinatario == "usuarios" &&
            usuarioIds.isEmpty()
        ) {
            _mensaje.value = "Selecciona al menos un usuario."
            return
        }

        viewModelScope.launch {
            _enviando.value = true
            _mensaje.value = null
            _enviado.value = false

            try {
                val resultado =
                    repository.enviarNotificacion(
                        titulo = titulo.trim(),
                        mensaje = mensaje.trim(),
                        tipo = tipo,
                        destinatario = destinatario,
                        usuarioIds =
                            if (destinatario == "usuarios") {
                                usuarioIds
                            } else {
                                null
                            }
                    )

                resultado
                    .onSuccess { response ->
                        _enviado.value = true

                        val enviados =
                            response.data?.enviados ?: 0

                        _mensaje.value =
                            when (destinatario) {
                                "todos" ->
                                    "Notificación enviada a $enviados vecinos."

                                "directiva" ->
                                    "Notificación enviada a $enviados miembros de la directiva."

                                "usuarios" ->
                                    "Notificación enviada a $enviados usuarios."

                                else ->
                                    "Notificación enviada correctamente."
                            }
                    }
                    .onFailure { error ->
                        _mensaje.value =
                            error.message
                                ?: "No se pudo enviar la notificación."
                    }

            } catch (e: Exception) {
                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudo enviar la notificación."

            } finally {
                _enviando.value = false
            }
        }
    }

    // ============================================================
    // LIMPIAR ESTADO
    // ============================================================

    fun limpiar() {
        _mensaje.value = null
        _enviado.value = false
    }
    fun limpiarResultado() {
        _enviado.value = false
        _mensaje.value = null
    }
}