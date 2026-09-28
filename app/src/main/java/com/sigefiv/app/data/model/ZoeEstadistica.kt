package com.sigefiv.app.data.model

data class ZoeDatoEstadistico(
    val periodo_id: Int? = null,
    val anio: Int? = null,
    val mes: Int? = null,
    val periodo: String? = null,
    val ingresos: Double? = null,
    val egresos: Double? = null,
    val saldo_inicial: Double? = null,
    val saldo_final: Double? = null,
    val categoria: String? = null,
    val monto: Double? = null
)

data class ZoeEstadistica(
    val tipo: String? = null,
    val titulo: String? = null,
    val descripcion: String? = null,
    val unidad: String? = null,
    val cantidad_periodos: Int? = null,
    val periodo: String? = null,
    val total_egresos: Double? = null,



    val ingresos_total: Double? = null,
    val egresos_total: Double? = null,
    val diferencia: Double? = null,
    val saldo_inicial: Double? = null,
    val saldo_final: Double? = null,

    val datos: List<ZoeDatoEstadistico> = emptyList()

)