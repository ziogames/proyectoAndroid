@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.notificaciones

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.viewmodel.EnviarNotificacionViewModel
import com.sigefiv.app.viewmodel.UsuarioViewModel
import androidx.compose.foundation.layout.PaddingValues

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
    val primaryColor = MaterialTheme.colorScheme.primary

    // Estados del formulario
    var titulo by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("Aviso") }
    var destinatario by remember { mutableStateOf("Todos los vecinos") }

    var tipoExpandido by remember { mutableStateOf(false) }
    var destinatarioExpandido by remember { mutableStateOf(false) }
    var busquedaUsuario by remember { mutableStateOf("") }
    var usuariosSeleccionados by remember { mutableStateOf(setOf<Int>()) }

    // Estados del ViewModel
    val enviando by viewModel.enviando.collectAsState()
    val mensajeEstado by viewModel.mensaje.collectAsState()
    val enviado by viewModel.enviado.collectAsState()
    val usuarioUiState by usuarioViewModel.uiState.collectAsState()

    // Manejo de envío exitoso
    LaunchedEffect(enviado) {
        if (enviado) {
            val cantidad = when (destinatario) {
                "Seleccionar usuarios" -> usuariosSeleccionados.size
                else -> 0
            }
            onEnvioExitoso(titulo, mensaje, tipo, destinatario, cantidad)
        }
    }

    // Cargar usuarios cuando se selecciona la opción específica
    LaunchedEffect(destinatario) {
        if (destinatario == "Seleccionar usuarios") {
            usuarioViewModel.cargarUsuarios()
        }
    }

    // Filtro de usuarios en tiempo real
    val usuariosFiltrados = usuarioUiState.usuarios.filter { usuario ->
        if (busquedaUsuario.isBlank()) {
            true
        } else {
            val texto = busquedaUsuario.trim().lowercase()
            usuario.name.lowercase().contains(texto) || usuario.email.lowercase().contains(texto)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nueva Notificación",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Centro de comunicados vecinales",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            val puedeEnviar = !enviando &&
                    titulo.isNotBlank() &&
                    mensaje.isNotBlank() &&
                    (destinatario != "Seleccionar usuarios" || usuariosSeleccionados.isNotEmpty())

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        enabled = !enviando,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 13.sp,
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
                            .weight(1.4f)
                            .height(48.dp),
                        enabled = puedeEnviar,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = Color.White,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        if (enviando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(19.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Enviando...", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Send,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Enviar aviso",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // TARJETA DE GUÍA / HERO
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.NotificationsActive,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Nuevo comunicado vecinal",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Transmite avisos oficiales a la comunidad en tiempo real.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // CONTENEDOR PRINCIPAL DEL FORMULARIO
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Título
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Título del comunicado *",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    "Ej. Convocatoria a reunión extraordinaria",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = coloresCampo(primaryColor)
                        )
                    }

                    // Mensaje
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mensaje detallado *",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${mensaje.length}/500",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedTextField(
                            value = mensaje,
                            onValueChange = { mensaje = it.take(500) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            placeholder = {
                                Text(
                                    "Escribe aquí el contenido del aviso a difundir...",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = coloresCampo(primaryColor)
                        )
                    }

                    // Categoría o Tipo
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Categoría o tipo *",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        ExposedDropdownMenuBox(
                            expanded = tipoExpandido,
                            onExpandedChange = { tipoExpandido = !tipoExpandido },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = tipo,
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = {
                                    SelectorIcon(
                                        icono = when (tipo) {
                                            "Asamblea" -> Icons.Outlined.Groups
                                            "Financiero" -> Icons.Outlined.AccountBalanceWallet
                                            "Sistema" -> Icons.Outlined.NotificationsActive
                                            else -> Icons.Outlined.Campaign
                                        },
                                        primaryColor = primaryColor
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (tipoExpandido) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = coloresCampo(primaryColor)
                            )

                            DropdownMenu(
                                expanded = tipoExpandido,
                                onDismissRequest = { tipoExpandido = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                OpcionSelector(
                                    texto = "Aviso",
                                    seleccionado = tipo == "Aviso",
                                    icono = Icons.Outlined.Campaign,
                                    primaryColor = primaryColor,
                                    onClick = {
                                        tipo = "Aviso"
                                        tipoExpandido = false
                                    }
                                )
                                OpcionSelector(
                                    texto = "Asamblea",
                                    seleccionado = tipo == "Asamblea",
                                    icono = Icons.Outlined.Groups,
                                    primaryColor = primaryColor,
                                    onClick = {
                                        tipo = "Asamblea"
                                        tipoExpandido = false
                                    }
                                )
                                OpcionSelector(
                                    texto = "Financiero",
                                    seleccionado = tipo == "Financiero",
                                    icono = Icons.Outlined.AccountBalanceWallet,
                                    primaryColor = primaryColor,
                                    onClick = {
                                        tipo = "Financiero"
                                        tipoExpandido = false
                                    }
                                )
                                OpcionSelector(
                                    texto = "Sistema",
                                    seleccionado = tipo == "Sistema",
                                    icono = Icons.Outlined.NotificationsActive,
                                    primaryColor = primaryColor,
                                    onClick = {
                                        tipo = "Sistema"
                                        tipoExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    // Destinatario
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Enviar a *",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        ExposedDropdownMenuBox(
                            expanded = destinatarioExpandido,
                            onExpandedChange = { destinatarioExpandido = !destinatarioExpandido },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = destinatario,
                                onValueChange = {},
                                readOnly = true,
                                leadingIcon = {
                                    SelectorIcon(
                                        icono = when (destinatario) {
                                            "Seleccionar usuarios" -> Icons.Outlined.Person
                                            else -> Icons.Outlined.Groups
                                        },
                                        primaryColor = primaryColor
                                    )
                                },
                                trailingIcon = {
                                    Icon(
                                        imageVector = if (destinatarioExpandido) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                colors = coloresCampo(primaryColor)
                            )

                            DropdownMenu(
                                expanded = destinatarioExpandido,
                                onDismissRequest = { destinatarioExpandido = false },
                                modifier = Modifier
                                    .fillMaxWidth(0.85f)
                                    .background(MaterialTheme.colorScheme.surface)
                            ) {
                                OpcionSelector(
                                    texto = "Todos los vecinos",
                                    seleccionado = destinatario == "Todos los vecinos",
                                    icono = Icons.Outlined.Groups,
                                    primaryColor = primaryColor,
                                    onClick = {
                                        destinatario = "Todos los vecinos"
                                        destinatarioExpandido = false
                                        busquedaUsuario = ""
                                        usuariosSeleccionados = emptySet()
                                    }
                                )
                                OpcionSelector(
                                    texto = "Miembros de la directiva",
                                    seleccionado = destinatario == "Miembros de la directiva",
                                    icono = Icons.Outlined.Groups,
                                    primaryColor = primaryColor,
                                    onClick = {
                                        destinatario = "Miembros de la directiva"
                                        destinatarioExpandido = false
                                        busquedaUsuario = ""
                                        usuariosSeleccionados = emptySet()
                                    }
                                )
                                OpcionSelector(
                                    texto = "Seleccionar usuarios",
                                    seleccionado = destinatario == "Seleccionar usuarios",
                                    icono = Icons.Outlined.Person,
                                    primaryColor = primaryColor,
                                    onClick = {
                                        destinatario = "Seleccionar usuarios"
                                        destinatarioExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    // Selector individual de usuarios
                    if (destinatario == "Seleccionar usuarios") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Destinatarios específicos",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = primaryColor.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "${usuariosSeleccionados.size} seleccionados",
                                        color = primaryColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = busquedaUsuario,
                                onValueChange = { busquedaUsuario = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "Buscar por nombre o correo...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Search,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = coloresCampo(primaryColor)
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(210.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                            ) {
                                when {
                                    usuarioUiState.cargando -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = primaryColor, strokeWidth = 2.5.dp)
                                        }
                                    }
                                    usuarioUiState.error != null -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = usuarioUiState.error ?: "Error al cargar usuarios.",
                                                color = MaterialTheme.colorScheme.error,
                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                    usuariosFiltrados.isEmpty() -> {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Person,
                                                    contentDescription = null,
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                                    modifier = Modifier.size(28.dp)
                                                )
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = "No se encontraron usuarios.",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 13.sp
                                                )
                                            }
                                        }
                                    }
                                    else -> {
                                        LazyColumn(
                                            modifier = Modifier.fillMaxSize(),
                                            contentPadding = PaddingValues(vertical = 4.dp)
                                        ) {
                                            items(usuariosFiltrados, key = { it.id }) { usuario ->
                                                val seleccionado = usuariosSeleccionados.contains(usuario.id)

                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            usuariosSeleccionados = if (seleccionado) {
                                                                usuariosSeleccionados - usuario.id
                                                            } else {
                                                                usuariosSeleccionados + usuario.id
                                                            }
                                                        }
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
                                                        colors = CheckboxDefaults.colors(
                                                            checkedColor = primaryColor,
                                                            checkmarkColor = Color.White
                                                        )
                                                    )

                                                    Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                            text = usuario.name,
                                                            color = MaterialTheme.colorScheme.onSurface,
                                                            fontSize = 13.sp,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Text(
                                                            text = usuario.email,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            fontSize = 11.sp
                                                        )
                                                    }

                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = primaryColor.copy(alpha = 0.10f)
                                                    ) {
                                                        Text(
                                                            text = usuario.rolPrincipal,
                                                            color = primaryColor,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

                    // Tarjeta informativa contextual
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = when (destinatario) {
                                    "Miembros de la directiva" -> "Se enviará la notificación exclusivamente a los directivos registrados."
                                    "Seleccionar usuarios" -> "Se enviará el comunicado de forma directa a los destinatarios marcados."
                                    else -> "Se enviará la notificación de difusión comunal a todos los vecinos del grupo."
                                },
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Mensaje de estado de envío
                    if (mensajeEstado != null) {
                        Text(
                            text = mensajeEstado ?: "",
                            color = if (enviado) primaryColor else MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SelectorIcon(
    icono: ImageVector,
    primaryColor: Color
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .background(primaryColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = primaryColor,
            modifier = Modifier.size(17.dp)
        )
    }
}

@Composable
private fun OpcionSelector(
    texto: String,
    seleccionado: Boolean,
    icono: ImageVector,
    primaryColor: Color,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = texto,
                fontSize = 13.sp,
                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                color = if (seleccionado) primaryColor else MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (seleccionado) primaryColor.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = if (seleccionado) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(17.dp)
                )
            }
        },
        trailingIcon = {
            if (seleccionado) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Seleccionado",
                    tint = primaryColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        },
        onClick = onClick,
        modifier = Modifier
            .padding(horizontal = 6.dp, vertical = 2.dp)
            .background(
                if (seleccionado) primaryColor.copy(alpha = 0.08f) else Color.Transparent,
                RoundedCornerShape(10.dp)
            )
    )
}

@Composable
private fun coloresCampo(primaryColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    focusedLabelColor = primaryColor,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedBorderColor = primaryColor,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
    cursorColor = primaryColor
)