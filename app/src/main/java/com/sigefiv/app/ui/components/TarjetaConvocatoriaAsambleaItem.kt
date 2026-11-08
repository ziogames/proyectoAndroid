package com.sigefiv.app.screens.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.screens.chat.model.AsambleaEstadisticasDto

@Composable
fun TarjetaConvocatoriaAsambleaItem(
    asambleaId: Long,
    texto: String,
    yaRespondido: Boolean,
    estadisticas: AsambleaEstadisticasDto?,
    colorPrincipal: Color,
    temaOscuro: Boolean,
    onConfirmar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var opcionSeleccionada by remember { mutableStateOf<String?>(null) }
    var enviado by remember(yaRespondido) { mutableStateOf(yaRespondido) }

    val fondoTarjeta = if (temaOscuro) Color(0xFF1F2C34) else Color(0xFFF0F2F5)
    val colorBorde = if (temaOscuro) Color(0xFF2A3942) else Color(0xFFE2E8F0)
    val colorTexto = if (temaOscuro) Color(0xFFE9EDEF) else Color(0xFF111B21)
    val colorSubtexto = if (temaOscuro) Color(0xFF8696A0) else Color(0xFF64748B)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = fondoTarjeta),
        border = BorderStroke(1.dp, colorBorde),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Texto descriptivo de la convocatoria
            Text(
                text = texto,
                fontSize = 13.5.sp,
                lineHeight = 18.sp,
                color = colorTexto
            )

            // Selector interactivo (se muestra si aún no ha respondido)
            if (!enviado) {
                HorizontalDivider(color = colorBorde.copy(alpha = 0.6f), thickness = 1.dp)

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    OpcionRadioRow(
                        texto = "Sí, asistiré",
                        seleccionado = opcionSeleccionada == "asistira",
                        colorActivo = Color(0xFF15803D),
                        onClick = { opcionSeleccionada = "asistira" }
                    )

                    OpcionRadioRow(
                        texto = "No podré asistir",
                        seleccionado = opcionSeleccionada == "no_asistira",
                        colorActivo = Color(0xFFDC2626),
                        onClick = { opcionSeleccionada = "no_asistira" }
                    )

                    OpcionRadioRow(
                        texto = "Estoy en duda",
                        seleccionado = opcionSeleccionada == "en_duda",
                        colorActivo = Color(0xFFD97706),
                        onClick = { opcionSeleccionada = "en_duda" }
                    )
                }

                // Botón de confirmación
                Button(
                    onClick = {
                        opcionSeleccionada?.let { opc ->
                            enviado = true
                            onConfirmar(opc)
                        }
                    },
                    enabled = opcionSeleccionada != null,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorPrincipal,
                        contentColor = Color.White,
                        disabledContainerColor = if (temaOscuro) Color(0xFF2A3942) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Text(
                        text = "Confirmar respuesta",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Gráfica de 3 barras horizontales (Verde, Ámbar, Rojo)
            AnimatedVisibility(
                visible = enviado && estadisticas != null,
                enter = fadeIn() + slideInVertically()
            ) {
                estadisticas?.let { stats ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider(color = colorBorde.copy(alpha = 0.6f), thickness = 1.dp)

                        Text(
                            text = "Participación vecinal hasta el momento",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorSubtexto
                        )

                        val total = (stats.asistiran + stats.en_duda + stats.no_asistiran).coerceAtLeast(1)

                        // 🟢 Asistirán
                        BarraHorizontalEstadistica(
                            etiqueta = "Asistirán",
                            conteo = stats.asistiran,
                            porcentaje = stats.asistiran.toFloat() / total,
                            colorBarra = Color(0xFF15803D),
                            trackColor = Color(0xFF15803D).copy(alpha = 0.18f),
                            colorTexto = colorTexto
                        )

                        // 🟠 En duda
                        BarraHorizontalEstadistica(
                            etiqueta = "En duda",
                            conteo = stats.en_duda,
                            porcentaje = stats.en_duda.toFloat() / total,
                            colorBarra = Color(0xFFD97706),
                            trackColor = Color(0xFFD97706).copy(alpha = 0.18f),
                            colorTexto = colorTexto
                        )

                        // 🔴 No asistirán
                        BarraHorizontalEstadistica(
                            etiqueta = "No asistirán",
                            conteo = stats.no_asistiran,
                            porcentaje = stats.no_asistiran.toFloat() / total,
                            colorBarra = Color(0xFFDC2626),
                            trackColor = Color(0xFFDC2626).copy(alpha = 0.18f),
                            colorTexto = colorTexto
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OpcionRadioRow(
    texto: String,
    seleccionado: Boolean,
    colorActivo: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .selectable(selected = seleccionado, onClick = onClick)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        RadioButton(
            selected = seleccionado,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = colorActivo,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            ),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = texto,
            fontSize = 13.sp,
            fontWeight = if (seleccionado) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun BarraHorizontalEstadistica(
    etiqueta: String,
    conteo: Int,
    porcentaje: Float,
    colorBarra: Color,
    trackColor: Color,
    colorTexto: Color
) {
    val pctTexto = (porcentaje * 100).toInt()

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = etiqueta,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = colorTexto
            )
            Text(
                text = "$conteo ($pctTexto%)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )
        }
        LinearProgressIndicator(
            progress = { porcentaje },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = colorBarra,
            trackColor = trackColor
        )
    }
}