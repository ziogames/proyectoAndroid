package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.MovimientoRequest
import com.sigefiv.app.data.repository.MovimientosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MovimientosViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        MovimientosRepository(
            ApiClient.create(context)
        )

    private val _movimientos =
        MutableStateFlow<List<Movimiento>>(emptyList())

    val movimientos: StateFlow<List<Movimiento>> =
        _movimientos

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando

    private val _guardando =
        MutableStateFlow(false)

    val guardando: StateFlow<Boolean> =
        _guardando

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje


    /**
     * Cargar movimientos desde Laravel.
     */
    fun cargarMovimientos(
        limite: Int? = null
    ) {

        viewModelScope.launch {

            _cargando.value = true



            try {

                val respuesta =
                    repository.obtenerMovimientos(
                        limite
                    )

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
     * Registrar un nuevo movimiento.
     */
    fun crearMovimiento(
        fecha: String,
        categoriaId: Int,
        concepto: String,
        persona: String?,
        formaPago: String,
        monto: Double,
        referencia: String?,
        observaciones: String?,
        onResultado: (Boolean) -> Unit = {}
    ) {

        viewModelScope.launch {

            _guardando.value = true

            _mensaje.value = null

            try {

                val request =
                    MovimientoRequest(

                        fecha =
                            fecha,

                        categoria_id =
                            categoriaId,

                        concepto =
                            concepto,

                        persona =
                            persona,

                        forma_pago =
                            formaPago,

                        monto =
                            monto,

                        referencia =
                            referencia,

                        observaciones =
                            observaciones
                    )


                val respuesta =
                    repository.crearMovimiento(
                        request
                    )


                if (respuesta.success) {

                    _mensaje.value =
                        respuesta.message

                    cargarMovimientos()

                    onResultado(true)

                } else {

                    _mensaje.value =
                        respuesta.message

                    onResultado(false)
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudo registrar el movimiento."

                onResultado(false)

            } finally {

                _guardando.value = false
            }
        }
    }


    /**
     * Limpiar mensaje.
     */
    fun limpiarMensaje() {

        _mensaje.value = null
    }
}