package com.sigefiv.app.data.model

import com.google.gson.annotations.SerializedName

data class FiltrosReporteResponse(
    val success: Boolean,
    val anios: List<Int>,
    val meses: List<MesDto>
)

data class MesDto(
    val id: Int,
    val nombre: String
)

data class ResumenReporteResponse(
    val success: Boolean,
    val anio: Int,
    val mes: Int?,
    val reporte: String,
    val data: ResumenDataDto
)

data class ResumenDataDto(
    @SerializedName("saldo_inicial") val saldoInicial: Double,
    val ingresos: Double,
    val disponible: Double,
    val egresos: Double,
    @SerializedName("saldo_caja") val saldoCaja: Double,
    @SerializedName("total_filas") val totalFilas: Int
)