package com.sigefiv.app.data.model

data class ConversacionItem(
    val id: String,
    val titulo: String,
    val hora: String,
    val grupo: String // "HOY", "AYER", "ANTERIORES"
)

data class HistorialResponse(
    val success: Boolean,
    val historial: List<ConversacionItem>
)

data class DetalleConversacionResponse(
    val success: Boolean,
    val mensajes: List<ZoeMensajeBackendDto>
)

// Definimos ambos nombres para que no haya conflictos sin importar cuál llames
typealias ZoeMensajeDto = ZoeMensajeBackendDto

data class ZoeMensajeBackendDto(
    val texto: String,
    val esUsuario: Boolean,
    val hora: String
)
