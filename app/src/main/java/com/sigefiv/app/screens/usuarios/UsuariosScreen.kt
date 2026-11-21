package com.sigefiv.app.screens.usuarios

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sigefiv.app.data.model.CargoItem
import com.sigefiv.app.data.model.RolDetalle
import com.sigefiv.app.data.model.UsuarioListado
import com.sigefiv.app.viewmodel.UsuarioViewModel

// Tonos semánticos universales legibles
private val VerdeSemantico = Color(0xFF16A34A)
private val AmbarSemantico = Color(0xFFD97706)
private val RojoSemantico = Color(0xFFDC2626)

private enum class FiltroUsuario(val etiqueta: String) {
    TODOS("Todos"),
    DIRECTIVA("Directiva"),
    VECINOS("Vecinos"),
    PENDIENTES("Pendientes")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsuariosScreen(
    viewModel: UsuarioViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var buscar by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf(FiltroUsuario.TODOS) }
    var mostrarModalNuevoUsuario by remember { mutableStateOf(false) }
    var usuarioPendienteCambio by remember { mutableStateOf<UsuarioListado?>(null) }
    var estadoPendienteCambio by remember { mutableStateOf<String?>(null) }
    var usuarioPendienteEliminacion by remember { mutableStateOf<UsuarioListado?>(null) }
    var usuarioPendienteCambioRol by remember { mutableStateOf<UsuarioListado?>(null) }
    var rolPendienteCambio by remember { mutableStateOf<String?>(null) }
    var cargoPendienteCambio by remember { mutableStateOf<CargoItem?>(null) }

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

