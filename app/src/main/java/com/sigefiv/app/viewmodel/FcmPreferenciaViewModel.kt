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

    // =========================================================
    // PREFERENCIA GENERAL
    // =========================================================

    private val _notificacionesActivadas =
        MutableStateFlow(true)

    val notificacionesActivadas: StateFlow<Boolean> =
        _notificacionesActivadas

    // =========================================================
    // NUEVOS INGRESOS
    // =========================================================

    private val _ingresos =
        MutableStateFlow(true)

    val ingresos: StateFlow<Boolean> =
        _ingresos

    // =========================================================
    // NUEVOS EGRESOS
    // =========================================================

    private val _egresos =
        MutableStateFlow(true)

    val egresos: StateFlow<Boolean> =
        _egresos

    // =========================================================
    // ZOE
    // =========================================================

    private val _zoe =
        MutableStateFlow(true)

    val zoe: StateFlow<Boolean> =
        _zoe

    // =========================================================
    // AVISOS VECINALES
    // =========================================================

    private val _avisos =
        MutableStateFlow(true)

    val avisos: StateFlow<Boolean> =
        _avisos

    // =========================================================
    // ESTADO DE CARGA
    // =========================================================

    private val _guardando =
        MutableStateFlow(false)

    val guardando: StateFlow<Boolean> =
        _guardando

    // =========================================================
    // MENSAJE
    // =========================================================

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje

    // =========================================================
    // CARGAR PREFERENCIAS DESDE EL SERVIDOR
    // =========================================================

    fun cargarPreferencias() {

        viewModelScope.launch {

            try {

                val token =
                    FcmTokenProvider.obtenerToken()

                repository
                    .obtener(token)
                    .onSuccess { preferencias ->

                        _notificacionesActivadas.value =
                            preferencias.activo

                        _ingresos.value =
                            preferencias.ingresos

                        _egresos.value =
                            preferencias.egresos

                        _zoe.value =
                            preferencias.zoe

                        _avisos.value =
                            preferencias.avisos
                    }
                    .onFailure { error ->

                        _mensaje.value =
                            error.message
                                ?: "No se pudieron cargar las preferencias."
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudieron cargar las preferencias."
            }
        }
    }

    // =========================================================
    // CAMBIAR NOTIFICACIONES GENERALES
    // =========================================================

    fun cambiarEstado(
        activo: Boolean
    ) {

        actualizarPreferencias(
            activo = activo,
            ingresos = _ingresos.value,
            egresos = _egresos.value,
            zoe = _zoe.value,
            avisos = _avisos.value
        )
    }

    // =========================================================
    // CAMBIAR INGRESOS
    // =========================================================

    fun cambiarIngresos(
        activo: Boolean
    ) {

        actualizarPreferencias(
            activo = _notificacionesActivadas.value,
            ingresos = activo,
            egresos = _egresos.value,
            zoe = _zoe.value,
            avisos = _avisos.value
        )
    }

    // =========================================================
    // CAMBIAR EGRESOS
    // =========================================================

    fun cambiarEgresos(
        activo: Boolean
    ) {

        actualizarPreferencias(
            activo = _notificacionesActivadas.value,
            ingresos = _ingresos.value,
            egresos = activo,
            zoe = _zoe.value,
            avisos = _avisos.value
        )
    }

    // =========================================================
    // CAMBIAR ZOE
    // =========================================================

    fun cambiarZoe(
        activo: Boolean
    ) {

        actualizarPreferencias(
            activo = _notificacionesActivadas.value,
            ingresos = _ingresos.value,
            egresos = _egresos.value,
            zoe = activo,
            avisos = _avisos.value
        )
    }

    // =========================================================
    // CAMBIAR AVISOS
    // =========================================================

    fun cambiarAvisos(
        activo: Boolean
    ) {

        actualizarPreferencias(
            activo = _notificacionesActivadas.value,
            ingresos = _ingresos.value,
            egresos = _egresos.value,
            zoe = _zoe.value,
            avisos = activo
        )
    }

    // =========================================================
    // ACTUALIZAR TODAS LAS PREFERENCIAS
    // =========================================================

    private fun actualizarPreferencias(
        activo: Boolean,
        ingresos: Boolean,
        egresos: Boolean,
        zoe: Boolean,
        avisos: Boolean
    ) {

        viewModelScope.launch {

            _guardando.value = true
            _mensaje.value = null

            try {

                val token =
                    FcmTokenProvider.obtenerToken()

                val resultado =
                    repository.actualizar(
                        token = token,
                        activo = activo,
                        ingresos = ingresos,
                        egresos = egresos,
                        zoe = zoe,
                        avisos = avisos
                    )

                resultado
                    .onSuccess {

                        _notificacionesActivadas.value =
                            activo

                        _ingresos.value =
                            ingresos

                        _egresos.value =
                            egresos

                        _zoe.value =
                            zoe

                        _avisos.value =
                            avisos

                        _mensaje.value =
                            "Preferencias actualizadas."
                    }
                    .onFailure { error ->

                        _mensaje.value =
                            error.message
                                ?: "No se pudieron actualizar las preferencias."
                    }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudieron actualizar las preferencias."

            } finally {

                _guardando.value = false
            }
        }
    }

    // =========================================================
    // LIMPIAR MENSAJE
    // =========================================================

    fun limpiarMensaje() {

        _mensaje.value = null
    }
}