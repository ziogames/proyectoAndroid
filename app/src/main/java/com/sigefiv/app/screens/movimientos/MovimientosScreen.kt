@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import kotlin.math.roundToInt
import com.sigefiv.app.data.model.Categoria
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.Periodo
import com.sigefiv.app.viewmodel.CategoriasViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModel
import com.sigefiv.app.viewmodel.PeriodoViewModel
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import androidx.compose.material.icons.outlined.Lock


/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV (ESTILO DASHBOARD)
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)

private val AzulEditar = Color(0xFF2563EB)
private val VerdeSuave = Color(0xFFDCFCE7)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisClaro = Color(0xFF64748B)
private val Verde = Color(0xFF15803D)
private val Rojo = Color(0xFFDC2626)
private val FondoDetalle = Color(0xFFFFFFFF)
private val FondoFecha = Color(0xFFE2E8F0)

@Composable
fun MovimientosScreen(
    movimientosViewModel: MovimientosViewModel,
    categoriasViewModel: CategoriasViewModel? = null,
    periodoViewModel: PeriodoViewModel? = null,
    onMenuClick: () -> Unit = {},
    onNotificacionesClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {},
    onNuevoMovimientoClick: () -> Unit = {},
    onSigiClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onMovimientoClick: (Movimiento) -> Unit = {},
    puedeCerrarPeriodo: Boolean = false,
    onCerrarPeriodoClick: () -> Unit = {}

) {
    val context = LocalContext.current

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
    var mostrarFormulario by remember { mutableStateOf(false) }
    var movimientoEditar by remember { mutableStateOf<Movimiento?>(null) }
    var movimientoEliminar by remember { mutableStateOf<Movimiento?>(null) }
    var mostrarDialogoCerrarPeriodo by remember {
        mutableStateOf(false)
    }

    val handleOpenMenu = {
        onOpenDrawer()
        onMenuClick()
    }

    LaunchedEffect(Unit) {
        movimientosViewModel.cargarMovimientos()
        categoriasVM.cargarCategorias()
        periodoVM.cargarPeriodoAbierto()
    }

    /*
    |--------------------------------------------------------------------------
    | FILTRO
    |--------------------------------------------------------------------------
    */

    val movimientosFiltrados = when (filtroActual) {
        "Ingresos" -> movimientos.filter { it.tipo.equals("Ingreso", ignoreCase = true) }
        "Egresos" -> movimientos.filter { it.tipo.equals("Egreso", ignoreCase = true) }
        else -> movimientos
    }

    /*
    |--------------------------------------------------------------------------
    | TOTALES
    |--------------------------------------------------------------------------
    */

    val totalIngresos = movimientos
        .filter { it.tipo.equals("Ingreso", ignoreCase = true) }
        .sumOf { it.monto }

    val totalEgresos = movimientos
        .filter { it.tipo.equals("Egreso", ignoreCase = true) }
        .sumOf { it.monto }

    /*
    |--------------------------------------------------------------------------
    | MOVIMIENTOS AGRUPADOS POR FECHA
    |--------------------------------------------------------------------------
    */

    val movimientosPorFecha = movimientosFiltrados
        .groupBy { it.fecha ?: "Sin fecha" }
        .toSortedMap(
            compareByDescending { fecha ->
                if (fecha == "Sin fecha") "" else fecha
            }
        )

    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Movimientos",
                        color = Blanco,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = handleOpenMenu) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Abrir menú",
                            tint = Blanco
                        )
                    }
                },
                actions = {

                    if (puedeCerrarPeriodo && periodo != null) {
                        IconButton(
                            onClick = {
                                mostrarDialogoCerrarPeriodo = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Cerrar período",
                                tint = Blanco
                            )
                        }
                    }

                    IconButton(onClick = onSigiClick) {
                        Icon(
                            imageVector = Icons.Outlined.SmartToy,
                            contentDescription = "SIGI",
                            tint = Blanco
                        )
                    }

                    IconButton(onClick = onNotificacionesClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Blanco
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevoMovimientoClick,
                containerColor = SeasonalColors.primary(
                    SeasonalTheme.getSeason()
                ),
                contentColor = Blanco
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Agregar movimiento"
                )
            }
        },
        bottomBar = {
            BarraInferiorMovimientos(
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoSIGEFIV)
                .padding(innerPadding)
        ) {

            /*
            |--------------------------------------------------------------------------
            | RESUMEN DE INGRESOS Y EGRESOS
            |--------------------------------------------------------------------------
            */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResumenTotalCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Ingresos",
                    monto = totalIngresos,
                    color = Verde
                )

                ResumenTotalCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Egresos",
                    monto = totalEgresos,
                    color = Rojo
                )
            }

            /*
            |--------------------------------------------------------------------------
            | FILTROS
            |--------------------------------------------------------------------------
            */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Filtro(
                    texto = "Todos",
                    seleccionado = filtroActual == "Todos",
                    onClick = { filtroActual = "Todos" }
                )

                Filtro(
                    texto = "Ingresos",
                    seleccionado = filtroActual == "Ingresos",
                    onClick = { filtroActual = "Ingresos" }
                )

                Filtro(
                    texto = "Egresos",
                    seleccionado = filtroActual == "Egresos",
                    onClick = { filtroActual = "Egresos" }
                )

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.FilterList,
                        contentDescription = "Filtros",
                        tint = SeasonalColors.primary(
                            SeasonalTheme.getSeason()
                        )
                    )
                }
            }

            /*
            |--------------------------------------------------------------------------
            | TÍTULO Y CANTIDAD
            |--------------------------------------------------------------------------
            */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = when (filtroActual) {
                        "Ingresos" -> "Ingresos"
                        "Egresos" -> "Egresos"
                        else -> "Movimientos recientes"
                    },
                    color = TextoPrincipal,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${movimientosFiltrados.size}",
                    color = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    ),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            /*
            |--------------------------------------------------------------------------
            | MENSAJES
            |--------------------------------------------------------------------------
            */

            if (!mensaje.isNullOrBlank()) {
                Text(
                    text = mensaje!!,
                    color = Rojo,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            if (!cargandoCategorias && !mensajeCategorias.isNullOrBlank()) {
                Text(
                    text = mensajeCategorias!!,
                    color = Rojo,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (cargando && movimientos.isEmpty()) {
                Text(
                    text = "Cargando movimientos...",
                    color = GrisClaro,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            if (!cargando && movimientosFiltrados.isEmpty() && mensaje.isNullOrBlank()) {
                Text(
                    text = "No hay movimientos para mostrar.",
                    color = GrisClaro,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                )
            }

            /*
            |--------------------------------------------------------------------------
            | LISTA AGRUPADA POR FECHA
            |--------------------------------------------------------------------------
            */

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
                            onClick = {
                                onMovimientoClick(movimiento)
                            },
                            onEditar = {
                                movimientoEditar = movimiento
                                mostrarFormulario = true
                                movimientosViewModel.limpiarMensaje()
                            },
                            onEliminar = {
                                movimientoEliminar = movimiento
                                movimientosViewModel.limpiarMensaje()
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | FORMULARIO
    |--------------------------------------------------------------------------
    */

    if (mostrarFormulario) {
        NuevoMovimientoDialog(
            movimientoEditar = movimientoEditar,
            categorias = categorias,
            cargandoCategorias = cargandoCategorias,
            periodo = periodo,
            cargandoPeriodo = cargandoPeriodo,
            guardando = movimientosViewModel.guardando.collectAsState().value,
            mensaje = mensaje,
            onCerrar = {
                if (!guardando) {
                    mostrarFormulario = false
                    movimientoEditar = null
                    movimientosViewModel.limpiarMensaje()
                }
            },
            onGuardar = { fecha, categoriaId, concepto, persona, formaPago, monto, referencia, observaciones ->

                // Guardamos la referencia ANTES de cerrar el modal.
                val movimientoParaEditar = movimientoEditar

                // El modal desaparece inmediatamente al pulsar Guardar.
                mostrarFormulario = false
                movimientoEditar = null

                if (movimientoParaEditar == null) {

                    movimientosViewModel.crearMovimiento(
                        fecha = fecha,
                        categoriaId = categoriaId,
                        concepto = concepto,
                        persona = persona,
                        formaPago = formaPago,
                        monto = monto,
                        referencia = referencia,
                        observaciones = observaciones
                    )

                } else {

                    movimientosViewModel.actualizarMovimiento(
                        id = movimientoParaEditar.id,
                        fecha = fecha,
                        categoriaId = categoriaId,
                        concepto = concepto,
                        persona = persona,
                        formaPago = formaPago,
                        monto = monto,
                        referencia = referencia,
                        observaciones = observaciones
                    )
                }
            }
        )
    }

    /*
    |--------------------------------------------------------------------------
    | DIÁLOGO DE ELIMINACIÓN CON ANIMACIÓN SUAVE
    |--------------------------------------------------------------------------
    */
    movimientoEliminar?.let { movimiento ->
        Dialog(
            onDismissRequest = {
                if (!eliminando) {
                    movimientoEliminar = null
                }
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(
                    animationSpec = androidx.compose.animation.core.tween(220)
                ) + scaleIn(
                    initialScale = 0.92f,
                    animationSpec = androidx.compose.animation.core.tween(220)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(0.88f),
                    shape = RoundedCornerShape(22.dp),
                    color = FondoDetalle,
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(
                            text = "Eliminar movimiento",
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal,
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "¿Deseas eliminar el movimiento N° ${movimiento.numero}?\n\n${movimiento.concepto}",
                            color = TextoPrincipal,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = { movimientoEliminar = null },
                                enabled = !eliminando
                            ) {
                                Text("Cancelar", color = GrisClaro)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(
                                onClick = {
                                    // Cerrar el modal inmediatamente.
                                    val idMovimiento = movimiento.id
                                    movimientoEliminar = null

                                    // Ejecutar DELETE en segundo plano.
                                    movimientosViewModel.eliminarMovimiento(idMovimiento)
                                },
                                enabled = !eliminando
                            ) {
                                Text(
                                    text = if (eliminando) "Eliminando..." else "Eliminar",
                                    color = Rojo,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    /*
    |--------------------------------------------------------------------------
    | DIÁLOGO DE CONFIRMACIÓN — CERRAR PERÍODO
    |--------------------------------------------------------------------------
    */

    if (mostrarDialogoCerrarPeriodo) {

        val periodoActual = periodo

        if (periodoActual != null) {

            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoCerrarPeriodo = false
                },

                title = {
                    Text(
                        text = "Cerrar período",
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                },

                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "¿Deseas cerrar el período ${periodoActual.nombre_completo}?",
                            color = TextoPrincipal,
                            fontSize = 14.sp
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Saldo anterior: S/ %.2f"
                                .format(periodoActual.saldo_inicial),
                            color = TextoPrincipal,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Ingresos: S/ %.2f"
                                .format(totalIngresos),
                            color = Verde,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Egresos: S/ %.2f"
                                .format(totalEgresos),
                            color = Rojo,
                            fontSize = 14.sp
                        )

                        Text(
                            text = "Saldo final: S/ %.2f"
                                .format(
                                    periodoActual.saldo_inicial +
                                            totalIngresos -
                                            totalEgresos
                                ),
                            color = TextoPrincipal,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Una vez cerrado, no podrás registrar nuevos movimientos en este período.",
                            color = GrisClaro,
                            fontSize = 12.sp
                        )
                    }
                },

                confirmButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoCerrarPeriodo = false
                            onCerrarPeriodoClick()
                        }
                    ) {
                        Text(
                            text = "Cerrar período",
                            color = SeasonalColors.primary(
                                SeasonalTheme.getSeason()
                            ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                },

                dismissButton = {
                    TextButton(
                        onClick = {
                            mostrarDialogoCerrarPeriodo = false
                        }
                    ) {
                        Text(
                            text = "Cancelar",
                            color = GrisClaro
                        )
                    }
                }
            )
        }
    }


}



/*
|--------------------------------------------------------------------------
| RESUMEN SUPERIOR
|--------------------------------------------------------------------------
*/

@Composable
private fun ResumenTotalCard(
    modifier: Modifier,
    titulo: String,
    monto: Double,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = titulo,
                color = TextoPrincipal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "S/ %.2f".format(monto),
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| CABECERA DE FECHA
|--------------------------------------------------------------------------
*/

@Composable
private fun FechaHeader(fecha: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FondoFecha, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = formatearFecha(fecha),
            color = TextoPrincipal,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatearFecha(fecha: String): String {
    if (fecha.isBlank() || fecha == "Sin fecha") {
        return "Sin fecha"
    }

    val partes = fecha.split("-")
    if (partes.size != 3) {
        return fecha
    }

    return "${partes[2]}/${partes[1]}/${partes[0]}"
}

/*
|--------------------------------------------------------------------------
| FILTRO
|--------------------------------------------------------------------------
*/

@Composable
private fun Filtro(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clickable(onClick = onClick)
            .background(
                color = if (seleccionado) VerdeSuave else FondoTarjeta,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = texto,
            color = if (seleccionado) SeasonalColors.primary(
                SeasonalTheme.getSeason()
            ) else GrisClaro,
            fontSize = 13.sp,
            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/*
|--------------------------------------------------------------------------
| MOVIMIENTO (CON SWIPE CORREGIDO)
|--------------------------------------------------------------------------
*/

@Composable
private fun MovimientoCard(
    movimiento: Movimiento,
    onClick: () -> Unit,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val ingreso = movimiento.tipo.equals("Ingreso", ignoreCase = true)

    var offsetX by remember(movimiento.id) { mutableStateOf(0f) }
    var accionDisparada by remember(movimiento.id) { mutableStateOf(false) }

    val actionWidth = 120.dp
    val maxOffset = with(LocalDensity.current) {
        actionWidth.toPx()
    }

    val umbralAccion = maxOffset * 0.75f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
    ) {
        /*
         * Fondo de acciones corregido:
         * IZQUIERDA = EDITAR (AZUL) -> Se revela al deslizar a la DERECHA
         * DERECHA   = ELIMINAR (ROJO) -> Se revela al deslizar a la IZQUIERDA
         */
        Row(
            modifier = Modifier
                .matchParentSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // EDITAR — aparece al deslizar hacia la DERECHA
            Box(
                modifier = Modifier
                    .width(actionWidth)
                    .fillMaxSize()
                    .background(
                        color = AzulEditar,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Editar movimiento",
                    tint = Blanco,
                    modifier = Modifier.size(24.dp)
                )
            }

            // ELIMINAR — aparece al deslizar hacia la IZQUIERDA
            Box(
                modifier = Modifier
                    .width(actionWidth)
                    .fillMaxSize()
                    .background(
                        color = Rojo,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar movimiento",
                    tint = Blanco,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    IntOffset(offsetX.roundToInt(), 0)
                }
                .pointerInput(movimiento.id) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { _, dragAmount ->

                            if (accionDisparada) {
                                return@detectHorizontalDragGestures
                            }

                            offsetX = (offsetX + dragAmount)
                                .coerceIn(-maxOffset, maxOffset)

                            // DERECHA -> EDITAR (AZUL)
                            if (offsetX >= umbralAccion) {
                                accionDisparada = true
                                offsetX = 0f
                                onEditar()
                                return@detectHorizontalDragGestures
                            }

                            // IZQUIERDA -> ELIMINAR (ROJO)
                            if (offsetX <= -umbralAccion) {
                                accionDisparada = true
                                offsetX = 0f
                                onEliminar()
                                return@detectHorizontalDragGestures
                            }
                        },
                        onDragEnd = {
                            offsetX = 0f
                            accionDisparada = false
                        },
                        onDragCancel = {
                            offsetX = 0f
                            accionDisparada = false
                        }
                    )
                }
                .clickable {
                    if (offsetX != 0f) {
                        offsetX = 0f
                    } else {
                        onClick()
                    }
                },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(
                            color = if (ingreso) {
                                VerdeSuave
                            } else {
                                Color(0xFFFEE2E2)
                            },
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (ingreso) {
                            Icons.Outlined.ArrowUpward
                        } else {
                            Icons.Outlined.ArrowDownward
                        },
                        contentDescription = movimiento.concepto,
                        tint = if (ingreso) Verde else Rojo,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = movimiento.concepto,
                        color = TextoPrincipal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = movimiento.categoria ?: "Sin categoría",
                        color = SeasonalColors.primary(
                            SeasonalTheme.getSeason()
                        ),
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = if (ingreso) {
                        "+ S/ %.2f".format(movimiento.monto)
                    } else {
                        "- S/ %.2f".format(movimiento.monto)
                    },
                    color = if (ingreso) Verde else Rojo,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| FORMULARIO NUEVO MOVIMIENTO
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
    onCerrar: () -> Unit,
    onGuardar: (
        String,
        Int,
        String,
        String?,
        String,
        Double,
        String?,
        String?
    ) -> Unit
) {
    val hoy = remember { java.time.LocalDate.now() }

    val fechaInicial = remember(periodo?.id) {
        periodo?.let { java.time.LocalDate.of(it.anio, it.mes, 1) } ?: hoy
    }

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

    val periodoInicio = remember(periodo?.id) {
        periodo?.let { java.time.LocalDate.of(it.anio, it.mes, 1) }
    }

    val periodoFin = remember(periodo?.id) {
        periodoInicio?.withDayOfMonth(periodoInicio.lengthOfMonth())
    }

    val selectedDateMillis = remember(fecha) {
        runCatching {
            java.time.LocalDate.parse(fecha)
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }.getOrNull()
    }

    val initialMonthMillis = remember(periodo?.id) {
        periodoInicio
            ?.atStartOfDay(java.time.ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
    }

    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        initialDisplayedMonthMillis = initialMonthMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = java.time.Instant
                    .ofEpochMilli(utcTimeMillis)
                    .atZone(java.time.ZoneOffset.UTC)
                    .toLocalDate()

                return periodoInicio != null &&
                        periodoFin != null &&
                        !date.isBefore(periodoInicio) &&
                        !date.isAfter(periodoFin)
            }
        }
    )

    LaunchedEffect(periodo?.id, movimientoEditar?.id) {
        periodoInicio?.let { inicio ->
            val fechaObjetivo = movimientoEditar?.fecha
                ?.let { runCatching { java.time.LocalDate.parse(it) }.getOrNull() }
                ?.takeIf { fechaEditada ->
                    periodoFin?.let { fin ->
                        !fechaEditada.isBefore(inicio) && !fechaEditada.isAfter(fin)
                    } == true
                }
                ?: inicio

            datePickerState.displayedMonthMillis = fechaObjetivo
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            datePickerState.selectedDateMillis = fechaObjetivo
                .atStartOfDay(java.time.ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()

            fecha = fechaObjetivo.toString()
        }
    }

    LaunchedEffect(movimientoEditar?.id, categorias) {
        movimientoEditar?.let { movimiento ->
            fecha = movimiento.fecha ?: fecha
            categoriaSeleccionada = categorias.firstOrNull {
                it.nombre.equals(movimiento.categoria, ignoreCase = true)
            }
            concepto = movimiento.concepto
            persona = movimiento.persona.orEmpty()
            formaPago = movimiento.forma_pago ?: "Efectivo"
            monto = movimiento.monto.toString()
            referencia = movimiento.referencia.orEmpty()
            observaciones = movimiento.observaciones.orEmpty()
        }
    }

    val formularioValido = fecha.isNotBlank() &&
            categoriaSeleccionada != null &&
            concepto.isNotBlank() &&
            formaPago.isNotBlank() &&
            monto.toDoubleOrNull() != null &&
            monto.toDoubleOrNull()!! > 0 &&
            periodo != null

    Dialog(
        onDismissRequest = {
            if (!guardando) {
                onCerrar()
            }
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(
                animationSpec = androidx.compose.animation.core.tween(220)
            ) + scaleIn(
                initialScale = 0.92f,
                animationSpec = androidx.compose.animation.core.tween(220)
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .heightIn(max = 720.dp),
                shape = RoundedCornerShape(22.dp),
                color = FondoDetalle,
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 22.dp, top = 20.dp, end = 12.dp, bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (movimientoEditar == null) "Nuevo movimiento" else "Editar movimiento",
                                color = TextoPrincipal,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = if (periodo != null) {
                                    "Período activo: ${periodo.nombre} ${periodo.anio}"
                                } else {
                                    "Registrar movimiento contable"
                                },
                                color = GrisClaro,
                                fontSize = 12.sp
                            )
                        }

                        IconButton(
                            onClick = {
                                if (!guardando) onCerrar()
                            },
                            enabled = !guardando
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Cerrar",
                                tint = GrisClaro
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 22.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        when {
                            cargandoPeriodo -> {
                                Text(
                                    text = "Cargando período activo...",
                                    color = GrisClaro,
                                    fontSize = 13.sp
                                )
                            }

                            periodo == null -> {
                                Text(
                                    text = mensaje ?: "No existe un período contable abierto.",
                                    color = Rojo,
                                    fontSize = 13.sp
                                )
                            }

                            else -> {
                                Text(
                                    text = "Selecciona una fecha de ${periodo.nombre} ${periodo.anio}",
                                    color = SeasonalColors.primary(
                                        SeasonalTheme.getSeason()
                                    ),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        OutlinedTextField(
                            value = fecha,
                            onValueChange = {},
                            readOnly = true,
                            enabled = periodo != null && !cargandoPeriodo && !guardando,
                            label = { Text("Fecha") },
                            placeholder = { Text("Seleccionar fecha") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    enabled = periodo != null && !cargandoPeriodo && !guardando
                                ) {
                                    mostrarCalendario = true
                                },
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = { mostrarCalendario = true },
                                    enabled = periodo != null && !cargandoPeriodo && !guardando
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.DateRange,
                                        contentDescription = "Seleccionar fecha",
                                        tint = SeasonalColors.primary(
                                            SeasonalTheme.getSeason()
                                        )
                                    )
                                }
                            },
                            colors = coloresCampo()
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = categoriaSeleccionada?.nombre ?: "",
                                onValueChange = {},
                                readOnly = true,
                                enabled = !cargandoCategorias && !guardando,
                                label = { Text("Categoría") },
                                placeholder = {
                                    Text(
                                        if (cargandoCategorias) "Cargando categorías..." else "Seleccionar categoría"
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = { Text(text = "▼", color = SeasonalColors.primary(
                                    SeasonalTheme.getSeason()
                                )) },
                                colors = coloresCampo()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable(
                                        enabled = !cargandoCategorias && !guardando
                                    ) {
                                        categoriaMenuAbierto = true
                                    }
                            )

                            DropdownMenu(
                                expanded = categoriaMenuAbierto,
                                onDismissRequest = { categoriaMenuAbierto = false }
                            ) {
                                val ingresos = categorias.filter { it.tipo.equals("Ingreso", ignoreCase = true) }
                                val egresos = categorias.filter { it.tipo.equals("Egreso", ignoreCase = true) }

                                if (ingresos.isNotEmpty()) {
                                    Text(
                                        text = "INGRESOS",
                                        color = Verde,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )

                                    ingresos.forEach { categoria ->
                                        DropdownMenuItem(
                                            text = { Text(categoria.nombre, color = TextoPrincipal) },
                                            onClick = {
                                                categoriaSeleccionada = categoria
                                                categoriaMenuAbierto = false
                                            }
                                        )
                                    }
                                }

                                if (ingresos.isNotEmpty() && egresos.isNotEmpty()) {
                                    HorizontalDivider(color = GrisClaro.copy(alpha = 0.15f))
                                }

                                if (egresos.isNotEmpty()) {
                                    Text(
                                        text = "EGRESOS",
                                        color = Rojo,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )

                                    egresos.forEach { categoria ->
                                        DropdownMenuItem(
                                            text = { Text(categoria.nombre, color = TextoPrincipal) },
                                            onClick = {
                                                categoriaSeleccionada = categoria
                                                categoriaMenuAbierto = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        CampoMovimiento(
                            valor = concepto,
                            onValueChange = { concepto = it },
                            etiqueta = "Concepto",
                            placeholder = "Descripción del movimiento"
                        )

                        CampoMovimiento(
                            valor = persona,
                            onValueChange = { persona = it },
                            etiqueta = "Persona",
                            placeholder = "Persona relacionada",
                            requerido = false
                        )

                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = formaPago,
                                onValueChange = {},
                                readOnly = true,
                                enabled = !guardando,
                                label = { Text("Forma de pago") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                trailingIcon = { Text(text = "▼", color = SeasonalColors.primary(
                                    SeasonalTheme.getSeason()
                                )) },
                                colors = coloresCampo()
                            )

                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .clickable(enabled = !guardando) {
                                        formaPagoMenuAbierto = true
                                    }
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

                        OutlinedTextField(
                            value = monto,
                            onValueChange = { valor ->
                                monto = valor.filter { it.isDigit() || it == '.' }
                            },
                            label = { Text("Monto") },
                            placeholder = { Text("0.00") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            textStyle = TextStyle(textAlign = TextAlign.End),
                            prefix = { Text("S/ ", color = GrisClaro) },
                            colors = coloresCampo()
                        )

                        CampoMovimiento(
                            valor = referencia,
                            onValueChange = { referencia = it },
                            etiqueta = "Referencia",
                            placeholder = "Referencia opcional",
                            requerido = false
                        )

                        CampoMovimiento(
                            valor = observaciones,
                            onValueChange = { observaciones = it },
                            etiqueta = "Observaciones",
                            placeholder = "Observaciones adicionales",
                            requerido = false,
                            singleLine = false,
                            minLines = 3
                        )

                        if (!mensaje.isNullOrBlank()) {
                            Text(
                                text = mensaje,
                                color = if (mensaje.contains("correctamente", ignoreCase = true)) Verde else Rojo,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TextButton(
                            onClick = onCerrar,
                            enabled = !guardando,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Cancelar", color = GrisClaro)
                        }

                        Button(
                            onClick = {
                                val categoria = categoriaSeleccionada
                                val montoNumerico = monto.toDoubleOrNull()

                                if (
                                    categoria != null &&
                                    montoNumerico != null &&
                                    montoNumerico > 0.0 &&
                                    fecha.isNotBlank() &&
                                    concepto.isNotBlank() &&
                                    formaPago.isNotBlank()
                                ) {
                                    onGuardar(
                                        fecha,
                                        categoria.id,
                                        concepto.trim(),
                                        persona.trim().ifBlank { null },
                                        formaPago,
                                        montoNumerico,
                                        referencia.trim().ifBlank { null },
                                        observaciones.trim().ifBlank { null }
                                    )
                                }
                            },
                            enabled = formularioValido && !guardando,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
                                contentColor = Blanco
                            )
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


    if (mostrarCalendario && periodoInicio != null && periodoFin != null) {
        DatePickerDialog(
            onDismissRequest = {
                if (!guardando) mostrarCalendario = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val seleccion = java.time.Instant
                                .ofEpochMilli(millis)
                                .atZone(java.time.ZoneOffset.UTC)
                                .toLocalDate()

                            if (!seleccion.isBefore(periodoInicio) && !seleccion.isAfter(periodoFin)) {
                                fecha = seleccion.toString()
                                mostrarCalendario = false
                            }
                        }
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) {
                    Text(text = "Aceptar", color = SeasonalColors.primary(SeasonalTheme.getSeason()))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text(text = "Cancelar", color = GrisClaro)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Fecha del movimiento",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
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

/*
|--------------------------------------------------------------------------
| CAMPO DE MOVIMIENTO
|--------------------------------------------------------------------------
*/

@Composable
private fun CampoMovimiento(
    valor: String,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    placeholder: String,
    requerido: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = {
            Text(if (requerido) etiqueta else "$etiqueta (opcional)")
        },
        placeholder = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = coloresCampo()
    )
}

/*
|--------------------------------------------------------------------------
| COLORES DE CAMPOS
|--------------------------------------------------------------------------
*/

@Composable
private fun coloresCampo() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextoPrincipal,
    unfocusedTextColor = TextoPrincipal,
    disabledTextColor = GrisClaro,
    focusedBorderColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
    unfocusedBorderColor = GrisClaro.copy(alpha = 0.35f),
    focusedLabelColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
    unfocusedLabelColor = GrisClaro,
    cursorColor = SeasonalColors.primary(SeasonalTheme.getSeason())
)

/*
|--------------------------------------------------------------------------
| BARRA INFERIOR (Inicio, Asamblea, Periodos, Mi cuenta)
|--------------------------------------------------------------------------
*/

@Composable
private fun BarraInferiorMovimientos(
    onInicioClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onPeriodosClick: () -> Unit,
    onMiCuentaClick: () -> Unit
) {
    NavigationBar(
        containerColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onInicioClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = { Text(text = "Inicio") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = onAsambleasClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = "Asamblea"
                )
            },
            label = { Text(text = "Asamblea") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = onPeriodosClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Periodos"
                )
            },
            label = { Text(text = "Periodos") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )

        NavigationBarItem(
            selected = false,
            onClick = onMiCuentaClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Mi cuenta"
                )
            },
            label = { Text(text = "Mi cuenta") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )
    }
}

