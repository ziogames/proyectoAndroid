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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import java.text.SimpleDateFormat
import java.util.Locale

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF1F5F9)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdePrincipal = Color(0xFF0F766E)
private val VerdeSuave = Color(0xFFCCFBF1)
private val VerdeTexto = Color(0xFF0D9488)
private val Blanco = Color.White
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisSecundario = Color(0xFF475569)
private val GrisClaro = Color(0xFF94A3B8)
private val GrisBorde = Color(0xFFE2E8F0)
private val VerdeFondo = Color(0xFFF0FDFA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConvocatoriaAsambleaScreen(
    asamblea: Asamblea,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Convocatoria",
                            color = Blanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Asamblea de vecinos",
                            color = Blanco.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdePrincipal
                )
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ENCABEZADO DE LA CONVOCATORIA
            item {
                FormCard {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "CONVOCATORIA",
                            color = VerdeTexto,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "ASAMBLEA DE VECINOS",
                            color = TextoPrincipal,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = VerdeSuave
                        ) {
                            Text(
                                text = textoEstado(asamblea.estado),
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 5.dp
                                ),
                                color = VerdeTexto,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ASUNTO
            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Asunto principal",
                        subtitulo = "Título y modalidad de la convocatoria"
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = asamblea.titulo?.takeIf { it.isNotBlank() } ?: "Asamblea de vecinos",
                        color = TextoPrincipal,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!asamblea.tipo.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = asamblea.tipo,
                            color = GrisSecundario,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // FECHA Y HORARIOS
            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Fecha y horario",
                        subtitulo = "Programación y citaciones oficiales"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DatoConIcono(
                        icon = Icons.Outlined.CalendarMonth,
                        etiqueta = "Fecha",
                        valor = formatearFechaCompleta(asamblea.fecha)
                    )

                    if (!asamblea.hora.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        DatoConIcono(
                            icon = Icons.Outlined.Schedule,
                            etiqueta = "Hora de la asamblea",
                            valor = formatearHora12Horas(asamblea.hora)
                        )
                    }

                    if (!asamblea.primera_citacion.isNullOrBlank() || !asamblea.segunda_citacion.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = GrisBorde)
                        Spacer(modifier = Modifier.height(12.dp))

                        if (!asamblea.primera_citacion.isNullOrBlank()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Primera citación",
                                    color = TextoPrincipal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = formatearHora12Horas(asamblea.primera_citacion),
                                    color = VerdeTexto,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (!asamblea.segunda_citacion.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Segunda citación",
                                    color = TextoPrincipal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = formatearHora12Horas(asamblea.segunda_citacion),
                                    color = VerdeTexto,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // LUGAR
            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Ubicación",
                        subtitulo = "Lugar de encuentro"
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DatoConIcono(
                        icon = Icons.Outlined.LocationOn,
                        etiqueta = "Lugar de la asamblea",
                        valor = asamblea.lugar?.takeIf { it.isNotBlank() } ?: "Lugar no indicado"
                    )
                }
            }

            // CONVOCA
            if (!asamblea.convoca.isNullOrBlank()) {
                item {
                    FormCard {
                        SeccionTitulo(
                            titulo = "Convocante",
                            subtitulo = "Organizador de la sesión"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        DatoConIcono(
                            icon = Icons.Outlined.Campaign,
                            etiqueta = "Responsable de la convocatoria",
                            valor = asamblea.convoca
                        )
                    }
                }
            }

            // UBICACIÓN VECINAL
            if (
                !asamblea.sector.isNullOrBlank() ||
                !asamblea.grupo.isNullOrBlank() ||
                !asamblea.manzana.isNullOrBlank() ||
                !asamblea.lote.isNullOrBlank()
            ) {
                item {
                    FormCard {
                        SeccionTitulo(
                            titulo = "Ubicación vecinal",
                            subtitulo = "Referencia territorial"
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            UbicacionItem(
                                modifier = Modifier.weight(1f),
                                etiqueta = "Sector",
                                valor = asamblea.sector
                            )
                            UbicacionItem(
                                modifier = Modifier.weight(1f),
                                etiqueta = "Grupo",
                                valor = asamblea.grupo
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            UbicacionItem(
                                modifier = Modifier.weight(1f),
                                etiqueta = "Manzana",
                                valor = asamblea.manzana
                            )
                            UbicacionItem(
                                modifier = Modifier.weight(1f),
                                etiqueta = "Lote",
                                valor = asamblea.lote
                            )
                        }
                    }
                }
            }

            // IMPORTANCIA
            if (!asamblea.importancia.isNullOrBlank()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = VerdeFondo,
                        border = BorderStroke(1.dp, VerdeSuave)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Event,
                                contentDescription = null,
                                tint = VerdeTexto,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(
                                    text = "Nivel de importancia",
                                    color = GrisClaro,
                                    fontSize = 11.sp
                                )

                                Text(
                                    text = textoImportancia(asamblea.importancia),
                                    color = TextoPrincipal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
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
                            text = asamblea.descripcion,
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
                        subtitulo = "Puntos que serán tratados"
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
                            asamblea.agendas
                                .sortedBy { it.numero }
                                .forEach { agenda ->
                                    AgendaItem(
                                        numero = agenda.numero,
                                        descripcion = agenda.descripcion ?: ""
                                    )
                                }
                        }
                    }
                }
            }

            // PIE
            item {
                Text(
                    text = "Esta convocatoria corresponde a una asamblea publicada.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = GrisClaro,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
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
private fun DatoConIcono(
    icon: ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(VerdeSuave),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VerdeTexto,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = etiqueta,
                color = GrisClaro,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = valor,
                color = TextoPrincipal,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun UbicacionItem(
    modifier: Modifier = Modifier,
    etiqueta: String,
    valor: String?
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = FondoSIGEFIV,
        border = BorderStroke(1.dp, GrisBorde)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Text(
                text = etiqueta,
                color = GrisClaro,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = valor?.takeIf { it.isNotBlank() } ?: "No indicado",
                color = TextoPrincipal,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AgendaItem(
    numero: Int,
    descripcion: String
) {
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
                .clip(CircleShape)
                .background(VerdeSuave),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = numero.toString(),
                color = VerdeTexto,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = descripcion,
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp),
            color = TextoPrincipal,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

/*
|--------------------------------------------------------------------------
| FORMATO DE FECHA Y HORA
|--------------------------------------------------------------------------
*/

private fun formatearFechaCompleta(fecha: String?): String {
    if (fecha.isNullOrBlank()) {
        return "Fecha no indicada"
    }

    return try {
        val entrada = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        entrada.isLenient = false

        val salida = SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        val fechaParseada = entrada.parse(fecha.take(10))

        if (fechaParseada != null) {
            salida.format(fechaParseada).replaceFirstChar { it.uppercase(Locale("es", "ES")) }
        } else {
            fecha
        }
    } catch (e: Exception) {
        fecha
    }
}

private fun formatearHora12Horas(hora: String?): String {
    if (hora.isNullOrBlank()) {
        return ""
    }

    return try {
        val entrada = SimpleDateFormat("HH:mm", Locale.US)
        entrada.isLenient = false

        val salida = SimpleDateFormat("h:mm a", Locale.US)
        val horaParseada = entrada.parse(hora.take(5))

        if (horaParseada != null) {
            salida.format(horaParseada)
                .replace("AM", "a. m.")
                .replace("PM", "p. m.")
        } else {
            hora
        }
    } catch (e: Exception) {
        hora
    }
}

private fun textoEstado(estado: String?): String {
    return when (estado?.lowercase(Locale.getDefault())) {
        "publicada" -> "ASAMBLEA PUBLICADA"
        "cancelada" -> "ASAMBLEA CANCELADA"
        "borrador" -> "BORRADOR"
        else -> "ASAMBLEA"
    }
}

private fun textoImportancia(importancia: String?): String {
    return when (importancia?.lowercase(Locale.getDefault())) {
        "urgente" -> "Urgente"
        "importante" -> "Importante"
        else -> "Normal"
    }
}