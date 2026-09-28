package com.sigefiv.app.screens.sigi

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.ZoeDatoEstadistico
import com.sigefiv.app.data.model.ZoeEstadistica
import kotlin.math.max

@Composable
fun ZoeEstadisticaChart(
    estadistica: ZoeEstadistica,
    modifier: Modifier = Modifier
) {
    if (
        estadistica.datos.isEmpty() &&
        estadistica.tipo != "resumen_financiero"
    ) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = estadistica.titulo ?: "Estadística",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        estadistica.descripcion
            ?.takeIf { it.isNotBlank() }
            ?.let { descripcion ->
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = descripcion,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }

        Spacer(modifier = Modifier.height(12.dp))

        when (estadistica.tipo) {

            "ingresos_vs_egresos" -> {
                IngresosVsEgresosChart(
                    datos = estadistica.datos
                )
            }

            "evolucion_saldo" -> {
                EvolucionSaldoChart(
                    datos = estadistica.datos
                )
            }

            "egresos_por_categoria" -> {
                EgresosCategoriaChart(
                    datos = estadistica.datos
                )
            }
            "resumen_financiero" -> {
                ResumenFinancieroCard(estadistica)
            }

            else -> {
                Text(
                    text = "Tipo de estadística no reconocido.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun IngresosVsEgresosChart(
    datos: List<ZoeDatoEstadistico>
) {
    val maxValor = max(
        datos.maxOfOrNull { it.ingresos ?: 0.0 } ?: 0.0,
        datos.maxOfOrNull { it.egresos ?: 0.0 } ?: 0.0
    ).coerceAtLeast(1.0)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Leyenda("Ingresos")
        Spacer(modifier = Modifier.width(16.dp))
        Leyenda("Egresos")
    }

    Spacer(modifier = Modifier.height(12.dp))

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {
        val anchoGrupo = size.width / datos.size
        val anchoBarra = anchoGrupo * 0.25f

        datos.forEachIndexed { index, dato ->

            val ingresos = dato.ingresos ?: 0.0
            val egresos = dato.egresos ?: 0.0

            val xCentro = anchoGrupo * index + anchoGrupo / 2f

            val alturaIngresos =
                (ingresos / maxValor * (size.height - 35f)).toFloat()

            val alturaEgresos =
                (egresos / maxValor * (size.height - 35f)).toFloat()

            drawRect(
                color = Color(0xFF16A34A),
                topLeft = Offset(
                    xCentro - anchoBarra - 2f,
                    size.height - alturaIngresos - 25f
                ),
                size = androidx.compose.ui.geometry.Size(
                    anchoBarra,
                    alturaIngresos
                )
            )

            drawRect(
                color = Color(0xFFDC2626),
                topLeft = Offset(
                    xCentro + 2f,
                    size.height - alturaEgresos - 25f
                ),
                size = androidx.compose.ui.geometry.Size(
                    anchoBarra,
                    alturaEgresos
                )
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        datos.forEach { dato ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = abreviarPeriodo(dato.periodo),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EvolucionSaldoChart(
    datos: List<ZoeDatoEstadistico>
) {
    val valores = datos.map { it.saldo_final ?: 0.0 }

    val maxValor = valores.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
    ) {
        if (datos.size < 2) return@Canvas

        val ancho = size.width
        val alto = size.height - 25f
        val separacion = ancho / (datos.size - 1)

        val puntos = valores.mapIndexed { index, valor ->
            Offset(
                x = index * separacion,
                y = alto - (valor / maxValor * alto).toFloat()
            )
        }

        for (i in 0 until puntos.lastIndex) {
            drawLine(
                color = Color(0xFF2563EB),
                start = puntos[i],
                end = puntos[i + 1],
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }

        puntos.forEach { punto ->
            drawCircle(
                color = Color(0xFF2563EB),
                radius = 7f,
                center = punto
            )

            drawCircle(
                color = Color.White,
                radius = 3f,
                center = punto
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        datos.forEach { dato ->
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = abreviarPeriodo(dato.periodo),
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EgresosCategoriaChart(
    datos: List<ZoeDatoEstadistico>
) {
    val maxMonto =
        datos.maxOfOrNull { it.monto ?: 0.0 }
            ?.coerceAtLeast(1.0)
            ?: 1.0

    Column(
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        datos.forEach { dato ->

            val monto = dato.monto ?: 0.0
            val proporcion = (monto / maxMonto).toFloat()

            Text(
                text = "${dato.categoria ?: "Sin categoría"} — S/ ${"%.2f".format(monto)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(proporcion)
                        .height(10.dp)
                        .background(
                            Color(0xFFEA580C),
                            RoundedCornerShape(8.dp)
                        )
                )
            }
        }
    }
}

@Composable
private fun Leyenda(texto: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(10.dp)
                .height(10.dp)
                .background(
                    if (texto == "Ingresos")
                        Color(0xFF16A34A)
                    else
                        Color(0xFFDC2626),
                    RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.width(5.dp))

        Text(
            text = texto,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun abreviarPeriodo(periodo: String?): String {
    if (periodo.isNullOrBlank()) return ""

    return periodo
        .replace("Enero", "Ene")
        .replace("Febrero", "Feb")
        .replace("Marzo", "Mar")
        .replace("Abril", "Abr")
        .replace("Mayo", "May")
        .replace("Junio", "Jun")
        .replace("Julio", "Jul")
        .replace("Agosto", "Ago")
        .replace("Septiembre", "Sep")
        .replace("Octubre", "Oct")
        .replace("Noviembre", "Nov")
        .replace("Diciembre", "Dic")
}
@Composable
private fun ResumenFinancieroCard(
    estadistica: ZoeEstadistica
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        ResumenDato(
            titulo = "Ingresos totales",
            valor = estadistica.ingresos_total
        )

        ResumenDato(
            titulo = "Egresos totales",
            valor = estadistica.egresos_total
        )

        ResumenDato(
            titulo = "Diferencia",
            valor = estadistica.diferencia
        )

        ResumenDato(
            titulo = "Saldo inicial",
            valor = estadistica.saldo_inicial
        )

        ResumenDato(
            titulo = "Saldo final",
            valor = estadistica.saldo_final
        )
    }
}

@Composable
private fun ResumenDato(
    titulo: String,
    valor: Double?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = titulo,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "S/ ${"%.2f".format(valor ?: 0.0)}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}