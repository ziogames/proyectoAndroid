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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Periodo
import com.sigefiv.app.viewmodel.PeriodosViewModel
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV
|--------------------------------------------------------------------------
*/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodosAnioScreen(
    periodosViewModel: PeriodosViewModel,
    anio: Int,
    onBackClick: () -> Unit,
    onPeriodoClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onMovimientosClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onMasClick: () -> Unit,
    onOpenDrawer: (() -> Unit)? = null
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val periodos by periodosViewModel.periodos.collectAsState()

    LaunchedEffect(Unit) {
        periodosViewModel.cargarPeriodos()
    }

    val periodosDelAnio = remember(periodos, anio) {
        periodos
            .filter { it.anio == anio }
            .associateBy { it.mes }
    }

    val meses = remember {
        listOf(
            1 to "Enero",
            2 to "Febrero",
            3 to "Marzo",
            4 to "Abril",
            5 to "Mayo",
            6 to "Junio",
            7 to "Julio",
            8 to "Agosto",
            9 to "Septiembre",
            10 to "Octubre",
            11 to "Noviembre",
            12 to "Diciembre"
        )
    }

    val periodosRegistrados = periodosDelAnio.size

    val periodoAbierto = periodosDelAnio.values.firstOrNull {
        it.estado.equals("Abierto", ignoreCase = true)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Períodos - $anio",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "Meses del ejercicio $anio",
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    if (onOpenDrawer != null) {
                        IconButton(onClick = onOpenDrawer) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Abrir menú",
                                tint = MaterialTheme.colorScheme.onPrimary
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
            BarraInferiorPeriodosAnio(
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onPeriodosClick = {
                    // Ya estamos dentro de Períodos
                },
                onMiCuentaClick = onMasClick
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(10.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                .size(46.dp)
                                .background(MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = colorPrincipal,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Ejercicio $anio",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "$periodosRegistrados de 12 meses registrados",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        if (periodoAbierto != null) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Mes activo",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = periodoAbierto.nombre,
                                    color = colorPrincipal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "MESES DEL AÑO $anio",
                    color = colorPrincipal,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(
                items = meses,
                key = { it.first }
            ) { (mes, nombreMes) ->

                val periodo = periodosDelAnio[mes]

                PeriodoMesCard(
                    nombre = nombreMes,
                    periodo = periodo,
                    onClick = {
                        if (periodo != null) {
                            onPeriodoClick(periodo.id)
                        }
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun PeriodoMesCard(
    nombre: String,
    periodo: Periodo?,
    onClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val estado = when {
        periodo == null -> EstadoMes.NO_INICIADO
        periodo.estado.equals("Abierto", ignoreCase = true) -> EstadoMes.ABIERTO
        periodo.estado.equals("Cerrado", ignoreCase = true) -> EstadoMes.CERRADO
        else -> EstadoMes.OTRO
    }

    val fondoTarjeta = when (estado) {
        EstadoMes.ABIERTO -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val textoEstado = when (estado) {
        EstadoMes.ABIERTO -> "Abierto"
        EstadoMes.CERRADO -> "Cerrado"
        EstadoMes.NO_INICIADO -> "No iniciado"
        EstadoMes.OTRO -> periodo?.estado ?: "No iniciado"
    }

    val colorEstado = when (estado) {
        EstadoMes.ABIERTO -> colorPrincipal
        EstadoMes.CERRADO -> MaterialTheme.colorScheme.onSurfaceVariant
        EstadoMes.NO_INICIADO -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        EstadoMes.OTRO -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = periodo != null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = if (estado == EstadoMes.ABIERTO) 2.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (estado) {
                    EstadoMes.ABIERTO -> Icons.Outlined.CheckCircle
                    EstadoMes.CERRADO -> Icons.Outlined.Lock
                    EstadoMes.NO_INICIADO -> Icons.Outlined.RadioButtonUnchecked
                    EstadoMes.OTRO -> Icons.Outlined.CalendarMonth
                },
                contentDescription = textoEstado,
                tint = colorEstado,
                modifier = Modifier.size(21.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = nombre,
                color = if (estado == EstadoMes.ABIERTO) colorPrincipal else MaterialTheme.colorScheme.onSurface,
                fontSize = 15.sp,
                fontWeight = if (estado == EstadoMes.ABIERTO) FontWeight.Bold else FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = textoEstado,
                color = colorEstado,
                fontSize = 12.sp,
                fontWeight = if (estado == EstadoMes.ABIERTO) FontWeight.Bold else FontWeight.Normal
            )

            if (periodo != null) {
                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Ver mes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private enum class EstadoMes {
    ABIERTO,
    CERRADO,
    NO_INICIADO,
    OTRO
}

/*
|--------------------------------------------------------------------------
| BARRA INFERIOR UNIFICADA (ESTILO VERDE)
|--------------------------------------------------------------------------
*/

@Composable
private fun BarraInferiorPeriodosAnio(
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
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text(text = "Inicio")
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
            )
        )

        // ASAMBLEAS
        NavigationBarItem(
            selected = false,
            onClick = onAsambleasClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = "Asambleas"
                )
            },
            label = {
                Text(text = "Asambleas")
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
            )
        )

        // PERÍODOS
        NavigationBarItem(
            selected = true,
            onClick = onPeriodosClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Períodos"
                )
            },
            label = {
                Text(text = "Períodos")
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorPrincipal,
                selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                indicatorColor = MaterialTheme.colorScheme.onPrimary,
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
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
                Text(text = "Mi cuenta")
            },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f),
                unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
            )
        )
    }
}
