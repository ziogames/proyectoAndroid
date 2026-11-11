@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.incidencias

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AssignmentLate
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.data.model.IncidenciaItem

private val ColorPendiente = Color(0xFFF59E0B)
private val ColorEnProceso = Color(0xFF0284C7)
private val ColorAtendido = Color(0xFF10B981)

@Composable
fun MisReportesVecinoScreen(
    listaIncidencias: List<IncidenciaItem>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onNuevoReporteClick: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val totalPendientes = remember(listaIncidencias) {
        listaIncidencias.count { it.estado.isNullOrBlank() || it.estado.equals("pendiente", true) }
    }
    val totalEnProceso = remember(listaIncidencias) {
        listaIncidencias.count { it.estado.equals("en_proceso", true) }
    }
    val totalAtendidos = remember(listaIncidencias) {
        listaIncidencias.count { it.estado.equals("atendido", true) }
    }

    val incidenciasFiltradas = remember(listaIncidencias, filtroSeleccionado) {
        when (filtroSeleccionado) {
            "Pendientes" -> listaIncidencias.filter { it.estado.isNullOrBlank() || it.estado.equals("pendiente", true) }
            "En Proceso" -> listaIncidencias.filter { it.estado.equals("en_proceso", true) }
            "Atendidos" -> listaIncidencias.filter { it.estado.equals("atendido", true) }
            else -> listaIncidencias
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Reportes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Seguimiento y estado de avisos",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoReporteClick,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Nuevo reporte",
                        modifier = Modifier.size(20.dp)
                    )
                },
                text = {
                    Text(
                        text = "Nuevo Reporte",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. TARJETA HERO SUPERIOR DE MÉTRICAS
                item {
                    CardHeroReportes(
                        total = listaIncidencias.size,
                        pendientes = totalPendientes,
                        enProceso = totalEnProceso,
                        atendidos = totalAtendidos,
                        primaryColor = primaryColor
                    )
                }

                // 2. CHIPS DE FILTRO RÁPIDO
                item {
                    val filtros = listOf("Todos", "Pendientes", "En Proceso", "Atendidos")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filtros) { filtro ->
                            val seleccionado = filtroSeleccionado == filtro
                            FilterChip(
                                selected = seleccionado,
                                onClick = { filtroSeleccionado = filtro },
                                label = {
                                    Text(
                                        text = filtro,
                                        fontSize = 12.sp,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor.copy(alpha = 0.14f),
                                    selectedLabelColor = primaryColor,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (seleccionado) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                // 3. ESTADOS DE LA LISTA
                if (incidenciasFiltradas.isEmpty() && !isRefreshing) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surface,
                            tonalElevation = 1.dp,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 36.dp, horizontal = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.AssignmentLate,
                                        contentDescription = null,
                                        modifier = Modifier.size(30.dp),
                                        tint = primaryColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "Sin avisos en esta sección",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "No se encontraron reportes registrados para el filtro seleccionado.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    items(incidenciasFiltradas, key = { it.id }) { incidencia ->
                        TarjetaReporteVecinoItem(
                            incidencia = incidencia,
                            primaryColor = primaryColor
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }

            AnimatedVisibility(
                visible = isRefreshing,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                LinearProgressIndicator(color = primaryColor, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| PANEL HERO DE CONTROL METRICO
|--------------------------------------------------------------------------
*/

@Composable
private fun CardHeroReportes(
    total: Int,
    pendientes: Int,
    enProceso: Int,
    atendidos: Int,
    primaryColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
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
                Column {
                    Text(
                        text = "Centro de atención",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Estado y evolución de tus avisos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.RateReview,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniKpiBox(
                    modifier = Modifier.weight(1f),
                    titulo = "Total avisos",
                    valor = "$total",
                    icono = Icons.Outlined.AssignmentLate,
                    colorAcento = primaryColor
                )
                MiniKpiBox(
                    modifier = Modifier.weight(1f),
                    titulo = "Pendientes",
                    valor = "$pendientes",
                    icono = Icons.Outlined.HourglassEmpty,
                    colorAcento = ColorPendiente
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniKpiBox(
                    modifier = Modifier.weight(1f),
                    titulo = "En proceso",
                    valor = "$enProceso",
                    icono = Icons.Outlined.ErrorOutline,
                    colorAcento = ColorEnProceso
                )
                MiniKpiBox(
                    modifier = Modifier.weight(1f),
                    titulo = "Atendidos",
                    valor = "$atendidos",
                    icono = Icons.Outlined.CheckCircle,
                    colorAcento = ColorAtendido
                )
            }
        }
    }
}

@Composable
private fun MiniKpiBox(
    modifier: Modifier = Modifier,
    titulo: String,
    valor: String,
    icono: ImageVector,
    colorAcento: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, colorAcento.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(colorAcento.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorAcento,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = titulo,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = valor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA DE REPORTE VECINAL
|--------------------------------------------------------------------------
*/

@Composable
private fun TarjetaReporteVecinoItem(
    incidencia: IncidenciaItem,
    primaryColor: Color
) {
    val (colorEstado, textoEstado) = when (incidencia.estado?.lowercase()) {
        "en_proceso" -> ColorEnProceso to "En Proceso"
        "atendido" -> ColorAtendido to "Atendido"
        else -> ColorPendiente to "Pendiente"
    }

    val urlImagen = remember(incidencia.imagen_path) {
        incidencia.imagen_path?.let {
            val base = ServidorConfig.API_BASE_URL.substringBefore("/api/")
            "$base/storage/$it"
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera: ID + Fecha + Badge de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = primaryColor.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "#${incidencia.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (!incidencia.created_at.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = incidencia.created_at.take(10),
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    color = colorEstado.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, colorEstado.copy(alpha = 0.30f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(colorEstado)
                        )
                        Text(
                            text = textoEstado,
                            color = colorEstado,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Mensaje del reporte
            Text(
                text = incidencia.mensaje,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 20.sp
            )

            // Evidencia fotográfica
            if (!urlImagen.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                AsyncImage(
                    model = urlImagen,
                    contentDescription = "Evidencia fotográfica",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}