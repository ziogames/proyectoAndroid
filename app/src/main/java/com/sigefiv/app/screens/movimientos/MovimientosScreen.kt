@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.sigefiv.app.data.model.Categoria
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.Periodo
import com.sigefiv.app.notifications.NotificacionEventBus
import com.sigefiv.app.ui.components.AppBottomBar
import com.sigefiv.app.viewmodel.CategoriasViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModel
import com.sigefiv.app.viewmodel.PeriodoViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.roundToInt
import androidx.compose.ui.draw.clip

private val ColorIngreso = Color(0xFF16A34A)
private val ColorEgreso = Color(0xFFDC2626)
private val AzulEditar = Color(0xFF2563EB)

@Composable
fun MovimientosScreen(
    movimientosViewModel: MovimientosViewModel,
    categoriasViewModel: CategoriasViewModel? = null,
    periodoViewModel: PeriodoViewModel? = null,
    onMenuClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {},
    onNuevoIngresoClick: () -> Unit = {},
    onNuevoEgresoClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onMovimientoClick: (Movimiento) -> Unit = {},
    puedeCerrarPeriodo: Boolean = false,
    onCerrarPeriodoClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary

    val categoriasVM = categoriasViewModel ?: remember { CategoriasViewModel(context) }
    val periodoVM = periodoViewModel ?: remember { PeriodoViewModel(context) }

    val movimientos by movimientosViewModel.movimientos.collectAsState()
    val cargando by movimientosViewModel.cargando.collectAsState()
    val mensaje by movimientosViewModel.mensaje.collectAsState()
    val guardando by movimientosViewModel.guardando.collectAsState()
    val eliminando by movimientosViewModel.eliminando.collectAsState()

    val categorias by categoriasVM.categorias.collectAsState()
    val cargandoCategorias by categoriasVM.cargando.collectAsState()
    val mensajeCategorias by categoriasVM.mensaje.collectAsState()

    val periodo by periodoVM.periodo.collectAsState()
    val cargandoPeriodo by periodoVM.cargando.collectAsState()

    var filtroActual by remember { mutableStateOf("Todos") }
    var mostrarModalNuevoMovimiento by remember { mutableStateOf(false) }
    var mostrarFormulario by remember { mutableStateOf(false) }
    var movimientoEditar by remember { mutableStateOf<Movimiento?>(null) }
    var movimientoEliminar by remember { mutableStateOf<Movimiento?>(null) }
    var mostrarDialogoCerrarPeriodo by remember { mutableStateOf(false) }

    val handleOpenMenu = {
        onOpenDrawer()
        onMenuClick()
    }

    LaunchedEffect(Unit) {
        movimientosViewModel.cargarMovimientos()
        categoriasVM.cargarCategoriasParaMovimientos()
        periodoVM.cargarPeriodoAbierto()

        NotificacionEventBus.movimientoActualizado.collect {
            movimientosViewModel.cargarMovimientos()
            periodoVM.cargarPeriodoAbierto()
        }
    }

    val movimientosFiltrados = when (filtroActual) {
        "Ingresos" -> movimientos.filter { it.tipo.equals("Ingreso", ignoreCase = true) }
        "Egresos" -> movimientos.filter { it.tipo.equals("Egreso", ignoreCase = true) }
        else -> movimientos
    }

    val totalIngresos = movimientos
        .filter { it.tipo.equals("Ingreso", ignoreCase = true) }
        .sumOf { it.monto }

    val totalEgresos = movimientos
        .filter { it.tipo.equals("Egreso", ignoreCase = true) }
        .sumOf { it.monto }

    val movimientosPorFecha = movimientosFiltrados
        .groupBy { it.fecha ?: "Sin fecha" }
        .toSortedMap(compareByDescending { if (it == "Sin fecha") "" else it })

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Movimientos",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = handleOpenMenu) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Abrir menú",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (puedeCerrarPeriodo && periodo != null) {
                        IconButton(onClick = { mostrarDialogoCerrarPeriodo = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Cerrar período",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarModalNuevoMovimiento = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Nuevo movimiento",
                    modifier = Modifier.size(26.dp)
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                pantallaActual = "Movimientos",
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onEmprendimientosClick = onEmprendimientosClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            SeccionBalanceGeneral(
                periodoNombre = periodo?.let { "${it.nombre} ${it.anio}" },
                totalIngresos = totalIngresos,
                totalEgresos = totalEgresos,
                primaryColor = primaryColor
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Filtro(
                    texto = "Todos",
                    seleccionado = filtroActual == "Todos",
                    primaryColor = primaryColor,
                    onClick = { filtroActual = "Todos" }
                )

                Filtro(
                    texto = "Ingresos",
                    seleccionado = filtroActual == "Ingresos",
                    primaryColor = primaryColor,
                    onClick = { filtroActual = "Ingresos" }
                )

                Filtro(
                    texto = "Egresos",
                    seleccionado = filtroActual == "Egresos",
                    primaryColor = primaryColor,
                    onClick = { filtroActual = "Egresos" }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (filtroActual) {
                        "Ingresos" -> "Ingresos registrados"
                        "Egresos" -> "Egresos registrados"
                        else -> "Movimientos recientes"
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = primaryColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "${movimientosFiltrados.size}",
                        color = primaryColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            if (!mensaje.isNullOrBlank()) {
                Text(
                    text = mensaje!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (cargando && movimientos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryColor)
                }
            } else if (!cargando && movimientosFiltrados.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No hay movimientos registrados",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                movimientosPorFecha.entries.forEach { (fecha, movimientosDia) ->
                    item(key = "fecha_$fecha") {
                        FechaHeader(fecha = fecha)
                    }

                    items(
                        items = movimientosDia,
                        key = { "movimiento_${it.id}" }
                    ) { movimiento ->
                        MovimientoCard(
                            movimiento = movimiento,
                            primaryColor = primaryColor,
                            onClick = { onMovimientoClick(movimiento) },
                            onEditar = {
                                movimientosViewModel.obtenerMovimientoPorId(movimiento.id) { actualizado ->
                                    if (actualizado != null) {
                                        movimientoEditar = actualizado
                                        mostrarFormulario = true
                                        movimientosViewModel.limpiarMensaje()
                                    }
                                }
                            },
                            onEliminar = {
                                movimientoEliminar = movimiento
                                movimientosViewModel.limpiarMensaje()
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(88.dp))
                }
            }
        }
    }

    if (mostrarModalNuevoMovimiento) {
        NuevoMovimientoBottomSheet(
            onDismiss = { mostrarModalNuevoMovimiento = false },
            onIngresoClick = {
                mostrarModalNuevoMovimiento = false
                onNuevoIngresoClick()
            },
            onEgresoClick = {
                mostrarModalNuevoMovimiento = false
                onNuevoEgresoClick()
            }
        )
    }

    // Modal para editar movimiento con animación fluida
    if (mostrarFormulario) {
        NuevoMovimientoDialog(
            movimientoEditar = movimientoEditar,
            categorias = categorias,
            cargandoCategorias = cargandoCategorias,
            periodo = periodo,
            cargandoPeriodo = cargandoPeriodo,
            guardando = guardando,
            mensaje = mensaje,
            primaryColor = primaryColor,
            onCerrar = {
                if (!guardando) {
                    mostrarFormulario = false
                    movimientoEditar = null
                    movimientosViewModel.limpiarMensaje()
                }
            },
            onGuardar = { fecha, catId, conc, pers, fp, mnt, ref, obs ->
                val paraEditar = movimientoEditar
                mostrarFormulario = false
                movimientoEditar = null

                if (paraEditar != null) {
                    movimientosViewModel.actualizarMovimiento(paraEditar.id, fecha, catId, conc, pers, fp, mnt, ref, obs)
                }
            }
        )
    }

    // Modal de confirmación para eliminar con animación fluida
    movimientoEliminar?.let { movimiento ->
        Dialog(
            onDismissRequest = { if (!eliminando) movimientoEliminar = null },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.90f, animationSpec = tween(220)),
                exit = fadeOut(animationSpec = tween(180)) + scaleOut(targetScale = 0.90f, animationSpec = tween(180))
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.88f),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 6.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Eliminar movimiento",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 19.sp,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "¿Deseas eliminar el movimiento N° ${movimiento.numero}?\n\"${movimiento.concepto}\"",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TextButton(
                                onClick = { movimientoEliminar = null },
                                enabled = !eliminando,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Button(
                                onClick = {
                                    val id = movimiento.id
                                    movimientoEliminar = null
                                    movimientosViewModel.eliminarMovimiento(id)
                                },
                                enabled = !eliminando,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(if (eliminando) "Eliminando..." else "Eliminar")
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoCerrarPeriodo && periodo != null) {
        val p = periodo!!
        AlertDialog(
            onDismissRequest = { mostrarDialogoCerrarPeriodo = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("Cerrar período", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("¿Deseas cerrar el período ${p.nombre_completo}?", color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Saldo anterior: S/ %.2f".format(p.saldo_inicial), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Text("Ingresos: S/ %.2f".format(totalIngresos), color = ColorIngreso, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text("Egresos: S/ %.2f".format(totalEgresos), color = ColorEgreso, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Text(
                        "Saldo final: S/ %.2f".format(p.saldo_inicial + totalIngresos - totalEgresos),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCerrarPeriodo = false
                        onCerrarPeriodoClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text("Cerrar período")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoCerrarPeriodo = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

// ============================================================================
// COMPONENTES AUXILIARES ACTUALIZADOS
// ============================================================================

@Composable
private fun SeccionBalanceGeneral(
    periodoNombre: String?,
    totalIngresos: Double,
    totalEgresos: Double,
    primaryColor: Color
) {
    val balanceNeto = totalIngresos - totalEgresos
    val colorBalance = if (balanceNeto >= 0) ColorIngreso else ColorEgreso

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Balance del Período",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                if (!periodoNombre.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = primaryColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = periodoNombre,
                            color = primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "S/ %.2f".format(balanceNeto),
                color = colorBalance,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(ColorIngreso.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.ArrowUpward, contentDescription = null, tint = ColorIngreso, modifier = Modifier.size(15.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Ingresos", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        Text(text = "S/ %.2f".format(totalIngresos), color = ColorIngreso, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(ColorEgreso.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.ArrowDownward, contentDescription = null, tint = ColorEgreso, modifier = Modifier.size(15.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = "Egresos", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        Text(text = "S/ %.2f".format(totalEgresos), color = ColorEgreso, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun FechaHeader(fecha: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        ) {
            Text(
                text = formatearFecha(fecha),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}

private fun formatearFecha(fecha: String): String {
    if (fecha.isBlank() || fecha == "Sin fecha") return "Sin fecha"
    val partes = fecha.split("-")
    if (partes.size != 3) return fecha
    return "${partes[2]}/${partes[1]}/${partes[0]}"
}

@Composable
private fun Filtro(
    texto: String,
    seleccionado: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (seleccionado) primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(
            1.dp,
            if (seleccionado) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = texto,
            color = if (seleccionado) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

/*
|--------------------------------------------------------------------------
| MOVIMIENTO CARD CON SWIPE ANCLADO Y BOTONES REVELADOS
|--------------------------------------------------------------------------
*/

@Composable
private fun MovimientoCard(
    movimiento: Movimiento,
    primaryColor: Color,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val ingreso = movimiento.tipo.equals("Ingreso", ignoreCase = true)
    val actionWidth = 84.dp
    val actionWidthPx = with(LocalDensity.current) { actionWidth.toPx() }
    val offsetX = remember(movimiento.id) { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
    ) {
        // Capa de fondo con botones que quedan descubiertos
        Row(
            modifier = Modifier
                .matchParentSize()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón Editar (Izquierda - Azul)
            Box(
                modifier = Modifier
                    .width(actionWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp))
                    .background(AzulEditar)
                    .clickable {
                        scope.launch { offsetX.animateTo(0f, tween(200)) }
                        onEditar()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Edit, "Editar", tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Editar", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Botón Eliminar (Derecha - Rojo)
            Box(
                modifier = Modifier
                    .width(actionWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(topEnd = 14.dp, bottomEnd = 14.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .clickable {
                        scope.launch { offsetX.animateTo(0f, tween(200)) }
                        onEliminar()
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Outlined.Delete, "Eliminar", tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("Eliminar", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tarjeta principal frontal con gestos
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(movimiento.id) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val nuevoValor = (offsetX.value + dragAmount).coerceIn(-actionWidthPx, actionWidthPx)
                                offsetX.snapTo(nuevoValor)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                val objetivo = when {
                                    offsetX.value > actionWidthPx * 0.45f -> actionWidthPx
                                    offsetX.value < -actionWidthPx * 0.45f -> -actionWidthPx
                                    else -> 0f
                                }
                                offsetX.animateTo(objetivo, tween(250))
                            }
                        },
                        onDragCancel = {
                            scope.launch { offsetX.animateTo(0f, tween(200)) }
                        }
                    )
                }
                .clickable {
                    if (offsetX.value != 0f) {
                        scope.launch { offsetX.animateTo(0f, tween(200)) }
                    } else {
                        onClick()
                    }
                },
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 1.dp,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (ingreso) ColorIngreso.copy(alpha = 0.12f) else ColorEgreso.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (ingreso) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        tint = if (ingreso) ColorIngreso else ColorEgreso,
                        modifier = Modifier.size(19.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movimiento.concepto,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = movimiento.categoria ?: "Sin categoría",
                            color = primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (!movimiento.persona.isNullOrBlank()) {
                            Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                            Text(
                                text = movimiento.persona!!,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (ingreso) "+ S/ %.2f".format(movimiento.monto) else "- S/ %.2f".format(movimiento.monto),
                        color = if (ingreso) ColorIngreso else ColorEgreso,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!movimiento.forma_pago.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = movimiento.forma_pago!!,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| MODAL PARA EDITAR MOVIMIENTO (MODERNIZADO CON ANIMACIÓN)
|--------------------------------------------------------------------------
*/

@Composable
private fun NuevoMovimientoDialog(
    movimientoEditar: Movimiento? = null,
    categorias: List<Categoria>,
    cargandoCategorias: Boolean,
    periodo: Periodo?,
    cargandoPeriodo: Boolean,
    guardando: Boolean,
    mensaje: String?,
    primaryColor: Color,
    onCerrar: () -> Unit,
    onGuardar: (String, Int, String, String?, String, Double, String?, String?) -> Unit
) {
    val hoy = remember { LocalDate.now() }
    val fechaInicial = remember(periodo?.id) { periodo?.let { LocalDate.of(it.anio, it.mes, 1) } ?: hoy }

    var fecha by remember(periodo?.id) { mutableStateOf(fechaInicial.toString()) }
    var mostrarCalendario by remember { mutableStateOf(false) }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var categoriaMenuAbierto by remember { mutableStateOf(false) }
    var concepto by remember { mutableStateOf("") }
    var persona by remember { mutableStateOf("") }
    var formaPago by remember { mutableStateOf("Efectivo") }
    var formaPagoMenuAbierto by remember { mutableStateOf(false) }
    var monto by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }

    val periodoInicio = remember(periodo?.id) { periodo?.let { LocalDate.of(it.anio, it.mes, 1) } }
    val periodoFin = remember(periodo?.id) { periodoInicio?.withDayOfMonth(periodoInicio.lengthOfMonth()) }

    // Color semántico según sea ingreso o egreso
    val esEgreso = movimientoEditar?.tipo?.equals("Egreso", ignoreCase = true) == true
    val colorMonto = if (esEgreso) ColorEgreso else ColorIngreso

    val selectedDateMillis = remember(fecha) {
        runCatching {
            LocalDate.parse(fecha).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }.getOrNull()
    }

    val initialMonthMillis = remember(periodo?.id) {
        periodoInicio?.atStartOfDay(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        initialDisplayedMonthMillis = initialMonthMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                return periodoInicio != null && periodoFin != null && !date.isBefore(periodoInicio) && !date.isAfter(periodoFin)
            }
        }
    )

    LaunchedEffect(movimientoEditar?.id, categorias) {
        movimientoEditar?.let { mov ->
            fecha = mov.fecha ?: fecha
            categoriaSeleccionada = categorias.firstOrNull { it.nombre.equals(mov.categoria, ignoreCase = true) }
            concepto = mov.concepto
            persona = mov.persona.orEmpty()
            formaPago = mov.forma_pago ?: "Efectivo"
            monto = "%.2f".format(mov.monto).replace(",", ".")
            referencia = mov.referencia.orEmpty()
            observaciones = mov.observaciones.orEmpty()
        }
    }

    val formularioValido = fecha.isNotBlank() &&
            categoriaSeleccionada != null &&
            concepto.isNotBlank() &&
            formaPago.isNotBlank() &&
            monto.replace(",", ".").toDoubleOrNull() != null &&
            monto.replace(",", ".").toDoubleOrNull()!! > 0 &&
            periodo != null

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
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Editar movimiento",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = periodo?.let { "Período: ${it.nombre} ${it.anio}" } ?: "Actualizar datos",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(onClick = { if (!guardando) onCerrar() }, enabled = !guardando) {
                            Icon(Icons.Outlined.Close, contentDescription = "Cerrar", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        // =======================================================
                        // TARJETA DE MONTO PROFESIONAL DESTACADA
                        // =======================================================
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            border = BorderStroke(1.dp, colorMonto.copy(alpha = 0.35f))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (esEgreso) "Monto del egreso" else "Monto del ingreso",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = monto,
                                    onValueChange = { nuevoValor ->
                                        if (nuevoValor.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                            monto = nuevoValor
                                        }
                                    },
                                    enabled = !guardando,
                                    placeholder = {
                                        Text(
                                            "0.00",
                                            fontSize = 24.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                        )
                                    },
                                    prefix = {
                                        Text(
                                            "S/ ",
                                            color = colorMonto,
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    },
                                    textStyle = TextStyle(
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        textAlign = TextAlign.Start
                                    ),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = colorMonto,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        cursorColor = colorMonto
                                    )
                                )
                            }
                        }

                        // Fecha
                        OutlinedTextField(
                            value = fecha,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fecha") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { mostrarCalendario = true },
                            trailingIcon = {
                                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = primaryColor)
                            },
                            colors = coloresCampo()
                        )

                        // Categoría
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = categoriaSeleccionada?.nombre ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoría") },
                                placeholder = { Text(if (cargandoCategorias) "Cargando..." else "Seleccionar categoría") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Text("▼", color = primaryColor) },
                                colors = coloresCampo()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { categoriaMenuAbierto = true }
                            )

                            DropdownMenu(
                                expanded = categoriaMenuAbierto,
                                onDismissRequest = { categoriaMenuAbierto = false }
                            ) {
                                categorias.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.nombre, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            categoriaSeleccionada = cat
                                            categoriaMenuAbierto = false
                                        }
                                    )
                                }
                            }
                        }

                        // Concepto
                        CampoMovimiento(valor = concepto, onValueChange = { concepto = it }, etiqueta = "Concepto", placeholder = "Descripción")

                        // Persona
                        CampoMovimiento(valor = persona, onValueChange = { persona = it }, etiqueta = "Persona", placeholder = "Opcional", requerido = false)

                        // Forma de pago
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formaPago,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Forma de pago") },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = { Text("▼", color = primaryColor) },
                                colors = coloresCampo()
                            )
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable { formaPagoMenuAbierto = true }
                            )

                            DropdownMenu(
                                expanded = formaPagoMenuAbierto,
                                onDismissRequest = { formaPagoMenuAbierto = false }
                            ) {
                                listOf("Efectivo", "Yape", "Plin", "Transferencia", "Depósito", "Otro").forEach { opcion ->
                                    DropdownMenuItem(
                                        text = { Text(opcion) },
                                        onClick = {
                                            formaPago = opcion
                                            formaPagoMenuAbierto = false
                                        }
                                    )
                                }
                            }
                        }

                        // Referencia
                        CampoMovimiento(valor = referencia, onValueChange = { referencia = it }, etiqueta = "Referencia", placeholder = "Opcional", requerido = false)

                        // Observaciones
                        CampoMovimiento(valor = observaciones, onValueChange = { observaciones = it }, etiqueta = "Observaciones", placeholder = "Opcional", requerido = false, singleLine = false, minLines = 2)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(onClick = onCerrar, enabled = !guardando, modifier = Modifier.weight(1f)) {
                            Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = {
                                val cat = categoriaSeleccionada
                                val mnt = monto.replace(",", ".").toDoubleOrNull()
                                if (cat != null && mnt != null && mnt > 0.0) {
                                    onGuardar(fecha, cat.id, concepto.trim(), persona.trim().ifBlank { null }, formaPago, mnt, referencia.trim().ifBlank { null }, observaciones.trim().ifBlank { null })
                                }
                            },
                            enabled = formularioValido && !guardando,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (guardando) "Guardando..." else "Guardar", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (mostrarCalendario && periodoInicio != null && periodoFin != null) {
        DatePickerDialog(
            onDismissRequest = { if (!guardando) mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val seleccion = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                            if (!seleccion.isBefore(periodoInicio) && !seleccion.isAfter(periodoFin)) {
                                fecha = seleccion.toString()
                                mostrarCalendario = false
                            }
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text(text = "Aceptar", color = primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text(text = "Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Fecha del movimiento",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                headline = {
                    Text(
                        text = "${periodo?.nombre ?: ""} ${periodo?.anio ?: ""}",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        fontWeight = FontWeight.Bold
                    )
                },
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun CampoMovimiento(
    valor: String,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    placeholder: String,
    requerido: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(if (requerido) etiqueta else "$etiqueta (opcional)") },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        colors = coloresCampo()
    )
}

@Composable
private fun coloresCampo() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary
)