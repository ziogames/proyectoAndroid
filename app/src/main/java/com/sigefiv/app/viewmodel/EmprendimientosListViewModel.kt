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
    val categoria: String,
    val status: String,
    val description: String?,
    val phone: String,
    val modality: String,
    val attentionZone: String?,
    val schedule: String?,
    val showWhatsapp: Boolean,
    val primaryPhotoUrl: String?
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

    init {
        cargarMisEmprendimientos()
    }

    fun cargarMisEmprendimientos() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getMisEmprendimientos().fold(
                onSuccess = { listDto ->
                    val items = listDto.map { dto ->
                        EmprendimientoItemUi(
                            id = dto.id,
                            nombre = dto.name,
                            categoria = dto.categoria?.name ?: "Categoría #${dto.categoriaId}",
                            status = dto.status,
                            description = dto.description,
                            phone = dto.phone,
                            modality = dto.modality,
                            attentionZone = dto.attentionZone,
                            schedule = dto.schedule,
                            showWhatsapp = dto.showWhatsapp,
                            primaryPhotoUrl = dto.photos?.firstOrNull()?.path
                        )
                    }
                    _state.update { it.copy(isLoading = false, emprendimientos = items) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.localizedMessage ?: "Error al cargar") }
                }
            )
        }
    }

    fun cambiarEstado(id: Long, nuevoEstado: String) {
        viewModelScope.launch {
            repository.updateStatus(id, mapOf("status" to nuevoEstado)).fold(
                onSuccess = {
                    cargarMisEmprendimientos() // Recarga la lista para ver el cambio reflejado
                },
                onFailure = { error ->
                    _state.update { it.copy(errorMessage = error.localizedMessage ?: "Error al cambiar estado") }
                }
            )
        }
    }
}