package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Periodo
import com.sigefiv.app.data.repository.PeriodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PeriodoViewModel(
    context: Context
) : ViewModel() {

    private val repository =
        PeriodoRepository(
            ApiClient.create(context)
        )

    private val _periodo =
        MutableStateFlow<Periodo?>(null)

    val periodo: StateFlow<Periodo?> =
        _periodo

    private val _cargando =
        MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando

    private val _mensaje =
        MutableStateFlow<String?>(null)

    val mensaje: StateFlow<String?> =
        _mensaje


    fun cargarPeriodoAbierto() {

        viewModelScope.launch {

            _cargando.value = true

            _mensaje.value = null

            try {

                val respuesta =
                    repository.obtenerPeriodoAbierto()

                if (respuesta.success &&
                    respuesta.periodo != null
                ) {

                    _periodo.value =
                        respuesta.periodo

                } else {

                    _periodo.value = null

                    _mensaje.value =
                        respuesta.message
                            ?: "No se pudo obtener el período activo."
                }

            } catch (e: Exception) {

                e.printStackTrace()

                _periodo.value = null

                _mensaje.value =
                    e.message
                        ?: "No se pudo obtener el período activo."

            } finally {

                _cargando.value = false
            }
        }
    }


    fun limpiarMensaje() {

        _mensaje.value = null
    }

    fun cerrarPeriodo(
        id: Int,
        onResultado: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {

            _cargando.value = true
            _mensaje.value = null

            try {

                val respuesta =
                    repository.cerrarPeriodo(id)

                if (respuesta.success) {

                    onResultado(
                        true,
                        respuesta.message
                            ?: "Período cerrado correctamente."
                    )

                } else {

                    onResultado(
                        false,
                        respuesta.message
                            ?: "No se pudo cerrar el período."
                    )
                }

            } catch (e: Exception) {

                e.printStackTrace()

                onResultado(
                    false,
                    e.message
                        ?: "No se pudo cerrar el período."
                )

            } finally {

                _cargando.value = false
            }
        }
    }
}