@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.periodos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Periodo
import com.sigefiv.app.ui.components.AppBottomBar
import com.sigefiv.app.viewmodel.PeriodosViewModel

private val ColorAbierto = Color(0xFF16A34A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodosAnioScreen(
    periodosViewModel: PeriodosViewModel,
    anio: Int,
    onBackClick: () -> Unit,
    onPeriodoClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onEmprendimientosClick: () -> Unit,
    onPeriodosClick: () -> Unit,
    onMiCuentaClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
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
                            text = "Períodos $anio",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Meses del ejercicio fiscal",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(6.dp))

                // Tarjeta resumen del ejercicio
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 1.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Ejercicio fiscal $anio",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
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
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = ColorAbierto.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, ColorAbierto.copy(alpha = 0.25f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .background(ColorAbierto, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = periodoAbierto.nombre,
                                            color = ColorAbierto,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "MESES DEL EJERCICIO",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            items(
                items = meses,
                key = { it.first }
            ) { (mes, nombreMes) ->
                val periodo = periodosDelAnio[mes]

                PeriodoMesCard(
                    nombre = nombreMes,
                    numeroMes = mes,
                    periodo = periodo,
                    primaryColor = primaryColor,
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
    numeroMes: Int,
    periodo: Periodo?,
    primaryColor: Color,
    onClick: () -> Unit
) {
    val estado = when {
        periodo == null -> EstadoMes.NO_INICIADO
        periodo.estado.equals("Abierto", ignoreCase = true) -> EstadoMes.ABIERTO
        periodo.estado.equals("Cerrado", ignoreCase = true) -> EstadoMes.CERRADO
        else -> EstadoMes.OTRO
    }

    val colorEstado = when (estado) {
        EstadoMes.ABIERTO -> ColorAbierto
        EstadoMes.CERRADO -> MaterialTheme.colorScheme.onSurfaceVariant
        EstadoMes.NO_INICIADO -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        EstadoMes.OTRO -> primaryColor
    }

    val textoEstado = when (estado) {
        EstadoMes.ABIERTO -> "Abierto"
        EstadoMes.CERRADO -> "Cerrado"
        EstadoMes.NO_INICIADO -> "No iniciado"
        EstadoMes.OTRO -> periodo?.estado ?: "Sin registrar"
    }

    val borde = when (estado) {
        EstadoMes.ABIERTO -> BorderStroke(1.2.dp, ColorAbierto.copy(alpha = 0.45f))
        else -> BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = periodo != null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = if (estado == EstadoMes.ABIERTO) {
            ColorAbierto.copy(alpha = 0.05f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        tonalElevation = if (estado == EstadoMes.ABIERTO) 2.dp else 1.dp,
        border = borde
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor número / ícono
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(
                        color = when (estado) {
                            EstadoMes.ABIERTO -> ColorAbierto.copy(alpha = 0.12f)
                            EstadoMes.CERRADO -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        },
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "%02d".format(numeroMes),
                    color = colorEstado,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Nombre del mes
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = nombre,
                    color = if (estado == EstadoMes.NO_INICIADO) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    fontSize = 15.sp,
                    fontWeight = if (estado == EstadoMes.ABIERTO) FontWeight.Bold else FontWeight.SemiBold
                )
            }

            // Badge de estado
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = when (estado) {
                    EstadoMes.ABIERTO -> ColorAbierto.copy(alpha = 0.12f)
                    EstadoMes.CERRADO -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = when (estado) {
                            EstadoMes.ABIERTO -> Icons.Outlined.CheckCircle
                            EstadoMes.CERRADO -> Icons.Outlined.Lock
                            EstadoMes.NO_INICIADO -> Icons.Outlined.RadioButtonUnchecked
                            EstadoMes.OTRO -> Icons.Outlined.Info
                        },
                        contentDescription = null,
                        tint = colorEstado,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = textoEstado,
                        color = colorEstado,
                        fontSize = 11.sp,
                        fontWeight = if (estado == EstadoMes.ABIERTO) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            if (periodo != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = "Ver mes",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
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