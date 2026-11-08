package com.sigefiv.app.data.model

data class BitacoraResponse(
    val success: Boolean,
    val metricas: MetricasBitacora,
    val registros: List<BitacoraItem>
)

data class MetricasBitacora(
    val total_registros: Int,
    val usuarios_activos: Int,
    val modulos: Int,
    val acciones_hoy: Int
)