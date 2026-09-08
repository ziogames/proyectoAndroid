package com.sigefiv.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.UsuarioListado
import com.sigefiv.app.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class UsuariosUiState(
    val cargando: Boolean = false,
    val cambiandoEstado: Boolean = false,
    val cambiandoRol: Boolean = false,
    val eliminandoUsuario: Boolean = false,
    val usuarios: List<UsuarioListado> = emptyList(),
    val error: String? = null,
    val mensaje: String? = null
)

class UsuarioViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            UsuariosUiState()
        )

    val uiState: StateFlow<UsuariosUiState> =
        _uiState.asStateFlow()

    fun cargarUsuarios(
        buscar: String? = null
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    cargando = true,
                    error = null,
                    mensaje = null
                )

            repository.obtenerUsuarios(
                buscar = buscar
            )
                .onSuccess { respuesta ->

                    if (respuesta.success) {

                        _uiState.value =
                            _uiState.value.copy(
                                cargando = false,
                                usuarios = respuesta.usuarios,
                                error = null,
                                mensaje = respuesta.message
                            )

                    } else {

                        _uiState.value =
                            _uiState.value.copy(
                                cargando = false,
                                error =
                                    respuesta.message
                                        ?: "No se pudieron obtener los usuarios.",
                                mensaje = null
                            )
                    }
                }
                .onFailure { excepcion ->

                    _uiState.value =
                        _uiState.value.copy(
                            cargando = false,
                            error =
                                excepcion.message
                                    ?: "No se pudieron obtener los usuarios.",
                            mensaje = null
                        )
                }
        }
    }

    fun cambiarEstado(
        usuarioId: Int,
        estado: String
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    cambiandoEstado = true,
                    error = null,
                    mensaje = null
                )

            repository.cambiarEstado(
                usuarioId = usuarioId,
                estado = estado
            )
                .onSuccess { respuesta ->

                    if (respuesta.success) {

                        val usuarioActualizado =
                            respuesta.usuarios.firstOrNull()

                        val usuariosActualizados =
                            if (usuarioActualizado != null) {

                                _uiState.value.usuarios.map { usuario ->

                                    if (usuario.id == usuarioActualizado.id) {
                                        usuarioActualizado
                                    } else {
                                        usuario
                                    }
                                }

                            } else {

                                _uiState.value.usuarios

                            }

                        _uiState.value =
                            _uiState.value.copy(
                                cambiandoEstado = false,
                                usuarios = usuariosActualizados,
                                error = null,
                                mensaje = respuesta.message
                            )

                    } else {

                        _uiState.value =
                            _uiState.value.copy(
                                cambiandoEstado = false,
                                error =
                                    respuesta.message
                                        ?: "No se pudo cambiar el estado del usuario.",
                                mensaje = null
                            )
                    }
                }
                .onFailure { excepcion ->

                    _uiState.value =
                        _uiState.value.copy(
                            cambiandoEstado = false,
                            error =
                                excepcion.message
                                    ?: "No se pudo cambiar el estado del usuario.",
                            mensaje = null
                        )
                }
        }
    }

    fun cambiarRol(
        usuarioId: Int,
        rol: String
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    cambiandoRol = true,
                    error = null,
                    mensaje = null
                )

            repository.cambiarRol(
                usuarioId = usuarioId,
                rol = rol
            )
                .onSuccess { respuesta ->

                    if (respuesta.success) {

                        val usuarioActualizado =
                            respuesta.usuarios.firstOrNull()

                        val usuariosActualizados =
                            if (usuarioActualizado != null) {

                                _uiState.value.usuarios.map { usuario ->

                                    if (usuario.id == usuarioActualizado.id) {
                                        usuarioActualizado
                                    } else {
                                        usuario
                                    }
                                }

                            } else {

                                _uiState.value.usuarios

                            }

                        _uiState.value =
                            _uiState.value.copy(
                                cambiandoRol = false,
                                usuarios = usuariosActualizados,
                                error = null,
                                mensaje = respuesta.message
                            )

                    } else {

                        _uiState.value =
                            _uiState.value.copy(
                                cambiandoRol = false,
                                error =
                                    respuesta.message
                                        ?: "No se pudo cambiar el rol del usuario.",
                                mensaje = null
                            )
                    }
                }
                .onFailure { excepcion ->

                    _uiState.value =
                        _uiState.value.copy(
                            cambiandoRol = false,
                            error =
                                excepcion.message
                                    ?: "No se pudo cambiar el rol del usuario.",
                            mensaje = null
                        )
                }
        }
    }

    fun eliminarUsuario(
        usuarioId: Int
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    eliminandoUsuario = true,
                    error = null,
                    mensaje = null
                )

            repository.eliminarUsuario(
                usuarioId = usuarioId
            )
                .onSuccess {

                    val usuariosActualizados =
                        _uiState.value.usuarios.filter { usuario ->
                            usuario.id != usuarioId
                        }

                    _uiState.value =
                        _uiState.value.copy(
                            eliminandoUsuario = false,
                            usuarios = usuariosActualizados,
                            error = null,
                            mensaje = "Usuario eliminado correctamente."
                        )
                }
                .onFailure { excepcion ->

                    _uiState.value =
                        _uiState.value.copy(
                            eliminandoUsuario = false,
                            error =
                                excepcion.message
                                    ?: "No se pudo eliminar el usuario.",
                            mensaje = null
                        )
                }
        }
    }

    fun actualizarEstado(
        usuarioId: Int,
        nuevoEstado: String
    ) {
        cambiarEstado(
            usuarioId = usuarioId,
            estado = nuevoEstado
        )
    }

    fun limpiarMensaje() {

        _uiState.value =
            _uiState.value.copy(
                mensaje = null
            )
    }

    fun limpiarError() {

        _uiState.value =
            _uiState.value.copy(
                error = null
            )
    }
}