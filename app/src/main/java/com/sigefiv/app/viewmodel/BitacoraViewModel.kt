package com.sigefiv.app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.BitacoraItem
import com.sigefiv.app.data.model.MetricasBitacora
import com.sigefiv.app.data.repository.BitacoraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BitacoraUiState(
    val cargando: Boolean = false,
    val metricas: MetricasBitacora? = null,
    val registros: List<BitacoraItem> = emptyList(),
    val busqueda: String = "",
    val error: String? = null
)

class BitacoraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BitacoraRepository(application)

    private val _state = MutableStateFlow(BitacoraUiState())
    val state: StateFlow<BitacoraUiState> = _state.asStateFlow()

    init {
        cargarBitacora()
    }

    fun cargarBitacora(buscar: String? = null) {
        viewModelScope.launch {
            _state.update { it.copy(cargando = true, error = null) }
            try {
                val respuesta = repository.obtenerBitacora(buscar = buscar)
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val body = respuesta.body()!!

                    // 🔍 LOGS DE DIAGNÓSTICO
                    Log.d("BITACORA_DEBUG", "Registros recibidos: ${body.registros.size}")
                    Log.d("BITACORA_DEBUG", "Métricas: ${body.metricas}")

                    _state.update {
                        it.copy(
                            cargando = false,
                            metricas = body.metricas,
                            registros = body.registros
                        )
                    }
                } else {
                    val errorBody = respuesta.errorBody()?.string() ?: "Sin detalle"
                    Log.e("BITACORA_DEBUG", "Error HTTP ${respuesta.code()}: $errorBody")

                    _state.update {
                        it.copy(
                            cargando = false,
                            error = "No se pudo cargar la bitácora (Código: ${respuesta.code()})"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("BITACORA_DEBUG", "Excepción de red: ${e.localizedMessage}", e)
                _state.update {
                    it.copy(
                        cargando = false,
                        error = e.localizedMessage ?: "Error de conexión"
                    )
                }
            }
        }
    }

    fun onBuscarChange(nuevaBusqueda: String) {
        _state.update { it.copy(busqueda = nuevaBusqueda) }
        cargarBitacora(buscar = nuevaBusqueda.ifBlank { null })
    }
}