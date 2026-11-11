@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.chat

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.screens.chat.components.ChatEmptyState
import com.sigefiv.app.screens.chat.components.ChatInputArea
import com.sigefiv.app.screens.chat.components.ChatMessageItem
import com.sigefiv.app.viewmodel.ChatViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

val EmojisReaccion = listOf("👍", "❤️", "😂", "😮", "😢", "🙏")

private data class AccionRapidaZoe(
    val etiqueta: String,
    val comando: String,
    val icono: ImageVector,
    val colorAccento: Color
)

private enum class FiltroChat {
    TODOS, SOLO_ZOE, MULTIMEDIA
}

private fun esMensajeDeZoe(msg: ChatMessage): Boolean {
    val nombreAutor = msg.usuario?.name?.lowercase() ?: ""
    val contenido = msg.mensaje?.trim() ?: ""
    return nombreAutor.contains("zoe") || contenido.startsWith("🤖")
}

private fun obtenerEtiquetaFecha(fechaStr: String?): String {
    if (fechaStr.isNullOrBlank()) return ""
    return try {
        val limpia = fechaStr.trim().take(10)
        val formatos = listOf("yyyy-MM-dd", "dd/MM/yyyy", "dd-MM-yyyy")
        var fechaMensaje: Date? = null

        for (fmt in formatos) {
            try {
                fechaMensaje = SimpleDateFormat(fmt, Locale.getDefault()).parse(limpia)
                if (fechaMensaje != null) break
            } catch (_: Exception) {}
        }

        if (fechaMensaje == null) return ""

        val hoy = Calendar.getInstance()
        val calMensaje = Calendar.getInstance().apply { time = fechaMensaje }

        when {
            hoy.get(Calendar.YEAR) == calMensaje.get(Calendar.YEAR) &&
                    hoy.get(Calendar.DAY_OF_YEAR) == calMensaje.get(Calendar.DAY_OF_YEAR) -> "Hoy"

            hoy.get(Calendar.YEAR) == calMensaje.get(Calendar.YEAR) &&
                    hoy.get(Calendar.DAY_OF_YEAR) - calMensaje.get(Calendar.DAY_OF_YEAR) == 1 -> "Ayer"

            else -> SimpleDateFormat("EEE, d 'de' MMMM", Locale("es", "PE"))
                .format(fechaMensaje)
                .replaceFirstChar { it.uppercase() }
        }
    } catch (e: Exception) {
        ""
    }
}

@Suppress("DEPRECATION")
private fun crearMediaRecorder(context: Context): MediaRecorder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MediaRecorder(context)
    } else {
        MediaRecorder()
    }
}

