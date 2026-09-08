package com.sigefiv.app.data.model

data class Periodo(

    val id: Int,

    val nombre: String,

    val nombre_completo: String,

    val anio: Int,

    val mes: Int,

    val saldo_inicial: Double,

    val total_ingresos: Double,

    val total_egresos: Double,

    val saldo_final: Double,

    val estado: String,

    val fecha_cierre: String?
)