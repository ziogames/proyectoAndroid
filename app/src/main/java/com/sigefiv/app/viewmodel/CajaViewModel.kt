package com.sigefiv.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.CajaResponse
import com.sigefiv.app.data.repository.CajaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CajaViewModel(
    private val repository: CajaRepository
) : ViewModel() {

    private val _caja = MutableStateFlow<CajaResponse?>(null)
    val caja: StateFlow<CajaResponse?> = _caja.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun cargarCaja(anio: Int? = null) {

        viewModelScope.launch {

            _cargando.value = true
            _error.value = null

            val resultado = repository.obtenerCaja(anio)

            resultado
                .onSuccess { respuesta ->
                    _caja.value = respuesta
                }
                .onFailure { excepcion ->
                    _error.value =
                        excepcion.message
                            ?: "No se pudo obtener la información de Caja."
                }

            _cargando.value = false
        }
    }

    fun seleccionarAnio(anio: Int) {
        cargarCaja(anio)
    }

    fun limpiarError() {
        _error.value = null
    }
}