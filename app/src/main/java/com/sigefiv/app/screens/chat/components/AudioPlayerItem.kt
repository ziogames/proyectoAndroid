package com.sigefiv.app.screens.chat.components

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun AudioPlayerItem(
    audioUrl: String,
    esPropio: Boolean,
    colorNombre: Color,
    isDark: Boolean,
    horaMensaje: String = "",
    avatar: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    var estaReproduciendo by remember { mutableStateOf(false) }
    var estaCargando by remember { mutableStateOf(false) }
    var progreso by remember { mutableFloatStateOf(0f) }
    var posicionMs by remember { mutableIntStateOf(0) }
    var duracionTotalMs by remember { mutableIntStateOf(0) }
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }


    val barFractions = remember(audioUrl) {
        val rng = java.util.Random(audioUrl.hashCode().toLong())
        List(36) { i ->
            val base = 0.22f + rng.nextFloat() * 0.78f
            val factorExtremos = when {
                i < 3 -> 0.35f + (i * 0.2f)
                i > 32 -> 0.35f + ((35 - i) * 0.2f)
                else -> 1f
            }
            (base * factorExtremos).coerceIn(0.2f, 1f)
        }
    }

    DisposableEffect(audioUrl) {
        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (_: Exception) {}
            mediaPlayer = null
        }
    }

    LaunchedEffect(estaReproduciendo) {
        while (estaReproduciendo) {
            mediaPlayer?.let { player ->
                try {
                    if (player.isPlaying) {
                        posicionMs = player.currentPosition
                        val duracion = player.duration
                        if (duracion > 0) {
                            progreso = (posicionMs.toFloat() / duracion.toFloat()).coerceIn(0f, 1f)
                        }
                    }
                } catch (_: Exception) {}
            }
            delay(50)
        }
    }

    val colorCianWhatsApp = Color(0xFF34B7F1)
    val colorIconoPlay = if (isDark) Color(0xFFE9EDEF) else Color(0xFF54656F)
    val colorOndaInactiva = if (isDark) Color(0x60E9EDEF) else Color(0x50000000)
    val colorTextoTiempo = if (isDark) Color(0x99E9EDEF) else Color(0xFF667781)
    val colorFondoBurbuja = if (esPropio) (if (isDark) Color(0xFF005C4B) else Color(0xFFE7FFDB)) else (if (isDark) Color(0xFF202C33) else Color(0xFFFFFFFF))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Avatar 100% limpio sin bordes exteriores
        Box(
            modifier = Modifier.size(42.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
            ) {
                if (avatar != null) {
                    avatar()
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(colorNombre.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = colorNombre,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Badge de micrófono integrado con corte limpio
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(colorFondoBurbuja)
                    .padding(1.5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF005C4B)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = null,
                        tint = Color(0xFF25D366),
                        modifier = Modifier.size(9.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(2.dp))

        // 2. Botón Play / Pausa centrado
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, radius = 18.dp)
                ) {
                    if (estaReproduciendo) {
                        try { mediaPlayer?.pause() } catch (_: Exception) {}
                        estaReproduciendo = false
                    } else {
                        if (mediaPlayer == null) {
                            estaCargando = true
                            try {
                                val player = MediaPlayer().apply {
                                    setAudioAttributes(
                                        AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .build()
                                    )
                                    val urlLimpia = audioUrl.trim()
                                    if (urlLimpia.startsWith("http://") || urlLimpia.startsWith("https://")) {
                                        setDataSource(urlLimpia)
                                    } else {
                                        setDataSource(context, Uri.parse(urlLimpia))
                                    }

                                    setOnPreparedListener { mp ->
                                        estaCargando = false
                                        duracionTotalMs = mp.duration
                                        mp.start()
                                        estaReproduciendo = true
                                    }
                                    setOnCompletionListener {
                                        estaReproduciendo = false
                                        progreso = 0f
                                        posicionMs = 0
                                    }
                                    setOnErrorListener { _, _, _ ->
                                        estaCargando = false
                                        estaReproduciendo = false
                                        true
                                    }
                                    prepareAsync()
                                }
                                mediaPlayer = player
                            } catch (e: Exception) {
                                estaCargando = false
                            }
                        } else {
                            try {
                                mediaPlayer?.let { player ->
                                    if (posicionMs >= duracionTotalMs && duracionTotalMs > 0) {
                                        player.seekTo(0)
                                    }
                                    player.start()
                                    estaReproduciendo = true
                                }
                            } catch (_: Exception) {
                                estaReproduciendo = false
                            }
                        }
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (estaCargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = colorIconoPlay,

                )
            } else {
                Icon(
                    imageVector = if (estaReproduciendo) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (estaReproduciendo) "Pausar" else "Reproducir",
                    tint = colorIconoPlay,
                    modifier = Modifier
                        .size(24.dp)
                        .offset(x = if (!estaReproduciendo) 1.dp else 0.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(3.dp))

        // 3. Onda de audio con temporizador y hora alineada a la derecha
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .pointerInput(duracionTotalMs) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                if (duracionTotalMs > 0) {
                                    val newProg = (offset.x / size.width).coerceIn(0f, 1f)
                                    progreso = newProg
                                    val seekMs = (newProg * duracionTotalMs).toInt()
                                    posicionMs = seekMs
                                    try { mediaPlayer?.seekTo(seekMs) } catch (_: Exception) {}
                                }
                            },
                            onDrag = { change, _ ->
                                if (duracionTotalMs > 0) {
                                    change.consume()
                                    val newProg = (change.position.x / size.width).coerceIn(0f, 1f)
                                    progreso = newProg
                                    val seekMs = (newProg * duracionTotalMs).toInt()
                                    posicionMs = seekMs
                                    try { mediaPlayer?.seekTo(seekMs) } catch (_: Exception) {}
                                }
                            }
                        )
                    }
            ) {
                val totalWidth = size.width
                val totalHeight = size.height
                val barCount = barFractions.size
                val spacing = 2.2.dp.toPx()
                val barWidth = ((totalWidth - (spacing * (barCount - 1))) / barCount).coerceAtLeast(1.6.dp.toPx())
                val thumbX = totalWidth * progreso

                barFractions.forEachIndexed { index, fraction ->
                    val x = index * (barWidth + spacing) + (barWidth / 2f)
                    val barHeight = (totalHeight * fraction).coerceAtLeast(2.5.dp.toPx())
                    val startY = (totalHeight - barHeight) / 2f
                    val endY = startY + barHeight

                    val fueReproducido = x <= thumbX
                    val colorBarra = if (fueReproducido) colorCianWhatsApp else colorOndaInactiva

                    drawLine(
                        color = colorBarra,
                        start = Offset(x, startY),
                        end = Offset(x, endY),
                        strokeWidth = barWidth,
                        cap = StrokeCap.Round
                    )
                }

                // Thumb circular fino de 3.5 dp
                val radioThumb = 3.5.dp.toPx()
                val centroX = thumbX.coerceIn(radioThumb, totalWidth - radioThumb)
                drawCircle(
                    color = colorCianWhatsApp,
                    radius = radioThumb,
                    center = Offset(centroX, totalHeight / 2f)
                )
            }

            Spacer(modifier = Modifier.height(1.dp))

            // Fila inferior: Contador a la izquierda y Hora + Doble Check a la derecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val segundos = (if (estaReproduciendo || posicionMs > 0) posicionMs else duracionTotalMs) / 1000
                val textoTiempo = String.format("%d:%02d", segundos / 60, segundos % 60)

                Text(
                    text = if (duracionTotalMs == 0 && !estaReproduciendo) "0:00" else textoTiempo,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    color = colorTextoTiempo,
                    lineHeight = 12.sp
                )

                if (horaMensaje.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = horaMensaje,
                            fontSize = 10.sp,
                            color = colorTextoTiempo,
                            lineHeight = 12.sp
                        )
                        if (esPropio) {
                            Icon(
                                imageVector = Icons.Filled.DoneAll,
                                contentDescription = null,
                                tint = colorCianWhatsApp,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}