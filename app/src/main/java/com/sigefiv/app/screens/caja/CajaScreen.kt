package com.sigefiv.app.screens.caja

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.CajaMes
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.CajaViewModel
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.sigefiv.app.R
import androidx.compose.material.icons.outlined.Check
// ============================================================
// COLORES
// ============================================================

private val Verde = Color(0xFF15803D)
private val VerdeOscuro = Color(0xFF087443)
private val VerdeClaro = Color(0xFFE7F8ED)

private val Rojo = Color(0xFFDC2626)
private val RojoClaro = Color(0xFFFFE9E9)

private val Azul = Color(0xFF2563EB)
private val AzulClaro = Color(0xFFE8F1FF)

private val Blanco = Color.White

// ============================================================
// CAJA SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CajaScreen(
    viewModel: CajaViewModel,
    onBackClick: () -> Unit,
    onOpenDrawer: () -> Unit = {},
    darkTheme: Boolean = false
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
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
                    // Selector de año en la parte superior derecha
                    caja?.let { datos ->
                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            Row(
                                modifier = Modifier
                                    .background(
                                        Color.White.copy(alpha = 0.2f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { menuAnioAbierto = true }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = Blanco,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Año ${datos.anio}",
                                    color = Blanco,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "▼",
                                    color = Blanco,
                                    fontSize = 10.sp
                                )
                            }

                            DropdownMenu(

                                expanded = menuAnioAbierto,

                                onDismissRequest = {
                                    menuAnioAbierto = false
                                },

                                modifier = Modifier
                                    .width(190.dp),

                                shape = RoundedCornerShape(18.dp),

                                containerColor = MaterialTheme.colorScheme.surface,

                                tonalElevation = 4.dp,

                                shadowElevation = 8.dp

                            ) {

                                // =====================================================
                                // ENCABEZADO
                                // =====================================================

                                Row(

                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(
                                            horizontal = 16.dp,
                                            vertical = 12.dp
                                        ),

                                    verticalAlignment =
                                        Alignment.CenterVertically

                                ) {

                                    Box(

                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (darkTheme) Color(0xFF123D25) else VerdeClaro,
                                                RoundedCornerShape(10.dp)
                                            ),

                                        contentAlignment =
                                            Alignment.Center

                                    ) {

                                        Icon(

                                            imageVector =
                                                Icons.Outlined.CalendarMonth,

                                            contentDescription = null,

                                            tint = Verde,

                                            modifier =
                                                Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(
                                        modifier =
                                            Modifier.width(10.dp)
                                    )

                                    Column {

                                        Text(

                                            text = "Seleccionar año",

                                            fontSize = 13.sp,

                                            fontWeight =
                                                FontWeight.Bold,

                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurface
                                        )

                                        Text(

                                            text = "Período financiero",

                                            fontSize = 10.sp,

                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant
                                        )
                                    }
                                }


                                // =====================================================
                                // SEPARADOR
                                // =====================================================

                                HorizontalDivider(
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp
                                    ),

                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .outlineVariant
                                )


                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
                                )


                                // =====================================================
                                // AÑOS
                                // =====================================================

                                datos.anios.forEach { anio ->

                                    val seleccionado =
                                        anio == datos.anio

                                    DropdownMenuItem(

                                        text = {

                                            Text(

                                                text =
                                                    anio.toString(),

                                                fontSize = 15.sp,

                                                fontWeight =
                                                    if (seleccionado)
                                                        FontWeight.Bold
                                                    else
                                                        FontWeight.Normal,

                                                color =
                                                    if (seleccionado)
                                                        (if (darkTheme) Color(0xFF4ADE80) else VerdeOscuro)
                                                    else
                                                        MaterialTheme
                                                            .colorScheme
                                                            .onSurface
                                            )
                                        },

                                        leadingIcon = {

                                            Box(

                                                modifier =
                                                    Modifier
                                                        .size(34.dp)
                                                        .background(

                                                            if (seleccionado)
                                                                (if (darkTheme) Color(0xFF123D25) else VerdeClaro)
                                                            else
                                                                Color.Transparent,

                                                            RoundedCornerShape(9.dp)
                                                        ),

                                                contentAlignment =
                                                    Alignment.Center

                                            ) {

                                                Icon(

                                                    imageVector =
                                                        Icons.Outlined.CalendarMonth,

                                                    contentDescription =
                                                        null,

                                                    tint =
                                                        if (seleccionado)
                                                            Verde
                                                        else
                                                            MaterialTheme
                                                                .colorScheme
                                                                .onSurfaceVariant,

                                                    modifier =
                                                        Modifier.size(18.dp)
                                                )
                                            }
                                        },

                                        trailingIcon = {

                                            if (seleccionado) {

                                                Icon(

                                                    imageVector =
                                                        Icons.Outlined.Check,

                                                    contentDescription =
                                                        "Año seleccionado",

                                                    tint = if (darkTheme) Color(0xFF4ADE80) else Verde,

                                                    modifier =
                                                        Modifier.size(20.dp)
                                                )
                                            }
                                        },

                                        onClick = {

                                            menuAnioAbierto = false

                                            viewModel.seleccionarAnio(
                                                anio
                                            )
                                        },

                                        modifier = Modifier
                                            .padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            )
                                            .background(

                                                if (seleccionado)
                                                    (if (darkTheme) Color(0xFF123D25) else VerdeClaro)
                                                else
                                                    Color.Transparent,

                                                RoundedCornerShape(12.dp)
                                            )
                                    )
                                }


                                Spacer(
                                    modifier =
                                        Modifier.height(6.dp)
                                )
                            }
                        }
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
                // ====================================================
                // CARGANDO
                // ====================================================
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colorPrincipal
                    )
                }

                // ====================================================
                // ERROR
                // ====================================================
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

                // ====================================================
                // CONTENIDO
                // ====================================================
                caja != null -> {
                    val datos = caja!!

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 16.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        // =================================================
// RESUMEN FINANCIERO
// =================================================

                        item {

                            val imagenFinanzas =
                                if (darkTheme) {
                                    R.drawable.caja_finanzas_dark
                                } else {
                                    R.drawable.caja_finanzas_light
                                }

                            Card(

                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(292.dp),

                                shape =
                                    RoundedCornerShape(22.dp),

                                colors =
                                    CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                    ),

                                elevation =
                                    CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    )

                            ) {

                                Box(
                                    modifier =
                                        Modifier.fillMaxWidth()
                                ) {

                                    // =================================================
                                    // ILUSTRACIÓN DE FINANZAS
                                    // =================================================

                                    Image(

                                        painter =
                                            painterResource(
                                                id = imagenFinanzas
                                            ),

                                        contentDescription =
                                            "Resumen financiero",

                                        contentScale =
                                            ContentScale.Crop,

                                        modifier =
                                            Modifier
                                                .align(
                                                    Alignment.TopEnd
                                                )
                                                .size(
                                                    width = 175.dp,
                                                    height = 105.dp
                                                )
                                    )


                                    // =================================================
                                    // CONTENIDO
                                    // =================================================

                                    Column(

                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(
                                                    horizontal = 16.dp,
                                                    vertical = 16.dp
                                                )

                                    ) {

                                        // =============================================
                                        // TITULO
                                        // =============================================

                                        Text(

                                            text =
                                                "Resumen financiero",

                                            fontSize = 21.sp,

                                            fontWeight =
                                                FontWeight.Bold,

                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurface
                                        )


                                        Spacer(
                                            modifier =
                                                Modifier.height(3.dp)
                                        )


                                        Text(

                                            text =
                                                "Estado general de la caja en ${datos.anio}",

                                            fontSize = 12.sp,

                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .onSurfaceVariant
                                        )


                                        Spacer(
                                            modifier =
                                                Modifier.height(18.dp)
                                        )


                                        // =============================================
                                        // FILA 1
                                        // =============================================

                                        Row(

                                            modifier =
                                                Modifier.fillMaxWidth(),

                                            horizontalArrangement =
                                                Arrangement.spacedBy(10.dp)

                                        ) {

                                            CajaResumenCard(

                                                modifier =
                                                    Modifier.weight(1f),

                                                titulo =
                                                    "Saldo inicial",

                                                valor =
                                                    datos
                                                        .resumen
                                                        .saldo_inicial,

                                                icono =
                                                    Icons
                                                        .Outlined
                                                        .AccountBalanceWallet,

                                                fondoIcono =
                                                    if (darkTheme) Color(0xFF1E3A5F) else Color(0xFFD8E7FF),

                                                colorIcono =
                                                    Azul,

                                                fondoTarjeta =
                                                    if (darkTheme) Color(0xFF172554) else AzulClaro,

                                                colorValor =
                                                    if (darkTheme) Color(0xFF93C5FD) else Color(0xFF102A56)
                                            )


                                            CajaResumenCard(

                                                modifier =
                                                    Modifier.weight(1f),

                                                titulo =
                                                    "Ingresos",

                                                valor =
                                                    datos
                                                        .resumen
                                                        .ingresos,

                                                icono =
                                                    Icons
                                                        .Outlined
                                                        .TrendingUp,

                                                fondoIcono =
                                                    if (darkTheme) Color(0xFF174D2E) else Color(0xFFD2F3DE),

                                                colorIcono =
                                                    Verde,

                                                fondoTarjeta =
                                                    if (darkTheme) Color(0xFF123D25) else VerdeClaro,

                                                colorValor =
                                                    if (darkTheme) Color(0xFF4ADE80) else VerdeOscuro
                                            )
                                        }


                                        Spacer(
                                            modifier =
                                                Modifier.height(10.dp)
                                        )


                                        // =============================================
                                        // FILA 2
                                        // =============================================

                                        Row(

                                            modifier =
                                                Modifier.fillMaxWidth(),

                                            horizontalArrangement =
                                                Arrangement.spacedBy(10.dp)

                                        ) {

                                            CajaResumenCard(

                                                modifier =
                                                    Modifier.weight(1f),

                                                titulo =
                                                    "Egresos",

                                                valor =
                                                    datos
                                                        .resumen
                                                        .egresos,

                                                icono =
                                                    Icons
                                                        .Outlined
                                                        .TrendingDown,

                                                fondoIcono =
                                                    if (darkTheme) Color(0xFF5A2222) else Color(0xFFFFD5D5),

                                                colorIcono =
                                                    Rojo,

                                                fondoTarjeta =
                                                    if (darkTheme) Color(0xFF451A1A) else RojoClaro,

                                                colorValor =
                                                    if (darkTheme) Color(0xFFF87171) else Rojo
                                            )


                                            CajaSaldoFinalCard(

                                                modifier =
                                                    Modifier.weight(1f),

                                                valor =
                                                    datos
                                                        .resumen
                                                        .saldo_final,

                                                colorPrincipal =
                                                    colorPrincipal
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        // =================================================
                        // DISTRIBUCIÓN
                        // =================================================
                        item {
                            CajaDistribucionCard(
                                darkTheme = darkTheme,
                                porcentajeIngresos = datos.resumen.porcentaje_ingresos,
                                porcentajeEgresos = datos.resumen.porcentaje_egresos,
                                ingresos = datos.resumen.ingresos,
                                egresos = datos.resumen.egresos,
                                anio = datos.anio
                            )
                        }

                        // =================================================
                        // CONSOLIDADO MENSUAL
                        // =================================================
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                VerdeClaro,
                                                RoundedCornerShape(13.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.BarChart,
                                            contentDescription = null,
                                            tint = if (darkTheme) Color(0xFF4ADE80) else Verde,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = "Consolidado mensual",
                                            fontSize = 18.sp,
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

                                // Botón Ver Reporte
                                Box(
                                    modifier = Modifier
                                        .background(if (darkTheme) Color(0xFF123D25) else VerdeClaro, RoundedCornerShape(16.dp))
                                        .clickable { /* TODO: Acción de ver reporte */ }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Outlined.BarChart,
                                            contentDescription = null,
                                            tint = if (darkTheme) Color(0xFF4ADE80) else Verde,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "Ver reporte",
                                            color = if (darkTheme) Color(0xFF4ADE80) else Verde,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // =================================================
                        // MESES
                        // =================================================
                        items(
                            items = datos.consolidado,
                            key = { it.mes }
                        ) { mes ->
                            CajaMesCard(mes = mes, darkTheme = darkTheme)
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

// ============================================================
// TARJETA RESUMEN
// ============================================================
@Composable
private fun CajaResumenCard(
    modifier: Modifier,
    titulo: String,
    valor: Double,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    fondoIcono: Color,
    colorIcono: Color,
    fondoTarjeta: Color,
    colorValor: Color
) {
    Card(
        modifier = modifier.height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Cambiado de Column a Row para colocar el ícono al lado de los textos
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 9.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ICONO
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(fondoIcono, RoundedCornerShape(9.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))

            // TEXTOS
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
                    color = colorValor,

                    softWrap = false,

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
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        // Cambiado de Column a Row para coincidir con el nuevo diseño
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 9.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ICONO
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        Blanco.copy(alpha = 0.16f),
                        RoundedCornerShape(9.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Blanco,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(7.dp))

            // TEXTOS
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Saldo final",
                    fontSize = 11.sp,
                    color = Blanco.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(1.dp))

                Text(
                    text = moneda(valor),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blanco
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
    darkTheme: Boolean,
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
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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

            // =================================================
            // PORCENTAJES + DONUT
            // =================================================
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
                        text = "${porcentajeIngresos} %",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (darkTheme) Color(0xFF4ADE80) else Verde
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = moneda(ingresos),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // DONUT
                CajaDonutChart(
                    porcentajeIngresos = porcentajeIngresos,
                    darkTheme = darkTheme
                )

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
                        text = "${porcentajeEgresos} %",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (darkTheme) Color(0xFFF87171) else Rojo
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = moneda(egresos),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // =================================================
            // BARRA DE DISTRIBUCIÓN
            // =================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .background(if (darkTheme) Color(0xFF451A1A) else RojoClaro, RoundedCornerShape(20.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(
                            porcentajeIngresos.toFloat().coerceIn(0f, 100f) / 100f
                        )
                        .height(9.dp)
                        .background(if (darkTheme) Color(0xFF4ADE80) else Verde, RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${porcentajeIngresos} % Ingresos",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (darkTheme) Color(0xFF4ADE80) else Verde
                )
                Text(
                    text = "${porcentajeEgresos} % Egresos",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (darkTheme) Color(0xFFF87171) else Rojo
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
    porcentajeIngresos: Double,
    darkTheme: Boolean
) {

    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {

            val strokeWidth = 15.dp.toPx()

            val ingresosSweep =
                porcentajeIngresos
                    .toFloat()
                    .coerceIn(0f, 100f) * 3.6f

            // =================================================
            // VERDE = INGRESOS
            // =================================================
            //
            // Comienza desde la parte superior
            // y avanza hacia la izquierda.
            //
            drawArc(
                color = if (darkTheme) Color(0xFF4ADE80) else Verde,

                startAngle = -90f,

                sweepAngle = -ingresosSweep,

                useCenter = false,

                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )

            // =================================================
            // ROJO = EGRESOS
            // =================================================
            //
            // Completa el resto del círculo.
            //
            drawArc(
                color = if (darkTheme) Color(0xFFF87171) else Rojo,

                startAngle =
                    -90f - ingresosSweep,

                sweepAngle =
                    -(360f - ingresosSweep),

                useCenter = false,

                style = Stroke(
                    width = strokeWidth,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text = "100%",

                fontSize = 15.sp,

                fontWeight =
                    FontWeight.Bold,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurface
            )

            Text(
                text = "Movimientos",

                fontSize = 8.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )

            Text(
                text = "del año",

                fontSize = 8.sp,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

// ============================================================
// TARJETA MES
// ============================================================
@Composable
private fun CajaMesCard(
    mes: CajaMes,
    darkTheme: Boolean
) {
    val tieneMovimientos = mes.ingresos != 0.0 || mes.egresos != 0.0

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // =================================================
            // CABECERA
            // =================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (darkTheme) Color(0xFF123D25) else VerdeClaro, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = if (darkTheme) Color(0xFF4ADE80) else Verde,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(11.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = mes.nombre_mes,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (tieneMovimientos) "Con movimientos" else "Sin movimientos",
                        fontSize = 11.sp,
                        color = if (tieneMovimientos) Verde else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Píldora de Saldo Final en la esquina superior
                Box(
                    modifier = Modifier
                        .background(
                            if (mes.saldo_final >= 0) { if (darkTheme) Color(0xFF123D25) else VerdeClaro } else { if (darkTheme) Color(0xFF451A1A) else RojoClaro },
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = moneda(mes.saldo_final),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (mes.saldo_final >= 0) { if (darkTheme) Color(0xFF4ADE80) else Verde } else { if (darkTheme) Color(0xFFF87171) else Rojo }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (mes.saldo_final >= 0) { if (darkTheme) Color(0xFF4ADE80) else Verde } else { if (darkTheme) Color(0xFFF87171) else Rojo },
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            Spacer(modifier = Modifier.height(12.dp))

            // =================================================
            // DATOS DEL MES (4 Columnas)
            // =================================================
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
                    color = if (darkTheme) Color(0xFF4ADE80) else Verde
                )

                CajaDatoMes(
                    etiqueta = "Egresos",
                    valor = mes.egresos,
                    color = if (darkTheme) Color(0xFFF87171) else Rojo
                )

                CajaDatoMes(
                    etiqueta = "Saldo final",
                    valor = mes.saldo_final,
                    color = if (mes.saldo_final >= 0) { if (darkTheme) Color(0xFF4ADE80) else Verde } else { if (darkTheme) Color(0xFFF87171) else Rojo }
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
            fontSize = 9.sp,
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

