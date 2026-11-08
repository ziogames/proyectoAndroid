package com.sigefiv.app.data.model

data class IncidenciaItem(
    val id: Long,
    val user_id: Long,
    val mensaje: String,
    val imagen_path: String?,
    val estado: String?, // "pendiente", "en_proceso", "atendido"
    val created_at: String?
)

// Modelos para las respuestas de la API
data class IncidenciasResponse(
    val success: Boolean,
    val incidencias: List<IncidenciaItem>
)

data class ActualizarEstadoRequest(
    val estado: String
)

// 🛠️ Actualizado para soportar los campos del nuevo formulario y seguridad por Sanctum
data class IncidenciaRequest(
    val titulo: String,
    val categoria: String,
    val mensaje: String,
    val imagen_path: String? = null
)