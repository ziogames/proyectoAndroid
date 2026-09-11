package com.sigefiv.app.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class NotificacionEvento(
    val titulo: String,
    val mensaje: String
)

object NotificacionEventBus {

    private val _evento =
        MutableSharedFlow<NotificacionEvento>(
            extraBufferCapacity = 1
        )

    val evento =
        _evento.asSharedFlow()

    fun notificacionRecibida() {
        // Mantiene compatibilidad con el código existente.
    }

    suspend fun publicar(
        titulo: String,
        mensaje: String
    ) {
        _evento.emit(
            NotificacionEvento(
                titulo = titulo,
                mensaje = mensaje
            )
        )
    }
}