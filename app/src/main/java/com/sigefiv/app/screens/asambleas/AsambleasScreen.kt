@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.asambleas

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Publish
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.viewmodel.AsambleasViewModel
import kotlinx.coroutines.launch
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme


/*
|--------------------------------------------------------------------------
| PALETA DE COLORES PROFESIONAL (ESTILO SIGEFIV)
|--------------------------------------------------------------------------
*/

private val VerdeSuave = Color(0xFFCCFBF1)
private val VerdeTexto = Color(0xFF0D9488)
private val Blanco = Color.White

private val AzulBadge = Color(0xFF2563EB)
private val AzulSuave = Color(0xFFDBEAFE)
private val RojoEgreso = Color(0xFFE11D48)
private val RojoSuave = Color(0xFFFFE4E6)

/*
|--------------------------------------------------------------------------
| FILTROS
|--------------------------------------------------------------------------
*/

private enum class FiltroAsamblea(val titulo: String) {
    TODAS("Todas"),
    BORRADOR("Borrador"),
    PUBLICADAS("Publicadas"),
    CANCELADAS("Canceladas")
}

/*
|--------------------------------------------------------------------------
| PANTALLA PRINCIPAL
|--------------------------------------------------------------------------
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsambleasScreen(
    viewModel: AsambleasViewModel,
    onAsambleaClick: (Asamblea) -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onCrearClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var filtroActual by remember { mutableStateOf(FiltroAsamblea.TODAS) }
    var asambleaParaEliminar by remember { mutableStateOf<Asamblea?>(null) }
    var asambleaParaPublicar by remember { mutableStateOf<Asamblea?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.error, uiState.mensaje) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarError()
        }
        uiState.mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
        }
    }

    val asambleasFiltradas = when (filtroActual) {
        FiltroAsamblea.TODAS -> uiState.asambleas
        FiltroAsamblea.BORRADOR -> uiState.asambleas.filter { it.estado.equals("borrador", ignoreCase = true) }
        FiltroAsamblea.PUBLICADAS -> uiState.asambleas.filter { it.estado.equals("publicada", ignoreCase = true) }
        FiltroAsamblea.CANCELADAS -> uiState.asambleas.filter { it.estado.equals("cancelada", ignoreCase = true) }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Asambleas",
                            color = Blanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.asambleas.size} " + if (uiState.asambleas.size == 1) "registrada" else "registradas",
                            color = Blanco.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Abrir menú",
                            tint = Blanco
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.cargarAsambleas() }) {
                        if (uiState.cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = Blanco
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Actualizar",
                                tint = Blanco
                            )
                        }
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
                onClick = onCrearClick,
                containerColor =  SeasonalColors.primary(
                    SeasonalTheme.getSeason()
                ),
                contentColor = Blanco
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Crear asamblea"
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = SeasonalColors.primary(
                    SeasonalTheme.getSeason()
                ),
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onInicioClick,
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onAsambleasClick,
                    icon = { Icon(Icons.Outlined.Groups, contentDescription = "Asamblea") },
                    label = { Text("Asamblea") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = SeasonalColors.primary(
                            SeasonalTheme.getSeason()
                        ),
                        selectedTextColor = Blanco,
                        indicatorColor = Blanco,
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onPeriodosClick,
                    icon = { Icon(Icons.Outlined.CalendarMonth, contentDescription = "Periodos") },
                    label = { Text("Periodos") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMiCuentaClick,
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Mi cuenta") },
                    label = { Text("Mi cuenta") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(FiltroAsamblea.entries.toTypedArray()) { filtro ->
                    val seleccionado = filtroActual == filtro
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { filtroActual = filtro },
                        shape = RoundedCornerShape(12.dp),
                        color = if (seleccionado) {
                            SeasonalColors.primary(
                                SeasonalTheme.getSeason()
                            )
                        } else {
                            Blanco
                        },
                        border = if (seleccionado) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = filtro.titulo,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (seleccionado) Blanco else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp,
                            fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.cargando && uiState.asambleas.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = SeasonalColors.primary(
                                    SeasonalTheme.getSeason()
                                ))
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Obteniendo asambleas...", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                            }
                        }
                    }
                }

                if (!uiState.cargando && asambleasFiltradas.isEmpty()) {
                    item { EmptyAsambleas() }
                }

                items(
                    items = asambleasFiltradas,
                    key = { it.id }
                ) { asamblea ->
                    AsambleaCard(
                        asamblea = asamblea,
                        onClick = {
                            if (asamblea.estado.equals("borrador", ignoreCase = true)) {
                                onAsambleaClick(asamblea)
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Las asambleas publicadas o canceladas no pueden modificarse."
                                    )
                                }
                            }
                        },
                        onPublicarClick = {
                            asambleaParaPublicar = asamblea
                        },
                        onDeleteClick = {
                            if (asamblea.estado.equals("borrador", ignoreCase = true)) {
                                asambleaParaEliminar = asamblea
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Las asambleas publicadas o canceladas no pueden eliminarse."
                                    )
                                }
                            }
                        }
                    )
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }

    asambleaParaPublicar?.let { asamblea ->
        AlertDialog(
            onDismissRequest = { asambleaParaPublicar = null },
            title = { Text("Publicar asamblea", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas publicar la asamblea \"${asamblea.titulo ?: "Sin título"}\"? Una vez publicada ya no podrá editarse.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.publicarAsamblea(asamblea.id)
                    asambleaParaPublicar = null
                }) {
                    Text("Publicar", color = VerdeTexto, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { asambleaParaPublicar = null }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    asambleaParaEliminar?.let { asamblea ->
        AlertDialog(
            onDismissRequest = { asambleaParaEliminar = null },
            title = { Text("Eliminar asamblea", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold) },
            text = { Text("¿Deseas eliminar la asamblea \"${asamblea.titulo ?: "Sin título"}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarAsamblea(asamblea.id)
                    asambleaParaEliminar = null
                }) {
                    Text("Eliminar", color = RojoEgreso, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { asambleaParaEliminar = null }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA DE ASAMBLEA
|--------------------------------------------------------------------------
*/

