package com.sigefiv.app.screens.incidencias

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AssignmentLate
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.data.model.IncidenciaItem
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReportesVecinoScreen(
    listaIncidencias: List<IncidenciaItem>,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {},
    onNuevoReporteClick: () -> Unit = {},
    onBackClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())
    val colors = MaterialTheme.colorScheme

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
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mis Reportes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.onPrimary
                        )
                        Text(
                            text = "Seguimiento y estado de avisos",
                            fontSize = 12.sp,
                            color = colors.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = colors.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorPrincipal)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoReporteClick,
                containerColor = colorPrincipal,
                contentColor = colors.onPrimary,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 3.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Nuevo reporte")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo Reporte", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
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
                        colorPrincipal = colorPrincipal
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
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (seleccionado) colorPrincipal else colors.surface,
                                border = if (seleccionado) null else BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.clickable { filtroSeleccionado = filtro }
                            ) {
                                Text(
                                    text = filtro,
                                    fontSize = 12.sp,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                                    color = if (seleccionado) colors.onPrimary else colors.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }
                }

                // 3. ESTADOS DE LA LISTA
                if (incidenciasFiltradas.isEmpty() && !isRefreshing) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp, horizontal = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(68.dp),
                                shape = CircleShape,
                                color = colorPrincipal.copy(alpha = 0.12f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.AssignmentLate,
                                        contentDescription = null,
                                        modifier = Modifier.size(34.dp),
                                        tint = colorPrincipal
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Sin avisos en esta sección",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "No se encontraron reportes registrados para el filtro seleccionado.",
                                fontSize = 13.sp,
                                color = colors.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    items(incidenciasFiltradas, key = { it.id }) { incidencia ->
                        TarjetaReporteVecinoItem(
                            incidencia = incidencia,
                            colorPrincipal = colorPrincipal
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
                LinearProgressIndicator(color = colorPrincipal, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| PANEL HERO DE CONTROL ESTILO CAJA
|--------------------------------------------------------------------------
*/

@Composable
private fun CardHeroReportes(
    total: Int,
    pendientes: Int,
    enProceso: Int,
    atendidos: Int,
    colorPrincipal: Color
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(22.dp))
            ) {
                val cx = size.width + 15.dp.toPx()
                val cy = -5.dp.toPx()

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorPrincipal.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(cx - 50.dp.toPx(), 30.dp.toPx()),
                        radius = 150.dp.toPx()
                    ),
                    radius = 150.dp.toPx(),
                    center = Offset(cx - 50.dp.toPx(), 30.dp.toPx())
                )
                drawCircle(
                    color = colorPrincipal.copy(alpha = 0.12f),
                    radius = 100.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Centro de atención",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        Text(
                            text = "Estado y evolución de tus avisos",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(colorPrincipal, colorPrincipal.copy(alpha = 0.75f))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.RateReview,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniKpiBox(
                        modifier = Modifier.weight(1f),
                        titulo = "Total avisos",
                        valor = "$total",
                        icono = Icons.Outlined.AssignmentLate,
                        fondo = Color(0xFF1E293B),
                        colorAcento = Color(0xFF38BDF8)
                    )
                    MiniKpiBox(
                        modifier = Modifier.weight(1f),
                        titulo = "Pendientes",
                        valor = "$pendientes",
                        icono = Icons.Outlined.HourglassEmpty,
                        fondo = Color(0xFF78350F).copy(alpha = 0.85f),
                        colorAcento = Color(0xFFFBBF24)
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
                        fondo = Color(0xFF0C4A6E).copy(alpha = 0.85f),
                        colorAcento = Color(0xFF38BDF8)
                    )
                    MiniKpiBox(
                        modifier = Modifier.weight(1f),
                        titulo = "Atendidos",
                        valor = "$atendidos",
                        icono = Icons.Outlined.CheckCircle,
                        fondo = Color(0xFF064E3B).copy(alpha = 0.85f),
                        colorAcento = Color(0xFF34D399)
                    )
                }
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
    fondo: Color,
    colorAcento: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = fondo
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(colorAcento.copy(alpha = 0.20f), RoundedCornerShape(8.dp)),
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
                    color = Color.White.copy(alpha = 0.75f),
                    maxLines = 1
                )
                Text(
                    text = valor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
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
    colorPrincipal: Color
) {
    val colors = MaterialTheme.colorScheme

    val (colorEstado, textoEstado) = when (incidencia.estado?.lowercase()) {
        "en_proceso" -> Color(0xFF0284C7) to "En Proceso"
        "atendido" -> Color(0xFF10B981) to "Atendido"
        else -> Color(0xFFF59E0B) to "Pendiente"
    }

    val urlImagen = remember(incidencia.imagen_path) {
        incidencia.imagen_path?.let {
            val base = ServidorConfig.API_BASE_URL.substringBefore("/api/")
            "$base/storage/$it"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                        color = colorPrincipal.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.25f))
                    ) {
                        Text(
                            text = "#${incidencia.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            color = colorPrincipal,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (!incidencia.created_at.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = colors.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = incidencia.created_at.take(10),
                            fontSize = 11.5.sp,
                            color = colors.onSurfaceVariant
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
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = colors.onSurface,
                lineHeight = 21.sp
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
                        .background(colors.surfaceVariant.copy(alpha = 0.5f)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}