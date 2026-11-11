@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.reportes

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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.ui.components.AppBottomBar
import com.sigefiv.app.viewmodel.ReportesViewModel
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV COHERENTES CON DASHBOARD
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
private val AzulInstitucional = Color(0xFF0284C7)

@Composable
fun ReportesScreen(
    viewModel: ReportesViewModel,
    darkTheme: Boolean = false,
    onOpenDrawer: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reportes y Balances",
                        color = if (darkTheme) Color.White else VerdeCorporativoOscuro,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        letterSpacing = 0.5.sp
                    )
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

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            AppBottomBar(
                pantallaActual = "Reportes",
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
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 1. ENCABEZADO Y SELECTORES DE PERÍODO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (darkTheme) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f) else VerdeGrisaceoTenue
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = if (darkTheme) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f) else GrisBorde,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(VerdeCorporativoMedio.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CalendarMonth,
                                contentDescription = null,
                                tint = VerdeCorporativoMedio,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Período del reporte",
                                color = if (darkTheme) Color.White else VerdeCorporativoOscuro,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Selecciona año y mes para consultar balances",
                                color = TextoGris,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Selector Año
                        SelectorPeriodoSigefiv(
                            modifier = Modifier.weight(1f),
                            etiqueta = "Año",
                            opciones = state.aniosDisponibles.map { it.toString() },
                            seleccionado = state.anioSeleccionado.toString(),
                            darkTheme = darkTheme,
                            alSeleccionar = { viewModel.seleccionarAnio(it.toInt()) }
                        )

                        // Selector Mes
                        val opcionesMes = listOf("Todo el año") + state.mesesDisponibles.map { it.nombre }
                        val mesActualTexto = state.mesesDisponibles
                            .firstOrNull { it.id == state.mesSeleccionado }?.nombre ?: "Todo el año"

                        SelectorPeriodoSigefiv(
                            modifier = Modifier.weight(1.35f),
                            etiqueta = "Mes",
                            opciones = opcionesMes,
                            seleccionado = mesActualTexto,
                            darkTheme = darkTheme,
                            alSeleccionar = { texto ->
                                val mesId = state.mesesDisponibles.firstOrNull { it.nombre == texto }?.id
                                viewModel.seleccionarMes(mesId)
                            }
                        )
                    }
                }
            }

            // --- 2. TARJETA DESTACADA: SALDO FINAL EN CAJA ---
            SaldoCajaCard(
                monto = state.resumen?.saldoCaja ?: 0.0,
                anio = state.anioSeleccionado,
                mesNombre = state.mesesDisponibles.firstOrNull { it.id == state.mesSeleccionado }?.nombre ?: "Anual",
                cargando = state.cargandoResumen,
                darkTheme = darkTheme
            )

            // --- 3. TARJETAS DE SALDO INICIAL, INGRESOS Y EGRESOS ---
            Text(
                text = "Detalle del Balance",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenReporteCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Saldo Inicial",
                    monto = state.resumen?.saldoInicial ?: 0.0,
                    icono = Icons.Outlined.Savings,
                    color = TextoGris,
                    darkTheme = darkTheme
                )

                ResumenReporteCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Ingresos",
                    monto = state.resumen?.ingresos ?: 0.0,
                    icono = Icons.Outlined.ArrowUpward,
                    color = VerdeIngreso,
                    darkTheme = darkTheme
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResumenReporteCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Egresos",
                    monto = state.resumen?.egresos ?: 0.0,
                    icono = Icons.Outlined.ArrowDownward,
                    color = RojoEgreso,
                    darkTheme = darkTheme
                )

                ResumenReporteCard(
                    modifier = Modifier.weight(1f),
                    titulo = "Operaciones",
                    montoTexto = "${state.resumen?.totalFilas ?: 0} reg.",
                    icono = Icons.Outlined.AccountBalanceWallet,
                    color = AzulInstitucional,
                    darkTheme = darkTheme
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // --- 4. ACCIONES DE EXPORTACIÓN ---
            Text(
                text = "Exportar Documentos Oficiales",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            // Botón PDF Oficial
            Button(
                onClick = { viewModel.exportarPdf(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.descargandoPdf && !state.descargandoExcel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = VerdeCorporativoMedio,
                    disabledContainerColor = VerdeCorporativoMedio.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
            ) {
                if (state.descargandoPdf) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Generando PDF...",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.PictureAsPdf,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Compartir Estado de Cuenta (PDF)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Botón Excel Completo
            Button(
                onClick = { viewModel.exportarExcel(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = !state.descargandoPdf && !state.descargandoExcel,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (darkTheme) Color(0xFF0369A1) else AzulInstitucional,
                    disabledContainerColor = AzulInstitucional.copy(alpha = 0.5f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
            ) {
                if (state.descargandoExcel) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Generando Excel...",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.TableChart,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Exportar Libro Completo (Excel)",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/*
|--------------------------------------------------------------------------
| TARJETA DE SALDO EN CAJA (MISMO ESTILO QUE SALDO DISPONIBLE EN DASHBOARD)
|--------------------------------------------------------------------------
*/
@Composable
private fun SaldoCajaCard(
    monto: Double,
    anio: Int,
    mesNombre: String,
    cargando: Boolean,
    darkTheme: Boolean
) {
    val gradiente = if (darkTheme) {
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
            .height(130.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradiente)
        ) {
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
                    Text(
                        text = "SALDO EN CAJA DEL PERÍODO",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    if (cargando) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "S/ %,.2f".format(monto),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$mesNombre $anio",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountBalanceWallet,
                        contentDescription = "Saldo",
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
| TARJETA DE RESUMEN (INGRESOS, EGRESOS, SALDO INICIAL)
|--------------------------------------------------------------------------
*/
@Composable
private fun ResumenReporteCard(
    modifier: Modifier = Modifier,
    titulo: String,
    monto: Double? = null,
    montoTexto: String? = null,
    icono: ImageVector,
    color: Color,
    darkTheme: Boolean
) {
    val fondoCard = if (darkTheme) {
        MaterialTheme.colorScheme.surface
    } else {
        color.copy(alpha = 0.04f)
    }

    val textoValor = montoTexto ?: "S/ %,.2f".format(monto ?: 0.0)

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
                    text = textoValor,
                    color = if (darkTheme && color == TextoGris) MaterialTheme.colorScheme.onSurface else color,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| SELECTOR DE PERÍODO CON ESTILO COHERENTE
|--------------------------------------------------------------------------
*/
@Composable
private fun SelectorPeriodoSigefiv(
    modifier: Modifier = Modifier,
    etiqueta: String,
    opciones: List<String>,
    seleccionado: String,
    darkTheme: Boolean,
    alSeleccionar: (String) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }

    val fondoControl = if (darkTheme) MaterialTheme.colorScheme.surface else Color.White
    val bordeControl = if (darkTheme) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f) else GrisBorde
    val textoColor = MaterialTheme.colorScheme.onSurface

    Box(modifier = modifier) {
        Column {
            Text(
                text = etiqueta,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextoGris,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(fondoControl)
                    .border(1.dp, bordeControl, RoundedCornerShape(10.dp))
                    .clickable { expandido = true }
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (seleccionado.isNotEmpty()) seleccionado else "Elegir",
                    color = textoColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Outlined.ArrowDropDown,
                    contentDescription = null,
                    tint = TextoGris
                )
            }
        }

        DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcion,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp
                        )
                    },
                    onClick = {
                        alSeleccionar(opcion)
                        expandido = false
                    }
                )
            }
        }
    }
}