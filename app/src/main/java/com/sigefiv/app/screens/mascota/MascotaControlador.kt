package com.sigefiv.app.screens.mascota

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MascotaEventoUi(
    val titulo: String,
    val mensaje: String,
    val icono: String = "🤖"
)

object MascotaControlador {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _estado = MutableStateFlow(MascotaEstado.NORMAL)
    val estado: StateFlow<MascotaEstado> = _estado.asStateFlow()

    private val _eventoUi = MutableStateFlow<MascotaEventoUi?>(null)
    val eventoUi: StateFlow<MascotaEventoUi?> = _eventoUi.asStateFlow()

    private var eventoJob: Job? = null
    private var burbujaJob: Job? = null

    private const val DURACION_BURBUJA = 5000L

    // ============================================================
    // ESTADOS DE LA MASCOTA
    // ============================================================

    fun cambiarEstado(estado: MascotaEstado) {
        eventoJob?.cancel()
        _estado.value = estado
    }

    fun normal() =
        cambiarEstado(MascotaEstado.NORMAL)

    fun pensando() =
        cambiarEstado(MascotaEstado.PENSANDO)

    fun feliz() =
        cambiarEstado(MascotaEstado.FELIZ)

    fun atenta() =
        cambiarEstado(MascotaEstado.ATENTA)

    fun saludando() =
        cambiarEstado(MascotaEstado.SALUDANDO)

    fun celebrando() =
        cambiarEstado(MascotaEstado.CELEBRANDO)

    fun durmiendo() =
        cambiarEstado(MascotaEstado.DURMIENDO)

    // ============================================================
    // EVENTOS DE SIGEFIV
    // ============================================================

    fun evento(evento: MascotaEvento) {

        when (evento) {

            MascotaEvento.INGRESO_REGISTRADO ->
                reaccionar(
                    estado = MascotaEstado.FELIZ,
                    duracion = 2200,
                    titulo = "Nuevo ingreso",
                    mensaje = "Se registró un nuevo ingreso.",
                    icono = "💰"
                )

            MascotaEvento.EGRESO_REGISTRADO ->
                reaccionar(
                    estado = MascotaEstado.ATENTA,
                    duracion = 1800,
                    titulo = "Nuevo egreso",
                    mensaje = "Se registró un nuevo egreso.",
                    icono = "💸"
                )

            MascotaEvento.MOVIMIENTO_REGISTRADO ->
                reaccionar(
                    estado = MascotaEstado.FELIZ,
                    duracion = 1800,
                    titulo = "Movimiento registrado",
                    mensaje = "Se registró un nuevo movimiento.",
                    icono = "📋"
                )

            MascotaEvento.RESUMEN_ACTUALIZADO ->
                reaccionar(
                    estado = MascotaEstado.FELIZ,
                    duracion = 1500,
                    titulo = "Resumen actualizado",
                    mensaje = "Los totales del período fueron actualizados.",
                    icono = "📊"
                )

            MascotaEvento.PERIODO_ABIERTO ->
                reaccionar(
                    estado = MascotaEstado.SALUDANDO,
                    duracion = 2200,
                    titulo = "Período abierto",
                    mensaje = "ZOE ha abierto el nuevo período.",
                    icono = "📂"
                )

            MascotaEvento.PERIODO_CERRADO ->
                reaccionar(
                    estado = MascotaEstado.CELEBRANDO,
                    duracion = 3500,
                    titulo = "Período cerrado",
                    mensaje = "El período se cerró correctamente.",
                    icono = "🎉"
                )

            MascotaEvento.NOTIFICACION_RECIBIDA ->
                reaccionar(
                    estado = MascotaEstado.ATENTA,
                    duracion = 2200,
                    titulo = "Nueva notificación",
                    mensaje = "Tienes una nueva notificación.",
                    icono = "🔔"
                )

            MascotaEvento.ZOE_PENSANDO ->
                mostrarEstado(
                    estado = MascotaEstado.PENSANDO,
                    titulo = "ZOE",
                    mensaje = "Estoy pensando...",
                    icono = "🧠"
                )

            MascotaEvento.ZOE_RESPONDIENDO ->
                reaccionar(
                    estado = MascotaEstado.FELIZ,
                    duracion = 1800,
                    titulo = "ZOE",
                    mensaje = "Aquí tienes la respuesta.",
                    icono = "🤖"
                )

            MascotaEvento.ERROR ->
                reaccionar(
                    estado = MascotaEstado.ATENTA,
                    duracion = 2200,
                    titulo = "ZOE",
                    mensaje = "Ocurrió un problema. Intenta nuevamente.",
                    icono = "⚠️"
                )
        }
    }

    // ============================================================
    // ATAJOS DE EVENTOS
    // ============================================================

    fun ingresoRegistrado() =
        evento(MascotaEvento.INGRESO_REGISTRADO)

    fun egresoRegistrado() =
        evento(MascotaEvento.EGRESO_REGISTRADO)

    fun movimientoRegistrado() =
        evento(MascotaEvento.MOVIMIENTO_REGISTRADO)

    fun resumenActualizado() =
        evento(MascotaEvento.RESUMEN_ACTUALIZADO)

    fun periodoAbierto() =
        evento(MascotaEvento.PERIODO_ABIERTO)

    fun periodoCerrado() =
        evento(MascotaEvento.PERIODO_CERRADO)

    fun nuevaNotificacion() =
        evento(MascotaEvento.NOTIFICACION_RECIBIDA)

    fun error() =
        evento(MascotaEvento.ERROR)

    // ============================================================
    // MENSAJE INTELIGENTE DE ZOE
    //
    // Esta función permitirá posteriormente recibir mensajes
    // generados por n8n/ZOE sin modificar los eventos financieros.
    // ============================================================

    fun mensajeZoe(
        titulo: String = "ZOE",
        mensaje: String,
        icono: String = "🤖",
        estado: MascotaEstado = MascotaEstado.FELIZ,
        duracion: Long = 3000L
    ) {
        reaccionar(
            estado = estado,
            duracion = duracion,
            titulo = titulo,
            mensaje = mensaje,
            icono = icono
        )
    }

    // ============================================================
    // MOSTRAR ESTADO
    // ============================================================

    private fun mostrarEstado(
        estado: MascotaEstado,
        titulo: String,
        mensaje: String,
        icono: String
    ) {
        eventoJob?.cancel()
        burbujaJob?.cancel()

        _estado.value = estado

        _eventoUi.value = MascotaEventoUi(
            titulo = titulo,
            mensaje = mensaje,
            icono = icono
        )

        burbujaJob = scope.launch {
            delay(DURACION_BURBUJA)
            _eventoUi.value = null
        }
    }

    // ============================================================
    // REACCIÓN
    // ============================================================

    private fun reaccionar(
        estado: MascotaEstado,
        duracion: Long,
        titulo: String,
        mensaje: String,
        icono: String
    ) {
        eventoJob?.cancel()
        burbujaJob?.cancel()

        _estado.value = estado

        _eventoUi.value = MascotaEventoUi(
            titulo = titulo,
            mensaje = mensaje,
            icono = icono
        )

        // La nube desaparece después de 5 segundos.
        burbujaJob = scope.launch {
            delay(DURACION_BURBUJA)
            _eventoUi.value = null
        }

        // La mascota vuelve a NORMAL después de la reacción.
        eventoJob = scope.launch {
            delay(duracion)
            _estado.value = MascotaEstado.NORMAL
        }
    }

    // ============================================================
    // LIMPIAR BURBUJA
    // ============================================================

    fun limpiarEventoUi() {
        burbujaJob?.cancel()
        _eventoUi.value = null
    }
}