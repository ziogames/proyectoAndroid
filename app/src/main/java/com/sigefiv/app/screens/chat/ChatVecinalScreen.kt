package com.sigefiv.app.screens.chat

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.snapshotFlow

@Composable
private fun colorPrincipal(): Color {
    return SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
}

@Composable
private fun coloresChat(): ChatColors {
    val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return if (oscuro) {
        ChatColors(
            fondo = Color(0xFF121A16),
            burbujaPropia = Color(0xFF14532D),
            burbujaAjena = Color(0xFF26332C),
            textoPrincipal = Color(0xFFF1F5F2),
            textoSecundario = Color(0xFFB7C8BC),
            nombreEmisor = Color(0xFF4ADE80),
            fondoTarjeta = Color(0xFF1D3A29),
            bordeTarjeta = Color(0xFF356B49),
            fondoInput = Color(0xFF24342B),
            textoInput = Color(0xFFF1F5F2),
            placeholderInput = Color(0xFFB7C8BC)
        )
    } else {
        ChatColors(
            fondo = Color(0xFFF9FBF9),
            burbujaPropia = Color(0xFFD9FDD3),
            burbujaAjena = Color(0xFFF0F4F1),
            textoPrincipal = Color(0xFF172B1A),
            textoSecundario = Color(0xFF5F6B61),
            nombreEmisor = Color(0xFF15803D),
            fondoTarjeta = Color(0xFFE8F5E9),
            bordeTarjeta = Color(0xFFA5D6A7),
            fondoInput = Color.White,
            textoInput = Color(0xFF172B1A),
            placeholderInput = Color(0xFF667085)
        )
    }
}

private data class ChatColors(
    val fondo: Color,
    val burbujaPropia: Color,
    val burbujaAjena: Color,
    val textoPrincipal: Color,
    val textoSecundario: Color,
    val nombreEmisor: Color,
    val fondoTarjeta: Color,
    val bordeTarjeta: Color,
    val fondoInput: Color,
    val textoInput: Color,
    val placeholderInput: Color
)

private val VerdeMonto = Color(0xFF047857)
private val RojoMonto = Color(0xFFDC2626)

