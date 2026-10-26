package com.sigefiv.app.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.repository.EmprendimientoRepository
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

    // 🆔 Almacena el ID si estamos editando (null si es nuevo)
    private var emprendimientoIdEnEdicion: Long? = null

    fun onAction(action: EmprendimientoFormAction) {
        when (action) {
            is EmprendimientoFormAction.UpdateNombre -> _state.update { it.copy(nombre = action.nombre) }
            is EmprendimientoFormAction.UpdatePalabrasClave -> _state.update { it.copy(palabrasClave = action.palabrasClave) }
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

            val borrador = _state.value.descripcion.ifBlank { _state.value.palabrasClave }

            repository.generarDescripcionZoe(
                _state.value.nombre,
                borrador
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

    // 📥 Carga los datos del negocio existente en el formulario para editarlos
    fun cargarEmprendimiento(id: Long) {
        emprendimientoIdEnEdicion = id
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            repository.obtenerEmprendimientoPorId(id).fold(
                onSuccess = { item ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            nombre = item.name ?: "",
                            palabrasClave = item.keywords ?: "", // 🛡️ Protegido contra nulos
                            descripcion = item.description ?: "",
                            telefono = item.phone ?: "",
                            usarWhatsapp = item.showWhatsapp,
                            modalidad = item.modality ?: "ambos",
                            zonaAtencion = item.attentionZone ?: "",
                            horario = item.schedule ?: "",
                            status = item.status ?: "draft"
                        )
                    }
                },
                onFailure = { err ->
                    _state.update { it.copy(isLoading = false, errorMessage = "Error al cargar el negocio: ${err.localizedMessage}") }
                }
            )
        }
    }

    private fun saveEmprendimiento(targetStatus: String) {
        val st = _state.value
        if (st.nombre.isBlank() || st.palabrasClave.isBlank() || st.telefono.isBlank()) {
            _state.update {
                it.copy(errorMessage = "Completa los campos obligatorios: Nombre, Palabras clave y Teléfono.")
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val campos = mapOf(
                "name" to st.nombre.trim(),
                "keywords" to st.palabrasClave.trim(),
                "description" to st.descripcion.trim(),
                "phone" to st.telefono.trim(),
                "show_whatsapp" to if (st.usarWhatsapp) "1" else "0",
                "modality" to st.modalidad,
                "attention_zone" to st.zonaAtencion.trim(),
                "schedule" to st.horario.trim(),
                "status" to targetStatus
            )

            val urisFotos = st.fotos.map { Uri.parse(it) }
            val idActual = emprendimientoIdEnEdicion

            val resultado = if (idActual == null || idActual == 0L) {
                // Crear nuevo
                repository.crearEmprendimiento(campos, urisFotos)
            } else {
                // Actualizar existente
                repository.actualizarEmprendimiento(idActual, campos, urisFotos)
            }

            resultado.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            status = targetStatus,
                            successMessage = if (targetStatus == "published") "¡Emprendimiento guardado y publicado con éxito!" else "Borrador guardado correctamente."
                        )
                    }
                },
                onFailure = { err ->
                    _state.update { it.copy(isLoading = false, errorMessage = err.localizedMessage ?: "Error al guardar") }
                }
            )
        }
    }

    // 🧹 Limpia el formulario para registrar un negocio totalmente nuevo
    fun resetForm() {
        emprendimientoIdEnEdicion = null
        _state.value = EmprendimientoFormState()
    }
}