@Composable
fun ChatVecinalScreen(
    viewModel: ChatViewModel,
    usuarioActualId: Int,
    onBackClick: () -> Unit,
    darkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val esTemaOscuro = darkTheme || MaterialTheme.colorScheme.background.luminance() < 0.5f

    val prefs = remember(context) {
        context.getSharedPreferences("chat_vecinal_prefs", Context.MODE_PRIVATE)
    }
    var mostrarChipsZoe by remember {
        mutableStateOf(prefs.getBoolean("mostrar_chips_zoe", true))
    }

    var textoInput by remember { mutableStateOf("") }
    var mensajeAccionesSeleccionado by remember { mutableStateOf<ChatMessage?>(null) }
    var mensajeResaltadoId by remember { mutableStateOf<Int?>(null) }
    var mostrarMenuChat by remember { mutableStateOf(false) }
    var mostrarAyudaZoe by remember { mutableStateOf(false) }
    var mostrarBuscador by remember { mutableStateOf(false) }
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroActual by remember { mutableStateOf(FiltroChat.TODOS) }

    var separadorNuevosId by remember { mutableStateOf<Int?>(null) }
    var forzarOcultarNoLeidos by remember { mutableStateOf(false) }
    var ultimoConteoNoLeidos by remember { mutableIntStateOf(0) }
    var archivoSeleccionadoUri by remember { mutableStateOf<Uri?>(null) }
    var seHizoScrollInicial by remember { mutableStateOf(false) }

    // Control dinámico seguro de ZOE redactando
    var esperandoRespuestaZoe by remember { mutableStateOf(false) }
    var conteoRespuestasZoeBase by remember { mutableIntStateOf(-1) }

    // Estados para notas de voz
    var grabadorAudio by remember { mutableStateOf<MediaRecorder?>(null) }
    var rutaAudioActual by remember { mutableStateOf<String?>(null) }
    var audioGrabadoUri by remember { mutableStateOf<Uri?>(null) }
    var estaGrabandoAudio by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(uiState.mensajeEnEdicion) {
        uiState.mensajeEnEdicion?.let { msg ->
            textoInput = msg.mensaje ?: ""
        }
    }

    val conteoActualRespuestasZoe = remember(uiState.mensajes) {
        uiState.mensajes.count { esMensajeDeZoe(it) }
    }

    // Apagar la animación de ZOE únicamente cuando llegue una respuesta NUEVA de ZOE
    LaunchedEffect(conteoActualRespuestasZoe) {
        if (esperandoRespuestaZoe && conteoRespuestasZoeBase != -1 && conteoActualRespuestasZoe > conteoRespuestasZoeBase) {
            esperandoRespuestaZoe = false
            conteoRespuestasZoeBase = -1
        }
    }

    // Límite de seguridad: apagar tras 35 segundos si n8n no responde
    LaunchedEffect(esperandoRespuestaZoe) {
        if (esperandoRespuestaZoe) {
            delay(35_000)
            esperandoRespuestaZoe = false
            conteoRespuestasZoeBase = -1
        }
    }

    // Detectar también si el último mensaje en pantalla es una consulta a @zoe en curso
    val ultimoMensajeEsPreguntaZoe = remember(uiState.mensajes, uiState.enviando) {
        val ultimo = uiState.mensajes.lastOrNull()
        ultimo != null &&
                !esMensajeDeZoe(ultimo) &&
                ultimo.mensaje?.contains("@zoe", ignoreCase = true) == true &&
                uiState.enviando
    }

    val zoeEstaEscribiendo = remember(
        uiState.usuariosEscribiendo,
        esperandoRespuestaZoe,
        ultimoMensajeEsPreguntaZoe
    ) {
        val enLista = uiState.usuariosEscribiendo.any {
            it.name?.contains("zoe", ignoreCase = true) == true
        }
        enLista || esperandoRespuestaZoe || ultimoMensajeEsPreguntaZoe
    }

    val accionesRapidas = remember(primaryColor) {
        listOf(
            AccionRapidaZoe("Movimientos", "@zoe ¿Cuáles fueron los movimientos?", Icons.Outlined.ReceiptLong, Color(0xFF1E88E5)),
            AccionRapidaZoe("Saldo en caja", "@zoe ¿Cuál es el saldo en caja?", Icons.Outlined.AccountBalanceWallet, Color(0xFF16A34A)),
            AccionRapidaZoe("Ingresos", "@zoe ¿Cuáles fueron los ingresos?", Icons.Outlined.TrendingUp, Color(0xFF00897B)),
            AccionRapidaZoe("Egresos", "@zoe ¿Cuáles fueron los egresos?", Icons.Outlined.TrendingDown, Color(0xFFDC2626)),
            AccionRapidaZoe("Emergencias", "@zoe Números de emergencia", Icons.Outlined.Emergency, Color(0xFFD81B60)),
            AccionRapidaZoe("Directiva", "@zoe ¿Quiénes conforman la junta directiva?", Icons.Outlined.Badge, Color(0xFF7C3AED)),
            AccionRapidaZoe("Negocios", "@zoe ¿Qué emprendimientos hay en la comunidad?", Icons.Outlined.Storefront, Color(0xFFEA580C))
        )
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { archivoSeleccionadoUri = it }
    }

    fun iniciarGrabacion() {
        try {
            val dir = context.cacheDir ?: context.filesDir
            val audioFile = File(dir, "nota_voz_${System.currentTimeMillis()}.m4a")
            rutaAudioActual = audioFile.absolutePath

            val recorder = crearMediaRecorder(context).apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            grabadorAudio = recorder
            estaGrabandoAudio = true
        } catch (e: Exception) {
            Toast.makeText(context, "Error al iniciar grabación: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            estaGrabandoAudio = false
            grabadorAudio = null
            rutaAudioActual = null
        }
    }

    fun detenerGrabacion(guardar: Boolean) {
        var grabacionExitosa = true

        try {
            grabadorAudio?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {
            grabacionExitosa = false
            try {
                grabadorAudio?.release()
            } catch (_: Exception) {}
        } finally {
            grabadorAudio = null
            estaGrabandoAudio = false

            val ruta = rutaAudioActual
            if (ruta != null) {
                val file = File(ruta)
                if (guardar && grabacionExitosa && file.exists() && file.length() > 0L) {
                    audioGrabadoUri = Uri.fromFile(file)
                } else {
                    if (file.exists()) {
                        file.delete()
                    }
                    audioGrabadoUri = null
                }
                rutaAudioActual = null
            }
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            iniciarGrabacion()
        } else {
            Toast.makeText(context, "Se necesita permiso de micrófono para enviar notas de voz", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                grabadorAudio?.release()
            } catch (_: Exception) {}
            grabadorAudio = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.cargarChat()
        viewModel.actualizarPresencia()
        viewModel.iniciarActualizacionAutomatica()
    }

    val mensajesVisibles = remember(uiState.mensajes, filtroActual, textoBusqueda) {
        uiState.mensajes.filter { msg ->
            val coincideTexto = if (textoBusqueda.isBlank()) {
                true
            } else {
                val textoMensaje = msg.mensaje?.lowercase() ?: ""
                val nombreUsuario = msg.usuario?.name?.lowercase() ?: ""
                val busqueda = textoBusqueda.trim().lowercase()

                textoMensaje.contains(busqueda) || nombreUsuario.contains(busqueda)
            }

            val coincideFiltro = when (filtroActual) {
                FiltroChat.TODOS -> true
                FiltroChat.SOLO_ZOE -> {
                    val infoAutor = msg.usuario?.name?.lowercase() ?: ""
                    val textoMsg = msg.mensaje?.lowercase() ?: ""
                    infoAutor.contains("zoe") || textoMsg.contains("@zoe") || textoMsg.contains("📋") || textoMsg.contains("💰") || textoMsg.contains("💸")
                }
                FiltroChat.MULTIMEDIA -> {
                    val dump = msg.mensaje?.lowercase() ?: ""
                    dump.contains("http") || dump.contains("storage") || dump.contains(".jpg") || dump.contains(".png") || dump.contains(".m4a") || dump.contains(".opus")
                }
            }

            coincideTexto && coincideFiltro
        }
    }

    // Scroll inicial
    LaunchedEffect(uiState.mensajes.isNotEmpty(), uiState.cargando) {
        if (!uiState.cargando && uiState.mensajes.isNotEmpty() && !seHizoScrollInicial) {
            val primerNoLeido = uiState.primerNoLeidoId
            val indiceDestino = if (primerNoLeido != null && uiState.mensajesNoLeidos > 0) {
                val idx = mensajesVisibles.indexOfFirst { it.id == primerNoLeido }
                if (idx != -1) idx + 1 else mensajesVisibles.size + 1
            } else {
                mensajesVisibles.size + 1
            }

            listState.scrollToItem(indiceDestino)
            seHizoScrollInicial = true
        }
    }

    // Scroll automático al recibir mensajes nuevos o cuando ZOE empieza a redactar
    LaunchedEffect(uiState.mensajes.size, zoeEstaEscribiendo) {
        if (seHizoScrollInicial && uiState.mensajes.isNotEmpty() && filtroActual == FiltroChat.TODOS && textoBusqueda.isBlank()) {
            val ultimoVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount

            if (zoeEstaEscribiendo || ultimoVisible >= totalItems - 6) {
                delay(100)
                val nuevoTotal = listState.layoutInfo.totalItemsCount
                if (nuevoTotal > 0) {
                    listState.animateScrollToItem(nuevoTotal - 1)
                }
            }
        }
    }

    // Marcar leídos al scrollear al final
    LaunchedEffect(listState, uiState.mensajes.size, uiState.mensajesNoLeidos) {
        if (uiState.mensajesNoLeidos > 0 && uiState.mensajes.isNotEmpty()) {
            snapshotFlow {
                val ultimoVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
                val totalItems = listState.layoutInfo.totalItemsCount
                ultimoVisible >= totalItems - 2
            }
                .distinctUntilChanged()
                .collect { llegoAlFinal ->
                    if (llegoAlFinal) {
                        uiState.mensajes.lastOrNull()?.let { viewModel.marcarLeido(it.id) }
                    }
                }
        }
    }

    LaunchedEffect(uiState.primerNoLeidoId, uiState.mensajesNoLeidos) {
        val primerNoLeido = uiState.primerNoLeidoId
        val conteoActual = uiState.mensajesNoLeidos

        if (primerNoLeido != null && separadorNuevosId != primerNoLeido) {
            separadorNuevosId = primerNoLeido
            forzarOcultarNoLeidos = false
        }
        if (conteoActual > ultimoConteoNoLeidos || conteoActual == 0) {
            forzarOcultarNoLeidos = false
        }
        ultimoConteoNoLeidos = conteoActual
    }

    val mostrarBotonBajar by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val total = layoutInfo.totalItemsCount
            val ultimoVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            total > 0 && ultimoVisible < total - 3
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "online_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val grabacionAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rec_alpha"
    )

    if (!uiState.advertenciaModeracion.isNullOrBlank()) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarAdvertencia() },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Advertencia de Convivencia ZOE",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = uiState.advertenciaModeracion ?: "",
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.limpiarAdvertencia() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Entendido", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = primaryColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.Forum,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Grupo Residencial 21",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .scale(pulseScale)
                                                .clip(CircleShape)
                                                .background(Color(0xFF16A34A).copy(alpha = 0.4f))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .size(7.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF16A34A))
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    val textoConectados = if (uiState.personasEnLinea <= 1) {
                                        "1 vecino en línea • ZOE activa"
                                    } else {
                                        "${uiState.personasEnLinea} vecinos en línea • ZOE activa"
                                    }
                                    Text(
                                        text = textoConectados,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.Outlined.ArrowBack,
                                contentDescription = "Regresar",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    actions = {
                        IconButton(onClick = {
                            mostrarBuscador = !mostrarBuscador
                            if (!mostrarBuscador) textoBusqueda = ""
                        }) {
                            Icon(
                                imageVector = if (mostrarBuscador) Icons.Outlined.Close else Icons.Outlined.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            onClick = { mostrarAyudaZoe = true },
                            shape = RoundedCornerShape(20.dp),
                            color = primaryColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    Icons.Filled.SmartToy,
                                    contentDescription = "ZOE IA",
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "ZOE IA",
                                    color = primaryColor,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }

                        Box {
                            IconButton(onClick = { mostrarMenuChat = true }) {
                                Icon(
                                    Icons.Outlined.MoreVert,
                                    contentDescription = "Opciones",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            DropdownMenu(
                                expanded = mostrarMenuChat,
                                onDismissRequest = { mostrarMenuChat = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Comandos de ZOE") },
                                    leadingIcon = { Icon(Icons.Filled.SmartToy, contentDescription = null) },
                                    onClick = { mostrarMenuChat = false; mostrarAyudaZoe = true }
                                )
                                DropdownMenuItem(
                                    text = { Text(if (mostrarChipsZoe) "Ocultar barra rápida" else "Mostrar barra rápida") },
                                    leadingIcon = { Icon(Icons.Outlined.AutoAwesome, contentDescription = null) },
                                    onClick = {
                                        mostrarMenuChat = false
                                        val nuevoEstado = !mostrarChipsZoe
                                        mostrarChipsZoe = nuevoEstado
                                        prefs.edit().putBoolean("mostrar_chips_zoe", nuevoEstado).apply()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Ir al final del chat") },
                                    leadingIcon = { Icon(Icons.Outlined.VerticalAlignBottom, contentDescription = null) },
                                    onClick = {
                                        mostrarMenuChat = false
                                        coroutineScope.launch {
                                            val total = listState.layoutInfo.totalItemsCount
                                            if (total > 0) {
                                                listState.animateScrollToItem(total - 1)
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                )

                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        AnimatedVisibility(visible = mostrarBuscador) {
                            OutlinedTextField(
                                value = textoBusqueda,
                                onValueChange = { textoBusqueda = it },
                                placeholder = { Text("Buscar mensaje o vecino...", fontSize = 13.sp) },
                                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                                trailingIcon = {
                                    if (textoBusqueda.isNotEmpty()) {
                                        IconButton(onClick = { textoBusqueda = "" }) {
                                            Icon(Icons.Outlined.Clear, contentDescription = "Limpiar", modifier = Modifier.size(18.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FiltroChipItem(
                                texto = "Todos (${uiState.mensajes.size})",
                                icono = Icons.Outlined.ChatBubbleOutline,
                                seleccionado = filtroActual == FiltroChat.TODOS,
                                primaryColor = primaryColor,
                                onClick = { filtroActual = FiltroChat.TODOS }
                            )
                            FiltroChipItem(
                                texto = "ZOE IA",
                                icono = Icons.Filled.SmartToy,
                                seleccionado = filtroActual == FiltroChat.SOLO_ZOE,
                                primaryColor = primaryColor,
                                onClick = { filtroActual = FiltroChat.SOLO_ZOE }
                            )
                            FiltroChipItem(
                                texto = "Fotos / Audios",
                                icono = Icons.Outlined.PhotoLibrary,
                                seleccionado = filtroActual == FiltroChat.MULTIMEDIA,
                                primaryColor = primaryColor,
                                onClick = { filtroActual = FiltroChat.MULTIMEDIA }
                            )
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                            thickness = 0.8.dp
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    AnimatedVisibility(visible = uiState.mensajeEnEdicion != null) {
                        Surface(
                            color = primaryColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Editando mensaje",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                    Text(
                                        text = uiState.mensajeEnEdicion?.mensaje ?: "",
                                        fontSize = 11.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        viewModel.cancelarEdicion()
                                        textoInput = ""
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Cancelar edición", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = mostrarChipsZoe && archivoSeleccionadoUri == null && audioGrabadoUri == null && !estaGrabandoAudio && uiState.mensajeEnEdicion == null
                    ) {
                        Column {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                item {
                                    Surface(
                                        onClick = { mostrarAyudaZoe = true },
                                        shape = RoundedCornerShape(16.dp),
                                        color = primaryColor.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Filled.SmartToy,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "@zoe",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = primaryColor
                                            )
                                        }
                                    }
                                }

                                items(accionesRapidas) { accion ->
                                    Surface(
                                        onClick = { textoInput = accion.comando },
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        border = BorderStroke(
                                            width = 0.8.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = accion.icono,
                                                contentDescription = null,
                                                tint = accion.colorAccento,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Spacer(modifier = Modifier.width(5.dp))
                                            Text(
                                                text = accion.etiqueta,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = estaGrabandoAudio) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = grabacionAlpha))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Grabando nota de voz...",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { detenerGrabacion(guardar = false) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Delete,
                                        contentDescription = "Cancelar grabación",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { detenerGrabacion(guardar = true) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Check,
                                        contentDescription = "Finalizar grabación",
                                        tint = MaterialTheme.colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = audioGrabadoUri != null && !estaGrabandoAudio) {
                        Surface(
                            color = primaryColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(primaryColor.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.Mic,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Nota de voz lista",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                    Text(
                                        text = "Toca enviar para compartirla en el chat vecinal",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = {
                                    audioGrabadoUri?.let { File(it.path ?: "").delete() }
                                    audioGrabadoUri = null
                                }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Eliminar nota de voz")
                                }
                            }
                        }
                    }

                    AnimatedVisibility(visible = archivoSeleccionadoUri != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.3f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(primaryColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Outlined.AddPhotoAlternate,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Fotografía o evidencia adjunta",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Si es un reporte para la directiva, describe el incidente abajo",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(onClick = { archivoSeleccionadoUri = null }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Quitar archivo")
                                }
                            }
                        }
                    }

                    ChatInputArea(
                        texto = textoInput,
                        enviando = uiState.enviando,
                        esBloqueoPermanente = uiState.esBloqueoPermanente,
                        mensajeRespondido = uiState.mensajeRespondido,
                        usuariosEscribiendo = uiState.usuariosEscribiendo.filter {
                            it.id != usuarioActualId && it.name?.contains("zoe", ignoreCase = true) != true
                        },
                        estaGrabandoAudio = estaGrabandoAudio,
                        audioListoParaEnviar = audioGrabadoUri != null,
                        onTextoChange = {
                            textoInput = it
                            viewModel.actualizarEscribiendo(it.isNotBlank())
                        },
                        onEnviar = {
                            if (!uiState.enviando && !uiState.esBloqueoPermanente) {
                                when {
                                    audioGrabadoUri != null -> {
                                        viewModel.enviarMultimedia(audioGrabadoUri!!, textoInput.ifBlank { "Nota de voz" }, context)
                                        audioGrabadoUri = null
                                        textoInput = ""
                                    }
                                    archivoSeleccionadoUri != null -> {
                                        viewModel.enviarMultimedia(archivoSeleccionadoUri!!, textoInput, context)
                                        archivoSeleccionadoUri = null
                                        textoInput = ""
                                    }
                                    textoInput.isNotBlank() -> {
                                        val textoAEnviar = textoInput.trim()
                                        val esPreguntaZoe = textoAEnviar.contains("@zoe", ignoreCase = true)

                                        if (esPreguntaZoe) {
                                            conteoRespuestasZoeBase = uiState.mensajes.count { esMensajeDeZoe(it) }
                                            esperandoRespuestaZoe = true
                                        }

                                        viewModel.enviarMensaje(textoAEnviar)
                                        textoInput = ""
                                        viewModel.actualizarEscribiendo(false)
                                    }
                                }
                            }
                        },
                        onGrabarAudio = {
                            if (estaGrabandoAudio) {
                                detenerGrabacion(guardar = true)
                            } else {
                                val permissionCheck = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.RECORD_AUDIO
                                )
                                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    iniciarGrabacion()
                                } else {
                                    audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                        },
                        onAdjuntar = { imagePickerLauncher.launch("image/*") },
                        onCancelarRespuesta = { viewModel.cancelarRespuesta() }
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ChatWallpaperBackground(colorPatron = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f))

            when {
                uiState.cargando -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Sincronizando mensajes vecinales...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                mensajesVisibles.isEmpty() -> {
                    if (textoBusqueda.isNotBlank() || filtroActual != FiltroChat.TODOS) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.SearchOff,
                                contentDescription = null,
                                modifier = Modifier.size(56.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No se encontraron mensajes con ese filtro",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TextButton(onClick = {
                                textoBusqueda = ""
                                filtroActual = FiltroChat.TODOS
                            }) {
                                Text("Restablecer vista completa", color = primaryColor)
                            }
                        }
                    } else {
                        ChatEmptyState()
                    }
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 12.dp)
                    ) {
                        item(key = "header_seguridad_comunidad") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Outlined.VerifiedUser,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Canal oficial del Grupo Residencial 21. Menciona a @zoe para consultas automáticas.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        itemsIndexed(
                            items = mensajesVisibles,
                            key = { _, m -> m.id }
                        ) { index, mensaje ->
                            val fechaActual = obtenerEtiquetaFecha(mensaje.created_at)
                            val fechaAnterior = if (index > 0) obtenerEtiquetaFecha(mensajesVisibles[index - 1].created_at) else null

                            if (fechaActual.isNotBlank() && fechaActual != fechaAnterior) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                                        shadowElevation = 0.5.dp,
                                        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    ) {
                                        Text(
                                            text = fechaActual,
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            if (mensaje.id == separadorNuevosId) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = primaryColor.copy(alpha = 0.35f),
                                        thickness = 1.dp
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = primaryColor.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                                        modifier = Modifier.padding(horizontal = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.MarkChatUnread,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(15.dp)
                                            )
                                            Text(
                                                text = "Mensajes nuevos",
                                                color = primaryColor,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.weight(1f),
                                        color = primaryColor.copy(alpha = 0.35f),
                                        thickness = 1.dp
                                    )
                                }
                            }

                            ChatMessageItem(
                                mensaje = mensaje,
                                esPropio = mensaje.usuario?.id == usuarioActualId,
                                esResaltado = mensaje.id == mensajeResaltadoId,
                                darkTheme = esTemaOscuro,
                                onResponder = { viewModel.prepararRespuesta(mensaje) },
                                onMostrarReacciones = { mensajeAccionesSeleccionado = mensaje },
                                onReaccionar = { emoji -> viewModel.reaccionar(mensaje.id, emoji) },
                                onIrAlMensajeOriginal = { id ->
                                    coroutineScope.launch {
                                        val idx = mensajesVisibles.indexOfFirst { it.id == id }
                                        if (idx != -1) {
                                            mensajeResaltadoId = id
                                            listState.animateScrollToItem(idx + 1)
                                            delay(2000)
                                            if (mensajeResaltadoId == id) mensajeResaltadoId = null
                                        }
                                    }
                                }
                            )
                        }

                        // Indicador animado de ZOE redactando en onda
                        if (zoeEstaEscribiendo) {
                            item(key = "indicador_zoe_escribiendo") {
                                ZoeTypingBubble(primaryColor = primaryColor)
                            }
                        }

                        item(key = "spacer_inferior_chat") {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = mostrarBotonBajar,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            val total = listState.layoutInfo.totalItemsCount
                            if (total > 0) {
                                listState.animateScrollToItem(total - 1)
                            }
                        }
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = primaryColor,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.KeyboardDoubleArrowDown,
                        contentDescription = "Ir al final"
                    )
                }
            }

            val mostrarBotonNoLeidos = uiState.mensajesNoLeidos > 0 && !forzarOcultarNoLeidos
            AnimatedVisibility(
                visible = mostrarBotonNoLeidos,
                enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { 50 }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .clickable {
                            coroutineScope.launch {
                                forzarOcultarNoLeidos = true
                                val idDestino = separadorNuevosId ?: uiState.primerNoLeidoId
                                val idx = mensajesVisibles.indexOfFirst { it.id == idDestino }
                                if (idx >= 0) {
                                    listState.animateScrollToItem(idx + 1)
                                } else {
                                    val total = listState.layoutInfo.totalItemsCount
                                    if (total > 0) listState.animateScrollToItem(total - 1)
                                }
                            }
                        },
                    color = primaryColor,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ArrowDownward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${uiState.mensajesNoLeidos} mensajes nuevos",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (!uiState.error.isNullOrBlank() && !uiState.esBloqueoPermanente) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.error ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.limpiarError() }) {
                            Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            // Menú contextual estilo WhatsApp
            val msgSeleccionado = mensajeAccionesSeleccionado
            if (msgSeleccionado != null && !msgSeleccionado.eliminado) {
                val esPropio = msgSeleccionado.usuario?.id == usuarioActualId
                val esDirectiva = msgSeleccionado.usuario?.cargo_id != null

                Dialog(onDismissRequest = { mensajeAccionesSeleccionado = null }) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        tonalElevation = 6.dp,
                        shadowElevation = 8.dp,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth(0.92f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                EmojisReaccion.forEach { emoji ->
                                    Text(
                                        text = emoji,
                                        fontSize = 26.sp,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable {
                                                viewModel.reaccionar(msgSeleccionado.id, emoji)
                                                mensajeAccionesSeleccionado = null
                                            }
                                            .padding(4.dp)
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.prepararRespuesta(msgSeleccionado)
                                        mensajeAccionesSeleccionado = null
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Outlined.Reply, contentDescription = null, tint = primaryColor)
                                Spacer(modifier = Modifier.width(14.dp))
                                Text(
                                    text = "Responder",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            val esMultimedia = msgSeleccionado.obtenerArchivoAdjunto() != null

                            if (esPropio && !esMultimedia) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.prepararEdicion(msgSeleccionado)
                                            mensajeAccionesSeleccionado = null
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Edit, contentDescription = null, tint = primaryColor)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = "Editar mensaje",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            if (esPropio || esDirectiva) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable {
                                            viewModel.eliminarMensaje(msgSeleccionado.id)
                                            mensajeAccionesSeleccionado = null
                                        }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Text(
                                        text = if (esPropio) "Eliminar para todos" else "Eliminar como moderador",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (mostrarAyudaZoe) {
                ModalBottomSheet(
                    onDismissRequest = { mostrarAyudaZoe = false },
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .padding(bottom = 28.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(primaryColor.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.SmartToy,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Centro de Comandos ZOE",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Toca cualquier consulta para usarla en el chat",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )

                        accionesRapidas.forEach { item ->
                            Surface(
                                onClick = {
                                    textoInput = item.comando
                                    mostrarAyudaZoe = false
                                },
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(item.colorAccento.copy(alpha = 0.12f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = item.icono,
                                            contentDescription = null,
                                            tint = item.colorAccento,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.etiqueta,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = item.comando,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(
                                        Icons.Outlined.NorthWest,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| BURBUJA DE CHAT: ZOE REDACTANDO EN ONDA
|--------------------------------------------------------------------------
*/

@Composable
private fun ZoeTypingBubble(
    primaryColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(primaryColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.SmartToy,
                contentDescription = "ZOE redactando",
                tint = primaryColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomEnd = 16.dp,
                bottomStart = 4.dp
            ),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f),
            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f)),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ZOE redactando",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor
                )
                TypingWaveIndicator(
                    dotColor = primaryColor,
                    dotSize = 6.dp,
                    travelDistance = 4.dp
                )
            }
        }
    }
}

@Composable
private fun TypingWaveIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 6.dp,
    travelDistance: Dp = 4.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_wave_anim")
    val dotDelays = listOf(0, 160, 320)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        dotDelays.forEachIndexed { index, delay ->
            val fraction by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1000
                        0.0f at delay using FastOutSlowInEasing
                        0.5f at delay + 200 using FastOutSlowInEasing
                        0.0f at delay + 400 using FastOutSlowInEasing
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dot_fraction_$index"
            )

            val desplazamientoY = -travelDistance.value * fraction
            val opacidad = 0.35f + (fraction * 0.65f)

            Box(
                modifier = Modifier
                    .size(dotSize)
                    .offset(y = desplazamientoY.dp)
                    .background(
                        color = dotColor.copy(alpha = opacidad),
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun FiltroChipItem(
    texto: String,
    icono: ImageVector,
    seleccionado: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (seleccionado) primaryColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = if (seleccionado) null else BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = texto,
                fontSize = 11.sp,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                color = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ChatWallpaperBackground(colorPatron: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val paso = 38.dp.toPx()
        val radioPunto = 1.4.dp.toPx()
        val cols = (size.width / paso).toInt() + 1
        val rows = (size.height / paso).toInt() + 1

        for (r in 0..rows) {
            for (c in 0..cols) {
                val offsetX = c * paso + if (r % 2 == 0) 0f else paso / 2f
                val offsetY = r * paso
                drawCircle(
                    color = colorPatron,
                    radius = radioPunto,
                    center = Offset(offsetX, offsetY)
                )
            }
        }
    }
}