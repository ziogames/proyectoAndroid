@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.dashboard

import android.content.Context
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.R
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.PeriodoDashboard
import com.sigefiv.app.notifications.NotificacionEventBus
import com.sigefiv.app.viewmodel.DashboardViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModel
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.layout.fillMaxSize
import com.sigefiv.app.ui.theme.AppSeason
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)
//private val VerdePrincipal = Color(0xFF15803D)
private val VerdeSuave = Color(0xFFDCFCE7)
private val Turquesa = Color(0xFF15803D)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisClaro = Color(0xFF64748B)
private val Verde = Color(0xFF15803D)
private val Rojo = Color(0xFFDC2626)

/*
|--------------------------------------------------------------------------
| DASHBOARD
|--------------------------------------------------------------------------
*/

@Composable
fun DashboardScreen(
    context: Context,
    dashboardViewModel: DashboardViewModel,
    movimientosViewModel: MovimientosViewModel,
    notificacionesNoLeidas: Int = 0,
    onNotificacionesClick: () -> Unit = {},
    onMovimientosClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {},
    onSigiClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val sessionManager = remember { SessionManager(context) }

    val nombreUsuario by sessionManager.nombre.collectAsState(initial = "Usuario")
    val seudonimo by sessionManager.seudonimo.collectAsState(initial = null)
    val rol by sessionManager.rol.collectAsState(initial = null)

    val nombreMostrar =
        seudonimo?.trim()?.takeIf { it.isNotEmpty() }
            ?: (nombreUsuario?.trim()?.takeIf { it.isNotEmpty() } ?: "Usuario")

    /*
     * Solo el Tesorero puede acceder al módulo completo
     * de Movimientos.
     *
     * Los demás roles siguen viendo los movimientos
     * recientes en el Dashboard.
     */
    val puedeVerMovimientos =
        rol?.equals("Tesorero", ignoreCase = true) == true

    val periodo by dashboardViewModel.periodo.collectAsState()
    val movimientos by movimientosViewModel.movimientos.collectAsState()

    /*
     * Carga inicial y sincronización en tiempo real.
     *
     * Cuando otro dispositivo registra, actualiza o elimina
     * un movimiento, Laravel envía el evento FCM:
     *
     * tipo = movimiento_actualizado
     *
     * El EventBus recibe el evento y aquí recargamos
     * nuevamente la información del Dashboard y los
     * movimientos recientes.
     */
    LaunchedEffect(Unit) {

        // Carga inicial de los movimientos.
        movimientosViewModel.cargarMovimientos()

        // Escucha cambios realizados desde otros dispositivos.
        NotificacionEventBus.movimientoActualizado.collect {

            // Actualiza saldo, ingresos y egresos.
            dashboardViewModel.cargarDashboard()

            // Actualiza movimientos recientes.
            movimientosViewModel.cargarMovimientos()
        }
    }

    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SIGEFIV",
                        color = Blanco,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
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
                    IconButton(onClick = onSigiClick) {
                        Icon(
                            imageVector = Icons.Outlined.SmartToy,
                            contentDescription = "SIGI",
                            tint = Blanco
                        )
                    }

                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        IconButton(
                            onClick = onNotificacionesClick
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = Blanco
                            )
                        }

                        if (notificacionesNoLeidas > 0) {
                            Box(
                                modifier = Modifier
                                    .size(19.dp)
                                    .offset(
                                        x = 7.dp,
                                        y = (-7).dp
                                    )
                                    .clip(CircleShape)
                                    .background(Rojo),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (notificacionesNoLeidas > 99) {
                                        "99+"
                                    } else {
                                        notificacionesNoLeidas.toString()
                                    },
                                    color = Blanco,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                    // Se eliminó .fillMaxSize() para que el Box lo centre perfecto
                                )
                            }
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
        bottomBar = {
            SigefivBottomBar(
                onInicioClick = { },
                onAsambleasClick = onAsambleasClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->
        DashboardContenido(
            modifier = Modifier.padding(innerPadding),
            nombreUsuario = nombreMostrar,
            periodo = periodo,
            movimientos = movimientos,
            puedeVerMovimientos = puedeVerMovimientos,
            onMovimientosClick = onMovimientosClick
        )
    }
}

/*
|--------------------------------------------------------------------------
| CONTENIDO DEL DASHBOARD
|--------------------------------------------------------------------------
*/