    // Diálogo: Confirmación de cambio de rol y cargo
    if (usuarioPendienteCambioRol != null && rolPendienteCambio != null) {
        val usuario = usuarioPendienteCambioRol!!
        val nuevoRol = rolPendienteCambio!!
        val nuevoCargo = cargoPendienteCambio

        val detalleCargoTexto = when {
            nuevoRol.equals("Secretario", ignoreCase = true) && nuevoCargo != null ->
                " con el cargo de ${nuevoCargo.nombre}"
            nuevoRol.equals("Tesorero", ignoreCase = true) ->
                " (Secretaría de Economía)"
            else ->
                " (Cargo: Vecino)"
        }

        AlertDialog(
            onDismissRequest = {
                usuarioPendienteCambioRol = null
                rolPendienteCambio = null
                cargoPendienteCambio = null
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
                    text = "¿Deseas asignar el rol $nuevoRol$detalleCargoTexto a ${formatearNombre(usuario.name)}?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.cambiarRol(
                            usuarioId = usuario.id,
                            rol = nuevoRol,
                            cargoId = if (nuevoRol.equals("Secretario", ignoreCase = true)) nuevoCargo?.id else null
                        )
                        usuarioPendienteCambioRol = null
                        rolPendienteCambio = null
                        cargoPendienteCambio = null
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
                        cargoPendienteCambio = null
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

    // Modal: Nuevo Usuario
    if (mostrarModalNuevoUsuario) {
        NuevoUsuarioDialog(
            rolesDisponibles = uiState.roles,
            cargosDisponibles = uiState.cargos,
            guardando = uiState.creandoUsuario,
            onCerrar = {
                if (!uiState.creandoUsuario) {
                    mostrarModalNuevoUsuario = false
                }
            },
            onGuardar = { nombre, correo, rol, cargoId, password, confirmarPassword ->
                viewModel.crearUsuario(
                    name = nombre,
                    email = correo,
                    role = rol,
                    cargoId = cargoId,
                    password = password,
                    passwordConfirmation = confirmarPassword,
                    onExito = {
                        mostrarModalNuevoUsuario = false
                    }
                )
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

    // Conteos para los chips de filtro rápido
    val conteosFiltro = remember(uiState.usuarios) {
        val total = uiState.usuarios.size
        val directiva = uiState.usuarios.count { it.cargoId != null && it.cargoId != 10 }
        val vecinos = uiState.usuarios.count { it.cargoId == null || it.cargoId == 10 }
        val pendientes = uiState.usuarios.count { it.estado.equals("pendiente", ignoreCase = true) }
        mapOf(
            FiltroUsuario.TODOS to total,
            FiltroUsuario.DIRECTIVA to directiva,
            FiltroUsuario.VECINOS to vecinos,
            FiltroUsuario.PENDIENTES to pendientes
        )
    }

    val usuariosFiltrados = remember(uiState.usuarios, buscar, filtroSeleccionado) {
        uiState.usuarios.filter { usuario ->
            val cumpleChip = when (filtroSeleccionado) {
                FiltroUsuario.TODOS -> true
                FiltroUsuario.DIRECTIVA -> usuario.cargoId != null && usuario.cargoId != 10
                FiltroUsuario.VECINOS -> usuario.cargoId == null || usuario.cargoId == 10
                FiltroUsuario.PENDIENTES -> usuario.estado.equals("pendiente", ignoreCase = true)
            }

            val cumpleTexto = if (buscar.isBlank()) {
                true
            } else {
                val q = buscar.trim()
                usuario.name.contains(q, ignoreCase = true) ||
                        usuario.email.contains(q, ignoreCase = true) ||
                        usuario.cargoNombre.contains(q, ignoreCase = true)
            }

            cumpleChip && cumpleTexto
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
                                text = "${uiState.usuarios.size} usuarios registrados",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Botón Nuevo Usuario integrado en la cabecera (evita tapar las tarjetas inferiores)
                        Surface(
                            onClick = {
                                viewModel.limpiarError()
                                mostrarModalNuevoUsuario = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Nuevo usuario",
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Nuevo",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = buscar,
                        onValueChange = { buscar = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Buscar por nombre, correo o cargo...",
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Chips de filtro rápido
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(FiltroUsuario.entries) { filtro ->
                            val seleccionado = filtroSeleccionado == filtro
                            val cantidad = conteosFiltro[filtro] ?: 0
                            FilterChip(
                                selected = seleccionado,
                                onClick = { filtroSeleccionado = filtro },
                                label = {
                                    Text(
                                        text = "${filtro.etiqueta} ($cantidad)",
                                        fontSize = 12.sp,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                                    selectedLabelColor = MaterialTheme.colorScheme.primary
                                )
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
                        descripcion = "Prueba con otro filtro o término de búsqueda.",
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 4.dp),
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
                                cargosDisponibles = uiState.cargos,
                                onGuardarEstado = { nuevoEstado ->
                                    usuarioPendienteCambio = usuario
                                    estadoPendienteCambio = nuevoEstado
                                },
                                onGuardarRol = { nuevoRol, nuevoCargo ->
                                    usuarioPendienteCambioRol = usuario
                                    rolPendienteCambio = nuevoRol
                                    cargoPendienteCambio = nuevoCargo
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
private fun NuevoUsuarioDialog(
    rolesDisponibles: List<RolDetalle>,
    cargosDisponibles: List<CargoItem>,
    guardando: Boolean,
    onCerrar: () -> Unit,
    onGuardar: (String, String, String, Int?, String, String) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var rolSeleccionado by remember(rolesDisponibles) {
        mutableStateOf(
            rolesDisponibles.firstOrNull { it.name.equals("Consulta", ignoreCase = true) }?.name
                ?: rolesDisponibles.firstOrNull()?.name
                ?: "Consulta"
        )
    }
    var menuRolAbierto by remember { mutableStateOf(false) }

    val secretariasDisponibles = remember(cargosDisponibles) {
        cargosDisponibles.filter { it.id != 4 && it.id != 10 }
    }
    val esSecretario = rolSeleccionado.equals("Secretario", ignoreCase = true)

    var cargoSeleccionado by remember { mutableStateOf<CargoItem?>(null) }
    var menuCargoAbierto by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmarVisible by remember { mutableStateOf(false) }

    val passwordsCoinciden = password.isNotEmpty() && password == confirmarPassword
    val cargoValido = !esSecretario || cargoSeleccionado != null
    val formularioValido = nombre.trim().isNotEmpty() &&
            correo.trim().contains("@") &&
            rolSeleccionado.isNotBlank() &&
            cargoValido &&
            password.length >= 6 &&
            passwordsCoinciden

    Dialog(
        onDismissRequest = { if (!guardando) onCerrar() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.90f, animationSpec = tween(220)),
            exit = fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.90f, animationSpec = tween(180))
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .heightIn(max = 720.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Cabecera
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Nuevo Usuario",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Registrar acceso y cargo vecinal",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(onClick = onCerrar, enabled = !guardando) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Formulario
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            enabled = !guardando,
                            label = { Text("Nombre completo") },
                            placeholder = { Text("Ej. Juan Pérez") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = correo,
                            onValueChange = { correo = it },
                            enabled = !guardando,
                            label = { Text("Correo electrónico") },
                            placeholder = { Text("vecino@correo.com") },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Selector Rol
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = rolSeleccionado,
                                onValueChange = {},
                                readOnly = true,
                                enabled = !guardando,
                                label = { Text("Rol") },
                                leadingIcon = {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable(enabled = !guardando) { menuRolAbierto = true }
                            )

                            DropdownMenu(
                                expanded = menuRolAbierto,
                                onDismissRequest = { menuRolAbierto = false }
                            ) {
                                rolesDisponibles.forEach { rol ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = rol.name,
                                                fontWeight = if (rol.name == rolSeleccionado) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            rolSeleccionado = rol.name
                                            if (!rol.name.equals("Secretario", ignoreCase = true)) {
                                                cargoSeleccionado = null
                                            }
                                            menuRolAbierto = false
                                        }
                                    )
                                }
                            }
                        }

                        // Selector Cargo: visible SOLO cuando el rol es Secretario
                        AnimatedVisibility(visible = esSecretario) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = cargoSeleccionado?.nombre ?: "Seleccionar secretaría...",
                                    onValueChange = {},
                                    readOnly = true,
                                    enabled = !guardando,
                                    label = { Text("Secretaría / Cargo") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                    },
                                    trailingIcon = {
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable(enabled = !guardando) { menuCargoAbierto = true }
                                )

                                DropdownMenu(
                                    expanded = menuCargoAbierto,
                                    onDismissRequest = { menuCargoAbierto = false }
                                ) {
                                    secretariasDisponibles.forEach { cargo ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = cargo.nombre,
                                                    fontWeight = if (cargoSeleccionado?.id == cargo.id) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                cargoSeleccionado = cargo
                                                menuCargoAbierto = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            enabled = !guardando,
                            label = { Text("Contraseña") },
                            placeholder = { Text("Mínimo 6 caracteres") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = confirmarPassword,
                            onValueChange = { confirmarPassword = it },
                            enabled = !guardando,
                            label = { Text("Confirmar contraseña") },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmarVisible = !confirmarVisible }) {
                                    Icon(
                                        imageVector = if (confirmarVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            isError = confirmarPassword.isNotEmpty() && !passwordsCoinciden,
                            supportingText = {
                                if (confirmarPassword.isNotEmpty() && !passwordsCoinciden) {
                                    Text("Las contraseñas no coinciden", color = MaterialTheme.colorScheme.error)
                                }
                            },
                            visualTransformation = if (confirmarVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    // Acciones
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = onCerrar,
                            enabled = !guardando,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = {
                                onGuardar(
                                    nombre.trim(),
                                    correo.trim(),
                                    rolSeleccionado,
                                    if (esSecretario) cargoSeleccionado?.id else null,
                                    password,
                                    confirmarPassword
                                )
                            },
                            enabled = formularioValido && !guardando,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (guardando) "Guardando..." else "Guardar",
                                fontWeight = FontWeight.Bold
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
    cargosDisponibles: List<CargoItem>,
    onGuardarEstado: (String) -> Unit,
    onGuardarRol: (String, CargoItem?) -> Unit,
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

    val estadoActual = usuario.estado ?: "pendiente"

    var estadoSeleccionado by remember(usuario.id, estadoActual) {
        mutableStateOf(estadoActual)
    }
    var menuEstadoAbierto by remember(usuario.id) { mutableStateOf(false) }

    var rolSeleccionado by remember(usuario.id, rolActual) {
        mutableStateOf(rolActual)
    }
    var menuRolAbierto by remember(usuario.id) { mutableStateOf(false) }

    val secretariasDisponibles = remember(cargosDisponibles) {
        cargosDisponibles.filter { it.id != 4 && it.id != 10 }
    }
    val esSecretario = rolSeleccionado.equals("Secretario", ignoreCase = true)

    var cargoSeleccionado by remember(usuario.id, usuario.cargoId, cargosDisponibles) {
        mutableStateOf(
            secretariasDisponibles.firstOrNull { it.id == usuario.cargoId }
        )
    }
    var menuCargoAbierto by remember(usuario.id) { mutableStateOf(false) }

    // Detección de cambios pendientes para mostrar los botones Guardar solo cuando hagan falta
    val hayCambiosRol = !rolSeleccionado.equals(rolActual, ignoreCase = true) ||
            (esSecretario && cargoSeleccionado?.id != usuario.cargoId)
    val puedeGuardarRol = hayCambiosRol && (!esSecretario || cargoSeleccionado != null)
    val hayCambiosEstado = !estadoSeleccionado.equals(estadoActual, ignoreCase = true)

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
            if (hayCambiosRol || hayCambiosEstado)
                MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            else
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

                    Spacer(modifier = Modifier.height(4.dp))

                    // Insignia de Cargo / Tipo de Residente
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                        border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = usuario.cargoNombre,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
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

            // Selector de Rol + (opcional) Selector de Cargo si es Secretario
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Desplegable Rol
                    Box(modifier = Modifier.fillMaxWidth()) {
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
                                            if (!opcionRol.name.equals("Secretario", ignoreCase = true)) {
                                                cargoSeleccionado = null
                                            }
                                            menuRolAbierto = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Desplegable Cargo: aparece SOLO cuando Rol == Secretario
                    AnimatedVisibility(visible = esSecretario) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                                border = BorderStroke(
                                    1.dp,
                                    if (cargoSeleccionado != null)
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                                    else
                                        AmbarSemantico.copy(alpha = 0.7f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { menuCargoAbierto = true }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Text(
                                        text = "Cargo:",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    Text(
                                        text = cargoSeleccionado?.nombre ?: "Seleccionar secretaría...",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (cargoSeleccionado != null) FontWeight.Bold else FontWeight.Normal,
                                        color = if (cargoSeleccionado != null)
                                            MaterialTheme.colorScheme.onSurface
                                        else
                                            AmbarSemantico,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Seleccionar cargo",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = menuCargoAbierto,
                                onDismissRequest = { menuCargoAbierto = false }
                            ) {
                                if (secretariasDisponibles.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Sin secretarías disponibles") },
                                        enabled = false,
                                        onClick = {}
                                    )
                                } else {
                                    secretariasDisponibles.forEach { opcionCargo ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = opcionCargo.nombre,
                                                    fontWeight = if (opcionCargo.id == cargoSeleccionado?.id) FontWeight.Bold else FontWeight.Normal
                                                )
                                            },
                                            onClick = {
                                                cargoSeleccionado = opcionCargo
                                                menuCargoAbierto = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Botón guardar rol/cargo: aparece solo si hay cambios pendientes
                AnimatedVisibility(
                    visible = hayCambiosRol,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    BotonAccionCircular(
                        color = if (puedeGuardarRol)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        enabled = puedeGuardarRol,
                        onClick = { onGuardarRol(rolSeleccionado, cargoSeleccionado) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Guardar rol y cargo",
                            tint = if (puedeGuardarRol)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
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

                // Botón guardar estado: aparece solo si el estado fue modificado
                AnimatedVisibility(
                    visible = hayCambiosEstado,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
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
}

@Composable
private fun BotonAccionCircular(
    color: Color,
    enabled: Boolean = true,
    onClick: () -> Unit,
    contenido: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
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

private fun formatearNombre(nombre: String): String {
    if (nombre.isBlank()) return "Usuario"
    return nombre
        .trim()
        .split("\\s+".toRegex())
        .joinToString(" ") { palabra ->
            palabra.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}