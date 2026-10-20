package com.sigefiv.app.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sigefiv.app.screens.chat.components.ChatEmptyState
import com.sigefiv.app.screens.chat.components.ChatInputArea
import com.sigefiv.app.screens.chat.components.ChatMessageItem
import com.sigefiv.app.screens.chat.components.colorPrincipal
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color

val EmojisReaccion = listOf("👍", "❤️", "😂", "😮", "😢", "🙏")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatVecinalScreen(
    viewModel: ChatViewModel,
    usuarioActualId: Int,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var textoInput by remember { mutableStateOf("") }
    var mensajeReaccionSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var mensajeResaltadoId by remember { mutableStateOf<Int?>(null) }
    var mostrarMenuChat by remember { mutableStateOf(false) }
    var mostrarAyudaZoe by remember { mutableStateOf(false) }
    var separadorNuevosId by remember { mutableStateOf<Int?>(null) }
    var nuevosMensajesVisitados by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarChat()
        viewModel.actualizarPresencia()
        viewModel.iniciarActualizacionAutomatica()
    }

    // Scroll al final al cargar nuevos mensajes
    LaunchedEffect(uiState.mensajes.size) {
        if (uiState.mensajes.isNotEmpty() && listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 >= uiState.mensajes.size - 3) {
            delay(100)
            listState.animateScrollToItem(uiState.mensajes.size)
        }
    }

    // Lógica para marcador de nuevos mensajes
    LaunchedEffect(uiState.primerNoLeidoId) {
        if (uiState.primerNoLeidoId != null && separadorNuevosId != uiState.primerNoLeidoId) {
            separadorNuevosId = uiState.primerNoLeidoId
            nuevosMensajesVisitados = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Group, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chat Vecinal", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Text("${uiState.personasEnLinea} vecinos conectados", color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Outlined.ArrowBack, contentDescription = "Regresar", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SeasonalColors.primary(SeasonalTheme.getSeason())),
                actions = {
                    Box {
                        IconButton(onClick = { mostrarMenuChat = true }) {
                            Icon(Icons.Outlined.MoreVert, contentDescription = "Opciones", tint = MaterialTheme.colorScheme.onPrimary)
                        }
                        DropdownMenu(expanded = mostrarMenuChat, onDismissRequest = { mostrarMenuChat = false }) {
                            DropdownMenuItem(
                                text = { Text("Ayuda de ZOE") },
                                leadingIcon = { Icon(Icons.Outlined.HelpOutline, contentDescription = null) },
                                onClick = { mostrarMenuChat = false; mostrarAyudaZoe = true }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            ChatInputArea(
                texto = textoInput,
                enviando = uiState.enviando,
                esBloqueoPermanente = uiState.esBloqueoPermanente,
                mensajeRespondido = uiState.mensajeRespondido,
                usuariosEscribiendo = uiState.usuariosEscribiendo.filter { it.id != usuarioActualId },
                onTextoChange = {
                    textoInput = it
                    viewModel.actualizarEscribiendo(it.isNotBlank())
                },
                onEnviar = {
                    if (textoInput.isNotBlank() && !uiState.enviando && !uiState.esBloqueoPermanente) {
                        viewModel.enviarMensaje(textoInput)
                        textoInput = ""
                        viewModel.actualizarEscribiendo(false)
                    }
                },
                onAdjuntar = { /* Lógica de adjuntar original aquí */ },
                onCancelarRespuesta = { viewModel.cancelarRespuesta() }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = colorPrincipal())
                uiState.mensajes.isEmpty() -> ChatEmptyState()
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        itemsIndexed(items = uiState.mensajes, key = { _, m -> m.id }) { _, mensaje ->

                            // Separador de nuevos mensajes
                            if (mensaje.id == separadorNuevosId) {
                                Text(
                                    text = "Nuevos mensajes",
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            ChatMessageItem(
                                mensaje = mensaje,
                                esPropio = mensaje.usuario?.id == usuarioActualId,
                                esResaltado = mensaje.id == mensajeResaltadoId,
                                onResponder = { viewModel.prepararRespuesta(mensaje) },
                                onMostrarReacciones = { mensajeReaccionSeleccionadoId = mensaje.id },
                                onReaccionar = { emoji -> viewModel.reaccionar(mensaje.id, emoji) },
                                onIrAlMensajeOriginal = { id ->
                                    coroutineScope.launch {
                                        val idx = uiState.mensajes.indexOfFirst { it.id == id }
                                        if (idx != -1) {
                                            mensajeResaltadoId = id
                                            listState.animateScrollToItem(idx)
                                            delay(2000)
                                            if (mensajeResaltadoId == id) mensajeResaltadoId = null
                                        }
                                    }
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(30.dp)) }
                    }

                    // Botón flotante para nuevos mensajes
                    if (uiState.mensajesNoLeidos > 0 && !nuevosMensajesVisitados) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 16.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .clickable {
                                    coroutineScope.launch {
                                        val idDestino = separadorNuevosId ?: uiState.primerNoLeidoId
                                        val idx = uiState.mensajes.indexOfFirst { it.id == idDestino }
                                        if (idx >= 0) listState.animateScrollToItem(idx)
                                    }
                                },
                            color = colorPrincipal(),
                            shadowElevation = 6.dp
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("${uiState.mensajesNoLeidos} nuevos mensajes", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Error Card
            if (!uiState.error.isNullOrBlank()) {
                Card(
                    modifier = Modifier.align(Alignment.TopCenter).padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = uiState.error ?: "", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }

            // Dialog de Reacciones
            val msgSeleccionado = uiState.mensajes.firstOrNull { it.id == mensajeReaccionSeleccionadoId }
            if (msgSeleccionado != null) {
                Dialog(onDismissRequest = { mensajeReaccionSeleccionadoId = null }) {
                    Surface(shape = RoundedCornerShape(28.dp), color = MaterialTheme.colorScheme.onPrimary, shadowElevation = 8.dp) {
                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            EmojisReaccion.forEach { emoji ->
                                Text(
                                    text = emoji, fontSize = 28.sp,
                                    modifier = Modifier.clip(CircleShape).clickable {
                                        viewModel.reaccionar(msgSeleccionado.id, emoji)
                                        mensajeReaccionSeleccionadoId = null
                                    }.padding(5.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Dialog Ayuda ZOE
            if (mostrarAyudaZoe) {
                AlertDialog(
                    onDismissRequest = { mostrarAyudaZoe = false },
                    title = { Text("🤖 Ayuda de ZOE") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Hola, soy ZOE, la asistente virtual de SIGEFIV.")
                            Text("Puedes mencionarme en el chat utilizando @zoe.")
                            Text("Ejemplos de preguntas:\n• @zoe ¿Qué puedes hacer?\n• @zoe ¿Quién eres?")
                        }
                    },
                    confirmButton = { TextButton(onClick = { mostrarAyudaZoe = false }) { Text("Cerrar") } }
                )
            }
        }
    }
}