package com.sigefiv.app.data.model

import com.google.gson.Gson

data class ChatResponse(
    val success: Boolean,
    val message: String? = null,
    val chat: ChatInfo? = null,
    val personas: Int = 0,
    val personas_en_linea: Int = 0,
    val mensajes: List<ChatMessage> = emptyList(),
    val usuarios_escribiendo: List<ChatTypingUser> = emptyList(),

    // 📖 Estado de lectura del usuario
    val ultimo_leido_message_id: Int? = null,
    val primer_no_leido_id: Int? = null,
    val mensajes_no_leidos: Int = 0
)

data class ChatInfo(
    val id: Int,
    val nombre: String? = null
)

data class ChatMessage(
    val id: Int,
    val mensaje: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null,
    val editado: Boolean = false,
    val usuario: ChatUser? = null,
    val reply_to: ChatReplyInfo? = null,

    // ❤️ Reacciones del mensaje
    val reacciones: List<ChatReaccion>? = emptyList()
) {
    // 📁 Helper integrado para detectar y parsear archivos adjuntos de forma automática
    fun obtenerArchivoAdjunto(): ChatArchivoPayload? {
        return try {
            if (
                !mensaje.isNullOrBlank() &&
                mensaje.trim().startsWith("{") &&
                mensaje.contains("\"tipo\":\"archivo\"")
            ) {
                Gson().fromJson(
                    mensaje,
                    ChatArchivoPayload::class.java
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}

// ❤️ Modelo de una reacción agrupada
data class ChatReaccion(
    val emoji: String,
    val cantidad: Int = 0,
    val yo: Boolean = false
)

// ❤️ Respuesta de agregar/quitar reacción
data class ChatReaccionResponse(
    val success: Boolean,
    val message: String? = null,
    val accion: String? = null,
    val mensaje_id: Int? = null,
    val reacciones: List<ChatReaccion> = emptyList()
)

// 📁 Modelo exclusivo para mapear el JSON del archivo adjunto de Laravel
data class ChatArchivoPayload(
    val tipo: String?,
    val nombre: String?,
    val mime: String?,
    val tamano: Long?,
    val ruta: String?,
    val url: String?,
    val texto: String?
)

// 💬 Modelo ligero exclusivo para la tarjeta citada
data class ChatReplyInfo(
    val id: Int,
    val mensaje: String? = null,
    val usuario: ChatUser? = null
)

data class ChatUser(
    val id: Int,
    val name: String? = null,
    val email: String? = null
)

data class ChatTypingUser(
    val id: Int,
    val name: String? = null
)

data class ChatEnviarResponse(
    val success: Boolean,
    val message: String? = null,
    val mensaje: ChatMessage? = null,
    val sigi_analisis: SigiAnalisis? = null,
    val sigi_evento: SigiEvento? = null,
    val sigi_decision: Any? = null,
    val sigi: SigiMensaje? = null
)

data class SigiAnalisis(
    val analizado: Boolean = false,
    val categoria: String? = null,
    val intencion: String? = null,
    val prioridad: String? = null,
    val contextual: Boolean = false,
    val debe_intervenir: Boolean = false
)

data class SigiEvento(
    val id: Int? = null,
    val categoria: String? = null,
    val estado: String? = null,
    val total_reportes: Int = 0,
    val reportes_problema: Int = 0,
    val reportes_resueltos: Int = 0,
    val usuarios_afectados: Int = 0,
    val usuarios_restablecidos: Int = 0
)

data class SigiMensaje(
    val id: Int,
    val mensaje: String? = null,
    val tipo: String? = null,
    val editado: Boolean = false,
    val created_at: String? = null,
    val usuario: ChatUser? = null
)

data class ChatNuevosResponse(
    val success: Boolean,
    val message: String? = null,
    val personas_en_linea: Int = 0,
    val mensajes: List<ChatMessage> = emptyList(),
    val usuarios_escribiendo: List<ChatTypingUser> = emptyList()
)

data class ChatPresenciaResponse(
    val success: Boolean,
    val message: String? = null,
    val personas: Int = 0,
    val personas_en_linea: Int = 0,
    val usuarios_escribiendo: List<ChatTypingUser> = emptyList()
)

data class ChatEscribiendoRequest(
    val escribiendo: Boolean
)

data class ChatSimpleResponse(
    val success: Boolean,
    val message: String? = null
)

data class ChatMensajeRequest(
    val mensaje: String
)