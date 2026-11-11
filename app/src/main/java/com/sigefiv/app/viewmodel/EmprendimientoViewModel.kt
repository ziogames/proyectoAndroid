package com.sigefiv.app.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.data.api.OpinionRequest
import com.sigefiv.app.data.api.OpinionesDataResponse
import com.sigefiv.app.data.model.EmprendimientoModel
import com.sigefiv.app.data.repository.EmprendimientoRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.Normalizer

class EmprendimientoViewModel(
    private val repository: EmprendimientoRepository
) : ViewModel() {

    // Lista maestra intacta con todos los negocios cargados desde la API
    private var todosLosEmprendimientos: List<EmprendimientoModel> = emptyList()

    // Lista filtrada que observa EmprendimientosScreen
    var emprendimientos by mutableStateOf<List<EmprendimientoModel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var categoriaSeleccionada by mutableStateOf("Todos")
        private set

    private val baseUrl = ServidorConfig.API_BASE_URL.replace("/api/", "/storage/")
    private var cargaJob: Job? = null

    init {
        cargarEmprendimientosPublicos()
    }

    fun cargarEmprendimientosPublicos(categoria: String? = null) {
        categoriaSeleccionada = categoria ?: "Todos"
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            isLoading = true
            try {
                val categoriaParam = if (categoria == "Todos") null else categoria
                val resultado = repository.obtenerEmprendimientosPublicos(categoriaParam)

                val mapeados = resultado.map { item ->
                    val fotosMapeadas = item.photos?.map { foto ->
                        val rutaFinal = if (foto.path.startsWith("http")) foto.path else baseUrl + foto.path
                        foto.copy(path = rutaFinal)
                    } ?: emptyList()

                    EmprendimientoModel(
                        id = item.id.toInt(),
                        nombre = item.name,
                        descripcion = item.description ?: "",
                        categoria = item.categoria ?: "Servicios",
                        palabrasClave = item.keywords,
                        telefono = item.phone,
                        showWhatsapp = item.showWhatsapp,
                        modalidad = item.modality,
                        zonaAtencion = item.attentionZone,
                        horario = item.schedule,
                        esFavorito = item.esFavorito ?: false,
                        photos = fotosMapeadas,
                        promedioEstrellas = item.promedioCalificacion ?: 0.0f,
                        totalOpiniones = item.totalOpiniones ?: 0
                    )
                }

                // Guardamos la lista completa en memoria y aplicamos el filtro actual
                todosLosEmprendimientos = mapeados
                aplicarFiltroLocal(searchQuery)

                Log.d("API_EMPRENDIMIENTOS", "Total cargados: ${todosLosEmprendimientos.size}")
            } catch (e: Exception) {
                Log.e("API_EMPRENDIMIENTOS", "Error al cargar: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun buscarEmprendimientos(query: String) {
        searchQuery = query
        aplicarFiltroLocal(query)
    }

    private fun aplicarFiltroLocal(query: String) {
        val termino = normalizarTexto(query)

        if (termino.isEmpty()) {
            emprendimientos = todosLosEmprendimientos
            return
        }

        // Filtra siempre desde todosLosEmprendimientos para que al borrar letras vuelvan a aparecer
        emprendimientos = todosLosEmprendimientos.filter { negocio ->
            normalizarTexto(negocio.nombre).contains(termino) ||
                    normalizarTexto(negocio.descripcion).contains(termino) ||
                    normalizarTexto(negocio.palabrasClave ?: "").contains(termino) ||
                    normalizarTexto(negocio.categoria).contains(termino) ||
                    normalizarTexto(negocio.zonaAtencion ?: "").contains(termino)
        }
    }

    private fun normalizarTexto(texto: String): String {
        return Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD)
            .replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }

    // Lista específica para la pantalla de favoritos
    var emprendimientosFavoritos by mutableStateOf<List<EmprendimientoModel>>(emptyList())
        private set

    fun toggleFavorito(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.toggleFavorito(id)
                if (response.isSuccessful) {
                    val body = response.body()
                    val nuevoEstado = body?.esFavorito ?: false

                    todosLosEmprendimientos = todosLosEmprendimientos.map { negocio ->
                        if (negocio.id == id) negocio.copy(esFavorito = nuevoEstado) else negocio
                    }
                    emprendimientos = emprendimientos.map { negocio ->
                        if (negocio.id == id) negocio.copy(esFavorito = nuevoEstado) else negocio
                    }

                    cargarFavoritos()
                }
            } catch (e: Exception) {
                Log.e("API_EMPRENDIMIENTOS", "Error en favorito: ${e.message}", e)
            }
        }
    }

    fun cargarFavoritos() {
        viewModelScope.launch {
            try {
                val resultado = repository.getMisFavoritos()
                emprendimientosFavoritos = resultado.emprendimientos.map { dto ->
                    val fotosMapeadas = dto.photos?.map { foto ->
                        val rutaFinal = if (foto.path.startsWith("http")) foto.path else baseUrl + foto.path
                        foto.copy(path = rutaFinal)
                    } ?: emptyList()

                    EmprendimientoModel(
                        id = dto.id.toInt(),
                        nombre = dto.name,
                        descripcion = dto.description ?: "",
                        categoria = dto.categoria ?: "Servicios",
                        palabrasClave = dto.keywords,
                        telefono = dto.phone,
                        showWhatsapp = dto.showWhatsapp,
                        modalidad = dto.modality,
                        zonaAtencion = dto.attentionZone,
                        horario = dto.schedule,
                        esFavorito = true,
                        photos = fotosMapeadas,
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
    // ⭐ GESTIÓN DE OPINIONES Y CALIFICACIONES
    // ====================================================================
    var opinionesState by mutableStateOf<OpinionesDataResponse?>(null)
        private set

    var cargandoOpiniones by mutableStateOf(false)
        private set

    fun cargarOpiniones(emprendimientoId: Int) {
        viewModelScope.launch {
            cargandoOpiniones = true
            try {
                val response = repository.getOpiniones(emprendimientoId)
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
                val response = repository.enviarOpinion(emprendimientoId, request)
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