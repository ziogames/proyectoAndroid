package com.sigefiv.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.sigefiv.app.data.model.EmprendimientoModel
import com.sigefiv.app.data.repository.EmprendimientoRepository
import android.util.Log

class EmprendimientoViewModel(
    private val repository: EmprendimientoRepository // 👈 Inyectamos el repositorio que conecta con la API
) : ViewModel() {

    var emprendimientos by mutableStateOf<List<EmprendimientoModel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    // 🌐 URL base para el almacenamiento público en Laravel (Emulador Android por defecto)
    //private val baseUrl = "http://10.0.2.2:8080/storage/"
    // En tu EmprendimientoViewModel.kt:

    // O si prefieres usar el objeto de configuración directamente de forma dinámica:
 private val baseUrl = com.sigefiv.app.ServidorConfig.API_BASE_URL.replace("/api/", "/storage/")
    init {
        cargarEmprendimientosPublicos()
    }

    fun cargarEmprendimientosPublicos(categoria: String? = null) {
        viewModelScope.launch {
            isLoading = true
            try {
                val resultado = repository.obtenerEmprendimientosPublicos(categoria)

// 🔍 AÑADE ESTE LOG PARA INSPECCIONAR QUÉ LLEGA DESDE LA API
                resultado.forEach { item ->
                    Log.d("DEBUG_FOTOS", "Negocio: ${item.name} | Fotos encontradas: ${item.photos?.size ?: 0}")
                    item.photos?.forEach { foto ->
                        Log.d("DEBUG_FOTOS", " -> Path en DTO: ${foto.path}")
                    }
                }

                emprendimientos = resultado.map { item ->
                    // Mapeamos las fotos para asegurar que tengan la URL completa del servidor
                    val fotosMapeadas = item.photos?.map { foto ->
                        val rutaFinal = if (foto.path.startsWith("http")) foto.path else baseUrl + foto.path
                        foto.copy(path = rutaFinal)
                    } ?: emptyList()

                    EmprendimientoModel(
                        id = item.id.toInt(),
                        nombre = item.name,
                        descripcion = item.description ?: "",
                        categoria = if (!item.keywords.isNullOrBlank()) item.keywords!! else "General",
                        telefono = item.phone,
                        showWhatsapp = item.showWhatsapp,
                        modalidad = item.modality,
                        zonaAtencion = item.attentionZone,
                        horario = item.schedule,
                        esFavorito = item.esFavorito ?: false,
                        photos = fotosMapeadas // 👈 Fotos con URL completa listas para Coil
                    )
                }
                Log.d("API_EMPRENDIMIENTOS", "Total cargados: ${emprendimientos.size}")
            } catch (e: Exception) {
                Log.e("API_EMPRENDIMIENTOS", "Error al cargar: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun buscarEmprendimientos(query: String) {
        searchQuery = query
        viewModelScope.launch {
            if (query.length >= 2) {
                val filtrados = emprendimientos.filter {
                    it.nombre.contains(query, ignoreCase = true) ||
                            it.descripcion.contains(query, ignoreCase = true) ||
                            it.categoria.contains(query, ignoreCase = true)
                }
                emprendimientos = filtrados
            } else if (query.isEmpty()) {
                cargarEmprendimientosPublicos()
            }
        }
    }

    // Lista específica para la pantalla de favoritos
    var emprendimientosFavoritos by mutableStateOf<List<EmprendimientoModel>>(emptyList())
        private set

    // Alternar favorito y persistir en backend
    fun toggleFavorito(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleFavorito(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    val nuevoEstado = body?.esFavorito ?: false

                    // Actualizamos la lista pública local
                    emprendimientos = emprendimientos.map { negocio ->
                        if (negocio.id == id) negocio.copy(esFavorito = nuevoEstado) else negocio
                    }

                    // Actualizamos también la lista de favoritos si estamos en ella
                    cargarFavoritos()
                }
            } catch (e: Exception) {
                // Manejar error de red
            }
        }
    }

    // Cargar la lista de favoritos desde la API protegida de Laravel
    fun cargarFavoritos() {
        viewModelScope.launch {
            try {
                val resultado = repository.getMisFavoritos()
                emprendimientosFavoritos = resultado.emprendimientos.map { dto ->
                    // Mapeamos las fotos para los favoritos también
                    val fotosMapeadas = dto.photos?.map { foto ->
                        val rutaFinal = if (foto.path.startsWith("http")) foto.path else baseUrl + foto.path
                        foto.copy(path = rutaFinal)
                    } ?: emptyList()

                    EmprendimientoModel(
                        id = dto.id.toInt(),
                        nombre = dto.name,
                        descripcion = dto.description ?: "",
                        categoria = dto.keywords ?: "General",
                        telefono = dto.phone,
                        showWhatsapp = dto.showWhatsapp,
                        modalidad = dto.modality,
                        zonaAtencion = dto.attentionZone,
                        horario = dto.schedule,
                        esFavorito = true,
                        photos = fotosMapeadas // 👈 Fotos mapeadas en favoritos
                    )
                }
            } catch (e: Exception) {
                emprendimientosFavoritos = emptyList()
            }
        }
    }
}