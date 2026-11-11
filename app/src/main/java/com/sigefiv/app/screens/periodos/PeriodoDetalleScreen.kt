@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.periodos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.PeriodoDetalle
import com.sigefiv.app.ui.components.AppBottomBar
import com.sigefiv.app.viewmodel.PeriodosViewModel

private val ColorIngreso = Color(0xFF16A34A)
private val ColorEgreso = Color(0xFFDC2626)

@Composable
fun PeriodoDetalleScreen(
    periodosViewModel: PeriodosViewModel,
    periodoId: Int,
    onBackClick: () -> Unit,
    onMovimientoClick: (Movimiento) -> Unit,
    onInicioClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onEmprendimientosClick: () -> Unit,
    onPeriodosClick: () -> Unit,
    onMiCuentaClick: () -> Unit,

) {
    val primaryColor = MaterialTheme.colorScheme.primary

    val periodo by periodosViewModel.periodoDetalle.collectAsState()
    val movimientos by periodosViewModel.movimientos.collectAsState()
    val cargando by periodosViewModel.cargando.collectAsState()
    val mensaje by periodosViewModel.mensaje.collectAsState()
    val filtroMovimientos by periodosViewModel.filtroMovimientos.collectAsState()

    LaunchedEffect(periodoId) {
        periodosViewModel.cargarPeriodo(periodoId)
        periodosViewModel.cargarMovimientosPeriodo(periodoId)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = periodo?.nombre_completo ?: "Detalle del período",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Auditoría contable y balances",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                pantallaActual = "Periodos",
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
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            when {
                cargando -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = primaryColor)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Obteniendo datos del período...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                !mensaje.isNullOrBlank() -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.errorContainer,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = mensaje ?: "Ocurrió un problema al cargar el período.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                periodo == null -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = "No se encontraron datos para este período.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    val datos = periodo!!

                    // ENCABEZADO HEADER TIPO RESUMEN VOUCHER
                    HeaderPeriodoHero(datos = datos, primaryColor = primaryColor)

                    // SECCIÓN RESUMEN FINANCIERO
                    Text(
                        text = "RESUMEN FINANCIERO",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )

                    // TARJETA CONTABLE DE METRICAS
                    CardResumenFinanciero(datos = datos, primaryColor = primaryColor)

                    // TARJETAS SECUNDARIAS DUALES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SmallSummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.ReceiptLong,
                            iconColor = primaryColor,
                            label = "Movimientos",
                            value = "${datos.total_movimientos} reg."
                        )
                        SmallSummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.CalendarToday,
                            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "Cierre contable",
                            value = formatFechaCierre(datos.fecha_cierre)
                        )
                    }

                    // SECCIÓN DE MOVIMIENTOS DEL PERÍODO
                    Text(
                        text = "MOVIMIENTOS DEL PERÍODO",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )

                    FiltrosMovimientosPeriodo(
                        filtroSeleccionado = filtroMovimientos,
                        primaryColor = primaryColor,
                        onFiltroSeleccionado = { periodosViewModel.setFiltroMovimientos(it) }
                    )

                    val movimientosFiltrados = remember(movimientos, filtroMovimientos) {
                        when (filtroMovimientos) {
                            "Ingresos" -> movimientos.filter { it.tipo.trim().startsWith("ingreso", ignoreCase = true) }
                            "Egresos" -> movimientos.filter { it.tipo.trim().startsWith("egreso", ignoreCase = true) }
                            else -> movimientos
                        }
                    }

                    if (movimientosFiltrados.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(26.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = when (filtroMovimientos) {
                                        "Ingresos" -> "No hay ingresos registrados en este período."
                                        "Egresos" -> "No hay egresos registrados en este período."
                                        else -> "No hay movimientos registrados en este período."
                                    },
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            movimientosFiltrados.forEach { mov ->
                                MovimientoCardItem(
                                    movimiento = mov,
                                    primaryColor = primaryColor,
                                    onClick = { onMovimientoClick(mov) }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| COMPONENTES AUXILIARES ACTUALIZADOS
|--------------------------------------------------------------------------
*/

@Composable
private fun HeaderPeriodoHero(
    datos: PeriodoDetalle,
    primaryColor: Color
) {
    val isAbierto = datos.estado.equals("Abierto", ignoreCase = true)
    val colorEstado = if (isAbierto) ColorIngreso else MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                            contentDescription = null,
                            tint = primaryColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = datos.nombre_completo,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAbierto) "Balance contable activo" else "Auditoría contable finalizada",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = colorEstado.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, colorEstado.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isAbierto) Icons.Outlined.LockOpen else Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = colorEstado,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isAbierto) "Abierto" else "Cerrado",
                            color = colorEstado,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saldo final en caja",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "S/ %.2f".format(datos.saldo_caja),
                        color = if (datos.saldo_caja >= 0) ColorIngreso else ColorEgreso,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Disponible acumulado",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "S/ %.2f".format(datos.saldo_disponible),
                        color = if (datos.saldo_disponible >= 0) ColorIngreso else ColorEgreso,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CardResumenFinanciero(
    datos: PeriodoDetalle,
    primaryColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            SummaryRowItem(
                icon = Icons.Outlined.AccountBalanceWallet,
                iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                label = "Saldo anterior",
                value = datos.saldo_anterior,
                valueColor = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.8.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.ArrowUpward,
                iconColor = ColorIngreso,
                label = "Total de ingresos",
                value = datos.total_ingresos,
                valueColor = ColorIngreso,
                isPositive = true
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.8.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.ArrowDownward,
                iconColor = ColorEgreso,
                label = "Total de egresos",
                value = datos.total_egresos,
                valueColor = ColorEgreso,
                isNegative = true
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.8.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.AccountBalance,
                iconColor = primaryColor,
                label = "Saldo disponible",
                subLabel = "Saldo anterior + ingresos",
                value = datos.saldo_disponible,
                valueColor = primaryColor,
                isHighlight = true
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                thickness = 0.8.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.AccountBalanceWallet,
                iconColor = if (datos.saldo_caja >= 0) ColorIngreso else ColorEgreso,
                label = "Saldo en caja",
                subLabel = "Balance final neto",
                value = datos.saldo_caja,
                valueColor = if (datos.saldo_caja >= 0) ColorIngreso else ColorEgreso,
                isHighlight = true
            )
        }
    }
}

