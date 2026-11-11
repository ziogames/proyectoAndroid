package com.sigefiv.app.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ReportesApiService
import com.sigefiv.app.data.model.MesDto
import com.sigefiv.app.data.model.ResumenDataDto
import com.sigefiv.app.ui.components.ReporteFileHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Lista fija de los 12 meses para garantizar opciones inmediatas en la UI
val MESES_POR_DEFECTO = listOf(
    MesDto(1, "Enero"),
    MesDto(2, "Febrero"),
    MesDto(3, "Marzo"),
    MesDto(4, "Abril"),
    MesDto(5, "Mayo"),
    MesDto(6, "Junio"),
    MesDto(7, "Julio"),
    MesDto(8, "Agosto"),
    MesDto(9, "Septiembre"),
    MesDto(10, "Octubre"),
    MesDto(11, "Noviembre"),
    MesDto(12, "Diciembre")
)

data class ReportesUiState(
    val cargandoFiltros: Boolean = false,
    val cargandoResumen: Boolean = false,
    val descargandoPdf: Boolean = false,
    val descargandoExcel: Boolean = false,
    val aniosDisponibles: List<Int> = listOf(2026, 2025, 2024),
    val mesesDisponibles: List<MesDto> = MESES_POR_DEFECTO, // Precargados por defecto
    val anioSeleccionado: Int = 2026,
    val mesSeleccionado: Int? = null, // null representa "Todo el año"
    val resumen: ResumenDataDto? = null,
    val error: String? = null
)

class ReportesViewModel(
    private val api: ReportesApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportesUiState())
    val uiState: StateFlow<ReportesUiState> = _uiState.asStateFlow()

    init {
        cargarFiltrosIniciales()
    }

    fun cargarFiltrosIniciales() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoFiltros = true, error = null) }
            try {
                val res = api.obtenerFiltros()
                if (res.isSuccessful && res.body() != null) {
                    val body = res.body()!!
                    val anios = if (body.anios.isNotEmpty()) body.anios else listOf(2026, 2025)
                    val meses = if (body.meses.isNotEmpty()) body.meses else MESES_POR_DEFECTO
                    val anioDefault = anios.first()

                    _uiState.update {
                        it.copy(
                            cargandoFiltros = false,
                            aniosDisponibles = anios,
                            mesesDisponibles = meses,
                            anioSeleccionado = anioDefault,
                            mesSeleccionado = null
                        )
                    }
                } else {
                    Log.e("REPORTES", "Error filtros: ${res.code()} - ${res.errorBody()?.string()}")
                    _uiState.update {
                        it.copy(
                            cargandoFiltros = false,
                            error = "Error ${res.code()}: No se pudieron cargar los filtros del servidor"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("REPORTES", "Excepción filtros", e)
                _uiState.update {
                    it.copy(
                        cargandoFiltros = false,
                        error = e.localizedMessage
                    )
                }
            } finally {
                cargarResumen()
            }
        }
    }

    fun seleccionarAnio(anio: Int) {
        _uiState.update { it.copy(anioSeleccionado = anio) }
        cargarResumen()
    }

    fun seleccionarMes(mes: Int?) {
        _uiState.update { it.copy(mesSeleccionado = mes) }
        cargarResumen()
    }

    fun cargarResumen() {
        val anio = _uiState.value.anioSeleccionado
        val mes = _uiState.value.mesSeleccionado

        viewModelScope.launch {
            _uiState.update { it.copy(cargandoResumen = true) }
            try {
                val res = api.obtenerResumen(anio = anio, mes = mes)
                if (res.isSuccessful && res.body() != null) {
                    _uiState.update {
                        it.copy(
                            cargandoResumen = false,
                            resumen = res.body()!!.data
                        )
                    }
                } else {
                    Log.e("REPORTES", "Error resumen: ${res.code()} - ${res.errorBody()?.string()}")
                    _uiState.update { it.copy(cargandoResumen = false) }
                }
            } catch (e: Exception) {
                Log.e("REPORTES", "Excepción resumen", e)
                _uiState.update { it.copy(cargandoResumen = false) }
            }
        }
    }

    fun exportarPdf(context: Context) {
        val anio = _uiState.value.anioSeleccionado
        val mes = _uiState.value.mesSeleccionado

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(descargandoPdf = true) }
            try {
                val res = api.descargarPdf(anio, mes)
                if (res.isSuccessful && res.body() != null) {
                    val sufijo = if (mes != null) "_mes_$mes" else "_anual"
                    val nombre = "SIGEFIV_${anio}${sufijo}.pdf"
                    ReporteFileHelper.guardarYCompartir(
                        context = context,
                        body = res.body()!!,
                        nombreArchivo = nombre,
                        mimeType = "application/pdf"
                    )
                } else {
                    Log.e("REPORTES", "Error descarga PDF: ${res.code()} - ${res.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("REPORTES", "Error al exportar PDF", e)
            } finally {
                _uiState.update { it.copy(descargandoPdf = false) }
            }
        }
    }

    fun exportarExcel(context: Context) {
        val anio = _uiState.value.anioSeleccionado
        val mes = _uiState.value.mesSeleccionado

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(descargandoExcel = true) }
            try {
                val res = api.descargarExcel(anio, mes)
                if (res.isSuccessful && res.body() != null) {
                    val sufijo = if (mes != null) "_mes_$mes" else "_anual"
                    val nombre = "SIGEFIV_${anio}${sufijo}.xlsx"
                    ReporteFileHelper.guardarYCompartir(
                        context = context,
                        body = res.body()!!,
                        nombreArchivo = nombre,
                        mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )
                } else {
                    Log.e("REPORTES", "Error descarga Excel: ${res.code()} - ${res.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("REPORTES", "Error al exportar Excel", e)
            } finally {
                _uiState.update { it.copy(descargandoExcel = false) }
            }
        }
    }
}