package com.sigefiv.app.screens.chat

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import androidx.compose.runtime.snapshotFlow
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
    val context = LocalContext.current

    var textoInput by remember { mutableStateOf("") }
    var mensajeReaccionSeleccionadoId by remember { mutableStateOf<Int?>(null) }
    var mensajeResaltadoId by remember { mutableStateOf<Int?>(null) }
    var mostrarMenuChat by remember { mutableStateOf(false) }
    var mostrarAyudaZoe by remember { mutableStateOf(false) }
    var separadorNuevosId by remember { mutableStateOf<Int?>(null) }
    var forzarOcultarNoLeidos by remember { mutableStateOf(false) }
    var ultimoConteoNoLeidos by remember { mutableIntStateOf(0) }

    // Estado para almacenar temporalmente el archivo seleccionado antes de enviarlo
    var archivoSeleccionadoUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para seleccionar el archivo de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { archivoSeleccionadoUri = it }
    }

    LaunchedEffect(Unit) {
        viewModel.cargarChat()
        viewModel.actualizarPresencia()
        viewModel.iniciarActualizacionAutomatica()
    }

    // Scroll al final al cargar nuevos mensajes
    LaunchedEffect(uiState.mensajes.size) {
        if (uiState.mensajes.isNotEmpty() && (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 >= uiState.mensajes.size - 3)) {
            delay(100)
            listState.animateScrollToItem(uiState.mensajes.size)
        }
    }

    // Marcar como leídos cuando el usuario llega al final del chat.
    LaunchedEffect(listState, uiState.mensajes.size, uiState.mensajesNoLeidos) {
        if (uiState.mensajesNoLeidos > 0 && uiState.mensajes.isNotEmpty()) {
            snapshotFlow {
                val ultimoVisible =
                    listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

                ultimoVisible >= uiState.mensajes.size - 1
            }
                .distinctUntilChanged()
                .collect { llegoAlFinal ->
                    if (llegoAlFinal) {
                        val ultimoMensaje = uiState.mensajes.lastOrNull()
                        if (ultimoMensaje != null) {
                            viewModel.marcarLeido(ultimoMensaje.id)
                        }
                    }
                }
        }
    }


    // Marcador y botón de mensajes no leídos.
    // El botón vuelve a aparecer si aumenta el contador, aunque el primer
    // mensaje no leído siga siendo el mismo.
    LaunchedEffect(uiState.primerNoLeidoId, uiState.mensajesNoLeidos) {
        val primerNoLeido = uiState.primerNoLeidoId
        val conteoActual = uiState.mensajesNoLeidos

        if (primerNoLeido != null && separadorNuevosId != primerNoLeido) {
            separadorNuevosId = primerNoLeido
            forzarOcultarNoLeidos = false
        }

        // Si llegan mensajes nuevos después de ocultar el botón,
        // se muestra de nuevo aunque no cambie primerNoLeidoId.
        if (conteoActual > ultimoConteoNoLeidos) {
            forzarOcultarNoLeidos = false
        }

        // Si ya no hay mensajes no leídos, no dejamos el botón bloqueado
        // para la siguiente tanda.
        if (conteoActual == 0) {
            forzarOcultarNoLeidos = false
        }

        ultimoConteoNoLeidos = conteoActual
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

                        val textoConectados = if (uiState.personasEnLinea == 1) {
                            "1 vecino conectado"
                        } else {
                            "${uiState.personasEnLinea} vecinos conectados"
                        }

                        Text(
                            text = textoConectados,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
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
            Column {
                if (archivoSeleccionadoUri != null) {
                    Surface(
                        tonalElevation = 4.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.Image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Imagen lista para adjuntar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Escribe tu texto abajo y presiona enviar",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            IconButton(onClick = { archivoSeleccionadoUri = null }) {
                                Icon(Icons.Outlined.Close, contentDescription = "Cancelar")
                            }
                        }
                    }
                }

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
                        if (!uiState.enviando && !uiState.esBloqueoPermanente) {
                            if (archivoSeleccionadoUri != null) {
                                viewModel.enviarMultimedia(archivoSeleccionadoUri!!, textoInput, context)
                                archivoSeleccionadoUri = null
                                textoInput = ""
                            } else if (textoInput.isNotBlank()) {
                                viewModel.enviarMensaje(textoInput)
                                textoInput = ""
                                viewModel.actualizarEscribiendo(false)
                            }
                        }
                    },
                    onAdjuntar = {
                        imagePickerLauncher.launch("*/*")
                    },
                    onCancelarRespuesta = { viewModel.cancelarRespuesta() }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Capa 1: Contenido principal (Lista de mensajes o estado vacío)
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


                            if (mensaje.id == separadorNuevosId) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        thickness = 1.dp
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f),
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(
                                                horizontal = 14.dp,
                                                vertical = 7.dp
                                            ),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(18.dp)
                                            )

                                            Text(
                                                text = "Nuevos mensajes",
                                                color = MaterialTheme.colorScheme.primary,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                                        thickness = 1.dp
                                    )
                                }
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
                }
            }

            // Capa 2: Botón flotante independiente de nuevos mensajes (Rediseño Estilo WhatsApp)
            val mostrarBotonNoLeidos = uiState.mensajesNoLeidos > 0 && !forzarOcultarNoLeidos

            AnimatedVisibility(
                visible = mostrarBotonNoLeidos,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable {
                            coroutineScope.launch {
                                forzarOcultarNoLeidos = true
                                val idDestino = separadorNuevosId ?: uiState.primerNoLeidoId
                                val idx = uiState.mensajes.indexOfFirst { it.id == idDestino }
                                if (idx >= 0) {
                                    listState.animateScrollToItem(idx)
                                } else if (uiState.mensajes.isNotEmpty()) {
                                    listState.animateScrollToItem(uiState.mensajes.size - 1)
                                }
                            }
                        },
                    color = colorPrincipal(),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ArrowDownward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Ver ${uiState.mensajesNoLeidos} mensajes no leídos",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Capa 3: Notificaciones flotantes de Error
            if (!uiState.error.isNullOrBlank()) {
                Card(
                    modifier = Modifier.align(Alignment.TopCenter).padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = uiState.error ?: "", color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
                }
            }

            // Diálogos modales (Reacciones y Ayuda ZOE)
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

            if (mostrarAyudaZoe) {
                AlertDialog(
                    onDismissRequest = { mostrarAyudaZoe = false },
                    title = { Text("🤖 Ayuda de ZOE") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Hola, soy ZOE, la asistente virtual de SIGEFIV.")
                            Text("Puedes mencionarme en el chat utilizando @zoe.")
                            Text("Ejemplos de preguntas:\n@zoe ¿Qué puedes hacer?\n@zoe ¿Quién eres?")
                        }
                    },
                    confirmButton = { TextButton(onClick = { mostrarAyudaZoe = false }) { Text("Cerrar") } }
                )
            }
        }
    }
}