package com.sigefiv.app.screens.caja

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.R
import com.sigefiv.app.data.model.CajaMes
import com.sigefiv.app.viewmodel.CajaViewModel

// ============================================================
// COLORES SEMÁNTICOS
// ============================================================
private val VerdeSemantico = Color(0xFF16A34A)
private val RojoSemantico = Color(0xFFDC2626)
private val AzulSemantico = Color(0xFF2563EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CajaScreen(
    viewModel: CajaViewModel,
    onBackClick: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    darkTheme: Boolean = false
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val caja by viewModel.caja.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    var menuAnioAbierto by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.cargarCaja()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Caja General",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "Saldos y consolidados financieros",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = onOpenDrawer,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "Abrir menú",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                actions = {
                    caja?.let { datos ->
                        Box(modifier = Modifier.padding(end = 12.dp)) {
                            Surface(
                                onClick = { menuAnioAbierto = true },
                                shape = RoundedCornerShape(14.dp),
                                color = primaryColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CalendarMonth,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Año ${datos.anio}",
                                        color = primaryColor,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "▼",
                                        color = primaryColor,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = menuAnioAbierto,
                                onDismissRequest = { menuAnioAbierto = false },
                                modifier = Modifier.width(190.dp),
                                shape = RoundedCornerShape(18.dp),
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 4.dp,
                                shadowElevation = 8.dp
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = primaryColor.copy(alpha = 0.12f),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Outlined.CalendarMonth,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = "Seleccionar año",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Período financiero",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                datos.anios.forEach { anio ->
                                    val seleccionado = anio == datos.anio

                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = anio.toString(),
                                                fontSize = 15.sp,
                                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal,
                                                color = if (seleccionado) primaryColor else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        leadingIcon = {
                                            Surface(
                                                modifier = Modifier.size(34.dp),
                                                shape = RoundedCornerShape(9.dp),
                                                color = if (seleccionado) primaryColor.copy(alpha = 0.12f) else Color.Transparent
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Icon(
                                                        imageVector = Icons.Outlined.CalendarMonth,
                                                        contentDescription = null,
                                                        tint = if (seleccionado) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        },
                                        trailingIcon = {
                                            if (seleccionado) {
                                                Icon(
                                                    imageVector = Icons.Outlined.Check,
                                                    contentDescription = "Año seleccionado",
                                                    tint = primaryColor,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            menuAnioAbierto = false
                                            viewModel.seleccionarAnio(anio)
                                        },
                                        modifier = Modifier
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                            .background(
                                                if (seleccionado) primaryColor.copy(alpha = 0.08f) else Color.Transparent,
                                                RoundedCornerShape(12.dp)
                                            )
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = primaryColor
                    )
                }

                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "No se pudo cargar Caja",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error ?: "Error desconocido",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                caja != null -> {
                    val datos = caja!!

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // 1. RESUMEN FINANCIERO
                        item {
                            val imagenFinanzas = if (darkTheme) {
                                R.drawable.caja_finanzas_dark
                            } else {
                                R.drawable.caja_finanzas_light
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(292.dp),
                                shape = RoundedCornerShape(22.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
                            ) {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Image(
                                        painter = painterResource(id = imagenFinanzas),
                                        contentDescription = "Resumen financiero",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(width = 175.dp, height = 105.dp)
                                    )

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 16.dp)
                                    ) {
                                        Text(
                                            text = "Resumen financiero",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(3.dp))

                                        Text(
                                            text = "Estado general de la caja en ${datos.anio}",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(18.dp))

                                        // FILA 1: SALDO INICIAL & INGRESOS
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            CajaResumenCard(
                                                modifier = Modifier.weight(1f),
                                                titulo = "Saldo inicial",
                                                valor = datos.resumen.saldo_inicial,
                                                icono = Icons.Outlined.AccountBalanceWallet,
                                                colorAcento = AzulSemantico
                                            )

                                            CajaResumenCard(
                                                modifier = Modifier.weight(1f),
                                                titulo = "Ingresos",
                                                valor = datos.resumen.ingresos,
                                                icono = Icons.Outlined.TrendingUp,
                                                colorAcento = VerdeSemantico
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // FILA 2: EGRESOS & SALDO FINAL
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            CajaResumenCard(
                                                modifier = Modifier.weight(1f),
                                                titulo = "Egresos",
                                                valor = datos.resumen.egresos,
                                                icono = Icons.Outlined.TrendingDown,
                                                colorAcento = RojoSemantico
                                            )

                                            CajaSaldoFinalCard(
                                                modifier = Modifier.weight(1f),
                                                valor = datos.resumen.saldo_final,
                                                colorPrincipal = primaryColor
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // 2. DISTRIBUCIÓN
                        item {
                            CajaDistribucionCard(
                                porcentajeIngresos = datos.resumen.porcentaje_ingresos,
                                porcentajeEgresos = datos.resumen.porcentaje_egresos,
                                ingresos = datos.resumen.ingresos,
                                egresos = datos.resumen.egresos,
                                anio = datos.anio
                            )
                        }

                        // 3. CONSOLIDADO MENSUAL
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        modifier = Modifier.size(42.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = primaryColor.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Outlined.BarChart,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "Consolidado mensual",
                                            fontSize = 17.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Detalle de movimientos por mes",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = primaryColor.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                                    modifier = Modifier.clickable { /* Ver reporte */ }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.BarChart,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Ver reporte",
                                            color = primaryColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        // 4. MESES
                        items(
                            items = datos.consolidado,
                            key = { it.mes }
                        ) { mes ->
                            CajaMesCard(mes = mes)
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// TARJETA RESUMEN MODULAR
// ============================================================
@Composable
private fun CajaResumenCard(
    modifier: Modifier,
    titulo: String,
    valor: Double,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorAcento: Color
) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(9.dp),
                color = colorAcento.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorAcento,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = titulo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = moneda(valor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    softWrap = false
                )
            }
        }
    }
}

// ============================================================
// SALDO FINAL
// ============================================================
@Composable
private fun CajaSaldoFinalCard(
    modifier: Modifier,
    valor: Double,
    colorPrincipal: Color
) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorPrincipal),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = RoundedCornerShape(9.dp),
                color = Color.White.copy(alpha = 0.18f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Saldo final",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = moneda(valor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ============================================================
// DISTRIBUCIÓN
// ============================================================
@Composable
private fun CajaDistribucionCard(
    porcentajeIngresos: Double,
    porcentajeEgresos: Double,
    ingresos: Double,
    egresos: Double,
    anio: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Distribución de movimientos",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "Porcentaje de ingresos y egresos en $anio",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // INGRESOS
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ingresos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$porcentajeIngresos %",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdeSemantico
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = moneda(ingresos),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // DONUT
                CajaDonutChart(porcentajeIngresos = porcentajeIngresos)

                // EGRESOS
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Egresos",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$porcentajeEgresos %",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RojoSemantico
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = moneda(egresos),
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // BARRA DE DISTRIBUCIÓN
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(RojoSemantico.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth((porcentajeIngresos.toFloat().coerceIn(0f, 100f) / 100f))
                        .height(8.dp)
                        .background(VerdeSemantico, RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$porcentajeIngresos % Ingresos",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = VerdeSemantico
                )
                Text(
                    text = "$porcentajeEgresos % Egresos",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = RojoSemantico
                )
            }
        }
    }
}

// ============================================================
// DONUT
// ============================================================
@Composable
private fun CajaDonutChart(
    porcentajeIngresos: Double
) {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val ingresosSweep = porcentajeIngresos.toFloat().coerceIn(0f, 100f) * 3.6f

            // ARCO INGRESOS (Verde)
            drawArc(
                color = VerdeSemantico,
                startAngle = -90f,
                sweepAngle = -ingresosSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // ARCO EGRESOS (Rojo)
            drawArc(
                color = RojoSemantico,
                startAngle = -90f - ingresosSweep,
                sweepAngle = -(360f - ingresosSweep),
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "100%",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Auditado",
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================
// TARJETA MES
// ============================================================
@Composable
private fun CajaMesCard(
    mes: CajaMes
) {
    val tieneMovimientos = mes.ingresos != 0.0 || mes.egresos != 0.0
    val esSaldoPositivo = mes.saldo_final >= 0
    val colorBadge = if (esSaldoPositivo) VerdeSemantico else RojoSemantico

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(11.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mes.nombre_mes,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (tieneMovimientos) "Con movimientos" else "Sin movimientos",
                        fontSize = 11.sp,
                        color = if (tieneMovimientos) VerdeSemantico else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colorBadge.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, colorBadge.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = moneda(mes.saldo_final),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorBadge
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = colorBadge,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CajaDatoMes(
                    etiqueta = "Saldo inicial",
                    valor = mes.saldo_inicial,
                    color = MaterialTheme.colorScheme.onSurface
                )

                CajaDatoMes(
                    etiqueta = "Ingresos",
                    valor = mes.ingresos,
                    color = VerdeSemantico
                )

                CajaDatoMes(
                    etiqueta = "Egresos",
                    valor = mes.egresos,
                    color = RojoSemantico
                )

                CajaDatoMes(
                    etiqueta = "Saldo final",
                    valor = mes.saldo_final,
                    color = colorBadge
                )
            }
        }
    }
}

// ============================================================
// DATO DEL MES
// ============================================================
@Composable
private fun CajaDatoMes(
    etiqueta: String,
    valor: Double,
    color: Color
) {
    Column {
        Text(
            text = etiqueta,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = moneda(valor),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ============================================================
// MONEDA
// ============================================================
private fun moneda(valor: Double): String {
    return "S/ %.2f".format(java.util.Locale.US, valor)
}