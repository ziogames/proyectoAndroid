package com.sigefiv.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.data.model.CrearAsambleaRequest
import com.sigefiv.app.data.repository.AsambleaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AsambleasUiState(
    val cargando: Boolean = false,
    val creando: Boolean = false,
    val actualizando: Boolean = false,
    val publicando: Boolean = false,
    val asambleas: List<Asamblea> = emptyList(),
    val error: String? = null,
    val mensaje: String? = null
)

class AsambleasViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository: AsambleaRepository =
        AsambleaRepository(
            ApiClient.asambleaApi(
                application.applicationContext
            )
        )

    private val _uiState =
        MutableStateFlow(
            AsambleasUiState()
        )

    val uiState: StateFlow<AsambleasUiState> =
        _uiState.asStateFlow()

    init {
        cargarAsambleas()
    }

    fun cargarAsambleas() {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    cargando = true,
                    error = null,
                    mensaje = null
                )

            repository.obtenerAsambleas()
                .onSuccess { asambleas ->

                    _uiState.value =
                        _uiState.value.copy(
                            cargando = false,
                            asambleas = asambleas,
                            error = null
                        )
                }
                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            cargando = false,
                            error =
                                error.message
                                    ?: "No se pudieron cargar las asambleas."
                        )
                }
        }
    }

    fun crearAsamblea(
        request: CrearAsambleaRequest,
        onResultado: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    creando = true,
                    error = null,
                    mensaje = null
                )

            repository.crearAsamblea(request)
                .onSuccess { asamblea ->

                    _uiState.value =
                        _uiState.value.copy(
                            creando = false,
                            asambleas =
                                listOf(asamblea) +
                                        _uiState.value.asambleas,
                            mensaje =
                                "Asamblea creada correctamente.",
                            error = null
                        )

                    onResultado(true)
                }
                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            creando = false,
                            error =
                                error.message
                                    ?: "No se pudo crear la asamblea."
                        )

                    onResultado(false)
                }
        }
    }

    fun actualizarAsamblea(
        id: Int,
        request: CrearAsambleaRequest,
        onResultado: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    actualizando = true,
                    error = null,
                    mensaje = null
                )

            repository.actualizarAsamblea(
                id = id,
                request = request
            )
                .onSuccess { asambleaActualizada ->

                    _uiState.value =
                        _uiState.value.copy(
                            actualizando = false,
                            asambleas =
                                _uiState.value.asambleas.map { asamblea ->
                                    if (asamblea.id == id) {
                                        asambleaActualizada
                                    } else {
                                        asamblea
                                    }
                                },
                            mensaje =
                                "Asamblea actualizada correctamente.",
                            error = null
                        )

                    onResultado(true)
                }
                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            actualizando = false,
                            error =
                                error.message
                                    ?: "No se pudo actualizar la asamblea."
                        )

                    onResultado(false)
                }
        }
    }

    fun eliminarAsamblea(
        id: Int
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    cargando = true,
                    error = null,
                    mensaje = null
                )

            repository.eliminarAsamblea(id)
                .onSuccess {

                    _uiState.value =
                        _uiState.value.copy(
                            cargando = false,
                            asambleas =
                                _uiState.value.asambleas
                                    .filter {
                                        it.id != id
                                    },
                            mensaje =
                                "Asamblea eliminada correctamente."
                        )
                }
                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            cargando = false,
                            error =
                                error.message
                                    ?: "No se pudo eliminar la asamblea."
                        )
                }
        }
    }

    fun publicarAsamblea(
        id: Int,
        onResultado: (Boolean) -> Unit = {}
    ) {
        viewModelScope.launch {

            _uiState.value =
                _uiState.value.copy(
                    publicando = true,
                    error = null,
                    mensaje = null
                )

            repository.publicarAsamblea(id)
                .onSuccess { asambleaPublicada ->

                    _uiState.value =
                        _uiState.value.copy(
                            publicando = false,
                            asambleas =
                                _uiState.value.asambleas.map { asamblea ->
                                    if (asamblea.id == id) {
                                        asambleaPublicada
                                    } else {
                                        asamblea
                                    }
                                },
                            mensaje =
                                "Asamblea publicada correctamente.",
                            error = null
                        )

                    onResultado(true)
                }
                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            publicando = false,
                            error =
                                error.message
                                    ?: "No se pudo publicar la asamblea."
                        )

                    onResultado(false)
                }
        }
    }
    fun obtenerAsamblea(
        id: Int,
        onResultado: (Asamblea?) -> Unit = {}
    ) {
        viewModelScope.launch {

            repository.obtenerAsamblea(id)
                .onSuccess { asamblea ->

                    onResultado(asamblea)
                }
                .onFailure { error ->

                    _uiState.value =
                        _uiState.value.copy(
                            error =
                                error.message
                                    ?: "No se pudo cargar la asamblea."
                        )

                    onResultado(null)
                }
        }
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