@Composable
private fun AsambleaCard(
    asamblea: Asamblea,
    onClick: () -> Unit,
    onPublicarClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val esBorrador = asamblea.estado.equals("borrador", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(VerdeSuave, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = null,
                        tint = VerdeTexto,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asamblea.titulo?.takeIf { it.isNotBlank() } ?: "Sin título",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = textoTipo(asamblea.tipo),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f)
                    )
                }

                EstadoChip(estado = asamblea.estado)

                IconButton(
                    onClick = onDeleteClick,
                    enabled = esBorrador
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = if (esBorrador) "Eliminar" else "No se puede eliminar",
                        tint = if (esBorrador) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f) else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InfoItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CalendarMonth,
                    text = formatearFecha(asamblea.fecha)
                )
                InfoItem(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Schedule,
                    text = formatearHora(asamblea.hora)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            InfoItem(
                icon = Icons.Outlined.LocationOn,
                text = asamblea.lugar?.takeIf { it.isNotBlank() } ?: "Lugar no indicado"
            )

            if (!asamblea.convoca.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Convoca: ${asamblea.convoca}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (!asamblea.descripcion.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = asamblea.descripcion ?: "",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (asamblea.agendas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "AGENDA (${asamblea.agendas.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = VerdeTexto,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        asamblea.agendas.take(3).forEach { agenda ->
                            Text(
                                text = "${agenda.numero}. ${agenda.descripcion?.trim() ?: ""}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                        if (asamblea.agendas.size > 3) {
                            Text(
                                text = "+ ${asamblea.agendas.size - 3} puntos más",
                                fontSize = 11.sp,
                                color = SeasonalColors.primary(
                                    SeasonalTheme.getSeason()
                                ),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            if (esBorrador) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = onPublicarClick,
                        modifier = Modifier
                            .size(38.dp)
                            .background(SeasonalColors.primary(
                                SeasonalTheme.getSeason()
                            ), CircleShape),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = SeasonalColors.primary(
                                SeasonalTheme.getSeason()
                            ),
                            contentColor = Blanco
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Publish,
                            contentDescription = "Publicar asamblea",
                            tint = Blanco,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| COMPONENTES SECUNDARIOS
|--------------------------------------------------------------------------
*/

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SeasonalColors.primary(
                SeasonalTheme.getSeason()
            ),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun EstadoChip(estado: String?) {
    val estadoNormalizado = estado?.trim()?.lowercase() ?: ""
    val (texto, fondo, color) = when (estadoNormalizado) {
        "publicada" -> Triple("Publicada", VerdeSuave, VerdeTexto)
        "cancelada" -> Triple("Cancelada", RojoSuave, RojoEgreso)
        else -> Triple("Borrador", AzulSuave, AzulBadge)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = fondo
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun EmptyAsambleas() {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = Blanco,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(VerdeSuave, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = null,
                    tint = VerdeTexto,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No hay asambleas",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "No se encontraron registros en este filtro.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun textoTipo(tipo: String?): String {
    return when (tipo?.trim()?.lowercase()) {
        "ordinaria" -> "Ordinaria"
        "extraordinaria" -> "Extraordinaria"
        else -> tipo?.takeIf { it.isNotBlank() } ?: "Asamblea"
    }
}

private fun formatearFecha(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "Fecha pendiente"
    val partes = fecha.substringBefore("T").split("-")
    return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
}

private fun formatearHora(hora: String?): String {
    if (hora.isNullOrBlank()) return "Hora pendiente"
    return hora.substringBefore(".").substringBefore("Z")
}
