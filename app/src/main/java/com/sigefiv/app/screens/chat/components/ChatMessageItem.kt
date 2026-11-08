package com.sigefiv.app.screens.chat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.ChatArchivoPayload
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.screens.chat.model.ChatUIComponent
import com.sigefiv.app.screens.chat.model.procesarComponentes
import com.sigefiv.app.ui.components.AvatarUsuario
import com.sigefiv.app.ui.components.TarjetaEmprendimientoItem
import com.sigefiv.app.ui.components.TarjetaReporteFinanciero
import com.sigefiv.app.ui.components.ZoeEstadisticaChart
import com.sigefiv.app.utils.ChatMediaUtils
import kotlinx.coroutines.launch
import com.sigefiv.app.screens.ui.components.TarjetaConvocatoriaAsambleaItem

private fun esNotaDeVozPayload(payload: ChatArchivoPayload?): Boolean {
    if (payload == null) return false
    val nombre = payload.nombre?.lowercase() ?: ""
    val mime = payload.mime?.lowercase() ?: ""
    val url = payload.url?.lowercase() ?: ""
    val texto = payload.texto?.lowercase() ?: ""

    return nombre.endsWith(".m4a") ||
            nombre.endsWith(".opus") ||
            nombre.endsWith(".mp3") ||
            nombre.endsWith(".mp4") ||
            mime.contains("audio") ||
            url.contains(".m4a") ||
            url.contains(".opus") ||
            url.contains(".mp4") ||
            texto.contains("nota de voz")
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatMessageItem(
    mensaje: ChatMessage,
    esPropio: Boolean,
    esResaltado: Boolean,
    darkTheme: Boolean = false,
    onResponder: () -> Unit,
    onMostrarReacciones: () -> Unit,
    onReaccionar: (String) -> Unit,
    onIrAlMensajeOriginal: (Int) -> Unit,
    onConfirmarAsistencia: (Long, String) -> Unit = { _, _ -> }
) {
    val maxBurbujaWidth = (LocalConfiguration.current.screenWidthDp * 0.78).dp
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val thresholdPx = with(LocalDensity.current) { 60.dp.toPx() }

    val isDark = darkTheme || MaterialTheme.colorScheme.background.luminance() < 0.5f

    val colorBurbuja = if (esPropio) {
        if (isDark) Color(0xFF005C4B) else Color(0xFFE7FFDB)
    } else {
        if (isDark) Color(0xFF202C33) else Color(0xFFFFFFFF)
    }

    val colorTexto = if (isDark) Color(0xFFE9EDEF) else Color(0xFF111B21)
    val colorHora = if (isDark) Color(0x99E9EDEF) else Color(0xFF667781)
    val colorNombre = if (isDark) Color(0xFF53BDEB) else Color(0xFF15803D)
    val colorEliminado = if (isDark) Color(0xFF8696A0) else Color(0xFF667781)

    val esZoe = mensaje.tipo == "sigi"

    val mediaInfo = remember(mensaje.mensaje, mensaje.eliminado) {
        if (!mensaje.eliminado) {
            mensaje.mensaje?.let { ChatMediaUtils.parseMediaUrl(it) }
        } else {
            null
        }
    }

    // 📁 Detección limpia usando el modelo nativo
    val adjunto = remember(mensaje) { mensaje.obtenerArchivoAdjunto() }
    val esNotaDeVoz = remember(adjunto) { esNotaDeVozPayload(adjunto) }
    val audioUrl = adjunto?.url

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (esPropio) Alignment.End else Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (esPropio) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            // ✅ Se oculta el avatar exterior si el mensaje es una nota de voz
            if (!esPropio && !esNotaDeVoz) {
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
                horizontalAlignment = if (esPropio) Alignment.End else Alignment.Start
            ) {
                if (!esPropio) {
                    Text(
                        text = if (esZoe) "ZOE · ASISTENTE" else mensaje.usuario?.name ?: "Vecino",
                        color = colorNombre,
                        fontSize = 11.sp,
                        lineHeight = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp, bottom = 3.dp)
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
                    shadowElevation = if (esResaltado) 4.dp else 1.dp,
                    modifier = Modifier
                        .widthIn(max = maxBurbujaWidth)
                        .offset { IntOffset(offsetX.value.toInt(), 0) }
                        .pointerInput(thresholdPx, mensaje.eliminado) {
                            if (!mensaje.eliminado) {
                                detectHorizontalDragGestures(
                                    onDragEnd = {
                                        val debeResponder = offsetX.value >= thresholdPx
                                        coroutineScope.launch {
                                            offsetX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                            if (debeResponder) onResponder()
                                        }
                                    },
                                    onHorizontalDrag = { change, dragAmount ->
                                        change.consume()
                                        coroutineScope.launch {
                                            offsetX.snapTo((offsetX.value + dragAmount).coerceIn(0f, thresholdPx * 1.5f))
                                        }
                                    }
                                )
                            }
                        }
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                if (!mensaje.eliminado) {
                                    onMostrarReacciones()
                                }
                            }
                        )
                ) {
                    CompositionLocalProvider(LocalContentColor provides colorTexto) {
                        Column(
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                        ) {
                            // Cita de respuesta previa
                            if (mensaje.reply_to != null && !mensaje.eliminado) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 4.dp)
                                        .combinedClickable(
                                            onClick = { onIrAlMensajeOriginal(mensaje.reply_to.id) }
                                        )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(26.dp)
                                                .background(colorNombre, RoundedCornerShape(2.dp))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mensaje.reply_to.usuario?.name ?: "Vecino",
                                                color = colorNombre,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            // ✅ Ahora:
                                            val textoCita = remember(mensaje.reply_to) {
                                                if (mensaje.reply_to == null) ""
                                                else if (mensaje.reply_to.eliminado) "Mensaje eliminado"
                                                else {
                                                    val textoCrudo = mensaje.reply_to.mensaje ?: ""
                                                    if (textoCrudo.trim().startsWith("{") && textoCrudo.contains("\"tipo\":\"archivo\"")) {
                                                        if (textoCrudo.contains(".m4a") || textoCrudo.contains("audio") || textoCrudo.contains("Nota de voz")) {
                                                            "🎙️ Nota de voz"
                                                        } else {
                                                            "📎 Archivo adjunto"
                                                        }
                                                    } else {
                                                        textoCrudo
                                                    }
                                                }
                                            }

                                            Text(
                                                text = textoCita,
                                                color = colorHora,
                                                fontSize = 11.sp,
                                                maxLines = 1,
                                                fontStyle = if (mensaje.reply_to.eliminado) FontStyle.Italic else FontStyle.Normal
                                            )
                                        }
                                    }
                                }
                            }

                            // 🚫 CASO A: Mensaje Eliminado
                            if (mensaje.eliminado) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Mensaje eliminado",
                                        tint = colorEliminado,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = if (mensaje.eliminado_por_moderador) {
                                            "Este mensaje fue eliminado por un moderador"
                                        } else {
                                            "Este mensaje fue eliminado"
                                        },
                                        fontSize = 13.5.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = colorEliminado
                                    )
                                }
                            } else {
                                // 🎙️ CASO B: Nota de Voz
                                if (esNotaDeVoz && !audioUrl.isNullOrBlank()) {
                                    val textoAdicional = adjunto?.texto?.takeIf {
                                        !it.equals("Nota de voz", ignoreCase = true) && it.isNotBlank()
                                    }

                                    if (!textoAdicional.isNullOrBlank()) {
                                        Text(
                                            text = textoAdicional,
                                            fontSize = 14.sp,
                                            lineHeight = 18.sp,
                                            modifier = Modifier.padding(bottom = 4.dp)
                                        )
                                    }

                                    AudioPlayerItem(
                                        audioUrl = audioUrl,
                                        esPropio = esPropio,
                                        colorNombre = colorNombre,
                                        isDark = isDark,
                                        horaMensaje = mensaje.created_at?.substringAfter("T")?.take(5) ?: "",
                                        avatar = {
                                            AvatarUsuario(
                                                usuario = mensaje.usuario,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                            )
                                        }
                                    )
                                } else {
                                    // 💬 CASO C: Contenido Normal y Componentes
                                    val componentes = remember(mensaje) { mensaje.procesarComponentes() }

                                    componentes.forEach { componente ->
                                        when (componente) {
                                            is ChatUIComponent.Texto -> {
                                                val textoComponente = componente.contenido.trim()
                                                val esJson = textoComponente.startsWith("{") && textoComponente.endsWith("}")
                                                val esEnlaceMultimedia = ChatMediaUtils.parseMediaUrl(componente.contenido) != null

                                                if (!esJson && !esEnlaceMultimedia) {
                                                    Text(
                                                        text = componente.contenido,
                                                        fontSize = 14.sp,
                                                        lineHeight = 18.sp
                                                    )
                                                }
                                            }
                                            is ChatUIComponent.ReporteFinanciero -> {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                TarjetaReporteFinanciero(
                                                    titulo = componente.titulo,
                                                    movimientos = componente.movimientos,
                                                    colorPrincipal = colorNombre,
                                                    temaOscuro = isDark
                                                )
                                            }
                                            is ChatUIComponent.EncuestaAsamblea -> {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                TarjetaConvocatoriaAsambleaItem(
                                                    asambleaId = componente.asambleaId,
                                                    texto = componente.texto,
                                                    yaRespondido = componente.yaRespondido,
                                                    estadisticas = componente.estadisticas,
                                                    colorPrincipal = colorNombre,
                                                    temaOscuro = isDark,
                                                    onConfirmar = { opcion ->
                                                        onConfirmarAsistencia(componente.asambleaId, opcion)
                                                    }
                                                )
                                            }
                                            is ChatUIComponent.MovimientoFinanciero -> {
                                                Spacer(modifier = Modifier.height(3.dp))
                                                TarjetaMovimientoInteligente(
                                                    fecha = componente.fecha,
                                                    tipo = componente.tipo,
                                                    concepto = componente.concepto,
                                                    categoria = componente.categoria,
                                                    monto = componente.monto
                                                )
                                            }
                                            is ChatUIComponent.Archivo -> {
                                                TarjetaArchivoMensaje(archivo = componente.archivo)
                                            }
                                            is ChatUIComponent.Emprendimiento -> {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                TarjetaEmprendimientoItem(
                                                    emprendimiento = componente,
                                                    colorPrincipal = colorNombre,
                                                    temaOscuro = isDark
                                                )
                                            }
                                            is ChatUIComponent.Estadistica -> {
                                                Spacer(modifier = Modifier.height(4.dp))
                                                ZoeEstadisticaChart(
                                                    estadistica = componente.estadistica,
                                                    modifier = Modifier.fillMaxWidth()
                                                )
                                            }
                                        }
                                    }

                                    if (mediaInfo != null && !esNotaDeVoz) {
                                        MediaEmbedView(mediaInfo = mediaInfo)
                                    }
                                }
                            }
                        }
                    }
                }

                // 🕒 Hora exterior (solo para textos o fotos normales, no para notas de voz)
                if (!esNotaDeVoz) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        if (mensaje.editado && !mensaje.eliminado) {
                            Text(
                                text = "editado",
                                color = colorHora,
                                fontSize = 10.sp,
                                fontStyle = FontStyle.Italic,
                                lineHeight = 12.sp
                            )
                        }
                        Text(
                            text = mensaje.created_at?.substringAfter("T")?.take(5) ?: "",
                            color = colorHora,
                            fontSize = 10.sp,
                            lineHeight = 12.sp
                        )
                    }
                }
            }
        }

        // ❤️ Barra de Reacciones
        if (!mensaje.reacciones.isNullOrEmpty() && !mensaje.eliminado) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 1.dp,
                        start = if (esPropio) 0.dp else (if (esNotaDeVoz) 4.dp else 38.dp),
                        end = if (esPropio) 0.dp else 4.dp
                    ),
                horizontalArrangement = if (esPropio) Arrangement.End else Arrangement.Start
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    mensaje.reacciones.forEach { reaccion ->
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (reaccion.yo) Color(0xFF15803D).copy(alpha = 0.14f) else MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, if (reaccion.yo) Color(0xFF15803D).copy(alpha = 0.55f) else Color.Black.copy(alpha = 0.08f)),
                            modifier = Modifier.combinedClickable(
                                onClick = { onReaccionar(reaccion.emoji) },
                                onLongClick = onMostrarReacciones
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(text = reaccion.emoji, fontSize = 14.sp)
                                Text(
                                    text = reaccion.cantidad.toString(),
                                    fontSize = 10.sp,
                                    fontWeight = if (reaccion.yo) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDark) Color.LightGray else Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}