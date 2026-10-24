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
import androidx.compose.material3.MaterialTheme
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
import com.sigefiv.app.data.model.RolDetalle
import com.sigefiv.app.data.model.UsuarioListado
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.UsuarioViewModel
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.text.style.TextAlign
private val VerdeClaroContraste = Color(0xFFCCFBF1)

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
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val colorPrincipalOscuro = colorPrincipal.copy(alpha = 0.88f)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var buscar by remember { mutableStateOf("") }

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
        viewModel.cargarRoles()
    }

    // Confirmación de cambio de estado.
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
                    text = "Confirmar cambio de estado",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Deseas cambiar el estado de ${usuario.name} a " +
                            "${nuevoEstado.replaceFirstChar { it.uppercase() }}?"
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

    // Confirmación de cambio de rol.
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
                Text("¿Deseas asignar el rol $nuevoRol a ${usuario.name}?")
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

    // Confirmación de eliminación.
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
                    "¿Estás seguro de que deseas eliminar a ${usuario.name}? " +
                            "Esta acción no se puede deshacer."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarUsuario(usuario.id)
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

    // Diálogo de error.
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarError() },
            icon = {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = BloqueadoTexto
                )
            },
            title = {
                Text(
                    text = "No se pudo completar la acción",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(uiState.error ?: "Ocurrió un error inesperado.")
            },
            confirmButton = {
                Button(onClick = { viewModel.limpiarError() }) {
                    Text("Aceptar")
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Encabezado.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                colorPrincipal,
                                colorPrincipalOscuro
                            )
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.18f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gestión de usuarios",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Control de accesos de SIGEFIV",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.88f)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color.White.copy(alpha = 0.18f),
                        border = BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.25f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 7.dp
                            ),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = uiState.usuarios.size.toString(),
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "usuarios",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Contenido principal.
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Directorio de usuarios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Busca personas y administra sus permisos y estados.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = buscar,
                    onValueChange = { buscar = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Nombre o correo electrónico",
                            fontSize = 14.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar",
                            tint = colorPrincipal
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = colorPrincipal,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                when {
                    uiState.cargando -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator(
                                    color = colorPrincipal,
                                    strokeWidth = 3.dp
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Cargando usuarios...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    uiState.usuarios.isEmpty() -> {
                        EstadoVacioUsuarios(
                            titulo = "No hay usuarios",
                            descripcion = "Todavía no hay usuarios para mostrar.",
                            colorPrincipal = colorPrincipal,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    else -> {
                        val usuariosFiltrados =
                            if (buscar.isBlank()) {
                                uiState.usuarios
                            } else {
                                uiState.usuarios.filter { usuario ->
                                    usuario.name.contains(
                                        buscar.trim(),
                                        ignoreCase = true
                                    ) || usuario.email.contains(
                                        buscar.trim(),
                                        ignoreCase = true
                                    )
                                }
                            }

                        if (usuariosFiltrados.isEmpty()) {
                            EstadoVacioUsuarios(
                                titulo = "Sin resultados",
                                descripcion = "Prueba con otro nombre o correo.",
                                colorPrincipal = colorPrincipal,
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Text(
                                text = "${usuariosFiltrados.size} resultado(s)",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(
                                    items = usuariosFiltrados,
                                    key = { it.id }
                                ) { usuario ->
                                    UsuarioTarjetaProfional(
                                        usuario = usuario,
                                        rolesDisponibles = uiState.roles,
                                        colorPrincipal = colorPrincipal,
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
private fun EstadoVacioUsuarios(
    titulo: String,
    descripcion: String,
    colorPrincipal: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = colorPrincipal.copy(alpha = 0.10f),
                modifier = Modifier.size(76.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = colorPrincipal,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun UsuarioTarjetaProfional(
    usuario: UsuarioListado,
    rolesDisponibles: List<RolDetalle>,
    colorPrincipal: Color,
    onGuardarEstado: (String) -> Unit,
    onGuardarRol: (String) -> Unit,
    onEliminarUsuario: () -> Unit
) {
    val nombre = usuario.name.ifBlank { "Usuario" }
    val inicial = nombre.trim().firstOrNull()?.uppercase() ?: "U"

    val rolActual = usuario.roles
        .firstOrNull()
        ?.name
        ?.takeIf { it.isNotBlank() }
        ?: "Consulta"

    var estadoSeleccionado by remember(usuario.id, usuario.estado) {
        mutableStateOf(usuario.estado ?: "pendiente")
    }

    var menuEstadoAbierto by remember(usuario.id) {
        mutableStateOf(false)
    }

    var rolSeleccionado by remember(usuario.id, rolActual) {
        mutableStateOf(rolActual)
    }

    var menuRolAbierto by remember(usuario.id) {
        mutableStateOf(false)
    }

    val (bgColor, textColor, borderColor) =
        when (estadoSeleccionado.lowercase()) {
            "activo" -> Triple(ActivoBg, ActivoTexto, ActivoBorde)
            "bloqueado" -> Triple(BloqueadoBg, BloqueadoTexto, BloqueadoBorde)
            else -> Triple(PendienteBg, PendienteTexto, PendienteBorde)
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Datos del usuario.
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        color = VerdeClaroContraste,
                        border = BorderStroke(
                            2.dp,
                            colorPrincipal.copy(alpha = 0.20f)
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = inicial,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorPrincipal
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(textColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = usuario.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de rol y botón guardar en su propia fila.
            Text(
                text = "ROL DEL USUARIO",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.7.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { menuRolAbierto = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 11.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = colorPrincipal,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = rolSeleccionado,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar rol",
                                tint = colorPrincipal
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuRolAbierto,
                        onDismissRequest = { menuRolAbierto = false }
                    ) {
                        if (rolesDisponibles.isEmpty()) {
                            DropdownMenuItem(
                                text = {
                                    Text("Cargando roles...")
                                },
                                enabled = false,
                                onClick = {}
                            )
                        } else {
                            rolesDisponibles.forEach { opcionRol ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = opcionRol.name,
                                            fontWeight = if (
                                                opcionRol.name == rolSeleccionado
                                            ) {
                                                FontWeight.Bold
                                            } else {
                                                FontWeight.Normal
                                            }
                                        )
                                    },
                                    onClick = {
                                        rolSeleccionado = opcionRol.name
                                        menuRolAbierto = false
                                    }
                                )
                            }
                        }
                    }
                }

                BotonAccionCircular(
                    descripcion = "Guardar rol",
                    color = colorPrincipal,
                    onClick = { onGuardarRol(rolSeleccionado) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Selector de estado y acciones en una segunda fila.
            Text(
                text = "ESTADO DE LA CUENTA",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.7.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = bgColor,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { menuEstadoAbierto = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(
                                horizontal = 12.dp,
                                vertical = 11.dp
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = estadoSeleccionado.replaceFirstChar {
                                    it.uppercase()
                                },
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar estado",
                                tint = textColor
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuEstadoAbierto,
                        onDismissRequest = { menuEstadoAbierto = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Activo",
                                    color = ActivoTexto,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                estadoSeleccionado = "activo"
                                menuEstadoAbierto = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Pendiente",
                                    color = PendienteTexto,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                estadoSeleccionado = "pendiente"
                                menuEstadoAbierto = false
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Bloqueado",
                                    color = BloqueadoTexto,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            onClick = {
                                estadoSeleccionado = "bloqueado"
                                menuEstadoAbierto = false
                            }
                        )
                    }
                }

                BotonAccionCircular(
                    descripcion = "Guardar estado",
                    color = colorPrincipal,
                    onClick = { onGuardarEstado(estadoSeleccionado) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }

                BotonAccionCircular(
                    descripcion = "Eliminar usuario",
                    color = BloqueadoTexto,
                    onClick = onEliminarUsuario
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonAccionCircular(
    descripcion: String,
    color: Color,
    onClick: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = color,
        shadowElevation = 2.dp,
        modifier = Modifier.size(42.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            contenido()
        }
    }
}
