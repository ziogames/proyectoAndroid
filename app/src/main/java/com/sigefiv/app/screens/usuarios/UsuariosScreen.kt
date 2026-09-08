package com.sigefiv.app.screens.usuarios

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sigefiv.app.data.model.UsuarioListado
import com.sigefiv.app.viewmodel.UsuarioViewModel

// Paleta Oficial SIGEFIV (Verde Corporativo & Neutros)
private val VerdePrincipal = Color(0xFF0F766E)
private val VerdeGradienteFin = Color(0xFF115E59)
private val VerdeClaroContraste = Color(0xFFCCFBF1)
private val FondoSuperficie = Color(0xFFF8FAFC)
private val TextoTitulos = Color(0xFF0F172A)
private val TextoSecundario = Color(0xFF64748B)

// Paleta Semántica de Estados
private val ActivoBg = Color(0xFFDCFCE7)
private val ActivoTexto = Color(0xFF15803D)
private val ActivoBorde = Color(0xFF86EFAC)

private val PendienteBg = Color(0xFFFEF3C7)
private val PendienteTexto = Color(0xFFB45309)
private val PendienteBorde = Color(0xFFFCD34D)

private val BloqueadoBg = Color(0xFFFEE2E2)
private val BloqueadoTexto = Color(0xFFB91C1C)
private val BloqueadoBorde = Color(0xFFFCA5A5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    viewModel: UsuarioViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var buscar by remember {
        mutableStateOf("")
    }

    var usuarioPendienteCambio by remember {
        mutableStateOf<UsuarioListado?>(null)
    }

    var estadoPendienteCambio by remember {
        mutableStateOf<String?>(null)
    }

    var usuarioPendienteEliminacion by remember {
        mutableStateOf<UsuarioListado?>(null)
    }

    var usuarioPendienteCambioRol by remember {
        mutableStateOf<UsuarioListado?>(null)
    }

    var rolPendienteCambio by remember {
        mutableStateOf<String?>(null)
    }

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
    }

    /*
     * MODAL DE ERROR
     */
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.limpiarError()
            },
            title = {
                Text(
                    text = "No se puede realizar la acción",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = uiState.error
                        ?: "No se pudo realizar el cambio."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.limpiarError()
                    }
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    /*
     * MODAL DE CONFIRMACIÓN ESTADO
     */
    if (
        usuarioPendienteCambio != null &&
        estadoPendienteCambio != null
    ) {
        val usuario = usuarioPendienteCambio!!
        val nuevoEstado = estadoPendienteCambio!!

        AlertDialog(
            onDismissRequest = {
                usuarioPendienteCambio = null
                estadoPendienteCambio = null
            },
            title = {
                Text(
                    text = "Confirmar cambio",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Deseas cambiar el estado de " +
                            "${usuario.name} a " +
                            nuevoEstado.replaceFirstChar {
                                it.uppercase()
                            } + "?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.actualizarEstado(
                            usuarioId = usuario.id,
                            nuevoEstado = nuevoEstado
                        )
                        usuarioPendienteCambio = null
                        estadoPendienteCambio = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        usuarioPendienteCambio = null
                        estadoPendienteCambio = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    /*
     * MODAL DE CONFIRMACIÓN DE CAMBIO DE ROL
     */
    if (
        usuarioPendienteCambioRol != null &&
        rolPendienteCambio != null
    ) {
        val usuario = usuarioPendienteCambioRol!!
        val nuevoRol = rolPendienteCambio!!

        AlertDialog(
            onDismissRequest = {
                usuarioPendienteCambioRol = null
                rolPendienteCambio = null
            },
            title = {
                Text(
                    text = "Confirmar cambio de rol",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Deseas cambiar el rol de " +
                            "${usuario.name} a $nuevoRol?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cambiarRol(
                            usuarioId = usuario.id,
                            rol = nuevoRol
                        )
                        usuarioPendienteCambioRol = null
                        rolPendienteCambio = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        usuarioPendienteCambioRol = null
                        rolPendienteCambio = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    /*
     * MODAL DE CONFIRMACIÓN DE ELIMINACIÓN
     */
    if (usuarioPendienteEliminacion != null) {
        val usuario = usuarioPendienteEliminacion!!

        AlertDialog(
            onDismissRequest = {
                usuarioPendienteEliminacion = null
            },
            title = {
                Text(
                    text = "Eliminar usuario",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar " +
                            "${usuario.name}? Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarUsuario(
                            usuarioId = usuario.id
                        )
                        usuarioPendienteEliminacion = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        usuarioPendienteEliminacion = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VerdePrincipal)
    ) {
        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    // Aplicamos solo el padding bottom del scaffold para no duplicar el padding de la barra superior
                    .padding(bottom = paddingValues.calculateBottomPadding())
            ) {
                /*
                 * ENCABEZADO TIPO DASHBOARD
                 */
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 6.dp,
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        VerdePrincipal,
                                        VerdeGradienteFin
                                    )
                                )
                            )
                            .statusBarsPadding()
                            .padding(
                                horizontal = 20.dp,
                                vertical = 12.dp // Reducido a 12.dp para disminuir espacio vertical
                            )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                onClick = onBackClick,
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.18f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Gestión de Usuarios",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Panel de control de accesos SIGEFIV",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            // BADGE DE CONTEO
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.2f),
                                border = BorderStroke(
                                    1.dp,
                                    Color.White.copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${uiState.usuarios.size}",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "total",
                                        color = Color.White.copy(alpha = 0.8f),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                /*
                 * CUERPO PRINCIPAL
                 */
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(FondoSuperficie)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(18.dp))

                    /*
                     * BARRA DE BÚSQUEDA
                     */
                    OutlinedTextField(
                        value = buscar,
                        onValueChange = { buscar = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Buscar por nombre, apellido o correo...",
                                color = TextoSecundario,
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = VerdePrincipal,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = VerdePrincipal,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedTextColor = TextoTitulos,
                            unfocusedTextColor = TextoTitulos
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    when {
                        uiState.cargando -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = VerdePrincipal,
                                        strokeWidth = 3.dp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Cargando directorio...",
                                        color = TextoSecundario,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        uiState.error != null -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.ErrorOutline,
                                        contentDescription = null,
                                        tint = BloqueadoTexto,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = uiState.error ?: "Error al obtener usuarios",
                                        color = BloqueadoTexto,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        uiState.usuarios.isEmpty() -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.PersonSearch,
                                        contentDescription = null,
                                        tint = TextoSecundario,
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "No se encontraron usuarios registrados",
                                        color = TextoSecundario,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }

                        else -> {
                            val usuariosFiltrados = if (buscar.isBlank()) {
                                uiState.usuarios
                            } else {
                                uiState.usuarios.filter { usuario ->
                                    usuario.name.contains(buscar, ignoreCase = true) ||
                                            usuario.email.contains(buscar, ignoreCase = true)
                                }
                            }

                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(
                                    items = usuariosFiltrados,
                                    key = { usuario -> usuario.id }
                                ) { usuario ->
                                    UsuarioTarjetaProfional(
                                        usuario = usuario,
                                        onGuardarEstado = { nuevoEstado ->
                                            usuarioPendienteCambio = usuario
                                            estadoPendienteCambio = nuevoEstado
                                        },
                                        onGuardarRol = { nuevoRol ->
                                            usuarioPendienteCambioRol = usuario
                                            rolPendienteCambio = nuevoRol
                                        },
                                        onEliminarUsuario = {
                                            usuarioPendienteEliminacion = usuario
                                        }
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

@Composable
private fun UsuarioTarjetaProfional(
    usuario: UsuarioListado,
    onGuardarEstado: (String) -> Unit,
    onGuardarRol: (String) -> Unit,
    onEliminarUsuario: () -> Unit
) {
    val nombre = usuario.name.ifBlank { "Usuario" }
    val inicial = nombre.trim().firstOrNull()?.uppercase() ?: "U"

    val rol = usuario.roles
        .firstOrNull()
        ?.name
        ?.takeIf { it.isNotBlank() }
        ?: "Consulta"

    var estadoSeleccionado by remember(usuario.id, usuario.estado) {
        mutableStateOf(usuario.estado ?: "pendiente")
    }

    var menuEstadoAbierto by remember { mutableStateOf(false) }

    var rolSeleccionado by remember(usuario.id, rol) {
        mutableStateOf(rol)
    }

    var menuRolAbierto by remember { mutableStateOf(false) }

    val (bgColor, textColor, borderColor) = when (estadoSeleccionado.lowercase()) {
        "activo" -> Triple(ActivoBg, ActivoTexto, ActivoBorde)
        "bloqueado" -> Triple(BloqueadoBg, BloqueadoTexto, BloqueadoBorde)
        else -> Triple(PendienteBg, PendienteTexto, PendienteBorde)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                /*
                 * AVATAR CON ANILLO DE ESTADO
                 */
                Box {
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = CircleShape,
                        color = VerdeClaroContraste,
                        border = BorderStroke(2.dp, VerdePrincipal.copy(alpha = 0.2f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = inicial,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdePrincipal
                            )
                        }
                    }

                    // Punto indicador de estado
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(2.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(textColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = nombre,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextoTitulos,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = TextoSecundario
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = usuario.email,
                            fontSize = 12.sp,
                            color = TextoSecundario,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            /*
             * SECCIÓN INFERIOR: ROL + ESTADO + GUARDAR
             */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // SELECTOR DE ROL
                Box {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { menuRolAbierto = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                tint = VerdePrincipal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = rolSeleccionado,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = VerdePrincipal,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Cambiar rol",
                                tint = VerdePrincipal,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuRolAbierto,
                        onDismissRequest = { menuRolAbierto = false }
                    ) {
                        listOf("Administrador", "Tesorero", "Secretario", "Consulta").forEach { opcionRol ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = opcionRol,
                                        fontWeight = if (opcionRol == rolSeleccionado) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    rolSeleccionado = opcionRol
                                    menuRolAbierto = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // BOTÓN GUARDAR ROL
                    Surface(
                        onClick = { onGuardarRol(rolSeleccionado) },
                        shape = CircleShape,
                        color = VerdePrincipal,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Guardar rol",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    /*
                     * SELECTOR DE ESTADO
                     */
                    Box {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = bgColor,
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { menuEstadoAbierto = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = estadoSeleccionado.replaceFirstChar { it.uppercase() },
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Cambiar estado",
                                    tint = textColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = menuEstadoAbierto,
                            onDismissRequest = { menuEstadoAbierto = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Activo", fontWeight = FontWeight.SemiBold, color = ActivoTexto) },
                                onClick = {
                                    estadoSeleccionado = "activo"
                                    menuEstadoAbierto = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Pendiente", fontWeight = FontWeight.SemiBold, color = PendienteTexto) },
                                onClick = {
                                    estadoSeleccionado = "pendiente"
                                    menuEstadoAbierto = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Bloqueado", fontWeight = FontWeight.SemiBold, color = BloqueadoTexto) },
                                onClick = {
                                    estadoSeleccionado = "bloqueado"
                                    menuEstadoAbierto = false
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // BOTÓN GUARDAR ESTADO
                    Surface(
                        onClick = { onGuardarEstado(estadoSeleccionado) },
                        shape = CircleShape,
                        color = VerdePrincipal,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Guardar",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // BOTÓN ELIMINAR USUARIO
                    Surface(
                        onClick = onEliminarUsuario,
                        shape = CircleShape,
                        color = BloqueadoTexto,
                        shadowElevation = 2.dp,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Eliminar usuario",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}