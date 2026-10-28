package com.sigefiv.app.data.model

data class EmprendimientoModel(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val telefono: String,
    val showWhatsapp: Boolean,
    val modalidad: String,
    val zonaAtencion: String?,
    val horario: String?,
    val esFavorito: Boolean,
    val photos: List<com.sigefiv.app.data.model.EmprendimientoFotoDto>? = emptyList() // 👈 Añade esta línea
)