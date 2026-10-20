
package com.sigefiv.app.screens.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.data.model.ChatTypingUser
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

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
    onCancelarRespuesta: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())
    val colorCita = Color(0xFF15803D)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorPrincipal)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        // Vista previa del mensaje que se responderá
        if (mensajeRespondido != null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface
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
                            .height(42.dp)
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
                            text = mensajeRespondido.usuario?.name
                                ?: "Vecino",
                            color = colorCita,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Text(
                            text = mensajeRespondido.mensaje
                                ?: "Mensaje adjunto",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            maxLines = 2
                        )
                    }

                    IconButton(
                        onClick = onCancelarRespuesta,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Cancelar respuesta"
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    IconButton(
                        onClick = onAdjuntar,
                        enabled = !esBloqueoPermanente
                    ) {
                        Icon(
                            Icons.Outlined.AttachFile,
                            contentDescription = "Adjuntar"
                        )
                    }

                    BasicTextField(
                        value = if (esBloqueoPermanente) "" else texto,
                        onValueChange = onTextoChange,
                        enabled = !esBloqueoPermanente,
                        modifier = Modifier
                            .weight(1f)
                            .padding(
                                horizontal = 4.dp,
                                vertical = 9.dp
                            ),
                        maxLines = 4,
                        decorationBox = { innerTextField ->
                            if (texto.isBlank() || esBloqueoPermanente) {
                                Text(
                                    text = if (esBloqueoPermanente) {
                                        "Cuenta suspendida."
                                    } else {
                                        "Escribe un mensaje..."
                                    },
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            innerTextField()
                        }
                    )
                }
            }

            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = if (
                    !esBloqueoPermanente &&
                    texto.isNotBlank()
                ) {
                    colorPrincipal
                } else {
                    colorPrincipal.copy(alpha = 0.4f)
                }
            ) {
                IconButton(
                    onClick = onEnviar,
                    enabled = !esBloqueoPermanente &&
                            texto.isNotBlank() &&
                            !enviando
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Outlined.Send,
                            contentDescription = "Enviar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
