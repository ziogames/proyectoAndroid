@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.sigi

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.ERROR_INVALID_OPERATION
import android.media.MediaRecorder
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.sigefiv.app.data.model.ConversacionItem
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.ZoeMensaje
import com.sigefiv.app.viewmodel.ZoeViewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES PROFESIONAL SIGEFIV
|--------------------------------------------------------------------------
*/

private val VerdeSuaveAvatar = Color(0xFFDCFCE7)
private val VerdeOnline = Color(0xFF22C55E)
private val VerdeBurbujaUsuario = Color(0xFFDCF8C6)
private val AzulCheck = Color(0xFF0284C7)

@Composable
fun SigiScreen(
    onInicioClick: () -> Unit = {},
    onMovimientosClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onMasClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onOpenHistory: () -> Unit = {}
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val temaOscuro =
        MaterialTheme.colorScheme.background.luminance() < 0.5f

    val colorBarraTitulo =
        if (temaOscuro) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.White
        }

    val colorContenidoBarra =
        if (temaOscuro) {
            MaterialTheme.colorScheme.onSurface
        } else {
            Color(0xFF1F2937)
        }

    val context = LocalContext.current
    val viewModel = remember(context) {
        ZoeViewModel(context.applicationContext)
    }

    val mensajes by viewModel.mensajes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val hablando by viewModel.hablando.collectAsState()
    val escuchando by viewModel.escuchando.collectAsState()

    // Observamos el flujo de historial real desde el ViewModel
    val historialConversaciones by viewModel.historialConversaciones.collectAsState()

    // Estados para el Panel Lateral de Historial (Drawer)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Desplazamiento vertical para deslizar la pantalla hacia abajo/arriba con la barra superior
    val offsetY = remember { Animatable(0f) }
    val archivoAdjuntoUri by viewModel.archivoAdjuntoUri.collectAsState()
    // Disparador del saludo inicial al abrir la pantalla por primera vez
    LaunchedEffect(Unit) {
        if (mensajes.isEmpty()) {
            viewModel.enviarEventoSilencioso("evento_inicio_sesion")
        }
    }

    var consulta by remember { mutableStateOf("") }
    var grabandoVoz by remember { mutableStateOf(false) }
    var grabadora by remember { mutableStateOf<GrabadorWav?>(null) }
    var archivoVoz by remember { mutableStateOf<File?>(null) }
    var segundosGrabacion by remember { mutableStateOf(0) }

    // Estados profesionales para cámara y selector de archivos
    var uriFotoTemporal by remember { mutableStateOf<Uri?>(null) }
    var mostrarMenuAdjuntar by remember { mutableStateOf(false) }

    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && uriFotoTemporal != null) {
            viewModel.seleccionarArchivo(uriFotoTemporal)
        }
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { archivoUri ->
            viewModel.seleccionarArchivo(archivoUri)
        }
    }

    fun abrirCamaraDirectamente() {
        try {
            val archivoFoto = File(context.cacheDir, "zoe_camara_${System.currentTimeMillis()}.jpg")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                archivoFoto
            )
            uriFotoTemporal = uri
            lanzadorCamara.launch(uri)
        } catch (e: Exception) {
            android.util.Log.e("ZOE_CAMARA", "Error al preparar la cámara: ${e.message}", e)
        }
    }

    val listaEstado = rememberLazyListState()

    // Lanzador para solicitar el permiso de la cámara en tiempo de ejecución
    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            abrirCamaraDirectamente()
        } else {
            android.util.Log.w("ZOE_CAMARA", "Permiso de cámara denegado por el usuario")
        }
    }

    val permisoMicrofono = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            iniciarGrabacionVoz(
                context = context,
                onGrabando = { grabandoVoz = it },
                onGrabadora = { grabadora = it },
                onArchivo = { archivoVoz = it }
            )
        }
    }

    LaunchedEffect(grabandoVoz) {
        if (grabandoVoz) {
            segundosGrabacion = 0
            while (grabandoVoz) {
                delay(1000)
                if (grabandoVoz) {
                    segundosGrabacion++
                }
            }
        } else {
            segundosGrabacion = 0
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            grabadora?.let { grabador ->
                try {
                    grabador.detener()
                } catch (_: Exception) {
                }
            }
            archivoVoz?.delete()
        }
    }

    fun enviarConsulta() {
        val texto = consulta.trim()
        if (texto.isEmpty() || cargando) return
        viewModel.enviarConsulta(texto)
        consulta = ""
    }

    fun activarMicrofono() {
        android.util.Log.d("ZOE_AUDIO", "activarMicrofono() ejecutado")
        if (cargando) return

        val tienePermiso =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

        if (!tienePermiso) {
            permisoMicrofono.launch(Manifest.permission.RECORD_AUDIO)
            return
        }

        if (viewModel.escuchando.value) {
            viewModel.detenerEscucha()
        } else {
            viewModel.iniciarEscucha()
        }
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listaEstado.animateScrollToItem(mensajes.size - 1)
        }
    }

    // PANEL LATERAL DESLIZABLE (DRAWER) PROFESIONAL Y DINÁMICO
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                ContenidoPanelHistorial(
                    listaConversaciones = historialConversaciones,
                    onConversacionClick = { conversacionId ->
                        coroutineScope.launch {
                            drawerState.close()
                            viewModel.cargarConversacion(conversacionId)
                        }
                    },
                    onNuevaConversacion = {
                        coroutineScope.launch {
                            drawerState.close()
                            viewModel.iniciarNuevaConversacion()
                        }
                    },
                    onCerrar = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorBarraTitulo)
                        .draggable(
                            orientation = Orientation.Vertical,
                            state = rememberDraggableState { delta ->
                                coroutineScope.launch {
                                    val nuevoValor = (offsetY.value + delta).coerceAtLeast(0f)
                                    offsetY.snapTo(nuevoValor)
                                }
                            },
                            onDragStopped = { velocity ->
                                coroutineScope.launch {
                                    if (offsetY.value > 280f || velocity > 900f) {
                                        offsetY.animateTo(
                                            targetValue = 2200f,
                                            animationSpec = tween(durationMillis = 220)
                                        )
                                        onBackClick()
                                    } else {
                                        offsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = tween(durationMillis = 250)
                                        )
                                    }
                                }
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 2.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                colorContenidoBarra.copy(alpha = 0.30f)
                            )
                    )

                    TopAppBar(
                        windowInsets = WindowInsets(0.dp),
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(VerdeSuaveAvatar, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.SmartToy,
                                        contentDescription = "ZOE",
                                        tint = colorPrincipal,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column {
                                    Text(
                                        text = "ZOE Asistente",
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(VerdeOnline, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(5.dp))
                                        Text(
                                            text = if (cargando) "Escribiendo..." else "En línea",
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        viewModel.cargarHistorial()
                                        if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.History,
                                    contentDescription = "Historial de conversaciones",
                                    tint = colorContenidoBarra
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = colorBarraTitulo,
                            titleContentColor = colorContenidoBarra,
                            actionIconContentColor = colorContenidoBarra
                        )
                    )
                }
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // 1. BARRA DE ACCIONES RÁPIDAS
                SugerenciasChipsBar(
                    onPregunta = { pregunta ->
                        if (!cargando) viewModel.enviarConsulta(pregunta)
                    }
                )

                // 2. HISTORIAL DE CONVERSACIÓN
                LazyColumn(
                    state = listaEstado,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(
                        count = mensajes.size,
                        key = { index ->
                            val mensaje = mensajes[index]
                            "$index-${mensaje.esUsuario}-${mensaje.texto.hashCode()}"
                        }
                    ) { index ->
                        BurbujaChat(mensajes[index])
                    }

                    if (cargando) {
                        item(key = "loading_indicator") {
                            IndicadorCargaMensaje()
                        }
                    }
                }

                // ... dentro del Scaffold -> Column:
                BarraEntradaModerna(
                    consulta = consulta,
                    onConsultaChange = { consulta = it },
                    onEnviar = {
                        // Usamos la función inteligente que detecta si hay archivo o solo texto
                        viewModel.enviarConsultaOAdjunto(context, consulta)
                        consulta = "" // Limpiamos la caja de texto tras enviar
                    },
                    onMicrofono = { activarMicrofono() },
                    onDetenerVoz = { viewModel.detenerVoz() },
                    grabandoVoz = escuchando,
                    segundosGrabacion = segundosGrabacion,
                    cargando = cargando,
                    hablando = hablando,
                    mostrarMenuAdjuntar = mostrarMenuAdjuntar,
                    onToggleMenuAdjuntar = { mostrarMenuAdjuntar = it },
                    onCamaraClick = {
                        val tienePermisoCamara = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED

                        if (tienePermisoCamara) {
                            abrirCamaraDirectamente()
                        } else {
                            permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    onGaleriaClick = { lanzadorGaleria.launch("*/*") },
                    archivoAdjuntoUri = archivoAdjuntoUri, // <--- NUEVO
                    onEliminarAdjunto = { viewModel.limpiarAdjunto() } // <--- NUEVO
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| CONTENIDO DEL PANEL LATERAL DE HISTORIAL REFINADO
|--------------------------------------------------------------------------
*/

@Composable
private fun ContenidoPanelHistorial(
    listaConversaciones: List<ConversacionItem>,
    onConversacionClick: (String) -> Unit,
    onNuevaConversacion: () -> Unit,
    onCerrar: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.History,
                    contentDescription = null,
                    tint = colorPrincipal,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Historial de ZOE",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onCerrar) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Cerrar historial",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onNuevaConversacion),
            color = colorPrincipal.copy(alpha = 0.12f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = null,
                    tint = colorPrincipal,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+ Nueva Conversación",
                    color = colorPrincipal,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        if (listaConversaciones.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay conversaciones registradas",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val grupos = listaConversaciones.groupBy { it.grupo }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                grupos.forEach { (nombreGrupo, itemsDelGrupo) ->
                    item(key = "header_$nombreGrupo") {
                        Text(
                            text = nombreGrupo,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorPrincipal,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                        )
                    }

                    items(itemsDelGrupo, key = { it.id }) { chat ->
                        ItemConversacionHistorial(
                            titulo = chat.titulo,
                            onClick = { onConversacionClick(chat.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemConversacionHistorial(
    titulo: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        color = Color.Transparent,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 11.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = titulo,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| BURBUJA DE MENSAJE REFINADA (TIPO WHATSAPP)
|--------------------------------------------------------------------------
*/

@Composable
private fun BurbujaChat(mensaje: ZoeMensaje) {
    val context = LocalContext.current
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val temaOscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val colorBurbujaUsuario =
        if (temaOscuro) Color(0xFF075E3B) else Color(0xFFDCF8C6)

    val colorTextoUsuario =
        if (temaOscuro) Color.White else Color(0xFF14532D)

    val colorHoraUsuario =
        if (temaOscuro) Color.White.copy(alpha = 0.70f)
        else Color(0xFF45634D)

    if (mensaje.esUsuario) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 3.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = colorBurbujaUsuario
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.widthIn(min = 50.dp, max = 290.dp)
            ) {
                Column(
                    modifier = Modifier.padding(all = 8.dp)
                ) {
                    // 🖼️ SI EL MENSAJE TIENE UNA IMAGEN ADJUNTA, LA MOSTRAMOS AQUÍ EN LA BURBUJA
                    // 🖼️ SI EL MENSAJE TIENE UNA IMAGEN ADJUNTA, LA MOSTRAMOS AQUÍ EN LA BURBUJA
                    mensaje.imagenUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Imagen adjunta del reporte",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop // <--- CORREGIDO AQUÍ
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (mensaje.texto.isNotBlank()) {
                            Text(
                                text = mensaje.texto,
                                color = colorTextoUsuario,
                                fontSize = 14.5.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = mensaje.hora,
                                color = colorHoraUsuario,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "✓✓",
                                color = if (temaOscuro) Color(0xFF86EFAC) else AzulCheck,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(VerdeSuaveAvatar, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = "ZOE",
                    tint = colorPrincipal,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 3.dp,
                    bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ZoeRespuestaFormateada(
                        texto = mensaje.texto,
                        tipoMovimiento = mensaje.tipoMovimiento,
                        modifier = Modifier.fillMaxWidth()
                    )
                    mensaje.estadistica?.let { estadistica ->
                        ZoeEstadisticaChart(
                            estadistica = estadistica,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    mensaje.estadistica
                        ?.takeIf { it.tipo == "emprendimientos" }
                        ?.emprendimientos
                        ?.takeIf { it.isNotEmpty() }
                        ?.let { listaEmprendimientos ->
                            Spacer(modifier = Modifier.height(10.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                listaEmprendimientos.forEach { emprendimiento ->
                                    val colorFondoTarjeta = if (temaOscuro) Color(0xFF1E242B) else Color.White
                                    val colorBorde = if (temaOscuro) Color.White.copy(alpha = 0.09f) else Color(0xFFE2E8F0)

                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = CardDefaults.cardColors(containerColor = colorFondoTarjeta),
                                        border = BorderStroke(1.dp, colorBorde),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(42.dp)
                                                        .background(
                                                            colorPrincipal.copy(alpha = 0.12f),
                                                            RoundedCornerShape(12.dp)
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.Storefront,
                                                        contentDescription = null,
                                                        tint = colorPrincipal,
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                }

                                                Spacer(modifier = Modifier.width(12.dp))

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = emprendimiento.nombre?.takeIf { it.isNotBlank() }
                                                            ?: "Emprendimiento Vecinal",
                                                        color = MaterialTheme.colorScheme.onSurface,
                                                        fontSize = 16.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    val etiqueta = emprendimiento.categoria?.takeIf { it.isNotBlank() }
                                                        ?: emprendimiento.modalidad?.takeIf { it.isNotBlank() }

                                                    if (etiqueta != null) {
                                                        Spacer(modifier = Modifier.height(2.dp))
                                                        Text(
                                                            text = etiqueta.uppercase(),
                                                            color = colorPrincipal,
                                                            fontSize = 10.5.sp,
                                                            fontWeight = FontWeight.SemiBold,
                                                            letterSpacing = 0.5.sp
                                                        )
                                                    }
                                                }
                                            }

                                            emprendimiento.descripcion?.takeIf { it.isNotBlank() }?.let { desc ->
                                                Text(
                                                    text = desc,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                                                    fontSize = 13.5.sp,
                                                    lineHeight = 19.sp
                                                )
                                            }

                                            val tieneZona = !emprendimiento.zona_atencion.isNullOrBlank()
                                            val tieneHorario = !emprendimiento.horario.isNullOrBlank()

                                            if (tieneZona || tieneHorario) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(
                                                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                                            shape = RoundedCornerShape(10.dp)
                                                        )
                                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                                ) {
                                                    if (tieneZona) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = Icons.Outlined.LocationOn,
                                                                contentDescription = "Zona",
                                                                tint = colorPrincipal,
                                                                modifier = Modifier.size(15.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "Zona: ${emprendimiento.zona_atencion}",
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                fontSize = 12.sp
                                                            )
                                                        }
                                                    }

                                                    if (tieneHorario) {
                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Icon(
                                                                imageVector = Icons.Outlined.AccessTime,
                                                                contentDescription = "Horario",
                                                                tint = colorPrincipal,
                                                                modifier = Modifier.size(15.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = "Horario: ${emprendimiento.horario}",
                                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                fontSize = 12.sp
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            val telefonoLimpio = emprendimiento.telefono?.trim().orEmpty()

                                            if (telefonoLimpio.isNotEmpty()) {
                                                HorizontalDivider(
                                                    color = colorBorde,
                                                    thickness = 1.dp
                                                )

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Surface(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(RoundedCornerShape(12.dp))
                                                            .clickable {
                                                                llamarVecino(context, telefonoLimpio)
                                                            },
                                                        shape = RoundedCornerShape(12.dp),
                                                        color = colorPrincipal.copy(alpha = 0.10f),
                                                        border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.25f))
                                                    ) {
                                                        Row(
                                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                                            verticalAlignment = Alignment.CenterVertically
                                                        ) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(28.dp)
                                                                    .background(colorPrincipal, CircleShape),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Icon(
                                                                    imageVector = Icons.Outlined.Phone,
                                                                    contentDescription = "Llamar al vecino",
                                                                    tint = Color.White,
                                                                    modifier = Modifier.size(15.dp)
                                                                )
                                                            }
                                                            Spacer(modifier = Modifier.width(10.dp))
                                                            Column {
                                                                Text(
                                                                    text = "Llamar al vecino",
                                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                                    fontSize = 10.sp
                                                                )
                                                                Text(
                                                                    text = formatearTelefonoPeru(telefonoLimpio),
                                                                    color = MaterialTheme.colorScheme.onSurface,
                                                                    fontSize = 13.5.sp,
                                                                    fontWeight = FontWeight.Bold
                                                                )
                                                            }
                                                        }
                                                    }

                                                    if (emprendimiento.show_whatsapp) {
                                                        Spacer(modifier = Modifier.width(12.dp))

                                                        IconButton(
                                                            onClick = {
                                                                abrirWhatsApp(context, telefonoLimpio)
                                                            },
                                                            modifier = Modifier
                                                                .size(46.dp)
                                                                .background(Color(0xFF25D366), CircleShape)
                                                        ) {
                                                            IconoWhatsAppVector(
                                                                modifier = Modifier.size(24.dp),
                                                                tint = Color.White
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

                    Text(
                        text = mensaje.hora,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                end = 8.dp,
                                top = 3.dp
                            ),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| RESPUESTA FORMATEADA DE ZOE
|--------------------------------------------------------------------------
*/

@Composable
private fun ZoeRespuestaFormateada(
    texto: String,
    tipoMovimiento: String? = null,
    modifier: Modifier = Modifier
) {
    val lineas = texto.lines()

    val regexMovimiento = Regex(
        """^\s*(?:•|-|\d+\.)\s*(\d{2}-\d{2}-\d{4})\s*—\s*S/\s*([\d,]+(?:\.\d{1,2})?)\s*(?:—\s*(.*))?$"""
    )

    val movimientos = mutableListOf<MovimientoVisual>()

    for (linea in lineas) {
        val match = regexMovimiento.find(linea)

        if (match != null) {
            val fecha = match.groupValues[1]
            val monto = match.groupValues[2]
            val resto = match.groupValues
                .getOrNull(3)
                ?.trim()
                .orEmpty()

            val partes = resto
                .split(" — ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            val categoria = partes.getOrNull(0).orEmpty()
            val usuario = partes.getOrNull(1).orEmpty()
            val formaPago = partes.drop(2).joinToString(" — ")

            movimientos.add(
                MovimientoVisual(
                    fecha = fecha,
                    monto = "S/ $monto",
                    categoria = categoria,
                    usuario = usuario,
                    formaPago = formaPago
                )
            )
        }
    }

    if (movimientos.isEmpty()) {
        Text(
            text = texto,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.5.sp,
            lineHeight = 19.sp,
            modifier = modifier
        )
        return
    }

    val esEgreso = tipoMovimiento == "Egreso"
    val esIngreso = tipoMovimiento == "Ingreso"

    val colorTipo =
        when {
            esEgreso -> Color(0xFFDC2626)
            esIngreso -> Color(0xFF16A34A)
            else -> MaterialTheme.colorScheme.primary
        }

    val fechaReferencia = movimientos.firstOrNull()?.fecha.orEmpty()

    val tituloBase =
        when {
            esIngreso -> "📈 INGRESOS"
            esEgreso -> "📉 EGRESOS"
            else -> "📋 MOVIMIENTOS"
        }

    val titulo =
        if (fechaReferencia.length >= 10) {
            val anio = fechaReferencia.substring(6, 10)
            val mes = fechaReferencia.substring(3, 5)
            "$tituloBase — ${nombreMesZoe(mes)} $anio"
        } else {
            tituloBase
        }

    val icono =
        when {
            esIngreso -> "🟢"
            esEgreso -> "🔴"
            else -> "🔹"
        }

    val resumen = lineas.firstOrNull {
        it.startsWith("Encontré ", ignoreCase = true)
    }

    val totalTexto = lineas.lastOrNull {
        it.trim().startsWith("💰 Total:", ignoreCase = true)
    }

    val totalCalculado =
        movimientos.sumOf { movimiento ->
            movimiento.monto
                .removePrefix("S/")
                .replace(",", "")
                .trim()
                .toDoubleOrNull() ?: 0.0
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = titulo,
            color = colorTipo,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Bold
        )

        if (resumen != null) {
            Text(
                text = resumen,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        movimientos.forEach { movimiento ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 9.dp
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$icono ${movimiento.fecha}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = movimiento.monto,
                            color = colorTipo,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End
                        )
                    }

                    if (movimiento.categoria.isNotBlank()) {
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = movimiento.categoria,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (movimiento.usuario.isNotBlank()) {
                        Text(
                            text = "👤 ${movimiento.usuario}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    }

                    if (movimiento.formaPago.isNotBlank()) {
                        Text(
                            text = "💳 ${movimiento.formaPago}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorTipo.copy(alpha = 0.10f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Total",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "💰 ${
                            if (totalTexto != null) {
                                totalTexto.substringAfter(":").trim()
                            } else {
                                "S/ %.2f".format(totalCalculado)
                            }
                        }",
                        color = colorTipo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${movimientos.size} movimientos",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

private fun nombreMesZoe(mes: String): String {
    return when (mes) {
        "01" -> "ENERO"
        "02" -> "FEBRERO"
        "03" -> "MARZO"
        "04" -> "ABRIL"
        "05" -> "MAYO"
        "06" -> "JUNIO"
        "07" -> "JULIO"
        "08" -> "AGOSTO"
        "09" -> "SEPTIEMBRE"
        "10" -> "OCTUBRE"
        "11" -> "NOVIEMBRE"
        "12" -> "DICIEMBRE"
        else -> ""
    }
}

private data class MovimientoVisual(
    val fecha: String,
    val monto: String,
    val categoria: String,
    val usuario: String,
    val formaPago: String
)

/*
|--------------------------------------------------------------------------
| SUGERENCIAS DESLIZABLES TIPO CHIPS HORIZONTALES
|--------------------------------------------------------------------------
*/

@Composable
private fun SugerenciasChipsBar(
    onPregunta: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SugerenciaChip(
                icon = Icons.Outlined.CalendarMonth,
                texto = "¿Período actual?",
                onClick = { onPregunta("¿Cuál es el período actual?") }
            )
        }
        item {
            SugerenciaChip(
                icon = Icons.Outlined.Payments,
                texto = "¿Saldo en caja?",
                onClick = {
                    onPregunta("¿Cuál es el saldo en caja del período actual?")
                }
            )
        }
        item {
            SugerenciaChip(
                icon = Icons.Outlined.BarChart,
                texto = "Resumen del mes",
                onClick = {
                    onPregunta("RESUMEN_PERIODO_ACTIVO")
                }
            )
        }
    }
}

@Composable
private fun SugerenciaChip(
    icon: ImageVector,
    texto: String,
    onClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorPrincipal,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = texto,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| INDICADOR DE CARGA DINÁMICO
|--------------------------------------------------------------------------
*/

@Composable
private fun IndicadorCargaMensaje() {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(VerdeSuaveAvatar, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SmartToy,
                contentDescription = "ZOE",
                tint = colorPrincipal,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(13.dp),
                    color = colorPrincipal,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ZOE está redactando...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| BARRA DE ENTRADA MODERNA
|--------------------------------------------------------------------------
*/

private fun iniciarGrabacionVoz(
    context: android.content.Context,
    onGrabando: (Boolean) -> Unit,
    onGrabadora: (GrabadorWav?) -> Unit,
    onArchivo: (File?) -> Unit
) {
    try {
        val archivo = File(
            context.cacheDir,
            "zoe_${System.currentTimeMillis()}.wav"
        )

        val grabador = GrabadorWav(archivo)
        grabador.iniciar()

        onArchivo(archivo)
        onGrabadora(grabador)
        onGrabando(true)

    } catch (_: Exception) {
        onArchivo(null)
        onGrabadora(null)
        onGrabando(false)
    }
}

private class GrabadorWav(
    private val archivo: File
) {

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioRecord: AudioRecord? = null
    private var hiloGrabacion: Thread? = null
    private var grabando = false

    fun iniciar() {
        val bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        )

        if (bufferSize == ERROR_INVALID_OPERATION || bufferSize <= 0) {
            throw IllegalStateException("No se pudo obtener el buffer de audio")
        }

        val buffer = ByteArray(bufferSize)

        val record = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            bufferSize * 2
        )

        if (record.state != AudioRecord.STATE_INITIALIZED) {
            record.release()
            throw IllegalStateException("No se pudo inicializar el micrófono")
        }

        audioRecord = record
        grabando = true

        hiloGrabacion = Thread {
            val archivoPcm = File(
                archivo.parentFile,
                "${archivo.nameWithoutExtension}.pcm"
            )

            try {
                FileOutputStream(archivoPcm).use { output ->
                    record.startRecording()

                    while (grabando) {
                        val cantidad = record.read(
                            buffer,
                            0,
                            buffer.size
                        )

                        if (cantidad > 0) {
                            output.write(buffer, 0, cantidad)
                        }
                    }
                }

                convertirPcmAWav(archivoPcm, archivo)

            } catch (_: Exception) {
                archivo.delete()
            } finally {
                archivoPcm.delete()

                try {
                    record.stop()
                } catch (_: Exception) {
                }

                record.release()
                audioRecord = null
            }
        }.apply {
            start()
        }
    }

    fun detener(): File? {
        grabando = false

        try {
            hiloGrabacion?.join(1500)
        } catch (_: InterruptedException) {
        }

        hiloGrabacion = null

        return if (archivo.exists() && archivo.length() > 44L) {
            archivo
        } else {
            null
        }
    }

    private fun convertirPcmAWav(
        pcm: File,
        wav: File
    ) {
        val datosAudio = pcm.length()

        FileOutputStream(wav).use { output ->
            val header = ByteArray(44)

            escribirAscii(header, 0, "RIFF")
            escribirIntLE(header, 4, (36 + datosAudio).toInt())
            escribirAscii(header, 8, "WAVE")
            escribirAscii(header, 12, "fmt ")
            escribirIntLE(header, 16, 16)
            escribirShortLE(header, 20, 1)
            escribirShortLE(header, 22, 1)
            escribirIntLE(header, 24, SAMPLE_RATE)
            escribirIntLE(header, 28, SAMPLE_RATE * 16 * 1 / 8)
            escribirShortLE(header, 32, 2)
            escribirShortLE(header, 34, 16)
            escribirAscii(header, 36, "data")
            escribirIntLE(header, 40, datosAudio.toInt())

            output.write(header)

            pcm.inputStream().use { input ->
                input.copyTo(output)
            }
        }
    }

    private fun escribirAscii(
        buffer: ByteArray,
        offset: Int,
        texto: String
    ) {
        texto.toByteArray(Charsets.US_ASCII)
            .copyInto(buffer, offset)
    }

    private fun escribirIntLE(
        buffer: ByteArray,
        offset: Int,
        valor: Int
    ) {
        buffer[offset] = (valor and 0xff).toByte()
        buffer[offset + 1] = ((valor shr 8) and 0xff).toByte()
        buffer[offset + 2] = ((valor shr 16) and 0xff).toByte()
        buffer[offset + 3] = ((valor shr 24) and 0xff).toByte()
    }

    private fun escribirShortLE(
        buffer: ByteArray,
        offset: Int,
        valor: Int
    ) {
        buffer[offset] = (valor and 0xff).toByte()
        buffer[offset + 1] = ((valor shr 8) and 0xff).toByte()
    }
}


@Composable
private fun BarraEntradaModerna(
    consulta: String,
    onConsultaChange: (String) -> Unit,
    onEnviar: () -> Unit,
    onMicrofono: () -> Unit,
    onDetenerVoz: () -> Unit,
    grabandoVoz: Boolean,
    segundosGrabacion: Int,
    cargando: Boolean,
    hablando: Boolean,
    mostrarMenuAdjuntar: Boolean,
    onToggleMenuAdjuntar: (Boolean) -> Unit,
    onCamaraClick: () -> Unit,
    onGaleriaClick: () -> Unit,
    archivoAdjuntoUri: Uri?,           // <-- Parámetro añadido
    onEliminarAdjunto: () -> Unit      // <-- Parámetro añadido
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val hayTexto = consulta.trim().isNotEmpty() || archivoAdjuntoUri != null

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 🖼️ VISTA PREVIA FLOTANTE DE LA IMAGEN ADJUNTA
            if (archivoAdjuntoUri != null) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = archivoAdjuntoUri,
                        contentDescription = "Vista previa de imagen adjunta",
                        modifier = Modifier.fillMaxSize()
                    )

                    // Botón circular "X" para descartar la foto
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(22.dp)
                            .clickable { onEliminarAdjunto() },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.6f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "✕",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (grabandoVoz) {
                    BarraGrabandoVoz(
                        segundos = segundosGrabacion,
                        colorPrincipal = colorPrincipal,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                    )
                } else {
                    OutlinedTextField(
                        value = consulta,
                        onValueChange = onConsultaChange,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        enabled = !cargando,
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = if (archivoAdjuntoUri != null) "Añade un comentario (opcional)..." else "Escribe un mensaje...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        },
                        maxLines = 1,
                        shape = RoundedCornerShape(24.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (hayTexto) {
                                    onEnviar()
                                }
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            cursorColor = colorPrincipal
                        )
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Botón de Adjuntar con Menú Profesional Desplegable
                Box {
                    IconButton(
                        onClick = { onToggleMenuAdjuntar(true) },
                        enabled = !grabandoVoz,
                        modifier = Modifier.size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AttachFile,
                            contentDescription = "Adjuntar archivo o foto",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(23.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = mostrarMenuAdjuntar,
                        onDismissRequest = { onToggleMenuAdjuntar(false) }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tomar foto") },
                            onClick = {
                                onToggleMenuAdjuntar(false)
                                onCamaraClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.CameraAlt,
                                    contentDescription = null,
                                    tint = colorPrincipal
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Elegir de galería / archivos") },
                            onClick = {
                                onToggleMenuAdjuntar(false)
                                onGaleriaClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Image,
                                    contentDescription = null,
                                    tint = colorPrincipal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                if (hablando) {
                    IconButton(
                        onClick = onDetenerVoz,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = Color(0xFFEF4444),
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Stop,
                            contentDescription = "Detener voz de ZOE",
                            tint = Color.White,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                } else if (hayTexto && !grabandoVoz) {
                    IconButton(
                        onClick = onEnviar,
                        enabled = !cargando,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = colorPrincipal,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Enviar mensaje",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(19.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = { onMicrofono() },
                        enabled = !cargando,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = "Hablar con ZOE",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(23.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BarraGrabandoVoz(
    segundos: Int,
    colorPrincipal: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(
        label = "zoe_waveform"
    )

    val amplitudes = listOf(0.35f, 0.70f, 1.00f, 0.55f, 0.82f, 0.45f, 0.90f)

    Row(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .background(Color(0xFFEF4444), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = formatearTiempoGrabacion(segundos),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.width(10.dp))

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            amplitudes.forEachIndexed { indice, amplitud ->
                val altura by infiniteTransition.animateFloat(
                    initialValue = 0.35f,
                    targetValue = amplitud,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 420 + (indice * 70)
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "onda_$indice"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(24.dp * altura)
                        .clip(RoundedCornerShape(4.dp))
                        .background(colorPrincipal)
                )
            }
        }
    }
}

private fun formatearTiempoGrabacion(segundos: Int): String {
    val minutos = segundos / 60
    val segundosRestantes = segundos % 60

    return "%d:%02d".format(
        minutos,
        segundosRestantes
    )
}

/*
|--------------------------------------------------------------------------
| ÍCONO VECTORIAL OFICIAL DE WHATSAPP Y ACCIONES TELEFÓNICAS
|--------------------------------------------------------------------------
*/

@Composable
private fun IconoWhatsAppVector(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val bubblePath = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.22f, h * 0.68f)
            cubicTo(
                w * 0.10f, h * 0.50f,
                w * 0.18f, h * 0.20f,
                w * 0.45f, h * 0.15f
            )
            cubicTo(
                w * 0.75f, h * 0.10f,
                w * 0.92f, h * 0.35f,
                w * 0.85f, h * 0.62f
            )
            cubicTo(
                w * 0.78f, h * 0.85f,
                w * 0.50f, h * 0.90f,
                w * 0.33f, h * 0.79f
            )
            close()
        }

        drawPath(
            path = bubblePath,
            color = tint,
            style = Stroke(
                width = w * 0.085f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        val phonePath = Path().apply {
            moveTo(w * 0.38f, h * 0.36f)
            quadraticTo(w * 0.34f, h * 0.45f, w * 0.44f, h * 0.56f)
            quadraticTo(w * 0.55f, h * 0.66f, w * 0.64f, h * 0.62f)
        }

        drawPath(
            path = phonePath,
            color = tint,
            style = Stroke(
                width = w * 0.095f,
                cap = StrokeCap.Round
            )
        )
    }
}

private fun formatearTelefonoPeru(telefono: String): String {
    val digitos = telefono.filter { it.isDigit() }
    val local = if (digitos.startsWith("51") && digitos.length == 11) {
        digitos.substring(2)
    } else {
        digitos
    }
    return if (local.length == 9) {
        "${local.substring(0, 3)} ${local.substring(3, 6)} ${local.substring(6, 9)}"
    } else {
        telefono
    }
}

private fun llamarVecino(
    context: android.content.Context,
    telefono: String
) {
    val numero = telefono.filter { it.isDigit() || it == '+' }
    if (numero.isBlank()) return

    val intent = Intent(
        Intent.ACTION_DIAL,
        Uri.parse("tel:$numero")
    )
    context.startActivity(intent)
}

private fun abrirWhatsApp(
    context: android.content.Context,
    telefono: String
) {
    val numero = telefono.filter { it.isDigit() }
    if (numero.isBlank()) return

    val numeroPeru = if (numero.startsWith("51")) {
        numero
    } else {
        "51$numero"
    }

    val url = "https://wa.me/$numeroPeru"
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(url)
    )
    context.startActivity(intent)
}