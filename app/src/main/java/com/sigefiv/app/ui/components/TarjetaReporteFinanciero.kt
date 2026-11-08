package com.sigefiv.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MovimientoItemModel(
    val fecha: String,
    val categoria: String,
    val concepto: String,
    val monto: String,
    val esEgreso: Boolean = true
)

@Composable
fun TarjetaReporteFinanciero(
    titulo: String,
    movimientos: List<MovimientoItemModel>,
    colorPrincipal: Color,
    temaOscuro: Boolean,
    modifier: Modifier = Modifier,
    enBurbuja: Boolean = true // Al estar en la burbuja del chat, elimina el Card exterior redundante
) {
    if (movimientos.isEmpty()) return

    val esOscuro = temaOscuro || MaterialTheme.colorScheme.surface.luminance() < 0.5f
    val esEgreso = movimientos.any { it.esEgreso }
    val colorBadge = if (esEgreso) Color(0xFFEF4444) else Color(0xFF10B981)

    // Fondos adaptados al contraste de la burbuja activa
    val colorFondoItem = if (esOscuro) Color(0xFF2A3942) else Color(0xFFF0F2F5)
    val colorBordeCard = if (esOscuro) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0)
    val colorFondoCard = if (esOscuro) Color(0xFF1E242B) else Color.White

    var expandido by remember { mutableStateOf(false) }
    val limiteVisible = 4
    val hayMasMovimientos = movimientos.size > limiteVisible
    val movimientosMostrados = if (expandido || !hayMasMovimientos) movimientos else movimientos.take(limiteVisible)

    val totalSoles = remember(movimientos) {
        movimientos.sumOf { item ->
            item.monto.replace("S/", "")
                .replace("s/", "")
                .replace(",", "")
                .trim()
                .toDoubleOrNull() ?: 0.0
        }
    }

    val contenidoReporte = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (enBurbuja) PaddingValues(horizontal = 2.dp, vertical = 4.dp) else PaddingValues(12.dp))
        ) {
            // 1. CABECERA RESUMEN DIRECTA EN LA BURBUJA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(colorBadge.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ReceiptLong,
                        contentDescription = null,
                        tint = colorBadge,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (esEgreso) "📉 REPORTE DE EGRESOS" else "📈 REPORTE DE INGRESOS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorBadge,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = titulo.ifBlank { "${movimientos.size} movimientos registrados" },
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "S/ %.2f".format(totalSoles),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = colorBadge
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                color = if (esOscuro) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.06f),
                thickness = 0.8.dp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 2. LISTA DE MOVIMIENTOS
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                movimientosMostrados.forEach { mov ->
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = colorFondoItem
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = mov.fecha,
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontWeight = FontWeight.Medium
                                    )

                                    if (mov.categoria.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = colorPrincipal.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = mov.categoria.uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colorPrincipal,
                                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = mov.concepto,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${if (mov.esEgreso) "-" else "+"} S/ ${mov.monto.replace("S/", "").replace("s/", "").trim()}",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorBadge,
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }

            // 3. BOTÓN COLAPSABLE
            if (hayMasMovimientos) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { expandido = !expandido },
                    color = Color.Transparent
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (expandido) "Mostrar menos" else "Ver ${movimientos.size - limiteVisible} movimientos más",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorPrincipal
                        )
                        Icon(
                            imageVector = if (expandido) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = colorPrincipal,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    if (enBurbuja) {
        Box(modifier = modifier.fillMaxWidth()) {
            contenidoReporte()
        }
    } else {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colorFondoCard),
            border = BorderStroke(1.dp, colorBordeCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            contenidoReporte()
        }
    }
}