@Composable
private fun SummaryRowItem(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    subLabel: String? = null,
    value: Double,
    valueColor: Color,
    isPositive: Boolean = false,
    isNegative: Boolean = false,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isHighlight) iconColor.copy(alpha = 0.04f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(iconColor.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
            )
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
            }
        }

        val prefix = when {
            isPositive -> "+ "
            isNegative -> "- "
            else -> ""
        }

        Text(
            text = "$prefix S/ %.2f".format(value),
            color = valueColor,
            fontSize = if (isHighlight) 15.sp else 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SmallSummaryCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FiltrosMovimientosPeriodo(
    filtroSeleccionado: String,
    primaryColor: Color,
    onFiltroSeleccionado: (String) -> Unit
) {
    val filtros = listOf("Todos", "Ingresos", "Egresos")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filtros.forEach { filtro ->
            val seleccionado = filtroSeleccionado == filtro
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onFiltroSeleccionado(filtro) },
                shape = RoundedCornerShape(12.dp),
                color = if (seleccionado) primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                border = BorderStroke(
                    1.dp,
                    if (seleccionado) primaryColor else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = filtro,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center,
                    color = if (seleccionado) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MovimientoCardItem(
    movimiento: Movimiento,
    primaryColor: Color,
    onClick: () -> Unit
) {
    val esIngreso = movimiento.tipo.equals("Ingreso", ignoreCase = true)
    val colorMovimiento = if (esIngreso) ColorIngreso else ColorEgreso

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
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
                    .background(colorMovimiento.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esIngreso) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    contentDescription = movimiento.tipo,
                    tint = colorMovimiento,
                    modifier = Modifier.size(19.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.concepto,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
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
                    Text(text = "•", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    Text(
                        text = movimiento.fecha ?: "Sin fecha",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = if (esIngreso) "+ S/ %.2f".format(movimiento.monto) else "- S/ %.2f".format(movimiento.monto),
                color = colorMovimiento,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun formatFechaCierre(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "Pendiente"
    return try {
        val fechaParte = fecha.trim().substringBefore(" ")
        val partes = fechaParte.split("-")
        if (partes.size == 3) {
            "${partes[2]}/${partes[1]}/${partes[0]}"
        } else {
            fecha
        }
    } catch (_: Exception) {
        fecha
    }
}