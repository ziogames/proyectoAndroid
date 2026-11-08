package com.sigefiv.app.screens.bitacora

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.HistoryEdu
import androidx.compose.material.icons.outlined.Lan
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sigefiv.app.data.model.BitacoraItem
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.BitacoraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BitacoraScreen(
    onBackClick: () -> Unit = {},
    viewModel: BitacoraViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())
    val colors = MaterialTheme.colorScheme

    LaunchedEffect(Unit) {
        viewModel.cargarBitacora()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Bitácora del Sistema",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.onPrimary
                        )
                        Text(
                            text = "Auditoría de eventos y seguridad",
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal
                )
            )
        },
        containerColor = colors.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. TARJETA HERO: RESUMEN DE AUDITORÍA CON ILUSTRACIÓN DE FONDO
            item {
                CardResumenAuditoria(
                    totalRegistros = state.metricas?.total_registros ?: 0,
                    usuariosActivos = state.metricas?.usuarios_activos ?: 0,
                    modulos = state.metricas?.modulos ?: 0,
                    accionesHoy = state.metricas?.acciones_hoy ?: 0,
                    colorPrincipal = colorPrincipal
                )
            }

            // 2. TARJETA: DISTRIBUCIÓN DE ACTIVIDAD (DONUT CHART)
            item {
                val movCount = remember(state.registros) {
                    state.registros.count { it.modulo.contains("movimiento", ignoreCase = true) }
                }
                val segCount = remember(state.registros) {
                    state.registros.count { !it.modulo.contains("movimiento", ignoreCase = true) }
                }
                CardDistribucionAuditoria(
                    movimientosCount = movCount,
                    seguridadCount = segCount,
                    colorPrincipal = colorPrincipal
                )
            }

            // 3. BUSCADOR INTEGRADO
            item {
                OutlinedTextField(
                    value = state.busqueda,
                    onValueChange = { viewModel.onBuscarChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = {
                        Text(
                            text = "Buscar por usuario, acción o módulo...",
                            fontSize = 13.5.sp,
                            color = colors.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(26.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = colorPrincipal,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (state.busqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onBuscarChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = "Limpiar búsqueda",
                                    tint = colors.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedBorderColor = colorPrincipal,
                        unfocusedBorderColor = colors.outlineVariant.copy(alpha = 0.5f)
                    )
                )
            }

            // 4. ESTADO DE CARGA Y REGISTROS
            if (state.cargando) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorPrincipal,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            } else if (state.registros.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Outlined.EventNote,
                                contentDescription = null,
                                tint = colors.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No se encontraron registros de auditoría",
                                color = colors.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            } else {
                items(state.registros, key = { it.id }) { item ->
                    ItemRegistroBitacora(
                        item = item,
                        colorPrincipal = colorPrincipal
                    )
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA HERO: ESTILO CAJA CON ILUSTRACIÓN DE FONDO
|--------------------------------------------------------------------------
*/

@Composable
private fun CardResumenAuditoria(
    totalRegistros: Int,
    usuariosActivos: Int,
    modulos: Int,
    accionesHoy: Int,
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
            // ILUSTRACIÓN DE FONDO (ESCUDO Y ONDAS DE SEGURIDAD)
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(22.dp))
            ) {
                val cx = size.width + 20.dp.toPx()
                val cy = -10.dp.toPx()

                // Glow radial verde
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorPrincipal.copy(alpha = 0.28f), Color.Transparent),
                        center = Offset(cx - 60.dp.toPx(), 40.dp.toPx()),
                        radius = 160.dp.toPx()
                    ),
                    radius = 160.dp.toPx(),
                    center = Offset(cx - 60.dp.toPx(), 40.dp.toPx())
                )

                // Círculos concéntricos de radar/seguridad
                drawCircle(
                    color = colorPrincipal.copy(alpha = 0.15f),
                    radius = 110.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
                drawCircle(
                    color = colorPrincipal.copy(alpha = 0.10f),
                    radius = 160.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            Column(modifier = Modifier.padding(18.dp)) {
                // TÍTULO HERO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = "Resumen de auditoría",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Eventos de seguridad y control del sistema",
                            fontSize = 12.sp,
                            color = colors.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(colorPrincipal, colorPrincipal.copy(alpha = 0.7f))
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // MALLA 2x2 DE KPIS
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniKpiBox(
                        modifier = Modifier.weight(1f),
                        titulo = "Total eventos",
                        valor = "$totalRegistros",
                        icono = Icons.Outlined.HistoryEdu,
                        colorFondo = Color(0xFF1E293B),
                        colorAcento = Color(0xFF38BDF8)
                    )
                    MiniKpiBox(
                        modifier = Modifier.weight(1f),
                        titulo = "Usuarios activos",
                        valor = "$usuariosActivos",
                        icono = Icons.Outlined.Group,
                        colorFondo = Color(0xFF064E3B).copy(alpha = 0.85f),
                        colorAcento = Color(0xFF34D399)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniKpiBox(
                        modifier = Modifier.weight(1f),
                        titulo = "Módulos",
                        valor = "$modulos",
                        icono = Icons.Outlined.Layers,
                        colorFondo = Color(0xFF312E81).copy(alpha = 0.85f),
                        colorAcento = Color(0xFFA78BFA)
                    )
                    MiniKpiBox(
                        modifier = Modifier.weight(1f),
                        titulo = "Acciones hoy",
                        valor = "$accionesHoy",
                        icono = Icons.Outlined.Today,
                        colorFondo = Color(0xFF78350F).copy(alpha = 0.85f),
                        colorAcento = Color(0xFFFBBF24)
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
    colorFondo: Color,
    colorAcento: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = colorFondo
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(colorAcento.copy(alpha = 0.18f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorAcento,
                    modifier = Modifier.size(18.dp)
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
| TARJETA: DISTRIBUCIÓN DE MOVIMIENTOS VS SEGURIDAD (DONUT CHART)
|--------------------------------------------------------------------------
*/

@Composable
private fun CardDistribucionAuditoria(
    movimientosCount: Int,
    seguridadCount: Int,
    colorPrincipal: Color
) {
    val colors = MaterialTheme.colorScheme
    val total = (movimientosCount + seguridadCount).coerceAtLeast(1)
    val pctMov = (movimientosCount.toFloat() / total * 100f)
    val pctSeg = 100f - pctMov

    val colorMov = Color(0xFF10B981)
    val colorSeg = Color(0xFFF43F5E)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Distribución de actividad",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface
            )
            Text(
                text = "Porcentaje de operaciones por ámbito",
                fontSize = 12.sp,
                color = colors.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                // Leyenda Izquierda: Movimientos
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Movimientos", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "%.1f %%".format(pctMov),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorMov
                    )
                    Text(
                        text = "$movimientosCount reg.",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                }

                // DONUT CHART
                Box(
                    modifier = Modifier.size(105.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                        val strokeW = 10.dp.toPx()
                        val sweepMov = (pctMov / 100f) * 360f
                        val sweepSeg = 360f - sweepMov

                        // Movimientos Arc
                        drawArc(
                            color = colorMov,
                            startAngle = -90f,
                            sweepAngle = sweepMov,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round),
                            size = Size(size.width, size.height)
                        )

                        // Seguridad Arc
                        drawArc(
                            color = colorSeg,
                            startAngle = -90f + sweepMov,
                            sweepAngle = sweepSeg,
                            useCenter = false,
                            style = Stroke(width = strokeW, cap = StrokeCap.Round),
                            size = Size(size.width, size.height)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "100%",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface
                        )
                        Text(
                            text = "Auditado",
                            fontSize = 9.sp,
                            color = colors.onSurfaceVariant
                        )
                    }
                }

                // Leyenda Derecha: Roles y Seguridad
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Seguridad", fontSize = 12.sp, color = colors.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "%.1f %%".format(pctSeg),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorSeg
                    )
                    Text(
                        text = "$seguridadCount reg.",
                        fontSize = 11.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // BARRA HORIZONTAL BICOLOR (IGUAL A CAJA)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .weight(pctMov.coerceAtLeast(1f))
                        .fillMaxSize()
                        .background(colorMov)
                )
                Box(
                    modifier = Modifier
                        .weight(pctSeg.coerceAtLeast(1f))
                        .fillMaxSize()
                        .background(colorSeg)
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| ITEM DE REGISTRO EN BITÁCORA
|--------------------------------------------------------------------------
*/

@Composable
private fun ItemRegistroBitacora(
    item: BitacoraItem,
    colorPrincipal: Color
) {
    val colors = MaterialTheme.colorScheme

    // Distinción de colores por tipo de módulo
    val colorModulo = when {
        item.modulo.contains("movimiento", ignoreCase = true) -> Color(0xFF10B981)
        item.modulo.contains("rol", ignoreCase = true) -> Color(0xFFA855F7)
        item.modulo.contains("asamblea", ignoreCase = true) -> Color(0xFF3B82F6)
        item.modulo.contains("usuario", ignoreCase = true) -> Color(0xFFF59E0B)
        else -> colorPrincipal
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colorModulo.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, colorModulo.copy(alpha = 0.30f))
                ) {
                    Text(
                        text = item.modulo.uppercase(),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorModulo
                    )
                }

                Text(
                    text = "${item.fecha} • ${item.hora}",
                    fontSize = 11.sp,
                    color = colors.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.descripcion,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val inicial = item.usuario.firstOrNull()?.uppercaseChar()?.toString() ?: "U"
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(colorModulo.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = inicial,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorModulo
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = item.usuario,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurface
                        )
                        if (item.email.isNotBlank()) {
                            Text(
                                text = item.email,
                                fontSize = 10.5.sp,
                                color = colors.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = colors.surfaceVariant.copy(alpha = 0.6f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lan,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item.ip,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}