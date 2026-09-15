package com.sigefiv.app.screens.notificaciones

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import com.sigefiv.app.viewmodel.EnviarNotificacionViewModel
import com.sigefiv.app.viewmodel.UsuarioViewModel

// ====================================================================
// PALETA DE COLORES CORPORATIVA SIGEFIV
// ====================================================================
private val FondoPantalla = Color(0xFFF8FAFC)
private val Blanco = Color.White
private val VerdeOscuro = Color(0xFF166534)
private val VerdeSuave = Color(0xFFDCFCE7)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisTexto = Color(0xFF64748B)
private val GrisBorde = Color(0xFFCBD5E1)
private val RojoError = Color(0xFFDC2626)
private val AzulInfoFondo = Color(0xFFF0FDF4) // Tono suave armónico con verde para info
private val AzulInfoTexto = Color(0xFF166534)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnviarNotificacionScreen(
    viewModel: EnviarNotificacionViewModel,
    usuarioViewModel: UsuarioViewModel,
    onBackClick: () -> Unit,
    onEnvioExitoso: (
        titulo: String,
        mensaje: String,
        tipo: String,
        destinatario: String,
        cantidad: Int
    ) -> Unit
) {
    // ============================================================
    // CONFIGURAR BARRA DE ESTADO NATIVA (VERDE INSTITUCIONAL)
    // ============================================================
    val view = LocalView.current
    val context = LocalContext.current
    SideEffect {
        val window = (context as? Activity)?.window
        window?.let {
            it.statusBarColor = SeasonalColors.primary(SeasonalTheme.getSeason()).toArgb()
            WindowInsetsControllerCompat(it, view).isAppearanceLightStatusBars = false
        }
    }

    // ============================================================
    // ESTADO DEL FORMULARIO
    // ============================================================
    var titulo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Aviso") }
    var destinatario by remember { mutableStateOf("Todos los vecinos") }
    var tipoExpandido by remember { mutableStateOf(false) }
    var destinatarioExpandido by remember { mutableStateOf(false) }
    var busquedaUsuario by remember { mutableStateOf("") }
    var usuariosSeleccionados by remember { mutableStateOf(setOf<Int>()) }

    val enviando by viewModel.enviando.collectAsState()
    val mensajeEstado by viewModel.mensaje.collectAsState()
    val enviado by viewModel.enviado.collectAsState()
    val usuarioUiState by usuarioViewModel.uiState.collectAsState()

    // ============================================================
    // RESULTADO DEL ENVÍO
    // ============================================================
    LaunchedEffect(enviado) {
        if (enviado) {
            val cantidad = when (destinatario) {
                "Seleccionar usuarios" -> usuariosSeleccionados.size
                else -> 0
            }
            onEnvioExitoso(titulo, mensaje, tipo, destinatario, cantidad)
        }
    }

    // ============================================================
    // CARGAR USUARIOS
    // ============================================================
    LaunchedEffect(destinatario) {
        if (destinatario == "Seleccionar usuarios") {
            usuarioViewModel.cargarUsuarios()
        }
    }

    // ============================================================
    // FILTRAR USUARIOS
    // ============================================================
    val usuariosFiltrados = usuarioUiState.usuarios.filter { usuario ->
        if (busquedaUsuario.isBlank()) {
            true
        } else {
            val texto = busquedaUsuario.trim().lowercase()
            usuario.name.lowercase().contains(texto) ||
                    usuario.email.lowercase().contains(texto)
        }
    }

    // ============================================================
    // ESTRUCTURA DE LA PANTALLA
    // ============================================================
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
    ) {

        // ========================================================
        // ENCABEZADO INSTITUCIONAL
        // ========================================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SeasonalColors.primary(SeasonalTheme.getSeason()),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Blanco,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "SIGEFIV",
                        color = Blanco,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Centro de Notificaciones",
                        color = Blanco.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // ========================================================
        // CONTENIDO PRINCIPAL SCROLLABLE
        // ========================================================
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // TARJETA PRINCIPAL DEL FORMULARIO
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    // ENCABEZADO DE LA TARJETA
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(VerdeSuave, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = SeasonalColors.primary(SeasonalTheme.getSeason()),
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Nueva notificación",
                                color = TextoPrincipal,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Transmite avisos importantes a la comunidad",
                                color = GrisTexto,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ====================================================
                    // TÍTULO
                    // ====================================================
                    Text(
                        text = "Título del comunicado *",
                        color = TextoPrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Ej. Reunión extraordinaria de vecinos", color = GrisTexto) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColorsCustom()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====================================================
                    // MENSAJE
                    // ====================================================
                    Text(
                        text = "Mensaje detallado *",
                        color = TextoPrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = mensaje,
                        onValueChange = { mensaje = it.take(500) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        placeholder = { Text("Escribe aquí el contenido del mensaje...", color = GrisTexto) },
                        shape = RoundedCornerShape(10.dp),
                        colors = textFieldColorsCustom()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, end = 2.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${mensaje.length}/500",
                            color = GrisTexto,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====================================================
                    // TIPO DE NOTIFICACIÓN
                    // ====================================================
                    Text(
                        text = "Categoría o tipo *",
                        color = TextoPrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = tipoExpandido,
                        onExpandedChange = { tipoExpandido = !tipoExpandido }
                    ) {
                        OutlinedTextField(
                            value = tipo,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Campaign,
                                    contentDescription = null,
                                    tint = SeasonalColors.primary(SeasonalTheme.getSeason())
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = textFieldColorsCustom()
                        )

                        DropdownMenu(
                            expanded = tipoExpandido,
                            onDismissRequest = { tipoExpandido = false }
                        ) {
                            listOf("Aviso", "Asamblea", "Financiero", "Sistema").forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion, color = TextoPrincipal) },
                                    onClick = {
                                        tipo = opcion
                                        tipoExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====================================================
                    // DESTINATARIO
                    // ====================================================
                    Text(
                        text = "Enviar a *",
                        color = TextoPrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = destinatarioExpandido,
                        onExpandedChange = { destinatarioExpandido = !destinatarioExpandido }
                    ) {
                        OutlinedTextField(
                            value = destinatario,
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Groups,
                                    contentDescription = null,
                                    tint = SeasonalColors.primary(SeasonalTheme.getSeason())
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            shape = RoundedCornerShape(10.dp),
                            colors = textFieldColorsCustom()
                        )

                        DropdownMenu(
                            expanded = destinatarioExpandido,
                            onDismissRequest = { destinatarioExpandido = false }
                        ) {
                            listOf(
                                "Todos los vecinos",
                                "Miembros de la directiva",
                                "Seleccionar usuarios"
                            ).forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion, color = TextoPrincipal) },
                                    onClick = {
                                        destinatario = opcion
                                        destinatarioExpandido = false
                                        if (opcion != "Seleccionar usuarios") {
                                            busquedaUsuario = ""
                                            usuariosSeleccionados = emptySet()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    // ====================================================
                    // SELECCIÓN DE USUARIOS (CONDICIONAL)
                    // ====================================================
                    if (destinatario == "Seleccionar usuarios") {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Lista de destinatarios",
                            color = TextoPrincipal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = busquedaUsuario,
                            onValueChange = { busquedaUsuario = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Buscar por nombre o correo...", color = GrisTexto) },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = GrisTexto)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = textFieldColorsCustom()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${usuariosSeleccionados.size} usuario(s) seleccionado(s)",
                            color = SeasonalColors.primary(SeasonalTheme.getSeason()),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(210.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = FondoPantalla),
                            border = BorderStroke(1.dp, GrisBorde)
                        ) {
                            when {
                                usuarioUiState.cargando -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = SeasonalColors.primary(SeasonalTheme.getSeason()))
                                    }
                                }
                                usuarioUiState.error != null -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(
                                            text = usuarioUiState.error ?: "Error al cargar usuarios.",
                                            color = RojoError,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                                usuariosFiltrados.isEmpty() -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text(text = "No se encontraron usuarios.", color = GrisTexto, fontSize = 13.sp)
                                    }
                                }
                                else -> {
                                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                                        items(items = usuariosFiltrados, key = { it.id }) { usuario ->
                                            val seleccionado = usuariosSeleccionados.contains(usuario.id)
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Checkbox(
                                                    checked = seleccionado,
                                                    onCheckedChange = { marcado ->
                                                        usuariosSeleccionados = if (marcado) {
                                                            usuariosSeleccionados + usuario.id
                                                        } else {
                                                            usuariosSeleccionados - usuario.id
                                                        }
                                                    },
                                                    colors = CheckboxDefaults.colors(checkedColor = SeasonalColors.primary(SeasonalTheme.getSeason()))
                                                )

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = usuario.name,
                                                        color = TextoPrincipal,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Text(
                                                        text = usuario.email,
                                                        color = GrisTexto,
                                                        fontSize = 11.sp
                                                    )
                                                    Text(
                                                        text = usuario.rolPrincipal,
                                                        color = SeasonalColors.primary(SeasonalTheme.getSeason()),
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ====================================================
                    // TARJETA DE INFORMACIÓN / AYUDA
                    // ====================================================
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = AzulInfoFondo)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = AzulInfoTexto,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = when (destinatario) {
                                    "Miembros de la directiva" -> "Se enviará la notificación exclusivamente a los directivos con alertas activas."
                                    "Seleccionar usuarios" -> "Se enviará la notificación de forma personalizada a los destinatarios marcados."
                                    else -> "Se enviará la notificación de difusión masiva a todos los vecinos registrados."
                                },
                                color = AzulInfoTexto,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // MENSAJE DE ESTADO DE ERROR O RESPUESTA
                    if (mensajeEstado != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = mensajeEstado ?: "",
                            color = if (enviado) SeasonalColors.primary(SeasonalTheme.getSeason()) else RojoError,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
        }

        // ============================================================
        // BARRA DE ACCIONES INFERIOR FIJA
        // ============================================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Blanco,
            shadowElevation = 8.dp
        ) {
            val puedeEnviar = !enviando &&
                    titulo.isNotBlank() &&
                    mensaje.isNotBlank() &&
                    (destinatario != "Seleccionar usuarios" || usuariosSeleccionados.isNotEmpty())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .weight(0.4f)
                        .height(50.dp),
                    enabled = !enviando,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, GrisBorde),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GrisTexto)
                ) {
                    Text(
                        text = "Cancelar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {
                        val destino = when (destinatario) {
                            "Todos los vecinos" -> "todos"
                            "Miembros de la directiva" -> "directiva"
                            else -> "usuarios"
                        }

                        viewModel.enviar(
                            titulo = titulo,
                            mensaje = mensaje,
                            tipo = tipo.lowercase(),
                            destinatario = destino,
                            usuarioIds = usuariosSeleccionados.toList()
                        )
                    },
                    modifier = Modifier
                        .weight(0.6f)
                        .height(50.dp),
                    enabled = puedeEnviar,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
                        disabledContainerColor = SeasonalColors.primary(SeasonalTheme.getSeason()).copy(alpha = 0.4f),
                        contentColor = Blanco
                    )
                ) {
                    if (enviando) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Blanco,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Enviando...", fontSize = 15.sp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Enviar aviso",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ====================================================================
// ESTILOS PERSONALIZADOS PARA LOS CAMPOS DE TEXTO
// ====================================================================
@Composable
private fun textFieldColorsCustom() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
    unfocusedBorderColor = GrisBorde,
    focusedLabelColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
    cursorColor = SeasonalColors.primary(SeasonalTheme.getSeason())
)