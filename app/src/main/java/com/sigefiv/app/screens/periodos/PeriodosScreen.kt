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
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.viewmodel.PeriodosViewModel
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV (ESTILO DASHBOARD / MODERNO)
|--------------------------------------------------------------------------
*/
private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdeSuave = Color(0xFFDCFCE7)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisClaro = Color(0xFF64748B)
private val Rojo = Color(0xFFDC2626)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodosScreen(
    periodosViewModel: PeriodosViewModel,
    onBackClick: () -> Unit,
    onAnioClick: (Int) -> Unit,
    onInicioClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit,
    onOpenDrawer: () -> Unit = {}
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val colores = MaterialTheme.colorScheme
    val fondoPantalla = colores.background
    val fondoTarjeta = colores.surface
    val esOscuro = colores.background.luminance() < 0.5f
    val verdeSuave = if (esOscuro) Color(0xFF064E3B) else Color(0xFFDCFCE7)
    val textoPrincipal = colores.onSurface
    val grisClaro = colores.onSurfaceVariant
    val periodos by periodosViewModel.periodos.collectAsState()
    val cargando by periodosViewModel.cargando.collectAsState()
    val mensaje by periodosViewModel.mensaje.collectAsState()
    LaunchedEffect(Unit) {
        periodosViewModel.cargarPeriodos()
    }
    val anios = periodos
        .groupBy { it.anio }
        .toSortedMap(compareByDescending { it })
    Scaffold(
        containerColor = fondoPantalla,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Períodos contables",
                            color = Blanco,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "Gestión de ejercicios anuales",
                            color = Blanco.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal
                )
            )
        },
        bottomBar = {
            BarraInferiorPeriodos(
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(fondoPantalla)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "AÑOS DISPONIBLES",
                color = colorPrincipal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            when {
                cargando -> {
                    Text(
                        text = "Cargando períodos contables...",
                        color = grisClaro,
                        fontSize = 14.sp
                    )
                }
                mensaje != null -> {
                    Text(
                        text = mensaje ?: "No se pudieron cargar los períodos.",
                        color = Rojo,
                        fontSize = 14.sp
                    )
                }
                anios.isEmpty() -> {
                    Text(
                        text = "No existen períodos registrados en el sistema.",
                        color = grisClaro,
                        fontSize = 14.sp
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = anios.entries.toList(),
                            key = { it.key }
                        ) { entrada ->
                            AnioCard(
                                anio = entrada.key,
                                cantidadPeriodos = entrada.value.size,
                                onClick = { onAnioClick(entrada.key) }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
            }
        }
    }
}
@Composable
private fun AnioCard(
    anio: Int,
    cantidadPeriodos: Int,
    onClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val colores = MaterialTheme.colorScheme
    val fondoPantalla = colores.background
    val fondoTarjeta = colores.surface
    val esOscuro = colores.background.luminance() < 0.5f
    val verdeSuave = if (esOscuro) Color(0xFF064E3B) else Color(0xFFDCFCE7)
    val textoPrincipal = colores.onSurface
    val grisClaro = colores.onSurfaceVariant
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(verdeSuave, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Año $anio",
                    tint = colorPrincipal,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = anio.toString(),
                    color = textoPrincipal,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (cantidadPeriodos == 1) "1 período registrado" else "$cantidadPeriodos períodos registrados",
                    color = grisClaro,
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = "Ver períodos del año $anio",
                tint = GrisClaro,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
/*
|--------------------------------------------------------------------------
| BARRA INFERIOR UNIFICADA (Inicio, Asamblea, Periodos, Mi cuenta)
|--------------------------------------------------------------------------
*/
@Composable
private fun BarraInferiorPeriodos(
    onInicioClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onPeriodosClick: () -> Unit,
    onMiCuentaClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val colores = MaterialTheme.colorScheme
    val fondoPantalla = colores.background
    val fondoTarjeta = colores.surface
    val esOscuro = colores.background.luminance() < 0.5f
    val verdeSuave = if (esOscuro) Color(0xFF064E3B) else Color(0xFFDCFCE7)
    val textoPrincipal = colores.onSurface
    val grisClaro = colores.onSurfaceVariant
    NavigationBar(
        containerColor = colorPrincipal,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onInicioClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = { Text(text = "Inicio") },
            colors = NavigationBarItemDefaults.colors(
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
            label = { Text(text = "Asamblea") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = onPeriodosClick,
            icon = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "Periodos"
                )
            },
            label = { Text(text = "Periodos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = colorPrincipal,
                selectedTextColor = Color.White,
                indicatorColor = if (esOscuro) Color(0xFF064E3B) else Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.75f),
                unselectedTextColor = Color.White.copy(alpha = 0.75f)
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
            label = { Text(text = "Mi cuenta") },
            colors = NavigationBarItemDefaults.colors(
                unselectedIconColor = Blanco.copy(alpha = 0.75f),
                unselectedTextColor = Blanco.copy(alpha = 0.75f)
            )
        )
    }
}
