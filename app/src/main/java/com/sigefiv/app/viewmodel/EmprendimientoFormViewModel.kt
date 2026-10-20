package com.sigefiv.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.repository.EmprendimientoRepository
import com.sigefiv.app.screens.emprendimiento.CategoriaUi
import com.sigefiv.app.screens.emprendimiento.EmprendimientoFormAction
import com.sigefiv.app.screens.emprendimiento.EmprendimientoFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EmprendimientoFormViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmprendimientoRepository(
        ApiClient.emprendimientoApi(application),
        application
    )

    private val _state = MutableStateFlow(EmprendimientoFormState())
    val state: StateFlow<EmprendimientoFormState> = _state.asStateFlow()

    init {
        loadCategorias()
    }

    private fun loadCategorias() {
        viewModelScope.launch {
            repository.obtenerCategorias().fold(
                onSuccess = { list ->
                    val uiList = list.map { CategoriaUi(it.id, it.name) }
                    _state.update { it.copy(categorias = uiList) }
                },
                onFailure = { error ->
                    // 👈 AHORA SÍ VEREMOS QUÉ ESTÁ PASANDO
                    android.util.Log.e("ZOE_DEBUG", "Error REAL al cargar categorías: ", error)

                    _state.update {
                        it.copy(errorMessage = "Error de categorías: ${error.message}")
                    }
                }
            )
        }
    }

    fun onAction(action: EmprendimientoFormAction) {
        when (action) {
            is EmprendimientoFormAction.UpdateNombre -> _state.update { it.copy(nombre = action.nombre) }
            is EmprendimientoFormAction.UpdateCategoria -> _state.update { it.copy(categoriaId = action.categoriaId) }
            is EmprendimientoFormAction.UpdateDescripcion -> _state.update { it.copy(descripcion = action.descripcion) }
            is EmprendimientoFormAction.UpdateTelefono -> _state.update { it.copy(telefono = action.telefono) }
            is EmprendimientoFormAction.ToggleWhatsapp -> _state.update { it.copy(usarWhatsapp = action.usar) }
            is EmprendimientoFormAction.UpdateModality -> _state.update { it.copy(modalidad = action.modalidad) }
            is EmprendimientoFormAction.UpdateZonaAtencion -> _state.update { it.copy(zonaAtencion = action.zona) }
            is EmprendimientoFormAction.UpdateHorario -> _state.update { it.copy(horario = action.horario) }
            is EmprendimientoFormAction.AddPhoto -> addPhotoUri(action.uri)
            is EmprendimientoFormAction.RemovePhoto -> {
                val currentPhotos = _state.value.fotos.toMutableList()
                if (action.index in currentPhotos.indices) {
                    currentPhotos.removeAt(action.index)
                    _state.update { it.copy(fotos = currentPhotos) }
                }
            }
            is EmprendimientoFormAction.RequestZoeDescription -> generateDescriptionWithZoe()
            is EmprendimientoFormAction.SaveAsDraft -> saveEmprendimiento("draft")
            is EmprendimientoFormAction.Publish -> saveEmprendimiento("published")
            is EmprendimientoFormAction.ClearMessages -> _state.update { it.copy(errorMessage = null, successMessage = null) }
        }
    }

    private fun addPhotoUri(uriString: String) {
        val current = _state.value.fotos
        if (current.size < 5) {
            _state.update { it.copy(fotos = current + uriString) }
        } else {
            _state.update { it.copy(errorMessage = "Máximo 5 fotografías permitidas") }
        }
    }

    private fun generateDescriptionWithZoe() {
        if (_state.value.nombre.isBlank()) {
            _state.update { it.copy(errorMessage = "Ingresa el nombre del emprendimiento para que ZOE pueda redactar.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingWithZoe = true, errorMessage = null) }

            // Pasamos el nombre y la descripción actual de la pantalla
            repository.generarDescripcionZoe(
                _state.value.nombre,
                _state.value.descripcion ?: ""
            ).fold(
                onSuccess = { desc ->
                    _state.update { it.copy(descripcion = desc, isGeneratingWithZoe = false) }
                },
                onFailure = {
                    _state.update { it.copy(isGeneratingWithZoe = false, errorMessage = "Error al conectar con ZOE.") }
                }
            )
        }
    }

    private fun saveEmprendimiento(targetStatus: String) {
        val st = _state.value
        if (st.nombre.isBlank() || st.telefono.isBlank() || st.categoriaId == null) {
            _state.update { it.copy(errorMessage = "Completa los campos obligatorios: Nombre, Categoría y Teléfono.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val campos = mapOf(
                "categoria_id" to st.categoriaId.toString(),
                "name" to st.nombre,
                "description" to (st.descripcion ?: ""),
                "phone" to st.telefono,
                "show_whatsapp" to if (st.usarWhatsapp) "1" else "0",
                "modality" to st.modalidad,
                "attention_zone" to (st.zonaAtencion ?: ""),
                "schedule" to (st.horario ?: ""),
                "status" to targetStatus
            )

            val urisFotos = st.fotos.map { Uri.parse(it) }

            repository.crearEmprendimiento(campos, urisFotos).fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            status = targetStatus,
                            successMessage = if (targetStatus == "published") "¡Emprendimiento publicado con éxito!" else "Borrador guardado correctamente."
                        )
                    }
                },
                onFailure = { err ->
                    _state.update { it.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Error al guardar") }
                }
            )
        }
    }
}