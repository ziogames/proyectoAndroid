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

// 📸 Nuevo modelo para mapear cada fotografía del carrusel/listado
data class ZoeFoto(
    val id: Int? = null,
    val url: String? = null,
    val path: String? = null
)

data class ZoeEmprendimiento(
    val id: Int? = null,
    val nombre: String? = null,
    val name: String? = null,             // 👈 Soporte clave en inglés
    val descripcion: String? = null,
    val description: String? = null,      // 👈 Soporte clave en inglés
    val categoria: String? = null,
    val telefono: String? = null,
    val phone: String? = null,            // 👈 Soporte clave en inglés
    val show_whatsapp: Boolean = false,
    val modalidad: String? = null,
    val modality: String? = null,         // 👈 Soporte clave en inglés
    val zona_atencion: String? = null,
    val attention_zone: String? = null,   // 👈 Soporte clave en inglés
    val horario: String? = null,
    val schedule: String? = null,         // 👈 Soporte clave en inglés
    val imagen: String? = null,           // 👈 URL de la imagen principal
    val imagen_url: String? = null,       // 👈 Respaldo de imagen principal
    val fotos: List<ZoeFoto> = emptyList(), // 👈 Lista completa de fotos
    val photos: List<ZoeFoto> = emptyList() // 👈 Soporte clave en inglés
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
    val datos: List<ZoeDatoEstadistico> = emptyList(),

    // Emprendimientos publicados encontrados por ZOE
    val total: Int? = null,
    val emprendimientos: List<ZoeEmprendimiento> = emptyList()
)