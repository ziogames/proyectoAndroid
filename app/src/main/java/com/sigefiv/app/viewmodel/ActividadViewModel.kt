package com.sigefiv.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.ActividadResponse
import com.sigefiv.app.data.repository.ActividadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActividadViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository =
        ActividadRepository(application.applicationContext)

    private val _estado =
        MutableStateFlow<ActividadUiState>(ActividadUiState.Cargando)

    val estado: StateFlow<ActividadUiState> = _estado

    init {
        cargarActividades()
    }

    fun cargarActividades() {
        viewModelScope.launch {

            _estado.value = ActividadUiState.Cargando

            repository.obtenerActividades()
                .onSuccess { respuesta ->
                    _estado.value =
                        ActividadUiState.Exito(respuesta)
                }
                .onFailure { error ->
                    _estado.value =
                        ActividadUiState.Error(
                            error.message ?: "Error desconocido"
                        )
                }
        }
    }
}

sealed class ActividadUiState {

    data object Cargando : ActividadUiState()

    data class Exito(
        val respuesta: ActividadResponse
    ) : ActividadUiState()

    data class Error(
        val mensaje: String
    ) : ActividadUiState()
}