
package com.sigefiv.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.Permiso
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
    val guardando: Boolean = false,
    val permisosDisponibles: Map<String, List<Permiso>> = emptyMap(),
    val cargandoPermisos: Boolean = false
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
            _uiState.value = _uiState.value.copy(
                cargando = true,
                error = null
            )

            repository.obtenerRoles().fold(
                onSuccess = { lista ->
                    _uiState.value = _uiState.value.copy(
                        roles = lista,
                        cargando = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Error al obtener roles",
                        cargando = false
                    )
                }
            )
        }
    }

    fun cargarPermisosParaNuevoRol() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargandoPermisos = true,
                error = null
            )

            repository.obtenerPermisos().fold(
                onSuccess = { permisos ->
                    _uiState.value = _uiState.value.copy(
                        permisosDisponibles = permisos,
                        cargandoPermisos = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Error al cargar permisos",
                        cargandoPermisos = false
                    )
                }
            )
        }
    }

    fun cargarDetalleRol(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                cargando = true,
                error = null,
                rolEnEdicion = null
            )

            repository.obtenerRolDetalle(id).fold(
                onSuccess = { rol ->
                    _uiState.value = _uiState.value.copy(
                        rolEnEdicion = rol,
                        cargando = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "Error al cargar el rol",
                        cargando = false
                    )
                }
            )
        }
    }

    fun crearRol(
        nombre: String,
        permisos: List<String>,
        onCompletado: (Boolean) -> Unit
    ) {
        if (nombre.isBlank()) {
            _uiState.value = _uiState.value.copy(
                error = "Escribe el nombre del rol"
            )
            onCompletado(false)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                guardando = true,
                error = null
            )

            val resultado = repository.crearRol(
                nombre.trim(),
                permisos
            )

            _uiState.value = _uiState.value.copy(
                guardando = false
            )

            resultado.fold(
                onSuccess = {
                    cargarRoles()
                    onCompletado(true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "No se pudo crear el rol"
                    )
                    onCompletado(false)
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
            _uiState.value = _uiState.value.copy(
                guardando = true,
                error = null
            )

            val resultado = repository.actualizarRol(
                id,
                nuevoNombre,
                permisos
            )

            _uiState.value = _uiState.value.copy(
                guardando = false
            )

            resultado.fold(
                onSuccess = {
                    cargarRoles()
                    onCompletado(true)
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "No se pudo actualizar el rol"
                    )
                    onCompletado(false)
                }
            )
        }
    }
}
