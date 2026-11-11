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
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sigefiv.app.data.model.RolDetalle
import com.sigefiv.app.data.model.UsuarioListado
import com.sigefiv.app.viewmodel.UsuarioViewModel

// Tonos semánticos universales legibles
private val VerdeSemantico = Color(0xFF16A34A)
private val AmbarSemantico = Color(0xFFD97706)
private val RojoSemantico = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    viewModel: UsuarioViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var buscar by remember { mutableStateOf("") }
    var usuarioPendienteCambio by remember { mutableStateOf<UsuarioListado?>(null) }
    var estadoPendienteCambio by remember { mutableStateOf<String?>(null) }
    var usuarioPendienteEliminacion by remember { mutableStateOf<UsuarioListado?>(null) }
    var usuarioPendienteCambioRol by remember { mutableStateOf<UsuarioListado?>(null) }
    var rolPendienteCambio by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.cargarUsuarios()
        viewModel.cargarRoles()
    }

    // Diálogo: Confirmación de cambio de estado
    if (usuarioPendienteCambio != null && estadoPendienteCambio != null) {
        val usuario = usuarioPendienteCambio!!
        val nuevoEstado = estadoPendienteCambio!!

        AlertDialog(
            onDismissRequest = {
                usuarioPendienteCambio = null
                estadoPendienteCambio = null
            },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Confirmar cambio de estado",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "¿Deseas cambiar el estado de ${formatearNombre(usuario.name)} a ${nuevoEstado.replaceFirstChar { it.uppercase() }}?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.actualizarEstado(usuarioId = usuario.id, nuevoEstado = nuevoEstado)
                        usuarioPendienteCambio = null
                        estadoPendienteCambio = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
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
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Diálogo: Confirmación de cambio de rol
    if (usuarioPendienteCambioRol != null && rolPendienteCambio != null) {
        val usuario = usuarioPendienteCambioRol!!
        val nuevoRol = rolPendienteCambio!!

        AlertDialog(
            onDismissRequest = {
                usuarioPendienteCambioRol = null
                rolPendienteCambio = null
            },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Confirmar cambio de rol",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "¿Deseas asignar el rol $nuevoRol a ${formatearNombre(usuario.name)}?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cambiarRol(usuarioId = usuario.id, rol = nuevoRol)
                        usuarioPendienteCambioRol = null
                        rolPendienteCambio = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
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
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Diálogo: Confirmación de eliminación
    if (usuarioPendienteEliminacion != null) {
        val usuario = usuarioPendienteEliminacion!!

        AlertDialog(
            onDismissRequest = { usuarioPendienteEliminacion = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Eliminar usuario",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar a ${formatearNombre(usuario.name)}? Esta acción no se puede deshacer.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminarUsuario(usuario.id)
                        usuarioPendienteEliminacion = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { usuarioPendienteEliminacion = null }
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Diálogo: Error
    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.limpiarError() },
            containerColor = MaterialTheme.colorScheme.surface,
            icon = {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "No se pudo completar la acción",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = uiState.error ?: "Ocurrió un error inesperado.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.limpiarError() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Aceptar")
                }
            }
        )
    }

    val usuariosFiltrados = remember(uiState.usuarios, buscar) {
        if (buscar.isBlank()) {
            uiState.usuarios
        } else {
            uiState.usuarios.filter { usuario ->
                usuario.name.contains(buscar.trim(), ignoreCase = true) ||
                        usuario.email.contains(buscar.trim(), ignoreCase = true)
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = onBackClick,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Gestión de usuarios",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Control de accesos y roles",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = uiState.usuarios.size.toString(),
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "usuarios",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = buscar,
                        onValueChange = { buscar = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Buscar por nombre o correo...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            if (buscar.isNotEmpty()) {
                                IconButton(onClick = { buscar = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Limpiar búsqueda",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            when {
                uiState.cargando -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary,
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Cargando usuarios...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                uiState.usuarios.isEmpty() -> {
                    EstadoVacioUsuarios(
                        titulo = "No hay usuarios",
                        descripcion = "Todavía no hay usuarios para mostrar.",
                        modifier = Modifier.weight(1f)
                    )
                }

                usuariosFiltrados.isEmpty() -> {
                    EstadoVacioUsuarios(
                        titulo = "Sin resultados",
                        descripcion = "Prueba con otro nombre o correo.",
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Directorio",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${usuariosFiltrados.size} resultado(s)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 24.dp)
                    ) {
                        items(
                            items = usuariosFiltrados,
                            key = { it.id }
                        ) { usuario ->
                            UsuarioTarjetaProfional(
                                usuario = usuario,
                                rolesDisponibles = uiState.roles,
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

@Composable
private fun EstadoVacioUsuarios(
    titulo: String,
    descripcion: String,
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
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

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
    onGuardarEstado: (String) -> Unit,
    onGuardarRol: (String) -> Unit,
    onEliminarUsuario: () -> Unit
) {
    val nombreFormateado = remember(usuario.name) {
        formatearNombre(usuario.name)
    }
    val inicial = nombreFormateado.trim().firstOrNull()?.uppercase() ?: "U"

    val rolActual = usuario.roles
        .firstOrNull()
        ?.name
        ?.takeIf { it.isNotBlank() }
        ?: "Consulta"

    var estadoSeleccionado by remember(usuario.id, usuario.estado) {
        mutableStateOf(usuario.estado ?: "pendiente")
    }
    var menuEstadoAbierto by remember(usuario.id) { mutableStateOf(false) }

    var rolSeleccionado by remember(usuario.id, rolActual) {
        mutableStateOf(rolActual)
    }
    var menuRolAbierto by remember(usuario.id) { mutableStateOf(false) }

    val (bgColor, textColor, borderColor) = when (estadoSeleccionado.lowercase()) {
        "activo" -> Triple(
            VerdeSemantico.copy(alpha = 0.12f),
            VerdeSemantico,
            VerdeSemantico.copy(alpha = 0.35f)
        )
        "bloqueado" -> Triple(
            RojoSemantico.copy(alpha = 0.12f),
            RojoSemantico,
            RojoSemantico.copy(alpha = 0.35f)
        )
        else -> Triple(
            AmbarSemantico.copy(alpha = 0.12f),
            AmbarSemantico,
            AmbarSemantico.copy(alpha = 0.35f)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar circular
                Box {
                    Surface(
                        modifier = Modifier.size(42.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = inicial,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Indicador de estado
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(11.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(1.5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(textColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = nombreFormateado,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = usuario.email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Botón eliminar
                Surface(
                    onClick = onEliminarUsuario,
                    shape = CircleShape,
                    color = RojoSemantico.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, RojoSemantico.copy(alpha = 0.25f)),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar usuario",
                            tint = RojoSemantico,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
            )

            // Selector de Rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { menuRolAbierto = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Rol:",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = rolSeleccionado,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar rol",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = menuRolAbierto,
                        onDismissRequest = { menuRolAbierto = false }
                    ) {
                        if (rolesDisponibles.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("Cargando roles...") },
                                enabled = false,
                                onClick = {}
                            )
                        } else {
                            rolesDisponibles.forEach { opcionRol ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = opcionRol.name,
                                            fontWeight = if (opcionRol.name == rolSeleccionado) FontWeight.Bold else FontWeight.Normal
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
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onGuardarRol(rolSeleccionado) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar rol",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = bgColor,
                        border = BorderStroke(1.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { menuEstadoAbierto = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(textColor)
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Text(
                                text = "Estado:",
                                style = MaterialTheme.typography.labelMedium,
                                color = textColor.copy(alpha = 0.85f)
                            )

                            Spacer(modifier = Modifier.width(5.dp))

                            Text(
                                text = estadoSeleccionado.replaceFirstChar { it.uppercase() },
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Seleccionar estado",
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
                            text = {
                                Text(
                                    text = "Activo",
                                    color = VerdeSemantico,
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
                                    color = AmbarSemantico,
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
                                    color = RojoSemantico,
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
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onGuardarEstado(estadoSeleccionado) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Guardar estado",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonAccionCircular(
    color: Color,
    onClick: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = color,
        modifier = Modifier.size(36.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            contenido()
        }
    }
}

/**
 * Función auxiliar para formatear cadenas a Title Case limpio.
 */
private fun formatearNombre(nombre: String): String {
    if (nombre.isBlank()) return "Usuario"
    return nombre
        .trim()
        .split("\\s+".toRegex())
        .joinToString(" ") { palabra ->
            palabra.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}