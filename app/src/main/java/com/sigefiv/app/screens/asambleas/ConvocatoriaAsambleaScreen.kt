@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.asambleas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import java.text.SimpleDateFormat
import java.util.Locale

private val FondoLienzo = Color(0xFFF8FAFC)
private val Blanco = Color.White
private val GrisBorde = Color(0xFFE2E8F0)
private val TextoOscuro = Color(0xFF0F172A)
private val TextoSecundario = Color(0xFF64748B)

@Composable
fun ConvocatoriaAsambleaScreen(
    asamblea: Asamblea,
    onBackClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())

    Scaffold(
        containerColor = FondoLienzo,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Citación Oficial",
                        color = Blanco,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorPrincipal)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Lienzo tipo Cédula / Invitación Oficial Única
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, GrisBorde)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // 1. Membrete de Convocatoria
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(colorPrincipal.copy(alpha = 0.12f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Campaign,
                                        contentDescription = null,
                                        tint = colorPrincipal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "CONVOCATORIA",
                                        color = colorPrincipal,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.2.sp
                                    )
                                    Text(
                                        text = asamblea.tipo?.ifBlank { "Asamblea General" } ?: "Asamblea General",
                                        color = TextoSecundario,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // Badge de Importancia
                            val colorBadge = when (asamblea.importancia?.lowercase(Locale.getDefault())) {
                                "urgente" -> Color(0xFFEF4444)
                                "importante" -> Color(0xFFF59E0B)
                                else -> colorPrincipal
                            }
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = colorBadge.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = asamblea.importancia?.replaceFirstChar { it.uppercase() } ?: "Normal",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    color = colorBadge,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. Título Principal
                        Text(
                            text = asamblea.titulo?.takeIf { it.isNotBlank() } ?: "Asamblea de Vecinos",
                            color = TextoOscuro,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 26.sp
                        )

                        if (!asamblea.convoca.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Convoca: ${asamblea.convoca}",
                                color = TextoSecundario,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = GrisBorde)
                        Spacer(modifier = Modifier.height(20.dp))

                        // 3. Cuadrícula de Fecha, Horas y Lugar
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            ItemDatoInvitacion(
                                icon = Icons.Outlined.Event,
                                titulo = "Fecha programada",
                                valor = formatearFechaCompleta(asamblea.fecha),
                                colorIcono = colorPrincipal
                            )

                            // Bloque horario completo: Hora oficial + 1ª y 2ª citación
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                color = colorPrincipal.copy(alpha = 0.05f),
                                border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.2f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp, horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. Hora de la asamblea
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "Hora Asamblea",
                                            fontSize = 10.5.sp,
                                            color = TextoSecundario,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = formatearHora12Horas(asamblea.hora).ifBlank { "--:--" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colorPrincipal
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(28.dp)
                                            .width(1.dp)
                                            .background(colorPrincipal.copy(alpha = 0.25f))
                                    )

                                    // 2. Primera citación
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "1ª Citación",
                                            fontSize = 10.5.sp,
                                            color = TextoSecundario,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = formatearHora12Horas(asamblea.primera_citacion).ifBlank { "--:--" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoOscuro
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .height(28.dp)
                                            .width(1.dp)
                                            .background(colorPrincipal.copy(alpha = 0.25f))
                                    )

                                    // 3. Segunda citación
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "2ª Citación",
                                            fontSize = 10.5.sp,
                                            color = TextoSecundario,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = formatearHora12Horas(asamblea.segunda_citacion).ifBlank { "--:--" },
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoOscuro
                                        )
                                    }
                                }
                            }

                            ItemDatoInvitacion(
                                icon = Icons.Outlined.LocationOn,
                                titulo = "Lugar de encuentro",
                                valor = asamblea.lugar?.takeIf { it.isNotBlank() } ?: "Local comunal",
                                colorIcono = colorPrincipal
                            )

                            // Ubicación vecinal complementaria
                            val tieneUbicacion = !asamblea.sector.isNullOrBlank() || !asamblea.grupo.isNullOrBlank() || !asamblea.manzana.isNullOrBlank() || !asamblea.lote.isNullOrBlank()
                            if (tieneUbicacion) {
                                val coordenadas = listOfNotNull(
                                    asamblea.sector?.takeIf { it.isNotBlank() }?.let { "Sector $it" },
                                    asamblea.grupo?.takeIf { it.isNotBlank() }?.let { "Grupo $it" },
                                    asamblea.manzana?.takeIf { it.isNotBlank() }?.let { "Mz. $it" },
                                    asamblea.lote?.takeIf { it.isNotBlank() }?.let { "Lt. $it" }
                                ).joinToString(" • ")

                                Text(
                                    text = "📍 $coordenadas",
                                    fontSize = 12.sp,
                                    color = TextoSecundario,
                                    modifier = Modifier.padding(start = 38.dp)
                                )
                            }
                        }

                        // 4. Descripción adicional si existe
                        if (!asamblea.descripcion.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = GrisBorde)
                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Detalle de la citación",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = asamblea.descripcion,
                                color = TextoSecundario,
                                fontSize = 13.5.sp,
                                lineHeight = 20.sp
                            )
                        }

                        // 5. Agenda de temas
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = GrisBorde)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Orden del Día (${asamblea.agendas.size} puntos)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        if (asamblea.agendas.isEmpty()) {
                            Text(
                                text = "Temas generales según estatuto comunal.",
                                color = TextoSecundario,
                                fontSize = 13.sp
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                asamblea.agendas.sortedBy { it.numero }.forEach { punto ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text(
                                            text = "${punto.numero}.",
                                            color = colorPrincipal,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.width(22.dp)
                                        )
                                        Text(
                                            text = punto.descripcion ?: "",
                                            color = TextoOscuro,
                                            fontSize = 13.5.sp,
                                            lineHeight = 19.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Pie formal de la citación
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FondoLienzo, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Agradecemos de antemano su puntual asistencia y compromiso vecinal.",
                                fontSize = 11.5.sp,
                                color = TextoSecundario,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemDatoInvitacion(
    icon: ImageVector,
    titulo: String,
    valor: String,
    colorIcono: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(colorIcono.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = colorIcono, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(text = titulo, fontSize = 11.sp, color = TextoSecundario)
            Text(text = valor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoOscuro)
        }
    }
}

private fun formatearFechaCompleta(fecha: String?): String {
    if (fecha.isNullOrBlank()) return "Fecha no indicada"
    return try {
        val entrada = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        entrada.isLenient = false
        val salida = SimpleDateFormat("EEEE d 'de' MMMM, yyyy", Locale("es", "ES"))
        val fechaParseada = entrada.parse(fecha.take(10))
        if (fechaParseada != null) {
            salida.format(fechaParseada).replaceFirstChar { it.uppercase(Locale("es", "ES")) }
        } else {
            fecha
        }
    } catch (_: Exception) {
        fecha
    }
}

private fun formatearHora12Horas(hora: String?): String {
    if (hora.isNullOrBlank()) return ""
    return try {
        val entrada = SimpleDateFormat("HH:mm", Locale.US)
        entrada.isLenient = false
        val salida = SimpleDateFormat("hh:mm a", Locale.US)
        val horaParseada = entrada.parse(hora.take(5))
        if (horaParseada != null) {
            salida.format(horaParseada).uppercase(Locale.US)
        } else {
            hora
        }
    } catch (_: Exception) {
        hora
    }
}