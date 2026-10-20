package com.sigefiv.app.screens.chat.model

import com.sigefiv.app.data.model.ChatArchivoPayload
import com.sigefiv.app.data.model.ChatMessage

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

    data class Archivo(
        val archivo: ChatArchivoPayload,
        val esImagen: Boolean,
        val urlCorregida: String?
    ) : ChatUIComponent()
}

fun ChatMessage.procesarComponentes(): List<ChatUIComponent> {
    val componentes = mutableListOf<ChatUIComponent>()
    val archivoAdjunto = this.obtenerArchivoAdjunto()

    if (archivoAdjunto != null) {
        val urlCorregida = archivoAdjunto.url?.replace("localhost", "10.0.2.2")
        val esImagen = archivoAdjunto.mime?.startsWith("image/") == true
        componentes.add(ChatUIComponent.Archivo(archivoAdjunto, esImagen, urlCorregida))
    } else {
        val lineas = this.mensaje?.lines()?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList()
        val tieneListado = lineas.any { it.startsWith("•") }

        if (tieneListado) {
            lineas.forEach { linea ->
                val lineaLimpia = linea.replace("**", "")
                if (linea.startsWith("•") && !lineaLimpia.contains("total", ignoreCase = true)) {
                    val partes = lineaLimpia.removePrefix("•").split("|").map { it.trim() }
                    val tipo = partes.getOrNull(1) ?: ""
                    val montoStr = partes.getOrNull(4) ?: ""
                    val esEgreso = tipo.contains("egreso", ignoreCase = true) ||
                            tipo.contains("salida", ignoreCase = true) ||
                            montoStr.startsWith("-")

                    componentes.add(
                        ChatUIComponent.MovimientoFinanciero(
                            fecha = partes.getOrNull(0) ?: "",
                            tipo = tipo,
                            concepto = partes.getOrNull(2) ?: "",
                            categoria = partes.getOrNull(3) ?: "",
                            monto = montoStr,
                            esEgreso = esEgreso
                        )
                    )
                } else {
                    componentes.add(ChatUIComponent.Texto(lineaLimpia))
                }
            }
        } else {
            componentes.add(ChatUIComponent.Texto(this.mensaje ?: ""))
        }
    }
    return componentes
}