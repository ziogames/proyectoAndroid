package com.sigefiv.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.repository.EmprendimientoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmprendimientoItemUi(
    val id: Long,
    val nombre: String,
    val categoria: String?, // 👈 Cambiado a String? para admitir nulos de forma segura
    val description: String?,
    val phone: String,
    val showWhatsapp: Boolean,
    val modality: String,
    val attentionZone: String?,
    val schedule: String?,
    val status: String
)

data class EmprendimientosListState(
    val isLoading: Boolean = false,
    val emprendimientos: List<EmprendimientoItemUi> = emptyList(),
    val errorMessage: String? = null
)

class EmprendimientosListViewModel(
    private val repository: EmprendimientoRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EmprendimientosListState())
    val state: StateFlow<EmprendimientosListState> = _state.asStateFlow()

    fun cargarMisEmprendimientos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getMisEmprendimientos().fold(
                onSuccess = { listaDto ->
                    val itemsUi = listaDto.map { dto ->
                        EmprendimientoItemUi(
                            id = dto.id,
                            nombre = dto.name,
                            // 🔍 Si keywords es null, asignamos un texto por defecto o cadena vacía de forma segura
                            categoria = dto.keywords ?: "Sin palabras clave",
                            description = dto.description,
                            phone = dto.phone,
                            showWhatsapp = dto.showWhatsapp,
                            modality = dto.modality,
                            attentionZone = dto.attentionZone,
                            schedule = dto.schedule,
                            status = dto.status
                        )
                    }
                    _state.update { it.copy(isLoading = false, emprendimientos = itemsUi) }
                },
                onFailure = { err ->
                    _state.update {
                        it.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Error al cargar negocios")
                    }
                }
            )
        }
    }

    fun cambiarEstado(id: Long, nuevoEstado: String) {
        viewModelScope.launch {
            repository.updateStatus(id, mapOf("status" to nuevoEstado)).fold(
                onSuccess = {
                    cargarMisEmprendimientos() // Recargamos la lista para reflejar el cambio
                },
                onFailure = { err ->
                    _state.update { it.copy(errorMessage = "No se pudo cambiar el estado: ${err.localizedMessage}")}
                }
            )
        }
    }
    // 🗑️ Método para eliminar un emprendimiento por su ID
    fun eliminarEmprendimiento(id: Long) {
        viewModelScope.launch {
            repository.eliminarEmprendimiento(id).fold(
                onSuccess = {
                    // Recargamos la lista actual para que desaparezca la tarjeta borrada
                    cargarMisEmprendimientos()
                },
                onFailure = { err ->
                    _state.update {
                        it.copy(errorMessage = "No se pudo eliminar el negocio: ${err.localizedMessage}")
                    }
                }
            )
        }
    }
}