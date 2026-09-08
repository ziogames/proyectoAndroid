@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.periodos

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.automirrored.outlined.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.LockOpen
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.viewmodel.PeriodosViewModel

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV (ESTILO DASHBOARD / MODERNO)
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdePrincipal = Color(0xFF15803D)
private val VerdeSuave = Color(0xFFDCFCE7)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisClaro = Color(0xFF64748B)
private val Rojo = Color(0xFFDC2626)
private val Morado = Color(0xFF7C3AED)
private val Azul = Color(0xFF2563EB)
private val NaranjaText = Color(0xFFD97706)
private val NaranjaBg = Color(0xFFFEF3C7)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodoDetalleScreen(
    periodosViewModel: PeriodosViewModel,
    periodoId: Int,
    onBackClick: () -> Unit,
    onMovimientosClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onMovimientosPrincipalClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onMasClick: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val periodo by periodosViewModel.periodoDetalle.collectAsState()
    val cargando by periodosViewModel.cargando.collectAsState()
    val mensaje by periodosViewModel.mensaje.collectAsState()

    LaunchedEffect(periodoId) {
        periodosViewModel.cargarPeriodo(periodoId)
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
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Detalle contable y balances",
                            color = Blanco.copy(alpha = 0.85f),
                            fontSize = 11.sp
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
                    containerColor = VerdePrincipal
                )
            )
        },
        bottomBar = {
            BarraInferiorPeriodoDetalle(
                onInicioClick = onInicioClick,
                onMovimientosClick = onMovimientosPrincipalClick,
                onAsambleasClick = onAsambleasClick,
                onMasClick = onMasClick
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            when {
                cargando -> {
                    Text(
                        text = "Cargando resumen...",
                        color = GrisClaro,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
                !mensaje.isNullOrBlank() -> {
                    Text(
                        text = mensaje ?: "No se pudo cargar el período.",
                        color = Rojo,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
                periodo == null -> {
                    Text(
                        text = "No se encontró el período.",
                        color = GrisClaro,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
                else -> {
                    val datos = periodo!!

                    // ENCABEZADO DEL PERÍODO
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(VerdeSuave, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.TrendingUp,
                                    contentDescription = null,
                                    tint = VerdePrincipal,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = datos.nombre_completo,
                                        color = TextoPrincipal,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))

                                    val isAbierto = datos.estado.equals("Abierto", true)
                                    val badgeBg = if (isAbierto) VerdeSuave else NaranjaBg
                                    val badgeColor = if (isAbierto) VerdePrincipal else NaranjaText
                                    val badgeIcon = if (isAbierto) Icons.Outlined.LockOpen else Icons.Outlined.Lock

                                    Box(
                                        modifier = Modifier
                                            .background(badgeBg, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = badgeIcon,
                                                contentDescription = null,
                                                tint = badgeColor,
                                                modifier = Modifier.size(12.dp)
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

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = if (datos.estado.equals("Abierto", true)) {
                                        "Periodo contable en curso"
                                    } else {
                                        "Cerrado el ${formatFechaCierre(datos.fecha_cierre)}"
                                    },
                                    color = GrisClaro,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Text(
                        text = "RESUMEN FINANCIERO",
                        color = VerdePrincipal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    // CARD DE RESUMEN FINANCIERO
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            SummaryRow(
                                icon = Icons.Outlined.AccountBalanceWallet,
                                iconColor = Azul,
                                label = "Saldo anterior",
                                value = datos.saldo_anterior,
                                valueColor = TextoPrincipal
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = FondoSIGEFIV,
                                thickness = 1.dp
                            )
                            SummaryRow(
                                icon = Icons.Outlined.ArrowUpward,
                                iconColor = VerdePrincipal,
                                label = "Total de ingresos",
                                value = datos.total_ingresos,
                                valueColor = VerdePrincipal
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = FondoSIGEFIV,
                                thickness = 1.dp
                            )
                            SummaryRow(
                                icon = Icons.Outlined.AccountBalance,
                                iconColor = VerdePrincipal,
                                label = "Saldo disponible",
                                subLabel = "(Saldo anterior + Total ingresos)",
                                value = datos.saldo_disponible,
                                valueColor = VerdePrincipal
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = FondoSIGEFIV,
                                thickness = 1.dp
                            )
                            SummaryRow(
                                icon = Icons.Outlined.ArrowDownward,
                                iconColor = Rojo,
                                label = "Total de egresos",
                                value = datos.total_egresos,
                                valueColor = Rojo
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = FondoSIGEFIV,
                                thickness = 1.dp
                            )
                            SummaryRow(
                                icon = Icons.Outlined.AccountBalanceWallet,
                                iconColor = Morado,
                                label = "Saldo en caja",
                                subLabel = "(Saldo disponible - Total egresos)",
                                value = datos.saldo_caja,
                                valueColor = Morado
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SmallSummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.ReceiptLong,
                            iconColor = Azul,
                            label = "Total movimientos",
                            value = "${datos.total_movimientos}",
                            valueColor = TextoPrincipal
                        )
                        SmallSummaryCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.CalendarToday,
                            iconColor = VerdePrincipal,
                            label = "Fecha de cierre",
                            value = formatFechaCierre(datos.fecha_cierre),
                            valueColor = TextoPrincipal
                        )
                    }

                    // TARJETA DE VER MOVIMIENTOS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMovimientosClick(periodoId) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(VerdeSuave, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ReceiptLong,
                                    contentDescription = null,
                                    tint = VerdePrincipal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ver movimientos del período",
                                    color = TextoPrincipal,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Consultar todos los ingresos y egresos",
                                    color = GrisClaro,
                                    fontSize = 12.sp
                                )
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                                contentDescription = null,
                                tint = GrisClaro,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
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

@Composable
fun SummaryRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    subLabel: String? = null,
    value: Double,
    valueColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
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

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextoPrincipal,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            if (subLabel != null) {
                Text(
                    text = subLabel,
                    color = GrisClaro,
                    fontSize = 10.sp
                )
            }
        }

        Text(
            text = "S/ %.2f".format(value),
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SmallSummaryCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconColor: Color,
    label: String,
    value: String,
    valueColor: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = label,
                color = GrisClaro,
                fontSize = 11.sp
            )
            Text(
                text = value,
                color = valueColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun BarraInferiorPeriodoDetalle(
    onInicioClick: () -> Unit,
    onMovimientosClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onMasClick: () -> Unit
) {
    NavigationBar(
        containerColor = VerdePrincipal,
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
            selected = false,
            onClick = onMovimientosClick,
            icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = "Movimientos") },
            label = { Text("Movimientos") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(0.75f),
                unselectedTextColor = Blanco.copy(0.75f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onAsambleasClick,
            icon = { Icon(Icons.Outlined.Groups, contentDescription = "Asambleas") },
            label = { Text("Asambleas") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(0.75f),
                unselectedTextColor = Blanco.copy(0.75f)
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = onMasClick,
            icon = { Icon(Icons.Outlined.Menu, contentDescription = "Más") },
            label = { Text("Más") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = VerdePrincipal,
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
                unselectedIconColor = Blanco.copy(0.75f),
                unselectedTextColor = Blanco.copy(0.75f)
            )
        )
    }
}