package com.sigefiv.app.screens.caja

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.CajaMes
import com.sigefiv.app.viewmodel.CajaViewModel
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme


private val Verde = Color(0xFF15803D)
private val VerdeClaro = Color(0xFFDCFCE7)
private val Rojo = Color(0xFFDC2626)
private val RojoClaro = Color(0xFFFEE2E2)
private val Azul = Color(0xFF2563EB)
private val AzulClaro = Color(0xFFDBEAFE)
private val Blanco = Color.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CajaScreen(
    viewModel: CajaViewModel,
    onBackClick: () -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val caja by viewModel.caja.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val error by viewModel.error.collectAsState()

    var menuAnioAbierto by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        viewModel.cargarCaja()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Caja",
                        color = Blanco,
                        fontWeight = FontWeight.Bold
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



                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal
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
                        modifier = Modifier.align(
                            Alignment.Center
                        ),
                        color = colorPrincipal
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

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

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
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(
                            12.dp
                        )
                    ) {

                        /*
                         * SELECTOR DE AÑO
                         */
                        item {

                            Box(
                                modifier = Modifier.fillMaxWidth()
                            ) {

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    onClick = {
                                        menuAnioAbierto =
                                            true
                                    }
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                horizontal = 18.dp,
                                                vertical = 14.dp
                                            ),
                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Outlined.AccountBalanceWallet,
                                            contentDescription =
                                                null,
                                            tint =
                                                colorPrincipal,
                                            modifier =
                                                Modifier.size(26.dp)
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(12.dp)
                                        )

                                        Column(
                                            modifier =
                                                Modifier.weight(1f)
                                        ) {

                                            Text(
                                                text = "Año",
                                                fontSize =
                                                    12.sp,
                                                color =
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Text(
                                                text =
                                                    datos.anio.toString(),
                                                fontSize =
                                                    18.sp,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                color =
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        }

                                        Text(
                                            text = "▼",
                                            color =
                                                colorPrincipal,
                                            fontSize =
                                                14.sp
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded =
                                        menuAnioAbierto,
                                    onDismissRequest = {
                                        menuAnioAbierto =
                                            false
                                    }
                                ) {

                                    datos.anios.forEach { anio ->

                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text =
                                                        anio.toString()
                                                )
                                            },
                                            onClick = {
                                                menuAnioAbierto =
                                                    false

                                                viewModel
                                                    .seleccionarAnio(
                                                        anio
                                                    )
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        /*
                         * TÍTULO DEL RESUMEN
                         */
                        item {

                            Text(
                                text = "Resumen financiero",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(
                                    top = 4.dp
                                )
                            )
                        }

                        /*
                         * SALDO INICIAL
                         */
                        item {

                            CajaResumenCard(
                                titulo = "Saldo inicial",
                                valor =
                                    datos.resumen.saldo_inicial,
                                icono = Icons.Outlined.AccountBalanceWallet,
                                fondoIcono = AzulClaro,
                                colorIcono = Azul
                            )
                        }

                        /*
                         * INGRESOS
                         */
                        item {

                            CajaResumenCard(
                                titulo = "Ingresos",
                                valor =
                                    datos.resumen.ingresos,
                                icono = Icons.Outlined.TrendingUp,
                                fondoIcono = VerdeClaro,
                                colorIcono = Verde
                            )
                        }

                        /*
                         * EGRESOS
                         */
                        item {

                            CajaResumenCard(
                                titulo = "Egresos",
                                valor =
                                    datos.resumen.egresos,
                                icono = Icons.Outlined.TrendingDown,
                                fondoIcono = RojoClaro,
                                colorIcono = Rojo
                            )
                        }

                        /*
                         * SALDO FINAL
                         */
                        item {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                        colorPrincipal
                                )
                            ) {

                                Column(
                                    modifier = Modifier.padding(
                                        20.dp
                                    )
                                ) {

                                    Text(
                                        text = "Saldo final",
                                        color =
                                            Blanco.copy(
                                                alpha = 0.85f
                                            ),
                                        fontSize = 14.sp
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(4.dp)
                                    )

                                    Text(
                                        text =
                                            moneda(
                                                datos.resumen.saldo_final
                                            ),
                                        color = Blanco,
                                        fontSize = 28.sp,
                                        fontWeight =
                                            FontWeight.Bold
                                    )
                                }
                            }
                        }

                        /*
                         * DISTRIBUCIÓN
                         */
                        item {

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border =
                                    androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outlineVariant
                                    )
                            ) {

                                Column(
                                    modifier = Modifier.padding(
                                        18.dp
                                    )
                                ) {

                                    Text(
                                        text =
                                            "Distribución de movimientos",
                                        fontSize = 16.sp,
                                        fontWeight =
                                            FontWeight.Bold,
                                        color =
                                            MaterialTheme.colorScheme.onSurface
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.height(14.dp)
                                    )

                                    Row(
                                        modifier =
                                            Modifier.fillMaxWidth(),
                                        horizontalArrangement =
                                            Arrangement.SpaceBetween
                                    ) {

                                        Column {

                                            Text(
                                                text =
                                                    "Ingresos",
                                                fontSize =
                                                    13.sp,
                                                color =
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Text(
                                                text =
                                                    "${datos.resumen.porcentaje_ingresos} %",
                                                fontSize =
                                                    20.sp,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                color =
                                                    Verde
                                            )
                                        }

                                        Column(
                                            horizontalAlignment =
                                                Alignment.End
                                        ) {

                                            Text(
                                                text =
                                                    "Egresos",
                                                fontSize =
                                                    13.sp,
                                                color =
                                                    MaterialTheme.colorScheme.onSurfaceVariant
                                            )

                                            Text(
                                                text =
                                                    "${datos.resumen.porcentaje_egresos} %",
                                                fontSize =
                                                    20.sp,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                color =
                                                    Rojo
                                            )
                                        }
                                    }

                                    Spacer(
                                        modifier =
                                            Modifier.height(12.dp)
                                    )

                                    Box(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .height(10.dp)
                                                .background(
                                                    RojoClaro,
                                                    RoundedCornerShape(
                                                        20.dp
                                                    )
                                                )
                                    ) {

                                        Box(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth(
                                                        datos.resumen
                                                            .porcentaje_ingresos
                                                            .toFloat()
                                                            .coerceIn(
                                                                0f,
                                                                100f
                                                            ) / 100f
                                                    )
                                                    .height(10.dp)
                                                    .background(
                                                        Verde,
                                                        RoundedCornerShape(
                                                            20.dp
                                                        )
                                                    )
                                        )
                                    }
                                }
                            }
                        }

                        /*
                         * CONSOLIDADO MENSUAL
                         */
                        item {

                            Text(
                                text = "Consolidado mensual",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(
                                    top = 4.dp
                                )
                            )
                        }

                        items(
                            items = datos.consolidado,
                            key = {
                                it.mes
                            }
                        ) { mes ->

                            CajaMesCard(
                                mes = mes
                            )
                        }

                        item {

                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CajaResumenCard(
    titulo: String,
    valor: Double,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    fondoIcono: Color,
    colorIcono: Color
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
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
                    .background(
                        fondoIcono,
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(14.dp)
            )

            Column {

                Text(
                    text = titulo,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                Text(
                    text = moneda(valor),
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CajaMesCard(
    mes: CajaMes
) {

    val tieneMovimientos =
        mes.ingresos != 0.0 ||
                mes.egresos != 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = mes.nombre_mes,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text =
                            if (tieneMovimientos)
                                "Con movimientos"
                            else
                                "Sin movimientos",
                        fontSize = 12.sp,
                        color =
                            if (tieneMovimientos)
                                Verde
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = moneda(mes.saldo_final),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color =
                        if (mes.saldo_final >= 0)
                            Verde
                        else
                            Rojo
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            CajaMesFila(
                etiqueta = "Saldo inicial",
                valor = mes.saldo_inicial,
                color = MaterialTheme.colorScheme.onSurface
            )

            CajaMesFila(
                etiqueta = "Ingresos",
                valor = mes.ingresos,
                color = Verde
            )

            CajaMesFila(
                etiqueta = "Egresos",
                valor = mes.egresos,
                color = Rojo
            )

            CajaMesFila(
                etiqueta = "Saldo final",
                valor = mes.saldo_final,
                color =
                    if (mes.saldo_final >= 0)
                        Verde
                    else
                        Rojo
            )
        }
    }
}

@Composable
private fun CajaMesFila(
    etiqueta: String,
    valor: Double,
    color: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = etiqueta,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = moneda(valor),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

private fun moneda(
    valor: Double
): String {

    return "S/ %.2f".format(
        java.util.Locale.US,
        valor
    )
}
