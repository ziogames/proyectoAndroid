package com.sigefiv.app.screens.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.data.model.ChatTypingUser
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import androidx.compose.foundation.BorderStroke

@Composable
fun ChatInputArea(
    texto: String,
    enviando: Boolean,
    esBloqueoPermanente: Boolean,
    mensajeRespondido: ChatMessage?,
    usuariosEscribiendo: List<ChatTypingUser>,
    onTextoChange: (String) -> Unit,
    onEnviar: () -> Unit,
    onAdjuntar: () -> Unit,
    onCancelarRespuesta: () -> Unit,
    estaGrabandoAudio: Boolean = false,
    audioListoParaEnviar: Boolean = false,
    onGrabarAudio: () -> Unit = {}
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())
    val colorCita = Color(0xFF15803D)
    val tieneTextoParaEnviar = texto.isNotBlank() || audioListoParaEnviar

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface) // CORREGIDO: Usa el color de superficie del tema en lugar de un verde sólido
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {

        // Indicador de usuarios escribiendo
        if (usuariosEscribiendo.isNotEmpty()) {
            val nombres = usuariosEscribiendo.joinToString(", ") { usuario ->
                usuario.name?.split(" ")?.firstOrNull() ?: "Vecino"
            }
            Text(
                text = "$nombres escribiendo...",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 12.dp, bottom = 6.dp)
            )
        }

        // 💬 Vista previa del mensaje citado (sin texto JSON)
        AnimatedVisibility(
            visible = mensajeRespondido != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (mensajeRespondido != null) {
                val textoPreview = remember(mensajeRespondido) {
                    if (mensajeRespondido.eliminado) {
                        "Este mensaje fue eliminado"
                    } else {
                        val adjunto = mensajeRespondido.obtenerArchivoAdjunto()
                        if (adjunto != null) {
                            val nombre = adjunto.nombre?.lowercase() ?: ""
                            val mime = adjunto.mime?.lowercase() ?: ""
                            val url = adjunto.url?.lowercase() ?: ""
                            val textoAdjunto = adjunto.texto ?: ""

                            val esAudio = nombre.endsWith(".m4a") ||
                                    nombre.endsWith(".opus") ||
                                    nombre.endsWith(".mp3") ||
                                    mime.contains("audio") ||
                                    url.contains(".m4a") ||
                                    url.contains(".opus") ||
                                    textoAdjunto.contains("nota de voz", ignoreCase = true)

                            if (esAudio) {
                                if (textoAdjunto.isNotBlank() && !textoAdjunto.equals("Nota de voz", ignoreCase = true)) {
                                    "🎙️ Nota de voz · $textoAdjunto"
                                } else {
                                    "🎙️ Nota de voz"
                                }
                            } else {
                                if (textoAdjunto.isNotBlank()) "📎 $textoAdjunto" else "📎 ${adjunto.nombre ?: "Archivo adjunto"}"
                            }
                        } else {
                            val textoCrudo = mensajeRespondido.mensaje ?: ""
                            if (textoCrudo.trim().startsWith("{") && textoCrudo.contains("\"tipo\":\"archivo\"")) {
                                "📎 Archivo adjunto"
                            } else {
                                textoCrudo.ifBlank { "Mensaje" }
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(
                            start = 10.dp,
                            end = 4.dp,
                            top = 7.dp,
                            bottom = 7.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(40.dp)
                                .background(
                                    colorCita,
                                    RoundedCornerShape(4.dp)
                                )
                        )

                        Spacer(Modifier.width(9.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = mensajeRespondido.usuario?.name ?: "Vecino",
                                color = colorCita,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )

                            Text(
                                text = textoPreview,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                maxLines = 1,
                                fontStyle = if (mensajeRespondido.eliminado) FontStyle.Italic else FontStyle.Normal
                            )
                        }

                        IconButton(
                            onClick = onCancelarRespuesta,
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Cancelar respuesta",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Fila de Entrada de Texto + Botón Enviar / Micrófono
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Clip (Adjuntar)
                    IconButton(
                        onClick = onAdjuntar,
                        enabled = !esBloqueoPermanente && !estaGrabandoAudio
                    ) {
                        Icon(
                            Icons.Outlined.AttachFile,
                            contentDescription = "Adjuntar",
                            tint = if (estaGrabandoAudio) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    BasicTextField(
                        value = if (esBloqueoPermanente) "" else texto,
                        onValueChange = onTextoChange,
                        enabled = !esBloqueoPermanente && !estaGrabandoAudio,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp, vertical =.9.dp),
                        maxLines = 4,
                        textStyle = LocalTextStyle.current.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        ),
                        decorationBox = { innerTextField ->
                            if (estaGrabandoAudio) {
                                Text(
                                    text = "🔴 Grabando nota de voz...",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            } else if (texto.isBlank() || esBloqueoPermanente) {
                                Text(
                                    text = if (esBloqueoPermanente) {
                                        "Cuenta suspendida."
                                    } else {
                                        "Escribe un mensaje..."
                                    },
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }
                    )
                }
            }

            // 🟢 Botón Circular Único (Micrófono <-> Enviar <-> Detener grabación)
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = when {
                    esBloqueoPermanente -> colorPrincipal.copy(alpha = 0.4f)
                    estaGrabandoAudio -> MaterialTheme.colorScheme.error
                    else -> colorPrincipal
                }
            ) {
                IconButton(
                    onClick = {
                        if (tieneTextoParaEnviar) {
                            onEnviar()
                        } else {
                            onGrabarAudio()
                        }
                    },
                    enabled = !esBloqueoPermanente && !enviando
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = when {
                                estaGrabandoAudio -> Icons.Outlined.Stop
                                tieneTextoParaEnviar -> Icons.Outlined.Send
                                else -> Icons.Outlined.Mic
                            },
                            contentDescription = if (tieneTextoParaEnviar) "Enviar" else "Grabar voz",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }
    }
}