private val EmojisReaccion = listOf(
    "👍",
    "❤️",
    "😂",
    "😮",
    "😢",
    "🙏"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatVecinalScreen(
    viewModel: ChatViewModel,
    usuarioActualId: Int,
    onBackClick: () -> Unit
) {
    val c = coloresChat()

    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val uiState by viewModel.uiState.collectAsState()

    val listState = rememberLazyListState()

    var posicionInicialAplicada by remember {
        mutableStateOf(false)
    }

    // 🔽 Estado visual del botón y del separador "Nuevos mensajes".
    //
    // IMPORTANTE:
    // - El servidor sigue siendo la fuente de verdad para "no leídos".
    // - El separador visual se conserva después de marcar leído.
    // - El botón se oculta al visitar la tanda y no reaparece por hacer scroll.

    var nuevosMensajesVisitados by remember {
        mutableStateOf(false)
    }

    // ID que usamos SOLO para conservar visualmente el separador.
    // No se borra cuando el servidor marca los mensajes como leídos.

    var separadorNuevosId by remember {
        mutableStateOf<Int?>(null)
    }

    var ultimoConteoNoLeidos by remember {
        mutableStateOf(0)
    }

    // 📨 Detectar una nueva tanda real de mensajes no leídos.
    //
    // Si el servidor cambia el primerNoLeidoId, tenemos una nueva tanda
    // y el botón vuelve a aparecer.

    LaunchedEffect(
        uiState.primerNoLeidoId,
        uiState.mensajesNoLeidos
    ) {
        val primerNoLeido = uiState.primerNoLeidoId

        if (primerNoLeido != null) {

            if (separadorNuevosId != primerNoLeido) {

                separadorNuevosId = primerNoLeido
                nuevosMensajesVisitados = false

            } else if (
                uiState.mensajesNoLeidos > ultimoConteoNoLeidos
            ) {

                // Llegó un mensaje nuevo a la misma tanda.
                nuevosMensajesVisitados = false
            }
        }

        // Si no hay no leídos, NO borramos separadorNuevosId.
        // Así el separador permanece visualmente hasta que llegue
        // una nueva tanda.

        ultimoConteoNoLeidos = uiState.mensajesNoLeidos
    }

    // 📍 Llegar al final significa que el usuario ya visitó los nuevos.
    // Aquí SÍ sincronizamos la lectura persistente con el servidor.
    //
    // No usamos canScrollForward para mostrar/ocultar el botón;
    // solamente lo usamos para detectar que se llegó al final.

    LaunchedEffect(listState) {

        snapshotFlow {
            listState.canScrollForward &&
                    uiState.mensajesNoLeidos > 0
        }
            .collect { puedeSeguirBajandoYHayNoLeidos ->

                if (
                    !puedeSeguirBajandoYHayNoLeidos &&
                    uiState.mensajesNoLeidos > 0 &&
                    !nuevosMensajesVisitados
                ) {

                    nuevosMensajesVisitados = true

                    // Marcamos leído hasta el último mensaje actualmente
                    // disponible. El servidor impedirá retroceder la
                    // posición de lectura.

                    uiState.mensajes.lastOrNull()?.id?.let { ultimoMensajeId ->
                        viewModel.marcarLeido(ultimoMensajeId)
                    }
                }
            }
    }

    val mostrarBotonNuevos by remember {

        derivedStateOf {

            uiState.mensajesNoLeidos > 0 &&
                    !nuevosMensajesVisitados
        }
    }

    // 📌 Posición inicial del chat.

    LaunchedEffect(uiState.mensajes) {

        if (
            !posicionInicialAplicada &&
            uiState.mensajes.isNotEmpty()
        ) {

            val indiceNoLeido =
                uiState.primerNoLeidoId?.let { id ->

                    uiState.mensajes.indexOfFirst {
                        it.id == id
                    }

                } ?: -1

            if (indiceNoLeido >= 0) {

                listState.scrollToItem(
                    indiceNoLeido
                )

            } else {

                // Vamos al final real de la lista.
                // El último elemento es el Spacer inferior.

                listState.scrollToItem(
                    uiState.mensajes.size,
                    scrollOffset = 10000
                )
            }

            posicionInicialAplicada = true
        }
    }

    // ============================================================
    // 🔥 NUEVA LÓGICA DE SCROLL AUTOMÁTICO
    // ============================================================

    // Cantidad de mensajes conocida en la última actualización.

    var ultimoTamanoMensajes by remember {
        mutableStateOf(0)
    }

    // Último índice visible antes de que llegue un nuevo mensaje.

    var ultimoIndiceVisible by remember {
        mutableStateOf(-1)
    }

    // Guardamos continuamente cuál es el último elemento visible.

    LaunchedEffect(listState) {

        snapshotFlow {
            listState.layoutInfo
                .visibleItemsInfo
                .lastOrNull()
                ?.index ?: -1
        }.collect { indice ->

            ultimoIndiceVisible = indice
        }
    }

    // Cuando la cantidad de mensajes aumenta:
    //
    // 1. Comprobamos si el usuario estaba cerca del final.
    // 2. Esperamos a que LazyColumn mida el mensaje.
    // 3. Nos desplazamos hasta el Spacer final.
    //
    // De esta forma un mensaje largo queda completamente visible.

    LaunchedEffect(uiState.mensajes.size) {

        val tamanoAnterior = ultimoTamanoMensajes
        val tamanoNuevo = uiState.mensajes.size

        if (
            tamanoAnterior > 0 &&
            tamanoNuevo > tamanoAnterior &&
            ultimoIndiceVisible >= tamanoAnterior - 2
        ) {

            // Esperamos a que LazyColumn termine de medir
            // el nuevo mensaje.

            delay(100)

            // El índice uiState.mensajes.size corresponde al Spacer
            // que colocamos después del último mensaje.
            //
            // scrollOffset grande = llegar al máximo scroll posible.

            listState.animateScrollToItem(
                index = tamanoNuevo,
                scrollOffset = 10000
            )
        }

        ultimoTamanoMensajes = tamanoNuevo
    }

    var texto by remember {
        mutableStateOf("")
    }

    // 💬 Estados para el resaltado temporal del mensaje original citado

    var mensajeResaltadoId by remember {
        mutableStateOf<Int?>(null)
    }

    var mensajeReaccionSeleccionadoId by remember {
        mutableStateOf<Int?>(null)
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        viewModel.cargarChat()

        viewModel.actualizarPresencia()

        viewModel.iniciarActualizacionAutomatica()
    }

    // 📖 Marcar como leído solamente cuando el usuario
    // llega realmente al final mediante desplazamiento manual.

    Scaffold(

        containerColor = c.fondo,

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Group,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(22.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = "Chat Vecinal",
                                color = MaterialTheme.colorScheme.onPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Text(
                            text = "${uiState.personasEnLinea} vecinos conectados",
                            color = MaterialTheme.colorScheme.onPrimary.copy(
                                alpha = 0.85f
                            ),
                            fontSize = 12.sp
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onPrimary
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

                esBloqueoPermanente =
                    uiState.esBloqueoPermanente,

                mensajeRespondido =
                    uiState.mensajeRespondido,

                onCancelarRespuesta = {
                    viewModel.cancelarRespuesta()
                },

                usuariosEscribiendo =
                    uiState.usuariosEscribiendo.filter {
                        it.id != usuarioActualId
                    },

                onTextoChange = { nuevoTexto ->

                    texto = nuevoTexto

                    viewModel.actualizarEscribiendo(
                        nuevoTexto.isNotBlank()
                    )
                },

                onEnviar = {

                    if (
                        texto.isNotBlank() &&
                        !uiState.enviando &&
                        !uiState.esBloqueoPermanente
                    ) {

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
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        color = colorPrincipal()
                    )
                }

                uiState.mensajes.isEmpty() -> {

                    ChatEmptyState()
                }

                else -> {

                    LazyColumn(

                        state = listState,

                        modifier = Modifier.fillMaxSize(),

                        verticalArrangement =
                            Arrangement.spacedBy(6.dp),

                        contentPadding = PaddingValues(
                            start = 10.dp,
                            end = 10.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        )

                    ) {

                        itemsIndexed(

                            items = uiState.mensajes,

                            key = { _, mensaje ->
                                mensaje.id
                            }

                        ) { indice, mensaje ->

                            // 🔔 Separador de nuevos mensajes.

                            if (
                                mensaje.id == separadorNuevosId
                            ) {

                                Text(
                                    text = "Nuevos mensajes",

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            vertical = 8.dp
                                        ),

                                    textAlign =
                                        TextAlign.Center,

                                    style =
                                        MaterialTheme.typography.labelMedium
                                )
                            }

                            ChatMessageItem(

                                mensaje = mensaje,

                                esPropio =
                                    mensaje.usuario?.id ==
                                            usuarioActualId,

                                esResaltado =
                                    mensaje.id ==
                                            mensajeResaltadoId,

                                onResponder = {

                                    viewModel.prepararRespuesta(
                                        mensaje
                                    )
                                },

                                onMostrarReacciones = {

                                    mensajeReaccionSeleccionadoId =
                                        mensaje.id
                                },

                                onReaccionar = { emoji ->

                                    viewModel.reaccionar(
                                        mensaje.id,
                                        emoji
                                    )
                                },

                                onIrAlMensajeOriginal = {
                                        mensajeIdBuscado ->

                                    coroutineScope.launch {

                                        val indiceBuscado =
                                            uiState.mensajes.indexOfFirst {
                                                it.id ==
                                                        mensajeIdBuscado
                                            }

                                        if (
                                            indiceBuscado != -1
                                        ) {

                                            mensajeResaltadoId =
                                                mensajeIdBuscado

                                            listState.animateScrollToItem(
                                                indiceBuscado
                                            )

                                            delay(2000)

                                            if (
                                                mensajeResaltadoId ==
                                                mensajeIdBuscado
                                            ) {

                                                mensajeResaltadoId =
                                                    null
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        // ====================================================
                        // 🔽 ESPACIO INFERIOR
                        // ====================================================
                        //
                        // Este espacio es fundamental.
                        //
                        // Permite que el último mensaje, incluso si tiene
                        // muchas líneas, quede completamente por encima
                        // del campo de escritura.
                        //
                        // El scroll automático apunta a este elemento.

                        item {
                            Spacer(
                                modifier = Modifier.height(
                                    30.dp
                                )
                            )
                        }
                    }

                    // 🔽 Botón flotante para ir a los nuevos mensajes

                    if (mostrarBotonNuevos) {

                        Surface(

                            modifier = Modifier
                                .align(
                                    Alignment.BottomCenter
                                )
                                .padding(
                                    bottom = 16.dp
                                )
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {

                                    coroutineScope.launch {

                                        val idDestino =
                                            separadorNuevosId
                                                ?: uiState.primerNoLeidoId

                                        val indiceNoLeido =
                                            uiState.mensajes.indexOfFirst {
                                                it.id == idDestino
                                            }

                                        if (
                                            indiceNoLeido >= 0
                                        ) {

                                            listState.animateScrollToItem(
                                                indiceNoLeido
                                            )
                                        }
                                    }
                                },

                            color = colorPrincipal,

                            shadowElevation = 6.dp

                        ) {

                            Row(

                                modifier =
                                    Modifier.padding(
                                        horizontal = 14.dp,
                                        vertical = 8.dp
                                    ),

                                verticalAlignment =
                                    Alignment.CenterVertically

                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Outlined.ArrowDownward,

                                    contentDescription =
                                        "Ir a nuevos mensajes",

                                    tint =
                                        MaterialTheme.colorScheme.onPrimary,

                                    modifier =
                                        Modifier.size(18.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )

                                Text(

                                    text =
                                        if (
                                            uiState.mensajesNoLeidos == 1
                                        ) {
                                            "1 nuevo mensaje"
                                        } else {
                                            "${uiState.mensajesNoLeidos} nuevos mensajes"
                                        },

                                    color =
                                        MaterialTheme.colorScheme.onPrimary,

                                    fontSize = 12.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            val mensajeReaccionSeleccionado =
                uiState.mensajes.firstOrNull {

                    it.id ==
                            mensajeReaccionSeleccionadoId
                }

            if (
                mensajeReaccionSeleccionado != null
            ) {

                Dialog(

                    onDismissRequest = {

                        mensajeReaccionSeleccionadoId =
                            null
                    }

                ) {

                    Surface(

                        shape =
                            RoundedCornerShape(28.dp),

                        color =
                            MaterialTheme.colorScheme.onPrimary,

                        shadowElevation = 8.dp

                    ) {

                        Row(

                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 8.dp
                                ),

                            horizontalArrangement =
                                Arrangement.spacedBy(4.dp),

                            verticalAlignment =
                                Alignment.CenterVertically

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

                                                mensajeReaccionSeleccionadoId =
                                                    null
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

            if (
                !uiState.error.isNullOrBlank()
            ) {

                Card(

                    modifier = Modifier
                        .align(
                            Alignment.TopCenter
                        )
                        .padding(12.dp),

                    colors =
                        CardDefaults.cardColors(

                            containerColor =
                                MaterialTheme.colorScheme.errorContainer
                        ),

                    shape =
                        RoundedCornerShape(12.dp)

                ) {

                    Text(

                        text =
                            uiState.error ?: "",

                        color =
                            MaterialTheme.colorScheme.onErrorContainer,

                        fontSize = 13.sp,

                        modifier =
                            Modifier.padding(12.dp)
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

    val c = coloresChat()

    Column(

        modifier = Modifier
            .fillMaxWidth()
            .background(
                colorPrincipal()
            )
            .navigationBarsPadding()
            .imePadding()
            .padding(
                horizontal = 8.dp,
                vertical = 5.dp
            )
    ) {

        // 💬 Vista previa de la respuesta

        if (
            mensajeRespondido != null &&
            !esBloqueoPermanente
        ) {

            Surface(

                color =
                    Color.Black.copy(
                        alpha = 0.25f
                    ),

                shape =
                    RoundedCornerShape(
                        topStart = 8.dp,
                        topEnd = 8.dp
                    ),

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 4.dp
                        )

            ) {

                Row(

                    modifier =
                        Modifier.padding(8.dp),

                    verticalAlignment =
                        Alignment.CenterVertically

                ) {

                    Box(

                        modifier =
                            Modifier
                                .width(3.dp)
                                .height(30.dp)
                                .background(
                                    Color.White
                                )
                    )

                    Spacer(
                        modifier =
                            Modifier.width(8.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(

                            text =
                                mensajeRespondido.usuario?.name
                                    ?: "Vecino",

                            color =
                                MaterialTheme.colorScheme.onPrimary,

                            fontWeight =
                                FontWeight.Bold,

                            fontSize = 11.sp
                        )

                        Text(

                            text =
                                mensajeRespondido.mensaje
                                    ?: "Mensaje",

                            color =
                                MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.85f
                                ),

                            fontSize = 12.sp,

                            maxLines = 1
                        )
                    }

                    IconButton(

                        onClick =
                            onCancelarRespuesta,

                        modifier =
                            Modifier.size(24.dp)

                    ) {

                        Icon(

                            imageVector =
                                Icons.Outlined.Close,

                            contentDescription =
                                "Cancelar respuesta",

                            tint =
                                MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }

        // ✍️ Indicador de escritura

        if (
            usuariosEscribiendo.isNotEmpty()
        ) {

            val nombres =
                usuariosEscribiendo.joinToString(", ") {
                    it.name ?: "Vecino"
                }

            Text(

                text =
                    "$nombres está escribiendo...",

                color =
                    MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = 0.85f
                    ),

                fontSize = 12.sp,

                modifier =
                    Modifier.padding(
                        start = 12.dp,
                        bottom = 3.dp
                    )
            )
        }

        // 🟢 Barra compacta tipo WhatsApp

        Row(

            modifier =
                Modifier.fillMaxWidth(),

            verticalAlignment =
                Alignment.Bottom

        ) {

            Surface(

                modifier =
                    Modifier
                        .weight(1f)
                        .padding(end = 5.dp),

                shape =
                    RoundedCornerShape(24.dp),

                color =
                    if (esBloqueoPermanente) {
                        c.fondoInput.copy(
                            alpha = 0.65f
                        )
                    } else {
                        c.fondoInput
                    }

            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 2.dp,
                                vertical = 2.dp
                            ),

                    verticalAlignment =
                        Alignment.Bottom

                ) {

                    IconButton(

                        onClick =
                            onAdjuntar,

                        enabled =
                            !esBloqueoPermanente,

                        modifier =
                            Modifier.size(40.dp)

                    ) {

                        Icon(

                            imageVector =
                                Icons.Outlined.AttachFile,

                            contentDescription =
                                "Adjuntar archivo",

                            tint =
                                if (
                                    !esBloqueoPermanente
                                ) {

                                    c.textoSecundario

                                } else {

                                    c.textoSecundario.copy(
                                        alpha = 0.4f
                                    )
                                },

                            modifier =
                                Modifier.size(20.dp)
                        )
                    }

                    BasicTextField(

                        value =
                            if (
                                esBloqueoPermanente
                            ) {
                                ""
                            } else {
                                texto
                            },

                        onValueChange =
                            onTextoChange,

                        enabled =
                            !esBloqueoPermanente,

                        modifier =
                            Modifier
                                .weight(1f)
                                .padding(
                                    horizontal = 4.dp,
                                    vertical = 9.dp
                                ),

                        textStyle =
                            androidx.compose.ui.text.TextStyle(
                                color =
                                    c.textoInput,
                                fontSize =
                                    14.sp
                            ),

                        singleLine = false,

                        maxLines = 4,

                        decorationBox = {
                                innerTextField ->

                            if (
                                texto.isBlank() ||
                                esBloqueoPermanente
                            ) {

                                Text(

                                    text =
                                        if (
                                            esBloqueoPermanente
                                        ) {
                                            "Cuenta suspendida."
                                        } else {
                                            "Escribe un mensaje..."
                                        },

                                    color =
                                        if (
                                            esBloqueoPermanente
                                        ) {

                                            MaterialTheme
                                                .colorScheme
                                                .error

                                        } else {

                                            c.placeholderInput
                                        },

                                    fontSize = 14.sp,

                                    maxLines = 1
                                )
                            }

                            innerTextField()
                        }
                    )
                }
            }

            // ➤ Botón enviar compacto

            Surface(

                modifier =
                    Modifier.size(46.dp),

                shape =
                    CircleShape,

                color =
                    if (
                        !esBloqueoPermanente &&
                        texto.isNotBlank()
                    ) {

                        colorPrincipal()

                    } else {

                        colorPrincipal()
                            .copy(alpha = 0.4f)
                    }

            ) {

                IconButton(

                    onClick =
                        onEnviar,

                    enabled =
                        !esBloqueoPermanente &&
                                texto.isNotBlank() &&
                                !enviando

                ) {

                    if (enviando) {

                        CircularProgressIndicator(

                            modifier =
                                Modifier.size(20.dp),

                            strokeWidth = 2.dp,

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary
                        )

                    } else {

                        Icon(

                            imageVector =
                                Icons.Outlined.Send,

                            contentDescription =
                                "Enviar mensaje",

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary,

                            modifier =
                                Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatEmptyState() {

    val c = coloresChat()

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center

    ) {

        Surface(

            modifier =
                Modifier.size(80.dp),

            shape =
                CircleShape,

            color =
                colorPrincipal()
                    .copy(alpha = 0.12f)

        ) {

            Box(
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        Icons.Outlined.Group,

                    contentDescription =
                        null,

                    tint =
                        colorPrincipal(),

                    modifier =
                        Modifier.size(42.dp)
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Text(

            text =
                "Chat Vecinal",

            color =
                c.textoPrincipal,

            fontWeight =
                FontWeight.Bold,

            fontSize = 18.sp
        )

        Spacer(
            modifier =
                Modifier.height(6.dp)
        )

        Text(

            text =
                "Aún no hay mensajes.\nSé el primero en escribir.",

            color =
                c.textoSecundario,

            textAlign =
                TextAlign.Center,

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

    val c = coloresChat()

    val texto =
        mensaje.mensaje ?: ""

    val maxBurbujaWidth =
        (LocalConfiguration.current.screenWidthDp * 0.85)
            .dp

    // Variables para el gesto de deslizamiento horizontal
    // (Swipe to reply)

    var offsetX by remember {
        mutableStateOf(0f)
    }

    val density =
        LocalDensity.current

    val thresholdPx =
        with(density) {
            60.dp.toPx()
        }

    // 💬 Color dinámico de la burbuja

    val colorBurbujaBase =
        if (esPropio) {
            c.burbujaPropia
        } else {
            c.burbujaAjena
        }

    val colorBurbujaFinal =
        if (esResaltado) {

            if (
                MaterialTheme
                    .colorScheme
                    .background
                    .luminance() < 0.5f
            ) {

                Color(0xFF365314)

            } else {

                Color(0xFFECFCCB)
            }

        } else {

            colorBurbujaBase
        }

    Column(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 1.dp),

        horizontalAlignment =
            if (esPropio) {
                Alignment.End
            } else {
                Alignment.Start
            }

    ) {

        Surface(

            shape =
                RoundedCornerShape(

                    topStart = 14.dp,

                    topEnd = 14.dp,

                    bottomStart =
                        if (esPropio) {
                            14.dp
                        } else {
                            2.dp
                        },

                    bottomEnd =
                        if (esPropio) {
                            2.dp
                        } else {
                            14.dp
                        }
                ),

            color =
                colorBurbujaFinal,

            shadowElevation =
                if (esResaltado) {
                    6.dp
                } else {
                    1.dp
                },

            modifier =
                Modifier
                    .widthIn(
                        max =
                            maxBurbujaWidth
                    )
                    .offset {
                        IntOffset(
                            offsetX.toInt(),
                            0
                        )
                    }
                    .pointerInput(Unit) {

                        detectHorizontalDragGestures(

                            onDragEnd = {

                                if (
                                    offsetX >
                                    thresholdPx
                                ) {

                                    onResponder()
                                }

                                offsetX = 0f
                            },

                            onHorizontalDrag = {
                                    _, dragAmount ->

                                offsetX =
                                    (
                                            offsetX +
                                                    dragAmount
                                            )
                                        .coerceIn(
                                            0f,
                                            thresholdPx * 1.5f
                                        )
                            }
                        )
                    }
                    .combinedClickable(

                        onClick = {},

                        onLongClick =
                            onMostrarReacciones
                    )
        ) {

            Column(

                modifier =
                    Modifier
                        .wrapContentWidth()
                        .padding(
                            start = 9.dp,
                            end = 9.dp,
                            top = 6.dp,
                            bottom = 4.dp
                        )

            ) {

                // 💬 Tarjeta miniatura del mensaje citado

                if (
                    mensaje.reply_to != null
                ) {

                    Surface(

                        shape =
                            RoundedCornerShape(6.dp),

                        color =
                            c.fondoTarjeta.copy(
                                alpha = 0.7f
                            ),

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    bottom = 4.dp
                                )
                                .combinedClickable(

                                    onClick = {

                                        onIrAlMensajeOriginal(
                                            mensaje.reply_to.id
                                        )
                                    }
                                )

                    ) {

                        Row(

                            modifier =
                                Modifier.padding(6.dp),

                            verticalAlignment =
                                Alignment.CenterVertically

                        ) {

                            Box(

                                modifier =
                                    Modifier
                                        .width(2.5.dp)
                                        .height(26.dp)
                                        .background(
                                            c.nombreEmisor
                                        )
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )

                            Column(
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text(

                                    text =
                                        mensaje.reply_to.usuario?.name
                                            ?: "Vecino",

                                    color =
                                        c.nombreEmisor,

                                    fontSize = 11.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Text(

                                    text =
                                        mensaje.reply_to.mensaje
                                            ?: "Mensaje",

                                    color =
                                        c.textoSecundario,

                                    fontSize = 11.sp,

                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                if (
                    !esPropio
                ) {

                    Text(

                        text =
                            mensaje.usuario?.name
                                ?: "Vecino",

                        color =
                            c.nombreEmisor,

                        fontSize = 12.sp,

                        lineHeight = 11.sp,

                        fontWeight =
                            FontWeight.Bold,

                        maxLines = 1
                    )
                }

                // 📁 DETECCIÓN DE ARCHIVO ADJUNTO

                val archivoAdjunto =
                    mensaje.obtenerArchivoAdjunto()

                if (
                    archivoAdjunto != null
                ) {

                    TarjetaArchivoMensaje(
                        archivo =
                            archivoAdjunto
                    )

                    Spacer(
                        modifier =
                            Modifier.height(2.dp)
                    )

                    Text(

                        text =
                            formatearHoraChat(
                                mensaje.created_at
                            ),

                        color =
                            c.textoSecundario,

                        fontSize = 10.sp,

                        modifier =
                            Modifier.align(
                                Alignment.End
                            )
                    )

                } else {

                    val lineas =
                        texto
                            .lines()
                            .map {
                                it.trim()
                            }
                            .filter {
                                it.isNotBlank()
                            }

                    val tieneListado =
                        !esPropio &&
                                lineas.any {
                                    it.startsWith("•")
                                }

                    if (
                        tieneListado
                    ) {

                        var introduccionMostrada =
                            false

                        var sumaTotalAcumulada =
                            0.0

                        var hayCalculoTotal =
                            false

                        var textoLineaTotal =
                            ""

                        lineas.forEach { linea ->

                            val lineaLimpia =
                                linea.replace(
                                    "**",
                                    ""
                                )

                            val esLineaTotal =
                                lineaLimpia.contains(
                                    "total",
                                    ignoreCase = true
                                )

                            if (
                                linea.startsWith("•") &&
                                !esLineaTotal
                            ) {

                                val partes =
                                    lineaLimpia
                                        .removePrefix("•")
                                        .split("|")
                                        .map {
                                            it.trim()
                                        }

                                val fecha =
                                    partes.getOrNull(0)
                                        ?: ""

                                val tipo =
                                    partes.getOrNull(1)
                                        ?: ""

                                val concepto =
                                    partes.getOrNull(2)
                                        ?: ""

                                val categoria =
                                    partes.getOrNull(3)
                                        ?: ""

                                val montoStr =
                                    partes.getOrNull(4)
                                        ?: ""

                                val montoLimpio =
                                    montoStr
                                        .replace(
                                            "S/",
                                            ""
                                        )
                                        .replace(
                                            "s/",
                                            ""
                                        )
                                        .replace(
                                            ",",
                                            ""
                                        )
                                        .trim()

                                val valMonto =
                                    montoLimpio.toDoubleOrNull()

                                if (
                                    valMonto != null
                                ) {

                                    hayCalculoTotal =
                                        true

                                    val esEgreso =
                                        tipo.contains(
                                            "egreso",
                                            ignoreCase = true
                                        ) ||
                                                tipo.contains(
                                                    "salida",
                                                    ignoreCase = true
                                                ) ||
                                                montoStr.startsWith(
                                                    "-"
                                                )

                                    if (
                                        esEgreso &&
                                        valMonto > 0
                                    ) {

                                        sumaTotalAcumulada -=
                                            valMonto

                                    } else {

                                        sumaTotalAcumulada +=
                                            valMonto
                                    }
                                }

                                Spacer(
                                    modifier =
                                        Modifier.height(4.dp)
                                )

                                TarjetaMovimientoInteligente(

                                    fecha =
                                        fecha,

                                    tipo =
                                        tipo,

                                    concepto =
                                        concepto,

                                    categoria =
                                        categoria,

                                    monto =
                                        montoStr
                                )

                            } else if (
                                !introduccionMostrada &&
                                !esLineaTotal
                            ) {

                                Text(

                                    text =
                                        lineaLimpia,

                                    color =
                                        c.textoPrincipal,

                                    fontSize = 14.sp,

                                    lineHeight = 19.sp,

                                    textAlign =
                                        TextAlign.Start
                                )

                                introduccionMostrada =
                                    true

                            } else if (
                                esLineaTotal
                            ) {

                                textoLineaTotal =
                                    lineaLimpia

                            } else if (
                                lineaLimpia.isNotBlank()
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(4.dp)
                                )

                                Text(

                                    text =
                                        lineaLimpia,

                                    color =
                                        colorPrincipal(),

                                    fontSize = 13.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }

                        if (
                            hayCalculoTotal ||
                            textoLineaTotal.isNotBlank()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(4.dp)
                            )

                            TarjetaTotalInteligente(

                                totalSuma =
                                    if (
                                        hayCalculoTotal
                                    ) {
                                        sumaTotalAcumulada
                                    } else {
                                        null
                                    },

                                lineaOriginal =
                                    textoLineaTotal
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(

                            text =
                                formatearHoraChat(
                                    mensaje.created_at
                                ),

                            color =
                                c.textoSecundario,

                            fontSize = 10.sp,

                            modifier =
                                Modifier.align(
                                    Alignment.End
                                )
                        )

                    } else {

                        Row(

                            modifier =
                                Modifier.wrapContentWidth(),

                            verticalAlignment =
                                Alignment.Bottom

                        ) {

                            Text(

                                text =
                                    texto,

                                color =
                                    c.textoPrincipal,

                                fontSize = 14.sp,

                                lineHeight = 19.sp,

                                modifier =
                                    Modifier
                                        .weight(
                                            1f,
                                            fill = false
                                        )
                                        .padding(
                                            end = 8.dp
                                        )
                            )

                            Text(

                                text =
                                    formatearHoraChat(
                                        mensaje.created_at
                                    ),

                                color =
                                    c.textoSecundario,

                                fontSize = 10.sp,

                                modifier =
                                    Modifier.padding(
                                        bottom = 1.dp
                                    )
                            )
                        }
                    }
                }
            }
        }

        if (
            !mensaje.reacciones.isNullOrEmpty()
        ) {

            Row(

                modifier =
                    Modifier.padding(
                        top = 2.dp,
                        start = 6.dp,
                        end = 6.dp
                    ),

                horizontalArrangement =
                    Arrangement.spacedBy(4.dp),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                mensaje.reacciones
                    .orEmpty()
                    .forEach { reaccion ->

                        Surface(

                            shape =
                                RoundedCornerShape(
                                    16.dp
                                ),

                            color =
                                if (
                                    reaccion.yo
                                ) {

                                    colorPrincipal()
                                        .copy(
                                            alpha = 0.14f
                                        )

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .surface
                                },

                            border =
                                if (
                                    reaccion.yo
                                ) {

                                    BorderStroke(
                                        1.dp,
                                        colorPrincipal()
                                            .copy(
                                                alpha = 0.55f
                                            )
                                    )

                                } else {

                                    BorderStroke(
                                        1.dp,
                                        Color.Black.copy(
                                            alpha = 0.08f
                                        )
                                    )
                                },

                            shadowElevation = 1.dp,

                            modifier =
                                Modifier.combinedClickable(

                                    onClick = {

                                        onReaccionar(
                                            reaccion.emoji
                                        )
                                    },

                                    onLongClick =
                                        onMostrarReacciones
                                )

                        ) {

                            Row(

                                modifier =
                                    Modifier.padding(
                                        horizontal = 7.dp,
                                        vertical = 3.dp
                                    ),

                                verticalAlignment =
                                    Alignment.CenterVertically,

                                horizontalArrangement =
                                    Arrangement.spacedBy(3.dp)

                            ) {

                                Text(

                                    text =
                                        reaccion.emoji,

                                    fontSize = 14.sp
                                )

                                Text(

                                    text =
                                        reaccion.cantidad
                                            .toString(),

                                    fontSize = 10.sp,

                                    color =
                                        c.textoPrincipal,

                                    fontWeight =
                                        if (
                                            reaccion.yo
                                        ) {

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
private fun TarjetaArchivoMensaje(
    archivo: ChatArchivoPayload
) {
    val c = coloresChat()
    val context = LocalContext.current

    var mostrarImagenAmpliada by remember {
        mutableStateOf(false)
    }

    val urlCorregida = archivo.url?.replace(
        "localhost",
        "10.0.2.2"
    )

    val esImagen = archivo.mime?.startsWith("image/") == true

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {

        // ============================================================
        // 🖼️ IMAGEN DEL MENSAJE
        // ============================================================

        if (
            esImagen &&
            !urlCorregida.isNullOrBlank()
        ) {

            AsyncImage(
                model = urlCorregida,

                contentDescription =
                    archivo.nombre ?: "Imagen adjunta",

                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(
                        RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        mostrarImagenAmpliada = true
                    },

                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )
        }

        // ============================================================
        // 📝 TEXTO DEL ARCHIVO
        // ============================================================

        if (!archivo.texto.isNullOrBlank()) {

            Text(
                text = archivo.texto,

                color = c.textoPrincipal,

                fontSize = 14.sp,

                lineHeight = 19.sp
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )
        }

        // ============================================================
        // 📎 INFORMACIÓN DEL ARCHIVO
        // ============================================================

        Surface(

            shape = RoundedCornerShape(6.dp),

            color = MaterialTheme.colorScheme.onSurface.copy(
                alpha = 0.05f
            ),

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 2.dp)
                .combinedClickable(

                    onClick = {

                        // Para archivos que no son imágenes,
                        // se mantiene la apertura externa.

                        if (
                            !esImagen &&
                            !urlCorregida.isNullOrBlank()
                        ) {

                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(urlCorregida)
                            )

                            context.startActivity(intent)
                        }
                    }
                )

        ) {

            Row(

                modifier = Modifier.padding(8.dp),

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Icon(

                    imageVector =
                        Icons.Outlined.AttachFile,

                    contentDescription = null,

                    tint = colorPrincipal(),

                    modifier = Modifier.size(16.dp)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(

                    text =
                        archivo.nombre ?: "Archivo adjunto",

                    color = colorPrincipal(),

                    fontSize = 12.sp,

                    fontWeight = FontWeight.Medium,

                    maxLines = 1
                )
            }
        }
    }

    // ============================================================
    // 🔍 VENTANA DE IMAGEN AMPLIADA
    // ============================================================

    if (
        mostrarImagenAmpliada &&
        !urlCorregida.isNullOrBlank()
    ) {

        Dialog(

            onDismissRequest = {
                mostrarImagenAmpliada = false
            }

        ) {

            Surface(

                modifier = Modifier.fillMaxSize(),

                color = Color.Black

            ) {

                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    AsyncImage(

                        model = urlCorregida,

                        contentDescription =
                            archivo.nombre
                                ?: "Imagen ampliada",

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                top = 35.dp,
                                bottom = 35.dp
                            ),

                        contentScale =
                            ContentScale.Fit
                    )

                    // Botón para cerrar la imagen

                    IconButton(

                        onClick = {
                            mostrarImagenAmpliada = false
                        },

                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                Color.Black.copy(
                                    alpha = 0.55f
                                ),
                                CircleShape
                            )

                    ) {

                        Icon(

                            imageVector =
                                Icons.Outlined.Close,

                            contentDescription =
                                "Cerrar imagen",

                            tint = Color.White
                        )
                    }
                }
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

    val c = coloresChat()

    val esEgreso =
        tipo.contains(
            "egreso",
            ignoreCase = true
        ) ||
                tipo.contains(
                    "salida",
                    ignoreCase = true
                ) ||
                monto.startsWith("-")

    val colorMovimiento =
        if (esEgreso) {
            RojoMonto
        } else {
            VerdeMonto
        }

    val iconoMovimiento =
        if (esEgreso) {
            Icons.Outlined.ArrowDownward
        } else {
            Icons.Outlined.ArrowUpward
        }

    Surface(

        shape =
            RoundedCornerShape(10.dp),

        color =
            c.fondoTarjeta,

        border =
            BorderStroke(
                1.dp,
                c.bordeTarjeta
            ),

        modifier =
            Modifier.fillMaxWidth()

    ) {

        Column(
            modifier =
                Modifier.padding(10.dp)
        ) {

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Text(

                    text =
                        fecha,

                    color =
                        c.textoSecundario,

                    fontSize = 11.sp,

                    fontWeight =
                        FontWeight.Medium
                )

                if (
                    monto.isNotBlank()
                ) {

                    Text(

                        text =
                            monto,

                        color =
                            colorMovimiento,

                        fontSize = 13.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(3.dp)
            )

            if (
                tipo.isNotBlank()
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Icon(

                        imageVector =
                            iconoMovimiento,

                        contentDescription =
                            null,

                        tint =
                            colorMovimiento,

                        modifier =
                            Modifier.size(12.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(4.dp)
                    )

                    Text(

                        text =
                            tipo.uppercase(),

                        color =
                            colorMovimiento,

                        fontSize = 10.sp,

                        fontWeight =
                            FontWeight.Bold
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )
            }

            if (
                concepto.isNotBlank()
            ) {

                Text(

                    text =
                        concepto,

                    color =
                        c.textoPrincipal,

                    fontSize = 13.sp,

                    fontWeight =
                        FontWeight.SemiBold,

                    lineHeight = 17.sp
                )
            }

            if (
                categoria.isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(2.dp)
                )

                Text(

                    text =
                        "Categoría: $categoria",

                    color =
                        c.textoSecundario,

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

    val c = coloresChat()

    val montoTextoExtraido =
        if (
            totalSuma != null
        ) {

            val valAbsoluto =
                Math.abs(totalSuma)

            val strMonto =
                String.format(
                    Locale.US,
                    "%.2f",
                    valAbsoluto
                )

            if (
                totalSuma < 0
            ) {

                "-S/ $strMonto"

            } else {

                "S/ $strMonto"
            }

        } else {

            val partes =
                lineaOriginal.split(":")

            if (
                partes.size > 1 &&
                partes[1].isNotBlank()
            ) {

                partes[1].trim()

            } else {

                lineaOriginal
                    .replace(
                        "Total",
                        "",
                        ignoreCase = true
                    )
                    .replace(
                        "•",
                        ""
                    )
                    .trim()
                    .takeIf {
                        it.isNotBlank()
                    }
                    ?: "S/ 0.00"
            }
        }

    val esNegativo =
        (totalSuma ?: 0.0) < 0 ||
                montoTextoExtraido.startsWith("-")

    val colorTotal =
        if (esNegativo) {
            RojoMonto
        } else {
            VerdeMonto
        }

    Surface(

        shape =
            RoundedCornerShape(10.dp),

        color =
            c.fondoTarjeta,

        border =
            BorderStroke(
                1.dp,
                c.bordeTarjeta
            ),

        modifier =
            Modifier.fillMaxWidth()

    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),

            horizontalArrangement =
                Arrangement.SpaceBetween,

            verticalAlignment =
                Alignment.CenterVertically

        ) {

            Text(

                text =
                    "Total",

                color =
                    c.textoPrincipal,

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Bold
            )

            Text(

                text =
                    montoTextoExtraido,

                color =
                    colorTotal,

                fontSize = 13.sp,

                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}

private fun formatearHoraChat(
    fechaHora: String?
): String {

    if (
        fechaHora.isNullOrBlank()
    ) {
        return ""
    }

    return try {

        val hora =
            fechaHora
                .substringAfter("T")
                .substringBefore(".")
                .substringBefore("Z")

        if (
            hora.length >= 5
        ) {

            hora.substring(
                0,
                5
            )

        } else {

            hora
        }

    } catch (
        _: Exception
    ) {

        ""
    }
}