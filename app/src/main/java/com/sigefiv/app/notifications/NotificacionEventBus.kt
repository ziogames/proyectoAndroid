package com.sigefiv.app.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class NotificacionEvento(
    val titulo: String,
    val mensaje: String
)

data class MovimientoActualizadoEvento(
    val movimientoId: Int? = null
)

object NotificacionEventBus {

    private val _evento =
        MutableSharedFlow<NotificacionEvento>(
            extraBufferCapacity = 1
        )

    val evento =
        _evento.asSharedFlow()

    private val _movimientoActualizado =
        MutableSharedFlow<MovimientoActualizadoEvento>(
            extraBufferCapacity = 1
        )

    val movimientoActualizado =
        _movimientoActualizado.asSharedFlow()

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

    suspend fun publicarMovimientoActualizado(
        movimientoId: Int? = null
    ) {
        _movimientoActualizado.emit(
            MovimientoActualizadoEvento(
                movimientoId = movimientoId
            )
        )
    }
}