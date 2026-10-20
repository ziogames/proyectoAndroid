
package com.sigefiv.app.screens.chat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.screens.chat.model.ChatUIComponent
import com.sigefiv.app.screens.chat.model.procesarComponentes
import com.sigefiv.app.ui.components.AvatarUsuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageItem(
    mensaje: ChatMessage,
    esPropio: Boolean,
    esResaltado: Boolean,
    onResponder: () -> Unit,
    onMostrarReacciones: () -> Unit,
    onReaccionar: (String) -> Unit,
    onIrAlMensajeOriginal: (Int) -> Unit
) {
    val maxBurbujaWidth =
        (LocalConfiguration.current.screenWidthDp * 0.78).dp

    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val thresholdPx = with(LocalDensity.current) { 60.dp.toPx() }

    val colorBurbuja =
        if (esPropio) Color(0xFFD9FDD3)
        else Color(0xFFF0F4F1)

    val colorTexto = Color(0xFF172B1A)
    val colorHora = Color(0xFF5F6B61)
    val colorNombre = Color(0xFF15803D)

    val esZoe = mensaje.tipo == "sigi"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment =
            if (esPropio) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                if (esPropio) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            if (!esPropio) {
                if (esZoe) {
                    Box(
                        modifier = Modifier
                            .padding(top = 17.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF15803D)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.SmartToy,
                            contentDescription = "ZOE",
                            tint = Color.White,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                } else {
                    AvatarUsuario(
                        usuario = mensaje.usuario,
                        modifier = Modifier.padding(top = 17.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = maxBurbujaWidth),
                horizontalAlignment =
                    if (esPropio) Alignment.End else Alignment.Start
            ) {
                // Nombre fuera de la burbuja
                if (!esPropio) {
                    Text(
                        text = if (esZoe) {
                            "ZOE · ASISTENTE"
                        } else {
                            mensaje.usuario?.name ?: "Vecino"
                        },
                        color = colorNombre,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(
                            start = 4.dp,
                            bottom = 3.dp
                        )
                    )
                }

                Surface(
                    shape = RoundedCornerShape(
                        topStart = 13.dp,
                        topEnd = 13.dp,
                        bottomStart = if (esPropio) 13.dp else 3.dp,
                        bottomEnd = if (esPropio) 3.dp else 13.dp
                    ),
                    color = colorBurbuja,
                    shadowElevation = if (esResaltado) 4.dp else 0.dp,
                    modifier = Modifier
                        .widthIn(max = maxBurbujaWidth)
                        .offset {
                            IntOffset(offsetX.value.toInt(), 0)
                        }



                        .pointerInput(thresholdPx) {
                            detectHorizontalDragGestures(
                                onDragEnd = {
                                    val debeResponder = offsetX.value >= thresholdPx

                                    coroutineScope.launch {
                                        offsetX.animateTo(
                                            targetValue = 0f,
                                            animationSpec = spring(
                                                dampingRatio = Spring.DampingRatioMediumBouncy
                                            )
                                        )

                                        if (debeResponder) {
                                            onResponder()
                                        }
                                    }
                                },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()

                                    coroutineScope.launch {
                                        offsetX.snapTo(
                                            (offsetX.value + dragAmount).coerceIn(
                                                0f,
                                                thresholdPx * 1.5f
                                            )
                                        )
                                    }
                                }
                            )
                        }



                        .combinedClickable(
                            onClick = {},
                            onLongClick = onMostrarReacciones
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(
                            horizontal = 9.dp,
                            vertical = 5.dp
                        )
                    ) {
                        // Mensaje citado
                        if (mensaje.reply_to != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.05f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp)
                                    .combinedClickable(
                                        onClick = {
                                            onIrAlMensajeOriginal(
                                                mensaje.reply_to.id
                                            )
                                        }
                                    )
                            ) {
                                Row(
                                    modifier = Modifier.padding(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.5.dp)
                                            .height(26.dp)
                                            .background(colorNombre)
                                    )

                                    Spacer(
                                        modifier = Modifier.width(6.dp)
                                    )

                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = mensaje.reply_to.usuario?.name
                                                ?: "Vecino",
                                            color = colorNombre,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Text(
                                            text = mensaje.reply_to.mensaje
                                                ?: "Mensaje",
                                            color = colorHora,
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        // Texto, movimientos y archivos
                        val componentes = remember(mensaje) {
                            mensaje.procesarComponentes()
                        }

                        componentes.forEach { componente ->
                            when (componente) {
                                is ChatUIComponent.Texto -> {
                                    Text(
                                        text = componente.contenido,
                                        color = colorTexto,
                                        fontSize = 14.sp,
                                        lineHeight = 18.sp
                                    )
                                }

                                is ChatUIComponent.MovimientoFinanciero -> {
                                    Spacer(
                                        modifier = Modifier.height(3.dp)
                                    )

                                    TarjetaMovimientoInteligente(
                                        fecha = componente.fecha,
                                        tipo = componente.tipo,
                                        concepto = componente.concepto,
                                        categoria = componente.categoria,
                                        monto = componente.monto
                                    )
                                }

                                is ChatUIComponent.Archivo -> {
                                    TarjetaArchivoMensaje(
                                        archivo = componente.archivo
                                    )
                                }
                            }
                        }
                    }
                }

                // Hora fuera de la burbuja
                Text(
                    text = mensaje.created_at
                        ?.substringAfter("T")
                        ?.take(5)
                        ?: "",
                    color = colorHora,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.padding(
                        horizontal = 4.dp,
                        vertical = 2.dp
                    )
                )
            }
        }

        // Reacciones
        if (!mensaje.reacciones.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 1.dp,
                        start = if (esPropio) 0.dp else 38.dp,
                        end = if (esPropio) 0.dp else 4.dp
                    ),
                horizontalArrangement =
                    if (esPropio) Arrangement.End else Arrangement.Start
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    mensaje.reacciones.forEach { reaccion ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (reaccion.yo) {
                                Color(0xFF15803D).copy(alpha = 0.14f)
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            border = BorderStroke(
                                1.dp,
                                if (reaccion.yo) {
                                    Color(0xFF15803D).copy(alpha = 0.55f)
                                } else {
                                    Color.Black.copy(alpha = 0.08f)
                                }
                            ),
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    onReaccionar(reaccion.emoji)
                                },
                                onLongClick = onMostrarReacciones
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 7.dp,
                                    vertical = 3.dp
                                ),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement =
                                    Arrangement.spacedBy(3.dp)
                            ) {
                                Text(
                                    text = reaccion.emoji,
                                    fontSize = 14.sp
                                )

                                Text(
                                    text = reaccion.cantidad.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = if (reaccion.yo) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
