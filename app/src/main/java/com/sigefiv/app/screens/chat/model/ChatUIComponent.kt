package com.sigefiv.app.screens.chat.model

import com.google.gson.Gson
import com.sigefiv.app.data.model.ChatArchivoPayload
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.ui.components.MovimientoItemModel
import com.sigefiv.app.ServidorConfig

// DTO para representar el conteo de votos de la asamblea
data class AsambleaEstadisticasDto(
    val asistiran: Int = 0,
    val en_duda: Int = 0,
    val no_asistiran: Int = 0
)

sealed class ChatUIComponent {
    data class Texto(val contenido: String) : ChatUIComponent()

    data class MovimientoFinanciero(
        val fecha: String,
        val tipo: String,
        val concepto: String,
        val categoria: String,
        val monto: String,
        val esEgreso: Boolean
    ) : ChatUIComponent()

    // 📊 Agrupador para renderizar el reporte bancario con cabecera y botón "Ver más"
    data class ReporteFinanciero(
        val titulo: String,
        val movimientos: List<MovimientoItemModel>
    ) : ChatUIComponent()

    data class Archivo(
        val archivo: ChatArchivoPayload,
        val esImagen: Boolean,
        val urlCorregida: String?
    ) : ChatUIComponent()

    data class Estadistica(
        val estadistica: com.sigefiv.app.data.model.ZoeEstadistica
    ) : ChatUIComponent()

    // 🏪 Componente completo para tarjetas de emprendimientos vecinales
    data class Emprendimiento(
        val nombre: String,
        val categoria: String,
        val descripcion: String,
        val contacto: String,
        val imagenUrl: String? = null,
        val zona: String? = null,
        val horario: String? = null
    ) : ChatUIComponent()

    // 🗳️ Componente interactivo para votación y confirmación de Asambleas
    data class EncuestaAsamblea(
        val asambleaId: Long,
        val texto: String,
        val yaRespondido: Boolean = false,
        val estadisticas: AsambleaEstadisticasDto? = null
    ) : ChatUIComponent()
}

