@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.sigi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.AudioRecord.ERROR_INVALID_OPERATION
import android.media.MediaRecorder
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import com.sigefiv.app.components.LinkableText
import com.sigefiv.app.data.model.ConversacionItem
import com.sigefiv.app.screens.ui.components.TarjetaConvocatoriaAsambleaItem
import com.sigefiv.app.ui.components.MovimientoItemModel
import com.sigefiv.app.ui.components.TarjetaEmprendimientoItem
import com.sigefiv.app.ui.components.TarjetaReporteFinanciero
import com.sigefiv.app.ui.components.ZoeEstadisticaChart
import com.sigefiv.app.viewmodel.AsambleaActivaReporte
import com.sigefiv.app.viewmodel.ZoeMensaje
import com.sigefiv.app.viewmodel.ZoeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt

private val VerdeOnline = Color(0xFF16A34A)

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
    val primaryColor = MaterialTheme.colorScheme.primary
    val context = LocalContext.current
    val viewModel = remember(context) { ZoeViewModel(context.applicationContext) }

    val mensajes by viewModel.mensajes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val hablando by viewModel.hablando.collectAsState()
    val escuchando by viewModel.escuchando.collectAsState()
    val historialConversaciones by viewModel.historialConversaciones.collectAsState()
    val archivoAdjuntoUri by viewModel.archivoAdjuntoUri.collectAsState()

    val reporteAsamblea by viewModel.reporteAsamblea.collectAsState()
    var mostrarBottomSheetAsamblea by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        viewModel.verificarAvisosAsamblea()
        viewModel.actualizarEstadisticasAsamblea()
        if (mensajes.isEmpty()) {
            viewModel.enviarEventoSilencioso("evento_inicio_sesion")
        }
    }

    var consulta by remember { mutableStateOf("") }
    var grabandoVoz by remember { mutableStateOf(false) }
    var grabadora by remember { mutableStateOf<GrabadorWav?>(null) }
    var archivoVoz by remember { mutableStateOf<File?>(null) }
    var segundosGrabacion by remember { mutableStateOf(0) }

    var uriFotoTemporal by remember { mutableStateOf<Uri?>(null) }
    var mostrarMenuAdjuntar by remember { mutableStateOf(false) }

    val lanzadorCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito: Boolean ->
        if (exito && uriFotoTemporal != null) {
            viewModel.seleccionarArchivo(uriFotoTemporal)
        }
    }

    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { archivoUri -> viewModel.seleccionarArchivo(archivoUri) }
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
            Log.e("ZOE_CAMARA", "Error al preparar la cámara: ${e.message}", e)
        }
    }

    val listaEstado = rememberLazyListState()

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
        if (concedido) abrirCamaraDirectamente()
    }

    val permisoMicrofono = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido: Boolean ->
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
                if (grabandoVoz) segundosGrabacion++
            }
        } else {
            segundosGrabacion = 0
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            grabadora?.let { try { it.detener() } catch (_: Exception) {} }
            archivoVoz?.delete()
        }
    }

    fun activarMicrofono() {
        if (cargando) return
        val tienePermiso = ContextCompat.checkSelfPermission(
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

    val sheetShape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)

    // Contenedor raíz que desplaza TODA la ventana de ZOE (incluyendo el Drawer y la barra inferior)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(0, offsetY.value.roundToInt()) }
            .shadow(elevation = 16.dp, shape = sheetShape)
            .clip(sheetShape)
            .background(MaterialTheme.colorScheme.background)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
                shape = sheetShape
            )
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.surface
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
                        onCerrar = { coroutineScope.launch { drawerState.close() } }
                    )
                }
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
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
                                        if (offsetY.value > 150f || velocity > 650f) {
                                            offsetY.animateTo(
                                                targetValue = 2200f,
                                                animationSpec = tween(durationMillis = 200)
                                            )
                                            onBackClick()
                                        } else {
                                            offsetY.animateTo(
                                                targetValue = 0f,
                                                animationSpec = tween(durationMillis = 220)
                                            )
                                        }
                                    }
                                }
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Indicador superior de arrastre (Drag Handle)
                        Box(
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 2.dp)
                                .width(42.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f))
                        )

                        TopAppBar(
                            windowInsets = WindowInsets(0.dp),
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(38.dp),
                                        shape = CircleShape,
                                        color = primaryColor.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Outlined.SmartToy,
                                                contentDescription = "ZOE",
                                                tint = primaryColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

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
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                IconButton(onClick = onBackClick) {
                                    Icon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Cerrar asistente",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface
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
                    // 1. BARRA DE SUGERENCIAS Y EVENTOS
                    SugerenciasChipsBar(
                        reporteAsamblea = reporteAsamblea,
                        onAsambleaClick = { mostrarBottomSheetAsamblea = true },
                        onPregunta = { pregunta ->
                            if (!cargando) viewModel.enviarConsulta(pregunta)
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        thickness = 0.8.dp
                    )

                    // 2. HISTORIAL DE MENSAJES
                    LazyColumn(
                        state = listaEstado,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(vertical = 12.dp)
                    ) {
                        items(
                            count = mensajes.size,
                            key = { index ->
                                val mensaje = mensajes[index]
                                "$index-${mensaje.esUsuario}-${mensaje.texto.hashCode()}"
                            }
                        ) { index ->
                            BurbujaChat(
                                mensaje = mensajes[index],
                                onConfirmarAsistencia = { asambleaId, opcion ->
                                    viewModel.confirmarAsistenciaAsamblea(asambleaId, opcion)
                                }
                            )
                        }

                        if (cargando) {
                            item(key = "loading_indicator") { IndicadorCargaMensaje() }
                        }
                    }

                    // 3. BARRA DE ENTRADA
                    BarraEntradaModerna(
                        consulta = consulta,
                        onConsultaChange = { consulta = it },
                        onEnviar = {
                            viewModel.enviarConsultaOAdjunto(context, consulta)
                            consulta = ""
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
                            val tienePermiso = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED
                            if (tienePermiso) abrirCamaraDirectamente() else permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                        },
                        onGaleriaClick = { lanzadorGaleria.launch("*/*") },
                        archivoAdjuntoUri = archivoAdjuntoUri,
                        onEliminarAdjunto = { viewModel.limpiarAdjunto() }
                    )
                }
            }
        }
    }

    // 4. VENTANA EMERGENTE DE ASAMBLEA
    if (mostrarBottomSheetAsamblea && reporteAsamblea != null) {
        val r = reporteAsamblea!!
        val total = if (r.total > 0) r.total.toFloat() else 1f
        val pctSi = (r.asistiran / total * 100).toInt()
        val pctDuda = (r.enDuda / total * 100).toInt()
        val pctNo = (r.noAsistiran / total * 100).toInt()

        ModalBottomSheet(
            onDismissRequest = { mostrarBottomSheetAsamblea = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Participación en Asamblea",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = r.titulo ?: "Asamblea General",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "📅 ${r.fecha ?: "--"} a las ${r.hora ?: "--"} • 📍 ${r.lugar ?: "Lugar no indicado"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Asistirán", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text("${r.asistiran} ($pctSi%)", fontWeight = FontWeight.Bold, color = VerdeOnline)
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { r.asistiran / total },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = VerdeOnline,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("En duda", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text("${r.enDuda} ($pctDuda%)", fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { r.enDuda / total },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFFF59E0B),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("No asistirán", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                    Text("${r.noAsistiran} ($pctNo%)", fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { r.noAsistiran / total },
                    modifier = Modifier.fillMaxWidth().height(10.dp).clip(RoundedCornerShape(5.dp)),
                    color = Color(0xFFEF4444),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val (icono, estadoTexto, colorTexto) = when (r.miVoto) {
                            "asistira" -> Triple("✅", "Confirmaste tu asistencia", VerdeOnline)
                            "no_asistira" -> Triple("❌", "Indicaste que no asistirás", Color(0xFFDC2626))
                            "en_duda" -> Triple("❓", "Marcaste que estás en duda", Color(0xFFD97706))
                            else -> Triple("⏳", "Aún no has registrado tu respuesta", MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Text(text = icono, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Tu participación", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(estadoTexto, fontWeight = FontWeight.SemiBold, color = colorTexto)
                        }
                    }
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| SUGERENCIAS DESLIZABLES
|--------------------------------------------------------------------------
*/

@Composable
private fun SugerenciasChipsBar(
    reporteAsamblea: AsambleaActivaReporte?,
    onAsambleaClick: () -> Unit,
    onPregunta: (String) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (reporteAsamblea?.tieneAsamblea == true) {
            item {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(onClick = onAsambleaClick),
                    shape = RoundedCornerShape(20.dp),
                    color = primaryColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📢", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Asamblea (${reporteAsamblea.asistiran} confirmados)",
                            color = primaryColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

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
                onClick = { onPregunta("¿Cuál es el saldo en caja del período actual?") }
            )
        }
        item {
            SugerenciaChip(
                icon = Icons.Outlined.BarChart,
                texto = "Resumen del mes",
                onClick = { onPregunta("RESUMEN_PERIODO_ACTIVO") }
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
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = primaryColor,
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
| PANEL DE HISTORIAL
|--------------------------------------------------------------------------
*/

@Composable
private fun ContenidoPanelHistorial(
    listaConversaciones: List<ConversacionItem>,
    onConversacionClick: (String) -> Unit,
    onNuevaConversacion: () -> Unit,
    onCerrar: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

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
                    tint = primaryColor,
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
            color = primaryColor.copy(alpha = 0.12f),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "+ Nueva Conversación",
                    color = primaryColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
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
                            color = primaryColor,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                        )
                    }

                    items(
                        items = itemsDelGrupo,
                        key = { it.id }
                    ) { chat ->
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
| BURBUJA DE CHAT
|--------------------------------------------------------------------------
*/

@Composable
private fun BurbujaChat(
    mensaje: ZoeMensaje,
    onConfirmarAsistencia: (Long, String) -> Unit = { _, _ -> }
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    if (mensaje.esUsuario) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Card(
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp),
                colors = CardDefaults.cardColors(containerColor = primaryColor.copy(alpha = 0.14f)),
                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.widthIn(min = 60.dp, max = 290.dp)
            ) {
                Column(modifier = Modifier.padding(all = 10.dp)) {
                    mensaje.imagenUri?.let { uri ->
                        AsyncImage(
                            model = uri,
                            contentDescription = "Imagen adjunta del reporte",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.padding(start = 2.dp, end = 2.dp, bottom = 2.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        if (mensaje.texto.isNotBlank()) {
                            Text(
                                text = mensaje.texto,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.5.sp,
                                lineHeight = 19.sp,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = mensaje.hora,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "✓✓",
                                color = primaryColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = primaryColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.SmartToy,
                        contentDescription = "ZOE",
                        tint = primaryColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                shadowElevation = 0.5.dp,
                modifier = Modifier.widthIn(max = 310.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                    if (mensaje.tipo == "encuesta_asamblea" && mensaje.asambleaId != null) {
                        TarjetaConvocatoriaAsambleaItem(
                            asambleaId = mensaje.asambleaId,
                            texto = mensaje.texto,
                            yaRespondido = mensaje.asambleaEstadisticas != null,
                            estadisticas = mensaje.asambleaEstadisticas,
                            colorPrincipal = primaryColor,
                            temaOscuro = false,
                            onConfirmar = { opcion -> onConfirmarAsistencia(mensaje.asambleaId, opcion) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        ZoeRespuestaFormateada(
                            texto = mensaje.texto,
                            tipoMovimiento = mensaje.tipoMovimiento,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    mensaje.estadistica?.let { estadistica ->
                        Spacer(modifier = Modifier.height(6.dp))
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
                                    TarjetaEmprendimientoItem(
                                        emprendimiento = emprendimiento,
                                        colorPrincipal = primaryColor,
                                        temaOscuro = false
                                    )
                                }
                            }
                        }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = mensaje.hora,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
private fun ZoeRespuestaFormateada(
    texto: String,
    tipoMovimiento: String? = null,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val regexMovimientoGuiones = Regex(
        """^(?:•|-|\d+\.)\s*(\d{2}[-/]\d{2}[-/]\d{4})\s*(?:—|-)\s*(.*?)\s*(?:—|-)\s*(.*?)\s*(?:—|-)\s*S?/?\s*([\d,]+(?:\.\d{1,2})?)$""",
        RegexOption.IGNORE_CASE
    )

    val regexMovimientoPipes = Regex(
        """^\s*(?:•|-|\d+\.)\s*(\d{2}[-/]\d{2}[-/]\d{4})\s*—\s*S/\s*([\d,]+(?:\.\d{1,2})?)\s*(?:—\s*(.*))?$"""
    )

    val movimientosEncontrados = remember(texto, tipoMovimiento) {
        val lista = mutableListOf<MovimientoItemModel>()
        val esEgresoTexto = tipoMovimiento == "Egreso" ||
                texto.contains("egreso", ignoreCase = true) ||
                texto.contains("gasto", ignoreCase = true)

        texto.lines().forEach { lineaRaw ->
            val linea = lineaRaw.replace("**", "").trim()
            val matchGuiones = regexMovimientoGuiones.find(linea)
            if (matchGuiones != null) {
                lista.add(
                    MovimientoItemModel(
                        fecha = matchGuiones.groupValues[1],
                        categoria = matchGuiones.groupValues[2].trim(),
                        concepto = matchGuiones.groupValues[3].trim(),
                        monto = matchGuiones.groupValues[4].trim(),
                        esEgreso = esEgresoTexto
                    )
                )
            } else {
                val matchPipes = regexMovimientoPipes.find(linea)
                if (matchPipes != null) {
                    val fecha = matchPipes.groupValues[1]
                    val monto = matchPipes.groupValues[2]
                    val resto = matchPipes.groupValues.getOrNull(3)?.trim().orEmpty()
                    val partes = resto.split(" — ").map { it.trim() }.filter { it.isNotEmpty() }
                    val categoria = partes.getOrNull(0).orEmpty().ifBlank { "General" }
                    val concepto = partes.drop(1).joinToString(" — ").ifBlank { categoria }

                    lista.add(
                        MovimientoItemModel(
                            fecha = fecha,
                            categoria = categoria,
                            concepto = concepto,
                            monto = monto,
                            esEgreso = esEgresoTexto
                        )
                    )
                }
            }
        }
        lista
    }

    if (movimientosEncontrados.isNotEmpty()) {
        val titulo = texto.lines().firstOrNull {
            it.contains("egreso", ignoreCase = true) ||
                    it.contains("ingreso", ignoreCase = true) ||
                    it.contains("movimiento", ignoreCase = true)
        }?.replace("🤖", "")?.replace("📋", "")?.trim() ?: "Resumen de operaciones"

        TarjetaReporteFinanciero(
            titulo = titulo,
            movimientos = movimientosEncontrados,
            colorPrincipal = primaryColor,
            temaOscuro = false,
            modifier = modifier
        )
    } else {
        val parrafos = remember(texto) {
            texto.split(Regex("""\r?\n\s*\r?\n"""))
                .map { it.trim() }
                .filter { it.isNotEmpty() }
        }

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            parrafos.forEach { parrafo ->
                LinkableText(
                    text = parrafo,
                    modifier = Modifier.fillMaxWidth(),
                    textColor = MaterialTheme.colorScheme.onSurface,
                    linkColor = primaryColor,
                    fontSize = 14.5.sp,
                    lineHeight = 21.sp
                )
            }
        }
    }
}

@Composable
private fun BouncingDotsLoader() {
    val dotColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)

    val animations = listOf(
        remember { Animatable(0f) },
        remember { Animatable(0f) },
        remember { Animatable(0f) }
    )

    animations.forEachIndexed { index, animatable ->
        LaunchedEffect(animatable) {
            delay(index * 150L)
            animatable.animateTo(
                targetValue = -5f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        animations.forEach { animatable ->
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .offset(y = animatable.value.dp)
                    .background(dotColor, shape = CircleShape)
            )
        }
    }
}

@Composable
private fun IndicadorCargaMensaje() {
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = CircleShape,
            color = primaryColor.copy(alpha = 0.12f),
            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = "ZOE",
                    tint = primaryColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            BouncingDotsLoader()
        }
    }
}

private fun iniciarGrabacionVoz(
    context: Context,
    onGrabando: (Boolean) -> Unit,
    onGrabadora: (GrabadorWav?) -> Unit,
    onArchivo: (File?) -> Unit
) {
    try {
        val archivo = File(context.cacheDir, "zoe_${System.currentTimeMillis()}.wav")
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

private class GrabadorWav(private val archivo: File) {
    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    private var audioRecord: AudioRecord? = null
    private var hiloGrabacion: Thread? = null
    private var grabando = false

    fun iniciar() {
        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
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
            val archivoPcm = File(archivo.parentFile, "${archivo.nameWithoutExtension}.pcm")
            try {
                FileOutputStream(archivoPcm).use { output ->
                    record.startRecording()
                    while (grabando) {
                        val cantidad = record.read(buffer, 0, buffer.size)
                        if (cantidad > 0) output.write(buffer, 0, cantidad)
                    }
                }
                convertirPcmAWav(archivoPcm, archivo)
            } catch (_: Exception) {
                archivo.delete()
            } finally {
                archivoPcm.delete()
                try { record.stop() } catch (_: Exception) {}
                record.release()
                audioRecord = null
            }
        }.apply { start() }
    }

    fun detener(): File? {
        grabando = false
        try { hiloGrabacion?.join(1500) } catch (_: InterruptedException) {}
        hiloGrabacion = null
        return if (archivo.exists() && archivo.length() > 44L) archivo else null
    }

    private fun convertirPcmAWav(pcm: File, wav: File) {
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
            pcm.inputStream().use { input -> input.copyTo(output) }
        }
    }

    private fun escribirAscii(buffer: ByteArray, offset: Int, texto: String) {
        texto.toByteArray(Charsets.US_ASCII).copyInto(buffer, offset)
    }

    private fun escribirIntLE(buffer: ByteArray, offset: Int, valor: Int) {
        buffer[offset] = (valor and 0xff).toByte()
        buffer[offset + 1] = ((valor shr 8) and 0xff).toByte()
        buffer[offset + 2] = ((valor shr 16) and 0xff).toByte()
        buffer[offset + 3] = ((valor shr 24) and 0xff).toByte()
    }

    private fun escribirShortLE(buffer: ByteArray, offset: Int, valor: Int) {
        buffer[offset] = (valor and 0xff).toByte()
        buffer[offset + 1] = ((valor shr 8) and 0xff).toByte()
    }
}

/*
|--------------------------------------------------------------------------
| BARRA DE ENTRADA MODERNA
|--------------------------------------------------------------------------
*/

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
    archivoAdjuntoUri: Uri?,
    onEliminarAdjunto: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val hayTexto = consulta.trim().isNotEmpty() || archivoAdjuntoUri != null

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
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
                            Text(text = "✕", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                if (grabandoVoz) {
                    BarraGrabandoVoz(
                        segundos = segundosGrabacion,
                        colorPrincipal = primaryColor,
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
                            .heightIn(min = 48.dp, max = 120.dp),
                        enabled = true,
                        singleLine = false,
                        maxLines = 5,
                        shape = RoundedCornerShape(14.dp),
                        placeholder = {
                            Text(
                                text = if (archivoAdjuntoUri != null) "Añade un comentario (opcional)..." else "Escribe un mensaje...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Default
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            cursorColor = primaryColor
                        )
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

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
                                    tint = primaryColor
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
                                    tint = primaryColor
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
                            .background(color = Color(0xFFEF4444), shape = CircleShape)
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
                                color = if (cargando) primaryColor.copy(alpha = 0.5f) else primaryColor,
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
                            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = CircleShape)
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
    val infiniteTransition = rememberInfiniteTransition(label = "zoe_waveform")
    val amplitudes = listOf(0.35f, 0.70f, 1.00f, 0.55f, 0.82f, 0.45f, 0.90f)

    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(24.dp))
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
                        animation = tween(durationMillis = 420 + (indice * 70)),
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
    return "%d:%02d".format(minutos, segundosRestantes)
}