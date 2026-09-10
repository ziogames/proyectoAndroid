package com.sigefiv.app.data.model

data class Notificacion(
    val id: Int,
    val user_id: Int,
    val titulo: String,
    val mensaje: String,
    val tipo: String,
    val data: Map<String, String>?,
    val leida: Boolean,
    val fecha_lectura: String?,
    val created_at: String?,
    val updated_at: String?
)