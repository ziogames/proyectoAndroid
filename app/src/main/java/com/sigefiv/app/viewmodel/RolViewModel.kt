package com.sigefiv.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.RolDetalle
import com.sigefiv.app.data.repository.RolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RolUiState(
    val roles: List<RolDetalle> = emptyList(),
    val cargando: Boolean = false,
    val error: String? = null,
    val rolEnEdicion: RolDetalle? = null,
    val guardando: Boolean = false
)

class RolViewModel(
    private val repository: RolRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RolUiState())
    val uiState: StateFlow<RolUiState> = _uiState.asStateFlow()

    init {
        cargarRoles()
    }

    fun cargarRoles() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true, error = null)
            val resultado = repository.obtenerRoles()
            resultado.fold(
                onSuccess = { lista ->
                    _uiState.value = _uiState.value.copy(roles = lista, cargando = false)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message, cargando = false)
                }
            )
        }
    }

    fun cargarDetalleRol(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cargando = true, error = null, rolEnEdicion = null)
            val resultado = repository.obtenerRolDetalle(id)
            resultado.fold(
                onSuccess = { rol ->
                    _uiState.value = _uiState.value.copy(rolEnEdicion = rol, cargando = false)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(error = error.message, cargando = false)
                }
            )
        }
    }

    fun guardarRol(
        id: Int,
        nuevoNombre: String,
        permisos: List<String>,
        onCompletado: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(guardando = true)
            val resultado = repository.actualizarRol(id, nuevoNombre, permisos)
            _uiState.value = _uiState.value.copy(guardando = false)
            resultado.fold(
                onSuccess = {
                    cargarRoles()
                    onCompletado(true)
                },
                onFailure = {
                    onCompletado(false)
                }
            )
        }
    }
}