fun ChatMessage.procesarComponentes(): List<ChatUIComponent> {
    val componentes = mutableListOf<ChatUIComponent>()

    // 1. Archivo adjunto
    val archivoAdjunto = this.obtenerArchivoAdjunto()
    if (archivoAdjunto != null) {
        val urlCorregida = ServidorConfig.normalizarUrlArchivo(archivoAdjunto.url)
        val esImagen = archivoAdjunto.mime?.startsWith("image/") == true
        componentes.add(ChatUIComponent.Archivo(archivoAdjunto, esImagen, urlCorregida))
        return componentes
    }

    val textoBruto = this.mensaje ?: ""

    // 2. Detección de Encuesta de Asamblea
    if (this.tipo == "encuesta_asamblea") {
        // Extrae el ID si viene en el texto como [ASAMBLEA_ID:123], o usa el id del mensaje si no viene especificado
        val regexId = Regex("""\[ASAMBLEA_ID:(\d+)\]""")
        val matchId = regexId.find(textoBruto)
        val asambleaId = matchId?.groupValues?.get(1)?.toLongOrNull() ?: this.id.toLong()
        val textoLimpio = regexId.replace(textoBruto, "").trim()

        componentes.add(
            ChatUIComponent.EncuestaAsamblea(
                asambleaId = asambleaId,
                texto = textoLimpio,
                yaRespondido = false,
                estadisticas = null
            )
        )
        return componentes
    }

    // 3. Detección de JSON de estadísticas financieras
    if (textoBruto.trim().startsWith("{") && (
                textoBruto.contains("ingresos_vs_egresos") ||
                        textoBruto.contains("evolucion_saldo") ||
                        textoBruto.contains("egresos_por_categoria") ||
                        textoBruto.contains("resumen_financiero")
                )) {
        try {
            val estadisticaObj = Gson().fromJson(
                textoBruto,
                com.sigefiv.app.data.model.ZoeEstadistica::class.java
            )
            if (estadisticaObj != null) {
                componentes.add(ChatUIComponent.Estadistica(estadisticaObj))
                return componentes
            }
        } catch (_: Exception) {}
    }

    val lineas = textoBruto.lines().map { it.trim() }.filter { it.isNotBlank() }

    val regexMovimientoGuiones = Regex(
        """^(?:•|-|\d+\.)\s*(\d{2}[-/]\d{2}[-/]\d{4})\s*(?:—|-)\s*(.*?)\s*(?:—|-)\s*(.*?)\s*(?:—|-)\s*S?/?\s*([\d,]+(?:\.\d{1,2})?)$""",
        RegexOption.IGNORE_CASE
    )

    val movimientosReporte = mutableListOf<MovimientoItemModel>()
    val lineasTextoRestantes = mutableListOf<String>()

    lineas.forEach { linea ->
        val lineaLimpia = linea.replace("**", "").trim()
        val matchGuiones = regexMovimientoGuiones.find(lineaLimpia)

        if (matchGuiones != null) {
            val fecha = matchGuiones.groupValues[1]
            val categoria = matchGuiones.groupValues[2].trim()
            val concepto = matchGuiones.groupValues[3].trim()
            val monto = matchGuiones.groupValues[4].trim()

            movimientosReporte.add(
                MovimientoItemModel(
                    fecha = fecha,
                    categoria = categoria,
                    concepto = concepto,
                    monto = monto,
                    esEgreso = textoBruto.contains("egreso", ignoreCase = true) || textoBruto.contains("gasto", ignoreCase = true)
                )
            )
        } else if (linea.startsWith("•")) {
            val contenido = lineaLimpia.removePrefix("•").trim()
            val partes = contenido.split("|").map { it.trim() }

            val esMovimientoPipes = partes.getOrNull(0)?.matches(Regex("""^\d{2}[-/]\d{2}[-/]\d{4}$""")) == true

            if (esMovimientoPipes && !lineaLimpia.contains("total", ignoreCase = true)) {
                val tipo = partes.getOrNull(1) ?: ""
                val montoStr = partes.getOrNull(4) ?: ""
                val esEgreso = tipo.contains("egreso", ignoreCase = true) ||
                        tipo.contains("salida", ignoreCase = true) ||
                        montoStr.startsWith("-")

                movimientosReporte.add(
                    MovimientoItemModel(
                        fecha = partes.getOrNull(0) ?: "",
                        categoria = partes.getOrNull(3) ?: tipo,
                        concepto = partes.getOrNull(2) ?: "",
                        monto = montoStr,
                        esEgreso = esEgreso
                    )
                )
            } else if (!esMovimientoPipes && partes.size >= 2) {
                componentes.add(
                    ChatUIComponent.Emprendimiento(
                        nombre = partes.getOrNull(0)?.ifBlank { "Emprendimiento Vecinal" } ?: "Emprendimiento Vecinal",
                        categoria = partes.getOrNull(1) ?: "General",
                        descripcion = partes.getOrNull(2) ?: "",
                        contacto = partes.getOrNull(3) ?: "",
                        imagenUrl = partes.getOrNull(4)?.takeIf { it.isNotBlank() },
                        zona = partes.getOrNull(5)?.takeIf { it.isNotBlank() },
                        horario = partes.getOrNull(6)?.takeIf { it.isNotBlank() }
                    )
                )
            } else {
                lineasTextoRestantes.add(lineaLimpia)
            }
        } else {
            lineasTextoRestantes.add(lineaLimpia)
        }
    }

    // 4. Reporte financiero consolidado o texto simple
    if (movimientosReporte.isNotEmpty()) {
        val tituloReporte = lineasTextoRestantes.firstOrNull {
            it.contains("egreso", ignoreCase = true) ||
                    it.contains("ingreso", ignoreCase = true) ||
                    it.contains("movimiento", ignoreCase = true)
        }?.replace("🤖", "")?.replace("📋", "")?.trim() ?: "Resumen de operaciones"

        componentes.add(
            ChatUIComponent.ReporteFinanciero(
                titulo = tituloReporte,
                movimientos = movimientosReporte
            )
        )
    } else if (componentes.isEmpty()) {
        componentes.add(ChatUIComponent.Texto(textoBruto))
    }

    return componentes
}