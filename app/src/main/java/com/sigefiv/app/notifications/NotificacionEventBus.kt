package com.sigefiv.app.notifications

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificacionEventBus {

    private val _nuevaNotificacion =
        MutableSharedFlow<Unit>(
            extraBufferCapacity = 1
        )

    val nuevaNotificacion =
        _nuevaNotificacion.asSharedFlow()

    fun notificacionRecibida() {
        _nuevaNotificacion.tryEmit(Unit)
    }
}