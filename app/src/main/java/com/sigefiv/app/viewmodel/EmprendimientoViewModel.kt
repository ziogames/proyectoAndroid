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
import com.sigefiv.app.data.api.OpinionesDataResponse
import com.sigefiv.app.data.api.OpinionRequest

class EmprendimientoViewModel(
    private val repository: EmprendimientoRepository // 👈 Inyectamos el repositorio que conecta con la API
) : ViewModel() {

    var emprendimientos by mutableStateOf<List<EmprendimientoModel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set
    // 🌟 Nueva variable para recordar el chip seleccionado
    var categoriaSeleccionada by mutableStateOf("Todos")
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
        categoriaSeleccionada = categoria ?: "Todos"
        viewModelScope.launch {
            isLoading = true
            try {
                val categoriaParam = if (categoria == "Todos") null else categoria
                val resultado = repository.obtenerEmprendimientosPublicos(categoriaParam)

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
                        categoria = item.categoria ?: "Servicios",
                        telefono = item.phone,
                        showWhatsapp = item.showWhatsapp,
                        modalidad = item.modality,
                        zonaAtencion = item.attentionZone,
                        horario = item.schedule,
                        esFavorito = item.esFavorito ?: false,
                        photos = fotosMapeadas, // 👈 Fotos con URL completa listas para Coil
                        // 👇 AGREGA ESTAS DOS LÍNEAS AQUÍ:
                        promedioEstrellas = item.promedioCalificacion ?: 0.0f,
                        totalOpiniones = item.totalOpiniones ?: 0
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
                cargarEmprendimientosPublicos(if (categoriaSeleccionada == "Todos") null else categoriaSeleccionada)
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
                        categoria = dto.categoria ?: "Servicios",
                        telefono = dto.phone,
                        showWhatsapp = dto.showWhatsapp,
                        modalidad = dto.modality,
                        zonaAtencion = dto.attentionZone,
                        horario = dto.schedule,
                        esFavorito = true,
                        photos = fotosMapeadas,
                        // 👈 Fotos mapeadas en favoritos
                        promedioEstrellas = dto.promedioCalificacion ?: 0.0f,
                        totalOpiniones = dto.totalOpiniones ?: 0
                    )
                }
            } catch (e: Exception) {
                emprendimientosFavoritos = emptyList()
            }
        }
    }

    // ====================================================================
    // ⭐ GESTIÓN DE OPINIONES Y CALIFICACIONES (NUEVO)
    // ====================================================================
    var opinionesState by mutableStateOf<OpinionesDataResponse?>(null)
        private set

    var cargandoOpiniones by mutableStateOf(false)
        private set

    fun cargarOpiniones(emprendimientoId: Int) {
        viewModelScope.launch {
            cargandoOpiniones = true
            try {
                val response = repository.getOpiniones(emprendimientoId) // 👈 Directo al repositorio
                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("DEBUG_OPINIONES", "Promedio: ${body?.promedio} | Total: ${body?.total}")
                    opinionesState = response.body()
                }
            } catch (e: Exception) {
                Log.e("API_OPINIONES", "Error al cargar opiniones: ${e.message}", e)
            } finally {
                cargandoOpiniones = false
            }
        }
    }

    fun enviarOpinion(emprendimientoId: Int, calificacion: Int, comentario: String, onExito: () -> Unit) {
        viewModelScope.launch {
            try {
                val request = OpinionRequest(
                    calificacion = calificacion,
                    comentario = comentario.ifBlank { null }
                )
                val response = repository.enviarOpinion(emprendimientoId, request) // 👈 Directo al repositorio
                if (response.isSuccessful) {
                    cargarOpiniones(emprendimientoId)
                    onExito()
                }
            } catch (e: Exception) {
                Log.e("API_OPINIONES", "Error al enviar opinión: ${e.message}", e)
            }
        }
    }
}