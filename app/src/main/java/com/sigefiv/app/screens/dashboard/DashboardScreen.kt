
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
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.R
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.model.Movimiento
import com.sigefiv.app.data.model.PeriodoDashboard
import com.sigefiv.app.notifications.NotificacionEventBus
import com.sigefiv.app.ui.theme.AppSeason

import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.DashboardViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModel

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV
|--------------------------------------------------------------------------
*/

private val VerdeSuave = Color(0xFFDCFCE7)
private val Turquesa = Color(0xFF15803D)
private val Blanco = Color(0xFFFFFFFF)
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
    onOpenDrawer: () -> Unit = {},
    darkTheme: Boolean = false,
    onThemeToggle: () -> Unit = {}
) {

    val sessionManager = remember { SessionManager(context) }



    val nombreUsuario by sessionManager.nombre.collectAsState(initial = "Usuario")
    val seudonimo by sessionManager.seudonimo.collectAsState(initial = null)
    val rol by sessionManager.rol.collectAsState(initial = null)

    val nombreMostrar =
        seudonimo?.trim()?.takeIf { it.isNotEmpty() }
            ?: (nombreUsuario?.trim()?.takeIf { it.isNotEmpty() } ?: "Usuario")

    /*
    |--------------------------------------------------------------------------
    | PERMISOS
    |--------------------------------------------------------------------------
    */

    val puedeVerMovimientos =
        rol?.equals("Tesorero", ignoreCase = true) == true

    val periodo by dashboardViewModel.periodo.collectAsState()
    val movimientos by movimientosViewModel.movimientos.collectAsState()

    /*
    |--------------------------------------------------------------------------
    | CARGA INICIAL Y SINCRONIZACIÓN
    |--------------------------------------------------------------------------
    */

    LaunchedEffect(Unit) {

        movimientosViewModel.cargarMovimientos()

        NotificacionEventBus.movimientoActualizado.collect {

            dashboardViewModel.cargarDashboard()

            movimientosViewModel.cargarMovimientos()
        }
    }

    /*
    |--------------------------------------------------------------------------
    | TEMA LOCAL DEL DASHBOARD
    |--------------------------------------------------------------------------
    */


        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,

            /*
            |--------------------------------------------------------------------------
            | BARRA SUPERIOR
            |--------------------------------------------------------------------------
            */

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

                        IconButton(
                            onClick = onOpenDrawer
                        ) {

                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Abrir menú",
                                tint = Blanco
                            )
                        }
                    },

                    actions = {

                        /*
                        |--------------------------------------------------------------------------
                        | BOTÓN MODO CLARO / OSCURO
                        |--------------------------------------------------------------------------
                        */

                        IconButton(
                            onClick = onThemeToggle
                        ) {

                            Icon(
                                imageVector = if (darkTheme) {
                                    Icons.Outlined.LightMode
                                } else {
                                    Icons.Outlined.DarkMode
                                },

                                contentDescription = if (darkTheme) {
                                    "Cambiar a modo claro"
                                } else {
                                    "Cambiar a modo oscuro"
                                },

                                tint = Blanco
                            )
                        }

                        /*
                        |--------------------------------------------------------------------------
                        | SIGI
                        |--------------------------------------------------------------------------
                        */


                        /*
                        |--------------------------------------------------------------------------
                        | ZOE INTELIGENTE
                        |--------------------------------------------------------------------------
                        */

                        IconButton(
                            onClick = onSigiClick
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SmartToy,
                                contentDescription = "ZOE inteligente",
                                tint = Blanco
                            )
                        }

                        /*
                        |--------------------------------------------------------------------------
                        | NOTIFICACIONES
                        |--------------------------------------------------------------------------
                        */

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
                                        .background(
                                            color = Rojo,
                                            shape = CircleShape
                                        ),

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

            /*
            |--------------------------------------------------------------------------
            | BARRA INFERIOR
            |--------------------------------------------------------------------------
            */

            bottomBar = {

                SigefivBottomBar(
                    onInicioClick = {},
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
            .background(MaterialTheme.colorScheme.background)
    ) {

        /*
        |--------------------------------------------------------------------------
        | DECORACIÓN FIESTAS PATRIAS
        |--------------------------------------------------------------------------
        */

        if (temporada == AppSeason.FIESTAS_PATRIAS) {

            Image(
                painter = painterResource(
                    id = R.drawable.bandera_fiestas_patrias
                ),

                contentDescription = null,

                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),

                contentScale = ContentScale.FillWidth,

                alpha = 0.22f
            )
        }

        /*
        |--------------------------------------------------------------------------
        | DECORACIÓN NAVIDAD
        |--------------------------------------------------------------------------
        */

        if (temporada == AppSeason.NAVIDAD) {

            Image(
                painter = painterResource(
                    id = R.drawable.fondo_navidad_dashboard
                ),

                contentDescription = null,

                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),

                contentScale = ContentScale.FillWidth,

                alpha = 0.22f
            )
        }

        /*
        |--------------------------------------------------------------------------
        | CONTENIDO PRINCIPAL
        |--------------------------------------------------------------------------
        */

        Column(

            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ),

            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            /*
            |--------------------------------------------------------------------------
            | SALUDO
            |--------------------------------------------------------------------------
            */

            Column {

                Text(
                    text = "Hola, $nombreUsuario",

                    color = MaterialTheme.colorScheme.onBackground,

                    fontSize = 20.sp,

                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Bienvenido nuevamente a SIGEFIV",

                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    fontSize = 13.sp
                )
            }

            /*
            |--------------------------------------------------------------------------
            | SALDO
            |--------------------------------------------------------------------------
            */

            SaldoCard(
                periodo = periodo
            )

            /*
            |--------------------------------------------------------------------------
            | INGRESOS Y EGRESOS
            |--------------------------------------------------------------------------
            */

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                ResumenCard(

                    modifier = Modifier.weight(1f),

                    titulo = "Ingresos",

                    monto = "S/ %.2f".format(
                        periodo?.ingresos ?: 0.0
                    ),

                    icono = Icons.Outlined.ArrowUpward,

                    color = Verde
                )

                ResumenCard(

                    modifier = Modifier.weight(1f),

                    titulo = "Egresos",

                    monto = "S/ %.2f".format(
                        periodo?.egresos ?: 0.0
                    ),

                    icono = Icons.Outlined.ArrowDownward,

                    color = Rojo
                )
            }

            /*
            |--------------------------------------------------------------------------
            | PERIODO ACTUAL
            |--------------------------------------------------------------------------
            */

            CardPeriodo(
                periodo = periodo
            )

            /*
            |--------------------------------------------------------------------------
            | MOVIMIENTOS RECIENTES
            |--------------------------------------------------------------------------
            */

            Row(

                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceBetween,

                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(

                    text = "Movimientos recientes",

                    color = MaterialTheme.colorScheme.onBackground,

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

            /*
            |--------------------------------------------------------------------------
            | LISTA DE MOVIMIENTOS
            |--------------------------------------------------------------------------
            */

            Column(

                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                movimientos.forEach { movimiento ->

                    MovimientoItem(
                        movimiento = movimiento
                    )
                }

                if (movimientos.isEmpty()) {

                    Text(

                        text = "Cargando movimientos...",

                        color = MaterialTheme.colorScheme.onSurfaceVariant,

                        fontSize = 13.sp,

                        modifier = Modifier.padding(
                            vertical = 8.dp
                        )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )
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
    periodo: PeriodoDashboard?
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(16.dp),

        colors = CardDefaults.cardColors(

            containerColor = MaterialTheme.colorScheme.surface
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

                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    fontSize = 11.sp,

                    fontWeight = FontWeight.Bold,

                    letterSpacing = 0.5.sp
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

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

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

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

                        color = MaterialTheme.colorScheme.surfaceVariant,

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

            containerColor = MaterialTheme.colorScheme.surface
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

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(

                    text = titulo,

                    color = MaterialTheme.colorScheme.onSurface,

                    fontSize = 13.sp,

                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

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
private fun CardPeriodo(
    periodo: PeriodoDashboard?
) {

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(14.dp),

        colors = CardDefaults.cardColors(

            containerColor = MaterialTheme.colorScheme.surface
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

                        color = MaterialTheme.colorScheme.surfaceVariant,

                        shape = RoundedCornerShape(10.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(

                    imageVector = Icons.Outlined.Assignment,

                    contentDescription = "Periodo",

                    tint = SeasonalColors.primary(
                        SeasonalTheme.getSeason()
                    ),

                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(

                    text = "Periodo actual",

                    color = MaterialTheme.colorScheme.onSurfaceVariant,

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
private fun MovimientoItem(
    movimiento: Movimiento
) {

    val ingreso = movimiento.tipo.equals(
        "Ingreso",
        ignoreCase = true
    )

    Card(

        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(12.dp),

        colors = CardDefaults.cardColors(

            containerColor = MaterialTheme.colorScheme.surface
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

                        color = MaterialTheme.colorScheme.surfaceVariant,

                        shape = RoundedCornerShape(10.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(

                    imageVector = if (ingreso) {

                        Icons.Outlined.ArrowUpward

                    } else {

                        Icons.Outlined.ArrowDownward
                    },

                    contentDescription = movimiento.concepto,

                    tint = if (ingreso) Verde else Rojo,

                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(

                modifier = Modifier.weight(1f)
            ) {

                Text(

                    text = movimiento.concepto,

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

                        color = MaterialTheme.colorScheme.onSurfaceVariant,

                        fontSize = 11.sp
                    )

                    Text(

                        text = "•",

                        color = MaterialTheme.colorScheme.onSurfaceVariant,

                        fontSize = 11.sp
                    )

                    Text(

                        text = movimiento.categoria ?: "General",

                        color = SeasonalColors.primary(
                            SeasonalTheme.getSeason()
                        ),

                        fontSize = 11.sp
                    )
                }
            }

            Text(

                text = if (ingreso) {

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
| BARRA INFERIOR
|--------------------------------------------------------------------------
*/

@Composable
private fun SigefivBottomBar(

    onInicioClick: () -> Unit = {},

    onAsambleasClick: () -> Unit = {},

    onPeriodosClick: () -> Unit = {},

    onMiCuentaClick: () -> Unit = {}
) {

    val primaryColor = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    NavigationBar(

        containerColor = primaryColor,

        tonalElevation = 0.dp
    ) {

        /*
        |--------------------------------------------------------------------------
        | INICIO
        |--------------------------------------------------------------------------
        */

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

                Text(
                    text = "Inicio"
                )
            },

            colors = NavigationBarItemDefaults.colors(

                selectedIconColor = primaryColor,

                selectedTextColor = Blanco,

                indicatorColor = Blanco,

                unselectedIconColor = Blanco.copy(
                    alpha = 0.75f
                ),

                unselectedTextColor = Blanco.copy(
                    alpha = 0.75f
                )
            )
        )

        /*
        |--------------------------------------------------------------------------
        | ASAMBLEA
        |--------------------------------------------------------------------------
        */

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

                Text(
                    text = "Asamblea"
                )
            },

            colors = NavigationBarItemDefaults.colors(

                unselectedIconColor = Blanco.copy(
                    alpha = 0.75f
                ),

                unselectedTextColor = Blanco.copy(
                    alpha = 0.75f
                )
            )
        )

        /*
        |--------------------------------------------------------------------------
        | PERIODOS
        |--------------------------------------------------------------------------
        */

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

                Text(
                    text = "Periodos"
                )
            },

            colors = NavigationBarItemDefaults.colors(

                unselectedIconColor = Blanco.copy(
                    alpha = 0.75f
                ),

                unselectedTextColor = Blanco.copy(
                    alpha = 0.75f
                )
            )
        )

        /*
        |--------------------------------------------------------------------------
        | MI CUENTA
        |--------------------------------------------------------------------------
        */

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

                Text(
                    text = "Mi cuenta"
                )
            },

            colors = NavigationBarItemDefaults.colors(

                unselectedIconColor = Blanco.copy(
                    alpha = 0.75f
                ),

                unselectedTextColor = Blanco.copy(
                    alpha = 0.75f
                )
            )
        )
    }
}