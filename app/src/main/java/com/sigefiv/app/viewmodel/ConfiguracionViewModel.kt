package com.sigefiv.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.Configuracion
import com.sigefiv.app.data.repository.ConfiguracionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val TAG = "CONFIG_DEBUG"

data class ConfiguracionUiState(
    val configuracion: Configuracion = Configuracion(),
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val error: String? = null,
    val exitoMensaje: String? = null
)

class ConfiguracionViewModel(
    private val repository: ConfiguracionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    fun cargarConfiguracion() {
        viewModelScope.launch {
            Log.d(TAG, "Iniciando petición GET /api/configuracion...")
            _uiState.update { it.copy(cargando = true, error = null) }

            val result = repository.obtenerConfiguracion()

            result.onSuccess { data ->
                Log.d(TAG, "Configuración obtenida correctamente:")
                Log.d(TAG, " - Organización: ${data.organizacion}")
                Log.d(TAG, " - Moneda: ${data.simbolo_moneda} (${data.moneda})")
                Log.d(TAG, " - Directiva: Pte: ${data.presidente}, Tes: ${data.tesorero}")
                Log.d(TAG, " - Seguridad: Bitácora=${data.bitacora_activa}, Intentos=${data.intentos_login}")

                _uiState.update {
                    it.copy(
                        configuracion = data,
                        cargando = false,
                        error = null
                    )
                }
            }.onFailure { error ->
                Log.e(TAG, "Error al obtener configuración: ${error.message}", error)
                _uiState.update {
                    it.copy(
                        cargando = false,
                        error = error.localizedMessage ?: "Error al conectar con el servidor"
                    )
                }
            }
        }
    }

    fun guardarConfiguracion(nuevaConfiguracion: Configuracion, onExito: () -> Unit = {}) {
        viewModelScope.launch {
            Log.d(TAG, "Enviando actualización de configuración a /api/configuracion...")
            _uiState.update { it.copy(guardando = true, error = null, exitoMensaje = null) }

            val result = repository.guardarConfiguracion(nuevaConfiguracion)

            result.onSuccess { data ->
                Log.d(TAG, "Configuración guardada exitosamente en BD.")
                _uiState.update {
                    it.copy(
                        configuracion = data,
                        guardando = false,
                        exitoMensaje = "Configuración guardada exitosamente"
                    )
                }
                onExito()
            }.onFailure { error ->
                Log.e(TAG, "Error al guardar configuración: ${error.message}", error)
                _uiState.update {
                    it.copy(
                        guardando = false,
                        error = error.localizedMessage ?: "No se pudo guardar la configuración"
                    )
                }
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(error = null, exitoMensaje = null) }
    }
}