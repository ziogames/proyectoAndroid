package com.sigefiv.app.data.model

data class Movimiento(
    val id: Int,
    val numero: String,
    val fecha: String?,
    val tipo: String,
    val categoria: String?,
    val concepto: String,
    val persona: String?,
    val forma_pago: String?,
    val monto: Double,
    val comprobante: String?,
    val referencia: String?,
    val observaciones: String?,
    val estado: String,
    val periodo: MovimientoPeriodo? = null
)

data class MovimientoPeriodo(
    val id: Int,
    val nombre: String,
    val nombre_completo: String,
    val anio: Int,
    val mes: Int,
    val estado: String,
    val abierto: Boolean,
    val fecha_cierre: String?,
    val saldo_inicial: Double,
    val total_ingresos: Double,
    val total_egresos: Double,
    val saldo_final: Double
)