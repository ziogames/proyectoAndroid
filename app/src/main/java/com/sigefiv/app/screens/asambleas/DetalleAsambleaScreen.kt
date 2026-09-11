@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.asambleas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES PROFESIONAL (ESTILO SIGEFIV)
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF1F5F9)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val colorPrincipal = Color(0xFF0F766E)
private val VerdeSuave = Color(0xFFCCFBF1)
private val VerdeTexto = Color(0xFF0D9488)
private val Blanco = Color.White
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisSecundario = Color(0xFF475569)
private val GrisClaro = Color(0xFF94A3B8)
private val GrisBorde = Color(0xFFE2E8F0)

private val AzulBadge = Color(0xFF2563EB)
private val AzulSuave = Color(0xFFDBEAFE)
private val RojoBadge = Color(0xFFDC2626)
private val RojoSuave = Color(0xFFFEE2E2)

@Composable
fun DetalleAsambleaScreen(
    asamblea: Asamblea,
    onBackClick: () -> Unit,
    onInicioClick: () -> Unit,
    onMovimientosClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onPeriodosClick: () -> Unit,
    onOpenDrawer: () -> Unit,
    onEditarClick: () -> Unit = {}
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val estado = asamblea.estado?.lowercase()

    val estadoTexto = when (estado) {
        "publicada" -> "Publicada"
        "cancelada" -> "Cancelada"
        else -> "Borrador"
    }

    val estadoColor = when (estado) {
        "publicada" -> VerdeTexto
        "cancelada" -> RojoBadge
        else -> AzulBadge
    }

    val estadoFondo = when (estado) {
        "publicada" -> VerdeSuave
        "cancelada" -> RojoSuave
        else -> AzulSuave
    }

    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Detalle de Asamblea",
                            color = Blanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Información de la convocatoria",
                            color = Blanco.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Regresar",
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
            NavigationBar(
                containerColor = colorPrincipal,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = onInicioClick,
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onMovimientosClick,
                    icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = "Movimientos") },
                    label = { Text("Movimientos") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onAsambleasClick,
                    icon = { Icon(Icons.Outlined.Groups, contentDescription = "Asambleas") },
                    label = { Text("Asambleas") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colorPrincipal,
                        selectedTextColor = Blanco,
                        indicatorColor = Blanco,
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onPeriodosClick,
                    icon = { Icon(Icons.Outlined.Menu, contentDescription = "Más") },
                    label = { Text("Más") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Blanco.copy(0.75f),
                        unselectedTextColor = Blanco.copy(0.75f)
                    )
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ENCABEZADO
            item {
                FormCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(VerdeSuave, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Event,
                                contentDescription = null,
                                tint = VerdeTexto,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = asamblea.titulo ?: "Sin título",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            if (!asamblea.tipo.isNullOrBlank()) {
                                Text(
                                    text = asamblea.tipo ?: "",
                                    fontSize = 13.sp,
                                    color = GrisClaro
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = estadoFondo
                        ) {
                            Text(
                                text = estadoTexto,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = estadoColor
                            )
                        }
                    }

                    if (!asamblea.convoca.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = GrisBorde)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Convoca: ${asamblea.convoca}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = GrisSecundario
                        )
                    }
                }
            }

            // FECHAS Y HORARIOS
            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Fecha y horarios",
                        subtitulo = "Citaciones y programación"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InfoBox(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.CalendarMonth,
                            label = "Fecha",
                            valor = formatearFecha(asamblea.fecha)
                        )
                        InfoBox(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Schedule,
                            label = "Hora",
                            valor = formatearHora(asamblea.hora)
                        )
                    }
                    if (!asamblea.primera_citacion.isNullOrBlank() || !asamblea.segunda_citacion.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (!asamblea.primera_citacion.isNullOrBlank()) {
                                InfoBox(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Outlined.Schedule,
                                    label = "1ª Citación",
                                    valor = asamblea.primera_citacion ?: "-"
                                )
                            }
                            if (!asamblea.segunda_citacion.isNullOrBlank()) {
                                InfoBox(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Outlined.Schedule,
                                    label = "2ª Citación",
                                    valor = asamblea.segunda_citacion ?: "-"
                                )
                            }
                        }
                    }
                }
            }

            // UBICACIÓN
            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Ubicación",
                        subtitulo = "Lugar y referencia vecinal"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoBox(
                        modifier = Modifier.fillMaxWidth(),
                        icon = Icons.Outlined.LocationOn,
                        label = "Lugar",
                        valor = asamblea.lugar?.takeIf { it.isNotBlank() } ?: "Lugar no indicado"
                    )

                    val detallesUbicacion = listOfNotNull(
                        asamblea.sector?.takeIf { it.isNotBlank() }?.let { "Sector: $it" },
                        asamblea.grupo?.takeIf { it.isNotBlank() }?.let { "Grupo: $it" },
                        asamblea.manzana?.takeIf { it.isNotBlank() }?.let { "Mz: $it" },
                        asamblea.lote?.takeIf { it.isNotBlank() }?.let { "Lote: $it" }
                    )

                    if (detallesUbicacion.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = detallesUbicacion.joinToString("  •  "),
                            fontSize = 12.sp,
                            color = GrisSecundario,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // DESCRIPCIÓN
            if (!asamblea.descripcion.isNullOrBlank()) {
                item {
                    FormCard {
                        SeccionTitulo(
                            titulo = "Descripción",
                            subtitulo = "Detalles adicionales"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = asamblea.descripcion ?: "",
                            color = TextoPrincipal,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // AGENDA
            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Agenda (${asamblea.agendas.size})",
                        subtitulo = "Puntos a tratar en la sesión"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (asamblea.agendas.isEmpty()) {
                        Text(
                            text = "No se registraron puntos de agenda.",
                            color = GrisClaro,
                            fontSize = 13.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            asamblea.agendas.forEach { agenda ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(FondoSIGEFIV, RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(VerdeSuave, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = agenda.numero.toString(),
                                            color = VerdeTexto,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = agenda.descripcion ?: "",
                                        color = TextoPrincipal,
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BOTONES DE ACCIÓN INFERIORES (REGRESAR & EDITAR)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, GrisBorde)
                    ) {
                        Text(
                            text = "Volver",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisSecundario
                        )
                    }

                    Button(
                        onClick = onEditarClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorPrincipal,
                            contentColor = Blanco
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Editar asamblea",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| COMPONENTES AUXILIARES
|--------------------------------------------------------------------------
*/

@Composable
private fun FormCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = BorderStroke(1.dp, GrisBorde),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SeccionTitulo(
    titulo: String,
    subtitulo: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitulo,
            fontSize = 12.sp,
            color = GrisClaro
        )
    }
}

@Composable
private fun InfoBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    valor: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = FondoSIGEFIV,
        border = BorderStroke(1.dp, GrisBorde)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorPrincipal,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = GrisClaro
                )
                Text(
                    text = valor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextoPrincipal
                )
            }
        }
    }
}

private fun formatearFecha(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "Pendiente"
    val partes = fecha.substringBefore("T").split("-")
    return if (partes.size == 3) "${partes[2]}/${partes[1]}/${partes[0]}" else fecha
}

private fun formatearHora(hora: String?): String {
    if (hora.isNullOrBlank()) return "Pendiente"
    return hora.substringBefore(".").substringBefore("Z")
}