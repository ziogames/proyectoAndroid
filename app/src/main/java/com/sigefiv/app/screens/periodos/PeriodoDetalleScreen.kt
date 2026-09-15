@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.periodos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.viewmodel.PeriodosViewModel
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES PROFESIONAL (ESTILO MODERNO CONTABLE)
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF1F5F9)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdeSuave = Color(0xFFCCFBF1)
private val VerdeTexto = Color(0xFF0D9488)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisSecundario = Color(0xFF475569)
private val GrisClaro = Color(0xFF94A3B8)
private val GrisBorde = Color(0xFFE2E8F0)

private val RojoEgreso = Color(0xFFE11D48)
private val RojoSuave = Color(0xFFFFE4E6)
private val AzulSaldo = Color(0xFF2563EB)
private val AzulSuave = Color(0xFFDBEAFE)
private val MoradoCaja = Color(0xFF7C3AED)
private val MoradoSuave = Color(0xFFA78BFA)

private val NaranjaText = Color(0xFFD97706)
private val NaranjaBg = Color(0xFFFEF3C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodoDetalleScreen(
    periodosViewModel: PeriodosViewModel,
    periodoId: Int,
    onBackClick: () -> Unit,
    onMovimientoClick: (Movimiento) -> Unit,
    onMovimientosClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onMovimientosPrincipalClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onMiCuentaClick: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val periodo by periodosViewModel.periodoDetalle.collectAsState()
    val movimientos by periodosViewModel.movimientos.collectAsState()
    val cargando by periodosViewModel.cargando.collectAsState()
    val mensaje by periodosViewModel.mensaje.collectAsState()

    LaunchedEffect(periodoId) {
        periodosViewModel.cargarPeriodo(periodoId)
        periodosViewModel.cargarMovimientosPeriodo(periodoId)
    }

    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Resumen - ${periodo?.nombre_completo ?: "Período"}",
                            color = Blanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Auditoría contable y balances",
                            color = Blanco.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Blanco
                        )
                    }
                },
                actions = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Abrir menú",
                                tint = Blanco
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal
                )
            )
        },
        bottomBar = {
            BarraInferiorPeriodoDetalle(
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onPeriodosClick = onBackClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoSIGEFIV)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(2.dp))

            when {
                cargando -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = colorPrincipal)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Obteniendo datos del período...",
                                color = GrisSecundario,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
                !mensaje.isNullOrBlank() -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = RojoSuave
                    ) {
                        Text(
                            text = mensaje ?: "Ocurrió un problema al cargar el período.",
                            color = RojoEgreso,
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
                        color = Blanco
                    ) {
                        Text(
                            text = "No se encontraron datos para este período.",
                            color = GrisSecundario,
                            modifier = Modifier.padding(20.dp),
                            fontSize = 13.sp
                        )
                    }
                }
                else -> {
                    val datos = periodo!!

                    // ENCABEZADO HEADER TIPO BANCO (HERO)
                    HeaderPeriodoHero(datos = datos)

                    // SECCIÓN RESUMEN FINANCIERO
                    Text(
                        text = "RESUMEN FINANCIERO",
                        color = GrisSecundario,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                    )

                    // TARJETA CONTABLE DE METRICAS
                    CardResumenFinanciero(datos = datos)

                    // TARJETAS SECUNDARIAS DUALES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SmallSummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.ReceiptLong,
                            iconColor = AzulSaldo,
                            label = "Movimientos",
                            value = "${datos.total_movimientos} reg."
                        )
                        SmallSummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.CalendarToday,
                            iconColor = VerdeTexto,
                            label = "Cierre contable",
                            value = formatFechaCierre(datos.fecha_cierre)
                        )
                    }

                    // SECCIÓN DE MOVIMIENTOS DEL PERÍODO
                    var filtroMovimientos by remember { mutableStateOf("Todos") }

                    Text(
                        text = "MOVIMIENTOS DEL PERÍODO",
                        color = GrisSecundario,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    FiltrosMovimientosPeriodo(
                        filtroSeleccionado = filtroMovimientos,
                        onFiltroSeleccionado = { filtroMovimientos = it }
                    )

                    val movimientosFiltrados = when (filtroMovimientos) {
                        "Ingresos" -> movimientos.filter { it.tipo.equals("Ingreso", ignoreCase = true) }
                        "Egresos" -> movimientos.filter { it.tipo.equals("Egreso", ignoreCase = true) }
                        else -> movimientos
                    }

                    if (movimientosFiltrados.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = Blanco,
                            border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Outlined.ReceiptLong, contentDescription = null, tint = GrisClaro, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = when (filtroMovimientos) {
                                        "Ingresos" -> "No hay ingresos en este período."
                                        "Egresos" -> "No hay egresos en este período."
                                        else -> "No hay movimientos en este período."
                                    },
                                    color = GrisSecundario, fontSize = 13.sp, fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            movimientosFiltrados.forEach { mov ->
                                MovimientoCardItem(movimiento = mov, onClick = { onMovimientoClick(mov) })
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| COMPONENTES DISEÑADOS EXCLUSIVAMENTE PARA PANTALLA PROFESIONAL
|--------------------------------------------------------------------------
*/

@Composable
private fun HeaderPeriodoHero(datos: com.sigefiv.app.data.model.PeriodoDetalle) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val isAbierto = datos.estado.equals("Abierto", true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(colorPrincipal, colorPrincipal.copy(alpha = 0.85f))
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Blanco.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                        contentDescription = null,
                        tint = Blanco,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = datos.nombre_completo,
                            color = Blanco,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        val badgeBg = if (isAbierto) VerdeSuave else NaranjaBg
                        val badgeColor = if (isAbierto) VerdeTexto else NaranjaText
                        val badgeIcon = if (isAbierto) Icons.Outlined.LockOpen else Icons.Outlined.Lock

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = badgeBg
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = badgeIcon,
                                    contentDescription = null,
                                    tint = badgeColor,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isAbierto) "Abierto" else "Cerrado",
                                    color = badgeColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isAbierto) {
                            "Balance contable activo"
                        } else {
                            "Auditoría finalizada el ${formatFechaCierre(datos.fecha_cierre)}"
                        },
                        color = Blanco.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CardResumenFinanciero(datos: com.sigefiv.app.data.model.PeriodoDetalle) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(vertical = 6.dp)) {
            SummaryRowItem(
                icon = Icons.Outlined.AccountBalanceWallet,
                iconColor = AzulSaldo,
                label = "Saldo anterior",
                value = datos.saldo_anterior,
                valueColor = TextoPrincipal
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = FondoSIGEFIV,
                thickness = 1.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.ArrowUpward,
                iconColor = VerdeTexto,
                label = "Total de ingresos",
                value = datos.total_ingresos,
                valueColor = VerdeTexto,
                isPositive = true
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = FondoSIGEFIV,
                thickness = 1.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.AccountBalance,
                iconColor = colorPrincipal,
                label = "Saldo disponible",
                subLabel = "Acumulado disponible",
                value = datos.saldo_disponible,
                valueColor = colorPrincipal,
                isHighlight = true
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = FondoSIGEFIV,
                thickness = 1.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.ArrowDownward,
                iconColor = RojoEgreso,
                label = "Total de egresos",
                value = datos.total_egresos,
                valueColor = RojoEgreso,
                isNegative = true
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                color = FondoSIGEFIV,
                thickness = 1.dp
            )
            SummaryRowItem(
                icon = Icons.Outlined.AccountBalanceWallet,
                iconColor = MoradoCaja,
                label = "Saldo en caja",
                subLabel = "Balance final",
                value = datos.saldo_caja,
                valueColor = MoradoCaja,
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
            .background(if (isHighlight) iconColor.copy(alpha = 0.03f) else Color.Transparent)
            .padding(horizontal = 16.dp, vertical = 12.dp),
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
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextoPrincipal,
                fontSize = 13.sp,
                fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
            )
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = GrisClaro,
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
            fontSize = if (isHighlight) 16.sp else 14.sp,
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(iconColor.copy(alpha = 0.1f), CircleShape),
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
                    color = GrisClaro,
                    fontSize = 11.sp
                )
                Text(
                    text = value,
                    color = TextoPrincipal,
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
    onFiltroSeleccionado: (String) -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val filtros = listOf("Todos", "Ingresos", "Egresos")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        filtros.forEach { filtro ->
            val seleccionado = filtroSeleccionado == filtro
            Surface(
                modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).clickable { onFiltroSeleccionado(filtro) },
                shape = RoundedCornerShape(12.dp),
                color = if (seleccionado) colorPrincipal else Blanco,
                border = if (seleccionado) null else androidx.compose.foundation.BorderStroke(1.dp, GrisBorde)
            ) {
                Text(
                    text = filtro,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = if (seleccionado) Blanco else GrisSecundario,
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
    onClick: () -> Unit
) {
    val esIngreso = movimiento.tipo.equals("Ingreso", ignoreCase = true)
    val colorMovimiento = if (esIngreso) VerdeTexto else RojoEgreso
    val fondoIcono = if (esIngreso) VerdeSuave else RojoSuave

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(fondoIcono, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esIngreso) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    contentDescription = movimiento.tipo,
                    tint = colorMovimiento,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.concepto,
                    color = TextoPrincipal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${movimiento.fecha ?: "Sin fecha"} • ${movimiento.categoria ?: "General"}",
                    color = GrisClaro,
                    fontSize = 11.sp
                )
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

private val Transparent = Color(0x00000000)

@Composable
private fun BarraInferiorPeriodoDetalle(
    onInicioClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onPeriodosClick: () -> Unit,
    onMiCuentaClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    NavigationBar(
        containerColor = colorPrincipal,
        tonalElevation = 0.dp
    ) {

        // INICIO
        NavigationBarItem(
            selected = false,
            onClick = onInicioClick,
            icon = {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text("Inicio")
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(0.75f),
                unselectedTextColor = Blanco.copy(0.75f)
            )
        )

        // ASAMBLEAS
        NavigationBarItem(
            selected = false,
            onClick = onAsambleasClick,
            icon = {
                Icon(
                    Icons.Outlined.Groups,
                    contentDescription = "Asambleas"
                )
            },
            label = {
                Text("Asambleas")
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(0.75f),
                unselectedTextColor = Blanco.copy(0.75f)
            )
        )

        // PERÍODOS
        NavigationBarItem(
            selected = true,
            onClick = onPeriodosClick,
            icon = {
                Icon(
                    Icons.Outlined.CalendarToday,
                    contentDescription = "Períodos"
                )
            },
            label = {
                Text("Períodos")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorPrincipal,
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
                unselectedIconColor = Blanco.copy(0.75f),
                unselectedTextColor = Blanco.copy(0.75f)
            )
        )

        // MI CUENTA
        NavigationBarItem(
            selected = false,
            onClick = onMiCuentaClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Mi cuenta"
                )
            },
            label = {
                Text("Mi cuenta")
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(0.75f),
                unselectedTextColor = Blanco.copy(0.75f)
            )
        )
    }
}
