package com.sigefiv.app.screens.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Card
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sigefiv.app.data.model.ChatArchivoPayload
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

private val VerdePrincipal = Color(0xFF0F766E)
private val FondoChat = Color(0xFFE5DDD5)
private val BurbujaPropia = Color(0xFFDCF8C6)
private val BurbujaAjena = Color.White
private val TextoPrincipal = Color(0xFF111B21)
private val TextoSecundario = Color(0xFF667781)
private val NombreEmisorColor = Color(0xFF075E54)

private val FondoTarjetaInteligente = Color(0xFFF8FAFC)
private val BordeTarjetaInteligente = Color(0xFFE2E8F0)
private val VerdeMonto = Color(0xFF047857)
private val RojoMonto = Color(0xFFDC2626)

private val EmojisReaccion = listOf("👍", "❤️", "😂", "😮", "😢", "🙏")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatVecinalScreen(
    viewModel: ChatViewModel,
    usuarioActualId: Int,
    onBackClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    var texto by remember { mutableStateOf("") }

    // 💬 Estados para el resaltado temporal del mensaje original citado
    var mensajeResaltadoId by remember { mutableStateOf<Int?>(null) }
    var mensajeReaccionSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.cargarChat()
        viewModel.actualizarPresencia()
        viewModel.iniciarActualizacionAutomatica()
    }

    LaunchedEffect(uiState.mensajes.size) {
        if (uiState.mensajes.isNotEmpty()) {
            listState.animateScrollToItem(uiState.mensajes.lastIndex)
        }
    }

    Scaffold(
        containerColor = FondoChat,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Group,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chat Vecinal",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Text(
                            text = "${uiState.personasEnLinea} vecinos conectados",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    )
                )
            )
        },
        bottomBar = {
            ChatInput(
                texto = texto,
                enviando = uiState.enviando,
                esBloqueoPermanente = uiState.esBloqueoPermanente,
                mensajeRespondido = uiState.mensajeRespondido,
                onCancelarRespuesta = { viewModel.cancelarRespuesta() },
                usuariosEscribiendo =
                    uiState.usuariosEscribiendo.filter {
                        it.id != usuarioActualId
                    },
                onTextoChange = { nuevoTexto ->
                    texto = nuevoTexto
                    viewModel.actualizarEscribiendo(nuevoTexto.isNotBlank())
                },
                onEnviar = {
                    if (texto.isNotBlank() && !uiState.enviando && !uiState.esBloqueoPermanente) {
                        viewModel.enviarMensaje(texto)
                        texto = ""
                        viewModel.actualizarEscribiendo(false)
                    }
                },
                onAdjuntar = { }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = VerdePrincipal
                    )
                }

                uiState.mensajes.isEmpty() -> {
                    ChatEmptyState()
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(
                            start = 10.dp,
                            end = 10.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        )
                    ) {
                        items(
                            items = uiState.mensajes,
                            key = { it.id }
                        ) { mensaje ->
                            ChatMessageItem(
                                mensaje = mensaje,
                                esPropio = mensaje.usuario?.id == usuarioActualId,
                                esResaltado = mensaje.id == mensajeResaltadoId,
                                onResponder = {
                                    viewModel.prepararRespuesta(mensaje)
                                },
                                onMostrarReacciones = {
                                    mensajeReaccionSeleccionadoId = mensaje.id
                                },
                                onReaccionar = { emoji ->
                                    viewModel.reaccionar(mensaje.id, emoji)
                                },
                                onIrAlMensajeOriginal = { mensajeIdBuscado ->
                                    coroutineScope.launch {
                                        val indice = uiState.mensajes.indexOfFirst { it.id == mensajeIdBuscado }
                                        if (indice != -1) {
                                            mensajeResaltadoId = mensajeIdBuscado
                                            listState.animateScrollToItem(indice)
                                            delay(2000) // Mantiene el resaltado por 2 segundos
                                            if (mensajeResaltadoId == mensajeIdBuscado) {
                                                mensajeResaltadoId = null
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            val mensajeReaccionSeleccionado =
                uiState.mensajes.firstOrNull {
                    it.id == mensajeReaccionSeleccionadoId
                }

            if (mensajeReaccionSeleccionado != null) {
                Dialog(
                    onDismissRequest = {
                        mensajeReaccionSeleccionadoId = null
                    }
                ) {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = Color.White,
                        shadowElevation = 8.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 8.dp
                            ),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            EmojisReaccion.forEach { emoji ->
                                Text(
                                    text = emoji,
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .combinedClickable(
                                            onClick = {
                                                viewModel.reaccionar(
                                                    mensajeReaccionSeleccionado.id,
                                                    emoji
                                                )
                                                mensajeReaccionSeleccionadoId = null
                                            },
                                            onLongClick = null
                                        )
                                        .padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (!uiState.error.isNullOrBlank()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFEE2E2)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = uiState.error ?: "",
                        color = Color(0xFF991B1B),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatInput(
    texto: String,
    enviando: Boolean,
    esBloqueoPermanente: Boolean,
    mensajeRespondido: ChatMessage?,
    onCancelarRespuesta: () -> Unit,
    usuariosEscribiendo: List<com.sigefiv.app.data.model.ChatTypingUser>,
    onTextoChange: (String) -> Unit,
    onEnviar: () -> Unit,
    onAdjuntar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdePrincipal)
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        if (mensajeRespondido != null && !esBloqueoPermanente) {
            Surface(
                color = Color.Black.copy(alpha = 0.25f),
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(30.dp)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = mensajeRespondido.usuario?.name ?: "Vecino",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = mensajeRespondido.mensaje ?: "Mensaje",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                    IconButton(
                        onClick = onCancelarRespuesta,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = "Cancelar respuesta",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        if (usuariosEscribiendo.isNotEmpty()) {
            val nombres = usuariosEscribiendo.joinToString(", ") { it.name ?: "Vecino" }
            Text(
                text = "$nombres está escribiendo...",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 6.dp),
                shape = RoundedCornerShape(24.dp),
                color = if (esBloqueoPermanente) Color(0xFFF1F1F1) else Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onAdjuntar,
                        enabled = !esBloqueoPermanente,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AttachFile,
                            contentDescription = "Adjuntar archivo",
                            tint = if (!esBloqueoPermanente) TextoSecundario else TextoSecundario.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    OutlinedTextField(
                        value = if (esBloqueoPermanente) "" else texto,
                        onValueChange = onTextoChange,
                        enabled = !esBloqueoPermanente,
                        modifier = Modifier.weight(1f),
                        placeholder = {
                            Text(
                                if (esBloqueoPermanente) "Cuenta suspendida." else "Escribe un mensaje...",
                                fontSize = 14.sp,
                                color = if (esBloqueoPermanente) Color.Red.copy(alpha = 0.7f) else TextoSecundario
                            )
                        },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            disabledBorderColor = Color.Transparent,
                            focusedTextColor = TextoPrincipal,
                            unfocusedTextColor = TextoPrincipal,
                            disabledTextColor = TextoSecundario,
                            cursorColor = VerdePrincipal
                        )
                    )
                }
            }

            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = if (!esBloqueoPermanente && texto.isNotBlank()) VerdePrincipal else VerdePrincipal.copy(alpha = 0.4f)
            ) {
                IconButton(
                    onClick = onEnviar,
                    enabled = !esBloqueoPermanente && texto.isNotBlank() && !enviando
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = "Enviar mensaje",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatEmptyState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = VerdePrincipal.copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(42.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chat Vecinal",
            color = TextoPrincipal,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Aún no hay mensajes.\nSé el primero en escribir.",
            color = TextoSecundario,
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ChatMessageItem(
    mensaje: ChatMessage,
    esPropio: Boolean,
    esResaltado: Boolean,
    onResponder: () -> Unit,
    onMostrarReacciones: () -> Unit,
    onReaccionar: (String) -> Unit,
    onIrAlMensajeOriginal: (Int) -> Unit
) {
    val texto = mensaje.mensaje ?: ""
    val maxBurbujaWidth = (LocalConfiguration.current.screenWidthDp * 0.85).dp

    // Variables para el gesto de deslizamiento horizontal (Swipe to reply)
    var offsetX by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    val thresholdPx = with(density) { 60.dp.toPx() }

    // 💬 Color dinámico de la burbuja (Cambia a un tono amarillo suave si está resaltado)
    val colorBurbujaBase = if (esPropio) BurbujaPropia else BurbujaAjena
    val colorBurbujaFinal = if (esResaltado) Color(0xFFFEF08A) else colorBurbujaBase

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalAlignment = if (esPropio) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 14.dp,
                topEnd = 14.dp,
                bottomStart = if (esPropio) 14.dp else 2.dp,
                bottomEnd = if (esPropio) 2.dp else 14.dp
            ),
            color = colorBurbujaFinal,
            shadowElevation = if (esResaltado) 6.dp else 1.dp,
            modifier = Modifier
                .widthIn(max = maxBurbujaWidth)
                .offset { IntOffset(offsetX.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > thresholdPx) {
                                onResponder()
                            }
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            offsetX = (offsetX + dragAmount).coerceIn(0f, thresholdPx * 1.5f)
                        }
                    )
                }
                .combinedClickable(
                    onClick = {},
                    onLongClick = onMostrarReacciones
                )
        ) {
            Column(
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(start = 9.dp, end = 9.dp, top = 6.dp, bottom = 4.dp)
            ) {
                // 💬 Tarjeta miniatura del mensaje citado con clic habilitado para saltar al original
                if (mensaje.reply_to != null) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFE2E8F0).copy(alpha = 0.7f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                            .combinedClickable(
                                onClick = {
                                    onIrAlMensajeOriginal(mensaje.reply_to.id)
                                }
                            )
                    ) {
                        Row(
                            modifier = Modifier.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(26.dp)
                                    .background(NombreEmisorColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mensaje.reply_to.usuario?.name ?: "Vecino",
                                    color = NombreEmisorColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = mensaje.reply_to.mensaje ?: "Mensaje",
                                    color = TextoSecundario,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                if (!esPropio) {
                    Text(
                        text = mensaje.usuario?.name ?: "Vecino",
                        color = NombreEmisorColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }

                // 📁 DETECCIÓN DE ARCHIVO ADJUNTO
                val archivoAdjunto = mensaje.obtenerArchivoAdjunto()

                if (archivoAdjunto != null) {
                    TarjetaArchivoMensaje(archivo = archivoAdjunto)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatearHoraChat(mensaje.created_at),
                        color = TextoSecundario,
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                } else {
                    val lineas = texto.lines().map { it.trim() }.filter { it.isNotBlank() }
                    val tieneListado = !esPropio && lineas.any { it.startsWith("•") }

                    if (tieneListado) {
                        var introduccionMostrada = false
                        var sumaTotalAcumulada = 0.0
                        var hayCalculoTotal = false
                        var textoLineaTotal = ""

                        lineas.forEach { linea ->
                            val lineaLimpia = linea.replace("**", "")

                            val esLineaTotal = lineaLimpia.contains("total", ignoreCase = true)

                            if (linea.startsWith("•") && !esLineaTotal) {
                                val partes = lineaLimpia.removePrefix("•").split("|").map { it.trim() }
                                val fecha = partes.getOrNull(0) ?: ""
                                val tipo = partes.getOrNull(1) ?: ""
                                val concepto = partes.getOrNull(2) ?: ""
                                val categoria = partes.getOrNull(3) ?: ""
                                val montoStr = partes.getOrNull(4) ?: ""

                                val montoLimpio = montoStr.replace("S/", "").replace("s/", "").replace(",", "").trim()
                                val valMonto = montoLimpio.toDoubleOrNull()
                                if (valMonto != null) {
                                    hayCalculoTotal = true
                                    val esEgreso = tipo.contains("egreso", ignoreCase = true) ||
                                            tipo.contains("salida", ignoreCase = true) ||
                                            montoStr.startsWith("-")
                                    if (esEgreso && valMonto > 0) {
                                        sumaTotalAcumulada -= valMonto
                                    } else {
                                        sumaTotalAcumulada += valMonto
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                TarjetaMovimientoInteligente(
                                    fecha = fecha,
                                    tipo = tipo,
                                    concepto = concepto,
                                    categoria = categoria,
                                    monto = montoStr
                                )
                            } else if (!introduccionMostrada && !esLineaTotal) {
                                Text(
                                    text = lineaLimpia,
                                    color = TextoPrincipal,
                                    fontSize = 14.sp,
                                    lineHeight = 19.sp,
                                    textAlign = TextAlign.Start
                                )
                                introduccionMostrada = true
                            } else if (esLineaTotal) {
                                textoLineaTotal = lineaLimpia
                            } else if (lineaLimpia.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = lineaLimpia,
                                    color = VerdePrincipal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (hayCalculoTotal || textoLineaTotal.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))

                            TarjetaTotalInteligente(
                                totalSuma = if (hayCalculoTotal) sumaTotalAcumulada else null,
                                lineaOriginal = textoLineaTotal
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = formatearHoraChat(mensaje.created_at),
                            color = TextoSecundario,
                            fontSize = 10.sp,
                            modifier = Modifier.align(Alignment.End)
                        )
                    } else {
                        Row(
                            modifier = Modifier.wrapContentWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = texto,
                                color = TextoPrincipal,
                                fontSize = 14.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .padding(end = 8.dp)
                            )

                            Text(
                                text = formatearHoraChat(mensaje.created_at),
                                color = TextoSecundario,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(bottom = 1.dp)
                            )
                        }
                    }
                }
            }
        }

        if (!mensaje.reacciones.isNullOrEmpty()) {
            Row(
                modifier = Modifier.padding(
                    top = 2.dp,
                    start = 6.dp,
                    end = 6.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                mensaje.reacciones.orEmpty().forEach { reaccion ->
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (reaccion.yo) {
                            VerdePrincipal.copy(alpha = 0.14f)
                        } else {
                            Color.White.copy(alpha = 0.98f)
                        },
                        border = if (reaccion.yo) {
                            BorderStroke(
                                1.dp,
                                VerdePrincipal.copy(alpha = 0.55f)
                            )
                        } else {
                            BorderStroke(
                                1.dp,
                                Color.Black.copy(alpha = 0.08f)
                            )
                        },
                        shadowElevation = 1.dp,
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
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(
                                text = reaccion.emoji,
                                fontSize = 14.sp
                            )

                            Text(
                                text = reaccion.cantidad.toString(),
                                fontSize = 10.sp,
                                color = TextoPrincipal,
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

// 📁 COMPONENTE VISUAL PARA RENDERIZAR ARCHIVOS Y MULTIMEDIA ADJUNTOS
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TarjetaArchivoMensaje(archivo: ChatArchivoPayload) {
    val context = LocalContext.current
    // Reemplazamos localhost por 10.0.2.2 para que el emulador pueda descargar la imagen del contenedor Docker
    val urlCorregida = archivo.url?.replace("localhost", "10.0.2.2")
    val esImagen = archivo.mime?.startsWith("image/") == true

    Column(modifier = Modifier.fillMaxWidth()) {
        if (esImagen && !urlCorregida.isNullOrBlank()) {
            AsyncImage(
                model = urlCorregida,
                contentDescription = archivo.nombre ?: "Imagen adjunta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (!archivo.texto.isNullOrBlank()) {
            Text(
                text = archivo.texto,
                color = TextoPrincipal,
                fontSize = 14.sp,
                lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.Black.copy(alpha = 0.05f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .combinedClickable(
                    onClick = {
                        if (!urlCorregida.isNullOrBlank()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlCorregida))
                            context.startActivity(intent)
                        }
                    }
                )
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = archivo.nombre ?: "Archivo adjunto",
                    color = VerdePrincipal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun TarjetaMovimientoInteligente(
    fecha: String,
    tipo: String,
    concepto: String,
    categoria: String,
    monto: String
) {
    val esEgreso = tipo.contains("egreso", ignoreCase = true) ||
            tipo.contains("salida", ignoreCase = true) ||
            monto.startsWith("-")

    val colorMovimiento = if (esEgreso) RojoMonto else VerdeMonto
    val iconoMovimiento = if (esEgreso) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = FondoTarjetaInteligente,
        border = BorderStroke(1.dp, BordeTarjetaInteligente),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fecha,
                    color = TextoSecundario,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )

                if (monto.isNotBlank()) {
                    Text(
                        text = monto,
                        color = colorMovimiento,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(3.dp))

            if (tipo.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = iconoMovimiento,
                        contentDescription = null,
                        tint = colorMovimiento,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tipo.uppercase(),
                        color = colorMovimiento,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            if (concepto.isNotBlank()) {
                Text(
                    text = concepto,
                    color = TextoPrincipal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 17.sp
                )
            }

            if (categoria.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Categoría: $categoria",
                    color = TextoSecundario,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun TarjetaTotalInteligente(
    totalSuma: Double?,
    lineaOriginal: String
) {
    val montoTextoExtraido = if (totalSuma != null) {
        val valAbsoluto = Math.abs(totalSuma)
        val strMonto = String.format(Locale.US, "%.2f", valAbsoluto)
        if (totalSuma < 0) "-S/ $strMonto" else "S/ $strMonto"
    } else {
        val partes = lineaOriginal.split(":")
        if (partes.size > 1 && partes[1].isNotBlank()) {
            partes[1].trim()
        } else {
            lineaOriginal.replace("Total", "", ignoreCase = true).replace("•", "").trim().takeIf { it.isNotBlank() } ?: "S/ 0.00"
        }
    }

    val esNegativo = (totalSuma ?: 0.0) < 0 || montoTextoExtraido.startsWith("-")
    val colorTotal = if (esNegativo) RojoMonto else VerdeMonto

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = FondoTarjetaInteligente,
        border = BorderStroke(1.dp, BordeTarjetaInteligente),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                color = TextoPrincipal,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = montoTextoExtraido,
                color = colorTotal,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatearHoraChat(fechaHora: String?): String {
    if (fechaHora.isNullOrBlank()) return ""
    return try {
        val hora = fechaHora
            .substringAfter("T")
            .substringBefore(".")
            .substringBefore("Z")

        if (hora.length >= 5) {
            hora.substring(0, 5)
        } else {
            hora
        }
    } catch (_: Exception) {
        ""
    }
}