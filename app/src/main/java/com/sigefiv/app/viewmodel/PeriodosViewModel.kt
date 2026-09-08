package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.Periodo
import com.sigefiv.app.data.model.PeriodoDetalle
import com.sigefiv.app.data.repository.PeriodosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PeriodosViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        PeriodosRepository(
            ApiClient.create(context)
        )

    private val _periodos =
        MutableStateFlow<List<Periodo>>(emptyList())

    val periodos: StateFlow<List<Periodo>> =
        _periodos

    private val _periodoDetalle =
        MutableStateFlow<PeriodoDetalle?>(null)

    val periodoDetalle: StateFlow<PeriodoDetalle?> =
        _periodoDetalle

    private val _movimientos =
        MutableStateFlow<List<Movimiento>>(emptyList())

    val movimientos: StateFlow<List<Movimiento>> =
        _movimientos

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje


    /**
     * Cargar todos los períodos.
     */
    fun cargarPeriodos() {

        viewModelScope.launch {

            _cargando.value = true

            _mensaje.value = null

            try {

                val respuesta =
                    repository.obtenerPeriodos()

                if (respuesta.success) {

                    _periodos.value =
                        respuesta.periodos

                } else {

                    _mensaje.value =
                        "No se pudieron cargar los períodos."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudieron cargar los períodos."

            } finally {

                _cargando.value = false
            }
        }
    }


    /**
     * Cargar el resumen financiero de un período.
     */
    fun cargarPeriodo(
        id: Int
    ) {

        viewModelScope.launch {

            _cargando.value = true

            _mensaje.value = null

            try {

                val respuesta =
                    repository.obtenerPeriodo(id)

                if (respuesta.success) {

                    _periodoDetalle.value =
                        respuesta.periodo

                } else {

                    _mensaje.value =
                        "No se pudo cargar el período."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudo cargar el período."

            } finally {

                _cargando.value = false
            }
        }
    }


    /**
     * Cargar los movimientos de un período.
     */
    fun cargarMovimientosPeriodo(
        id: Int
    ) {

        viewModelScope.launch {

            _cargando.value = true

            _mensaje.value = null

            try {

                val respuesta =
                    repository.obtenerMovimientosPeriodo(id)

                if (respuesta.success) {

                    _movimientos.value =
                        respuesta.movimientos

                } else {

                    _mensaje.value =
                        "No se pudieron cargar los movimientos."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudieron cargar los movimientos."

            } finally {

                _cargando.value = false
            }
        }
    }


    /**
     * Limpiar el detalle seleccionado.
     */
    fun limpiarDetalle() {

        _periodoDetalle.value = null

        _movimientos.value = emptyList()

        _mensaje.value = null
    }


    /**
     * Limpiar mensaje.
     */
    fun limpiarMensaje() {

        _mensaje.value = null
    }
}