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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.sigefiv.app.ui.components.AppBottomBar

/*
|--------------------------------------------------------------------------
| COLORES SEMÁNTICOS
|--------------------------------------------------------------------------
*/

private val VerdePublicado = Color(0xFF16A34A)
private val RojoCancelado = Color(0xFFDC2626)
private val AzulBorrador = Color(0xFF2563EB)

@Composable
fun DetalleAsambleaScreen(
    asamblea: Asamblea,
    onBackClick: () -> Unit,
    onInicioClick: () -> Unit,
    onMovimientosClick: () -> Unit,
    onAsambleasClick: () -> Unit,
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit,
    onMiCuentaClick: () -> Unit = {},
    onOpenDrawer: () -> Unit,
    onEditarClick: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val estado = asamblea.estado?.trim()?.lowercase()

    val (estadoTexto, estadoColor) = when (estado) {
        "publicada" -> Pair("Publicada", VerdePublicado)
        "cancelada" -> Pair("Cancelada", RojoCancelado)
        else -> Pair("Borrador", AzulBorrador)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Detalle de Asamblea",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Información de la convocatoria",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Regresar",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
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
                pantallaActual = "Asamblea",
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onEmprendimientosClick = onEmprendimientosClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = onMiCuentaClick
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ENCABEZADO
            item {
                FormCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = primaryColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Outlined.Event,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = asamblea.titulo ?: "Sin título",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            if (!asamblea.tipo.isNullOrBlank()) {
                                Text(
                                    text = asamblea.tipo ?: "",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = estadoColor.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, estadoColor.copy(alpha = 0.25f))
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
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Convoca: ${asamblea.convoca}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            valor = formatearFecha(asamblea.fecha),
                            primaryColor = primaryColor
                        )
                        InfoBox(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Schedule,
                            label = "Hora oficial",
                            valor = formatearHora(asamblea.hora),
                            primaryColor = primaryColor
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
                                    valor = asamblea.primera_citacion ?: "-",
                                    primaryColor = primaryColor
                                )
                            }
                            if (!asamblea.segunda_citacion.isNullOrBlank()) {
                                InfoBox(
                                    modifier = Modifier.weight(1f),
                                    icon = Icons.Outlined.Schedule,
                                    label = "2ª Citación",
                                    valor = asamblea.segunda_citacion ?: "-",
                                    primaryColor = primaryColor
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
                        valor = asamblea.lugar?.takeIf { it.isNotBlank() } ?: "Lugar no indicado",
                        primaryColor = primaryColor
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            color = MaterialTheme.colorScheme.onSurface,
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
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            asamblea.agendas.forEach { agenda ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        modifier = Modifier.size(28.dp),
                                        shape = CircleShape,
                                        color = primaryColor.copy(alpha = 0.12f),
                                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = agenda.numero.toString(),
                                                color = primaryColor,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = agenda.descripcion ?: "",
                                        color = MaterialTheme.colorScheme.onSurface,
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
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            text = "Volver",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = onEditarClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
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
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitulo,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun InfoBox(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    valor: String,
    primaryColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = primaryColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = valor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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