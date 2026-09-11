package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Notificacion
import com.sigefiv.app.data.repository.NotificacionRepository
import com.sigefiv.app.notifications.NotificacionEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificacionViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        NotificacionRepository(
            ApiClient.create(context)
        )

    // ------------------------------------------------------------
    // NOTIFICACIONES
    // ------------------------------------------------------------

    private val _notificaciones =
        MutableStateFlow<List<Notificacion>>(emptyList())

    val notificaciones: StateFlow<List<Notificacion>> =
        _notificaciones

    // ------------------------------------------------------------
    // NO LEÍDAS
    // ------------------------------------------------------------

    private val _noLeidas =
        MutableStateFlow(0)

    val noLeidas: StateFlow<Int> =
        _noLeidas

    // ------------------------------------------------------------
    // ESTADOS
    // ------------------------------------------------------------

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando

    // ------------------------------------------------------------
    // MENSAJE / ERROR
    // ------------------------------------------------------------

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje

    init {

        /*
         * Escuchamos las nuevas notificaciones FCM.
         *
         * Cuando SIGEFIVFirebaseMessagingService recibe
         * una notificación, NotificacionEventBus emite
         * un evento y aquí volvemos a consultar el
         * contador en Laravel.
         */
        viewModelScope.launch {

            NotificacionEventBus
                .evento
                .collect {

                    cargarNotificaciones()
                    cargarNoLeidas()
                }
        }
    }

    // ------------------------------------------------------------
    // CARGAR NOTIFICACIONES
    // ------------------------------------------------------------

    fun cargarNotificaciones() {

        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            try {

                val resultado =
                    repository.obtenerNotificaciones()

                resultado
                    .onSuccess { lista ->

                        _notificaciones.value =
                            lista

                        actualizarContador(
                            lista
                        )
                    }
                    .onFailure { error ->

                        _mensaje.value =
                            error.message
                                ?: "No se pudieron cargar las notificaciones."
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudieron cargar las notificaciones."

            } finally {

                _cargando.value = false
            }
        }
    }

    // ------------------------------------------------------------
    // CARGAR NO LEÍDAS
    // ------------------------------------------------------------

    fun cargarNoLeidas() {

        viewModelScope.launch {

            try {

                val resultado =
                    repository.obtenerNoLeidas()

                resultado
                    .onSuccess { lista ->

                        _noLeidas.value =
                            lista.size
                    }

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    // ------------------------------------------------------------
    // MARCAR COMO LEÍDA
    // ------------------------------------------------------------

    fun marcarComoLeida(
        id: Int
    ) {

        viewModelScope.launch {

            try {

                val resultado =
                    repository.marcarComoLeida(id)

                resultado
                    .onSuccess { notificacion ->

                        if (notificacion != null) {

                            _notificaciones.value =
                                _notificaciones.value.map {

                                    if (it.id == id) {
                                        notificacion
                                    } else {
                                        it
                                    }
                                }

                            actualizarContador(
                                _notificaciones.value
                            )
                        }
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudo marcar la notificación como leída."
            }
        }
    }

    // ------------------------------------------------------------
    // MARCAR TODAS COMO LEÍDAS
    // ------------------------------------------------------------

    fun marcarTodasComoLeidas() {

        viewModelScope.launch {

            try {

                val resultado =
                    repository.marcarTodasComoLeidas()

                resultado
                    .onSuccess {

                        _notificaciones.value =
                            _notificaciones.value.map {

                                it.copy(
                                    leida = true,
                                    fecha_lectura =
                                        it.fecha_lectura
                                )
                            }

                        _noLeidas.value = 0
                    }
                    .onFailure { error ->

                        _mensaje.value =
                            error.message
                                ?: "No se pudieron marcar las notificaciones."
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudieron marcar las notificaciones."
            }
        }
    }

    // ------------------------------------------------------------
    // ACTUALIZAR CONTADOR
    // ------------------------------------------------------------

    private fun actualizarContador(
        lista: List<Notificacion>
    ) {

        _noLeidas.value =
            lista.count {
                !it.leida
            }
    }

    // ------------------------------------------------------------
    // LIMPIAR MENSAJE
    // ------------------------------------------------------------

    fun limpiarMensaje() {

        _mensaje.value = null
    }
}