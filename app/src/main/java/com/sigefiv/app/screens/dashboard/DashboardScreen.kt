@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.dashboard

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.R
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.PeriodoDashboard
import com.sigefiv.app.notifications.NotificacionEventBus
import com.sigefiv.app.ui.components.AppBottomBar
import com.sigefiv.app.ui.theme.AppSeason
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.DashboardViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModel
import java.util.Calendar
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV PROFESIONALES
|--------------------------------------------------------------------------
*/

private val VerdeCorporativoOscuro = Color(0xFF0F4C3A)
private val VerdeCorporativoMedio = Color(0xFF15803D)
private val VerdeGrisaceoTenue = Color(0xFFF0FDF4)

private val TextoOscuro = Color(0xFF0F172A)
private val TextoGris = Color(0xFF64748B)
private val GrisBorde = Color(0xFFE2E8F0)

private val VerdeIngreso = Color(0xFF16A34A)
private val RojoEgreso = Color(0xFFDC2626)

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
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {},
    onSigiClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    darkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {
    val sessionManager = remember { SessionManager(context) }

    val nombreUsuario by sessionManager.nombre.collectAsState(initial = "Usuario")
    val seudonimo by sessionManager.seudonimo.collectAsState(initial = null)
    val rol by sessionManager.rol.collectAsState(initial = null)

    val permisos by sessionManager.permisos.collectAsState(initial = emptySet())
    val puedeUsarZoe = "zoe.llamada" in permisos

    val nombreMostrar = seudonimo?.trim()?.takeIf { it.isNotEmpty() }
        ?: (nombreUsuario?.trim()?.takeIf { it.isNotEmpty() } ?: "Usuario")

    val puedeVerMovimientos = rol?.equals("Tesorero", ignoreCase = true) == true

    val periodo by dashboardViewModel.periodo.collectAsState()
    val movimientos by dashboardViewModel.movimientos.collectAsState()

    LaunchedEffect(Unit) {
        dashboardViewModel.cargarDashboard()
        NotificacionEventBus.movimientoActualizado.collect {
            dashboardViewModel.cargarDashboard()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SIGEFIV",
                            color = if (darkTheme) Color.White else VerdeCorporativoOscuro,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "Abrir menú",
                            tint = if (darkTheme) Color.White else TextoOscuro
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (darkTheme) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                            contentDescription = if (darkTheme) "Modo claro" else "Modo oscuro",
                            tint = if (darkTheme) Color.White else TextoGris
                        )
                    }

                    if (puedeUsarZoe) {
                        IconButton(onClick = onSigiClick) {
                            Icon(
                                imageVector = Icons.Outlined.SmartToy,
                                contentDescription = "Sigi inteligente",
                                tint = if (darkTheme) Color.White else VerdeCorporativoMedio
                            )
                        }
                    }

                    Box(
                        modifier = Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = onNotificacionesClick) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificaciones",
                                tint = if (darkTheme) Color.White else TextoOscuro
                            )
                        }

                        if (notificacionesNoLeidas > 0) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .background(RojoEgreso, shape = CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (notificacionesNoLeidas > 99) "99+" else notificacionesNoLeidas.toString(),
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                pantallaActual = "Inicio",
                onInicioClick = { /* Ya estás en inicio */ },
                onAsambleasClick = onAsambleasClick,
                onEmprendimientosClick = onEmprendimientosClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->
        DashboardContenido(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            nombreUsuario = nombreMostrar,
            periodo = periodo,
            movimientos = movimientos,
            cargandoMovimientos = false,
            puedeVerMovimientos = puedeVerMovimientos,
            onMovimientosClick = onMovimientosClick,
            darkTheme = darkTheme
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
    cargandoMovimientos: Boolean,
    puedeVerMovimientos: Boolean,
    onMovimientosClick: () -> Unit,
    darkTheme: Boolean
) {
    val scrollState = rememberScrollState()
    val temporada = SeasonalTheme.getSeason()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (temporada == AppSeason.FIESTAS_PATRIAS) {
            Image(
                painter = painterResource(id = R.drawable.bandera_fiestas_patrias),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth,
                alpha = 0.12f
            )
        }

        if (temporada == AppSeason.NAVIDAD) {
            Image(
                painter = painterResource(id = R.drawable.fondo_navidad_dashboard),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.FillWidth,
                alpha = 0.12f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val primerNombre = remember(nombreUsuario) {
                nombreUsuario
                    .trim()
                    .split("\\s+".toRegex())
                    .firstOrNull()
                    ?.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                    ?: "Usuario"
            }

            val saludo = remember(primerNombre) {
                val hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                when {
                    hora < 12 -> "¡Buenos días, $primerNombre! ☀️"
                    hora < 18 -> "¡Buenas tardes, $primerNombre! 😊"
                    else -> "¡Buenas noches, $primerNombre! 🌙"
                }
            }

            val frasesBienvenida = remember {
                listOf(
                    "Aquí tienes tu información actualizada",
                    "Tus finanzas y cuentas están al día",
                    "El balance del período está listo",
                    "Resumen de ingresos y egresos preparado",
                    "Panel listo con los últimos movimientos",
                    "Todo preparado para gestionar hoy"
                )
            }
            val fraseAleatoria = remember { frasesBienvenida.random() }

            Column {
                Text(
                    text = saludo,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = fraseAleatoria,
                    color = TextoGris,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            SaldoCard(
                periodo = periodo,
                darkTheme = darkTheme
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Ingresos",
                    monto = "S/ %,.2f".format(periodo?.ingresos ?: 0.0),
                    icono = Icons.Outlined.ArrowUpward,
                    color = VerdeIngreso,
                    darkTheme = darkTheme
                )

                ResumenCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Egresos",
                    monto = "S/ %,.2f".format(periodo?.egresos ?: 0.0),
                    icono = Icons.Outlined.ArrowDownward,
                    color = RojoEgreso,
                    darkTheme = darkTheme
                )
            }

            FiltroPeriodo(
                periodo = periodo,
                darkTheme = darkTheme
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Movimientos recientes",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                if (puedeVerMovimientos) {
                    Text(
                        text = "Ver todos",
                        color = VerdeCorporativoMedio,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onMovimientosClick() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                movimientos.forEach { movimiento ->
                    MovimientoItem(
                        movimiento = movimiento,
                        darkTheme = darkTheme
                    )
                }

                if (cargandoMovimientos) {
                    Text(
                        text = "Cargando movimientos...",
                        color = TextoGris,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else if (movimientos.isEmpty()) {
                    Text(
                        text = "No hay movimientos para mostrar.",
                        color = TextoGris,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA DE SALDO
|--------------------------------------------------------------------------
*/

@Composable
private fun SaldoCard(
    periodo: PeriodoDashboard?,
    darkTheme: Boolean
) {
    var saldoVisible by remember { mutableStateOf(true) }

    val gradienteFondo = if (darkTheme) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF064E3B), Color(0xFF0F766E))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF063A2B), Color(0xFF145E45))
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradienteFondo)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.05f), Color.Transparent),
                            radius = 400f
                        )
                    )
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "SALDO DISPONIBLE",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                                .clickable { saldoVisible = !saldoVisible },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (saldoVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                                contentDescription = if (saldoVisible) "Ocultar saldo" else "Mostrar saldo",
                                tint = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    androidx.compose.animation.AnimatedContent(
                        targetState = saldoVisible,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        },
                        label = "AnimacionSaldo"
                    ) { visible ->
                        Text(
                            text = if (visible) {
                                "S/ %,.2f".format(periodo?.saldo_final ?: 0.0)
                            } else {
                                "S/ ••••••"
                            },
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = periodo?.nombre ?: "Sin período",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "Billetera",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA DE RESUMEN
|--------------------------------------------------------------------------
*/

@Composable
private fun ResumenCard(
    modifier: Modifier,
    titulo: String,
    monto: String,
    icono: ImageVector,
    color: Color,
    darkTheme: Boolean
) {
    val fondoCard = if (darkTheme) {
        MaterialTheme.colorScheme.surface
    } else {
        color.copy(alpha = 0.04f)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondoCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (darkTheme) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f) else color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(color.copy(alpha = 0.12f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = titulo,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = monto,
                    color = color,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| INDICADOR DE PERÍODO
|--------------------------------------------------------------------------
*/

@Composable
private fun FiltroPeriodo(
    periodo: PeriodoDashboard?,
    darkTheme: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (darkTheme) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else VerdeGrisaceoTenue
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = VerdeCorporativoMedio.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarToday,
                    contentDescription = "Periodo actual",
                    tint = VerdeCorporativoMedio,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column {
                Text(
                    text = "Periodo actual",
                    color = if (darkTheme) Color.LightGray else TextoGris,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = periodo?.nombre ?: "Sin período",
                    color = if (darkTheme) Color.White else VerdeCorporativoOscuro,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| ÍTEM DE MOVIMIENTO
|--------------------------------------------------------------------------
*/

@Composable
private fun MovimientoItem(
    movimiento: Movimiento,
    darkTheme: Boolean
) {
    val ingreso = movimiento.tipo.equals("Ingreso", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = if (darkTheme) Color.Transparent else GrisBorde,
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(
                        color = if (ingreso) VerdeIngreso.copy(alpha = 0.1f) else RojoEgreso.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (ingreso) Icons.Outlined.ArrowUpward else Icons.Outlined.ArrowDownward,
                    contentDescription = movimiento.concepto,
                    tint = if (ingreso) VerdeIngreso else RojoEgreso,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movimiento.concepto.replaceFirstChar { it.uppercase() },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movimiento.fecha ?: "Sin fecha",
                        color = TextoGris,
                        fontSize = 11.sp
                    )
                    Text(
                        text = "•",
                        color = TextoGris,
                        fontSize = 11.sp
                    )
                    Text(
                        text = movimiento.categoria ?: "General",
                        color = VerdeCorporativoMedio,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Text(
                text = if (ingreso) "+ S/ %,.2f".format(movimiento.monto) else "- S/ %,.2f".format(movimiento.monto),
                color = if (ingreso) VerdeIngreso else RojoEgreso,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}