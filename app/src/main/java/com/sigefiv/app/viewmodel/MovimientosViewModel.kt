package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.MovimientoRequest
import com.sigefiv.app.data.repository.MovimientosRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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

    // ------------------------------------------------------------
    // MOVIMIENTOS
    // ------------------------------------------------------------

    private val _movimientos =
        MutableStateFlow<List<Movimiento>>(emptyList())

    val movimientos: StateFlow<List<Movimiento>> =
        _movimientos

    // ------------------------------------------------------------
    // ESTADOS
    // ------------------------------------------------------------

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando

    private val _guardando =
        MutableStateFlow(false)

    val guardando: StateFlow<Boolean> =
        _guardando

    private val _eliminando =
        MutableStateFlow(false)

    val eliminando: StateFlow<Boolean> =
        _eliminando

    // ------------------------------------------------------------
    // MENSAJES
    // ------------------------------------------------------------

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje

    private var mensajeJob: Job? = null

    // ------------------------------------------------------------
    // CARGAR MOVIMIENTOS
    // ------------------------------------------------------------

    fun cargarMovimientos(limite: Int? = null) {

        viewModelScope.launch {

            _cargando.value = true

            try {

                val respuesta =
                    repository.obtenerMovimientos(limite)

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

    // ------------------------------------------------------------
    // CREAR MOVIMIENTO
    // ------------------------------------------------------------

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
                        fecha = fecha,
                        categoria_id = categoriaId,
                        concepto = concepto,
                        persona = persona,
                        forma_pago = formaPago,
                        monto = monto,
                        referencia = referencia,
                        observaciones = observaciones
                    )

                val respuesta =
                    repository.crearMovimiento(request)

                if (respuesta.success) {

                    mostrarMensajeTemporal(
                        respuesta.message
                    )

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

    // ------------------------------------------------------------
    // ACTUALIZAR MOVIMIENTO
    // ------------------------------------------------------------

    fun actualizarMovimiento(
        id: Int,
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
                        fecha = fecha,
                        categoria_id = categoriaId,
                        concepto = concepto,
                        persona = persona,
                        forma_pago = formaPago,
                        monto = monto,
                        referencia = referencia,
                        observaciones = observaciones
                    )

                val respuesta =
                    repository.actualizarMovimiento(
                        id,
                        request
                    )

                if (respuesta.success) {

                    mostrarMensajeTemporal(
                        respuesta.message
                    )

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
                        ?: "No se pudo actualizar el movimiento."

                onResultado(false)

            } finally {

                _guardando.value = false
            }
        }
    }

    // ------------------------------------------------------------
    // ELIMINAR MOVIMIENTO
    // ------------------------------------------------------------

    fun eliminarMovimiento(
        id: Int,
        onResultado: (Boolean) -> Unit = {}
    ) {

        viewModelScope.launch {

            _eliminando.value = true
            _mensaje.value = null

            try {

                val respuesta =
                    repository.eliminarMovimiento(id)

                if (respuesta.isSuccessful) {

                    mostrarMensajeTemporal(
                        "Movimiento eliminado correctamente."
                    )

                    cargarMovimientos()

                    onResultado(true)

                } else {

                    _mensaje.value =
                        when (respuesta.code()) {

                            401 ->
                                "Sesión no autorizada."

                            403 ->
                                "No tienes permiso para eliminar este movimiento."

                            404 ->
                                "El movimiento no existe."

                            422 ->
                                "No se puede eliminar el movimiento."

                            else ->
                                "No se pudo eliminar el movimiento. Código HTTP: ${respuesta.code()}."
                        }

                    onResultado(false)
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    e.message
                        ?: "No se pudo eliminar el movimiento."

                onResultado(false)

            } finally {

                _eliminando.value = false
            }
        }
    }

    // ------------------------------------------------------------
    // MENSAJE TEMPORAL
    // ------------------------------------------------------------

    private fun mostrarMensajeTemporal(
        texto: String
    ) {

        // Cancelar cualquier temporizador anterior
        mensajeJob?.cancel()

        mensajeJob =
            viewModelScope.launch {

                _mensaje.value = texto

                // Mostrar durante 3 segundos
                delay(3000)

                _mensaje.value = null
            }
    }

    // ------------------------------------------------------------
    // LIMPIAR MENSAJE
    // ------------------------------------------------------------

    fun limpiarMensaje() {

        mensajeJob?.cancel()
        mensajeJob = null

        _mensaje.value = null
    }

    // ------------------------------------------------------------
    // LIMPIEZA DEL VIEWMODEL
    // ------------------------------------------------------------

    override fun onCleared() {

        mensajeJob?.cancel()

        super.onCleared()
    }
}