@Composable
private fun DashboardContenido(
    modifier: Modifier = Modifier,
    nombreUsuario: String,
    periodo: PeriodoDashboard?,
    movimientos: List<Movimiento>,
    puedeVerMovimientos: Boolean,
    onMovimientosClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val temporada = SeasonalTheme.getSeason()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FondoSIGEFIV)
    ) {

        // Decoración de Fiestas Patrias: se mantiene sin cambios.
        if (temporada == AppSeason.FIESTAS_PATRIAS) {
            Image(
                painter = painterResource(id = R.drawable.bandera_fiestas_patrias),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth,
                alpha = 0.22f
            )
        }

        // Decoración navideña: solo aparece del 1 de diciembre al 6 de enero.
        if (temporada == AppSeason.NAVIDAD) {
            Image(
                painter = painterResource(id = R.drawable.fondo_navidad_dashboard),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth,
                alpha = 0.22f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Column {
                Text(
                    text = "Hola, $nombreUsuario",
                    color = TextoPrincipal,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Bienvenido nuevamente a SIGEFIV",
                    color = GrisClaro,
                    fontSize = 13.sp
                )
            }

            SaldoCard(periodo = periodo)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                ResumenCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Ingresos",
                    monto = "S/ %.2f".format(periodo?.ingresos ?: 0.0),
                    icono = Icons.Outlined.ArrowUpward,
                    color = Verde
                )

                ResumenCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Egresos",
                    monto = "S/ %.2f".format(periodo?.egresos ?: 0.0),
                    icono = Icons.Outlined.ArrowDownward,
                    color = Rojo
                )
            }

            CardPeriodo(periodo = periodo)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "Movimientos recientes",
                    color = TextoPrincipal,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                if (puedeVerMovimientos) {
                    Text(
                        text = "Ver todos",
                        color = SeasonalColors.primary(
                            SeasonalTheme.getSeason()
                        ),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onMovimientosClick()
                        }
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                movimientos.forEach { movimiento ->
                    MovimientoItem(movimiento = movimiento)
                }

                if (movimientos.isEmpty()) {
                    Text(
                        text = "Cargando movimientos...",
                        color = GrisClaro,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA DE SALDO
|--------------------------------------------------------------------------
*/

@Composable
private fun SaldoCard(periodo: PeriodoDashboard?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = FondoTarjeta
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.5.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {

                Text(
                    text = "SALDO DISPONIBLE",
                    color = GrisClaro,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "S/ %.2f".format(
                        periodo?.saldo_final ?: 0.0
                    ),
                    color = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    ),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = periodo?.nombre ?: "Sin período",
                    color = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    ),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = VerdeSuave,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = "Saldo disponible",
                    tint = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    ),
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA RESUMEN
|--------------------------------------------------------------------------
*/

@Composable
private fun ResumenCard(
    modifier: Modifier,
    titulo: String,
    monto: String,
    icono: ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = FondoTarjeta
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.5.dp
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = titulo,
                    color = Color(0xFF334155),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = monto,
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| CARD PERIODO
|--------------------------------------------------------------------------
*/

@Composable
private fun CardPeriodo(periodo: PeriodoDashboard?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = FondoTarjeta
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.5.dp
        )
    ) {

        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = VerdeSuave,
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Outlined.Assignment,
                    contentDescription = "Periodo",
                    tint = Turquesa,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {

                Text(
                    text = "Periodo actual",
                    color = GrisClaro,
                    fontSize = 11.sp
                )

                Text(
                    text = periodo?.nombre ?: "Sin período",
                    color = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    ),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| MOVIMIENTO
|--------------------------------------------------------------------------
*/

@Composable
private fun MovimientoItem(movimiento: Movimiento) {

    val ingreso =
        movimiento.tipo.equals(
            "Ingreso",
            ignoreCase = true
        )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = FondoTarjeta
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
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
                    imageVector =
                        if (ingreso) {
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

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = movimiento.concepto,
                    color = TextoPrincipal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = movimiento.fecha ?: "Sin fecha",
                        color = GrisClaro,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "•",
                        color = GrisClaro,
                        fontSize = 11.sp
                    )

                    Text(
                        text = movimiento.categoria ?: "General",
                        color = Turquesa,
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                text =
                    if (ingreso) {
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

/*
|--------------------------------------------------------------------------
| BARRA INFERIOR (Inicio, Asamblea, Periodos, Mi cuenta)
|--------------------------------------------------------------------------
*/

@Composable
private fun SigefivBottomBar(
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = SeasonalColors.primary(
            SeasonalTheme.getSeason()
        ),
        tonalElevation = 0.dp
    ) {

        NavigationBarItem(
            selected = true,
            onClick = onInicioClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text(text = "Inicio")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = SeasonalColors.primary(
                    SeasonalTheme.getSeason()
                ),
                selectedTextColor = Blanco,
                indicatorColor = Blanco,
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
            label = {
                Text(text = "Asamblea")
            },
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
            label = {
                Text(text = "Periodos")
            },
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
            label = {
                Text(text = "Mi cuenta")
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )
    }
}