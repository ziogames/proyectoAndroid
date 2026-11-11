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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Menu
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
import com.sigefiv.app.ui.components.AppBottomBar
import com.sigefiv.app.viewmodel.AsambleasViewModel
import kotlinx.coroutines.launch

/*
|--------------------------------------------------------------------------
| COLORES SEMÁNTICOS ADAPTATIVOS
|--------------------------------------------------------------------------
*/

private val VerdePublicado = Color(0xFF16A34A)
private val RojoCancelado = Color(0xFFDC2626)
private val AzulBorrador = Color(0xFF2563EB)

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
    onEmprendimientosClick: () -> Unit = {},
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
    val primaryColor = MaterialTheme.colorScheme.primary

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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Asambleas",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Convocatorias y acuerdos",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = primaryColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                        ) {
                            Text(
                                text = "${uiState.asambleas.size}",
                                color = primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = onOpenDrawer,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Abrir menú",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.cargarAsambleas() },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        if (uiState.cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = primaryColor
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Refresh,
                                contentDescription = "Actualizar",
                                tint = primaryColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCrearClick,
                containerColor = primaryColor,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = androidx.compose.material3.FloatingActionButtonDefaults.elevation(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Crear asamblea"
                )
            }
        },
        bottomBar = {
            AppBottomBar(
                pantallaActual = "Asamblea",
                onInicioClick = onInicioClick,
                onAsambleasClick = { /* Ya estás aquí */ },
                onEmprendimientosClick = onEmprendimientosClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
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
                            primaryColor
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        border = if (seleccionado) {
                            null
                        } else {
                            BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                        }
                    ) {
                        Text(
                            text = filtro.titulo,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = if (seleccionado) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
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
                                CircularProgressIndicator(color = primaryColor, strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Obteniendo asambleas...",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp
                                )
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

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }

    asambleaParaPublicar?.let { asamblea ->
        AlertDialog(
            onDismissRequest = { asambleaParaPublicar = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Publicar asamblea",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Deseas publicar la asamblea \"${asamblea.titulo ?: "Sin título"}\"? Una vez publicada ya no podrá editarse.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.publicarAsamblea(asamblea.id)
                    asambleaParaPublicar = null
                }) {
                    Text("Publicar", color = primaryColor, fontWeight = FontWeight.Bold)
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
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Eliminar asamblea",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Deseas eliminar la asamblea \"${asamblea.titulo ?: "Sin título"}\"?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.eliminarAsamblea(asamblea.id)
                    asambleaParaEliminar = null
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
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
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
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
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = primaryColor.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Event,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = asamblea.titulo?.takeIf { it.isNotBlank() } ?: "Sin título",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = textoTipo(asamblea.tipo),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                        tint = if (esBorrador) {
                            MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        },
                        modifier = Modifier.size(19.dp)
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
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "AGENDA (${asamblea.agendas.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
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
                                color = primaryColor,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }

            if (esBorrador) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Surface(
                        onClick = onPublicarClick,
                        shape = RoundedCornerShape(10.dp),
                        color = primaryColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Publish,
                                contentDescription = "Publicar",
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Publicar convocatoria",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
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
| COMPONENTES SECUNDARIOS
|--------------------------------------------------------------------------
*/

@Composable
private fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = primaryColor,
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
    val (texto, colorBase) = when (estadoNormalizado) {
        "publicada" -> Pair("Publicada", VerdePublicado)
        "cancelada" -> Pair("Cancelada", RojoCancelado)
        else -> Pair("Borrador", AzulBorrador)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = colorBase.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, colorBase.copy(alpha = 0.25f))
    ) {
        Text(
            text = texto,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = colorBase
        )
    }
}

@Composable
private fun EmptyAsambleas() {
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(54.dp),
                shape = CircleShape,
                color = primaryColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Event,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(26.dp)
                    )
                }
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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