package com.sigefiv.app.data.model

data class EmprendimientoModel(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val palabrasClave: String? = null,
    val telefono: String,
    val showWhatsapp: Boolean,
    val modalidad: String,
    val zonaAtencion: String?,
    val horario: String?,
    val esFavorito: Boolean,
    val photos: List<EmprendimientoFotoDto>? = emptyList(),
    // 🌟 NUEVOS CAMPOS PARA CALIFICACIONES EN EL DIRECTORIO
    val promedioEstrellas: Float = 0.0f,
    val totalOpiniones: Int = 0
)