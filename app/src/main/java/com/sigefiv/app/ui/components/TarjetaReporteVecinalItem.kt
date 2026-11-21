package com.sigefiv.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.data.model.ZoeReporteVecinal

@Composable
fun TarjetaReporteVecinalItem(
    reporte: ZoeReporteVecinal,
    colorPrincipal: Color,
    modifier: Modifier = Modifier
) {
    val estadoNormalizado = reporte.estado.trim().lowercase()
    val (colorBadge, textoEstado) = when {
        estadoNormalizado.contains("atendido") || estadoNormalizado.contains("resuelto") ->
            Color(0xFF16A34A) to "Atendido"
        estadoNormalizado.contains("revision") || estadoNormalizado.contains("revisión") ->
            Color(0xFF2563EB) to "En revisión"
        else ->
            Color(0xFFD97706) to "Pendiente"
    }

    val urlImagenCompleta = remember(reporte.imagenUrl) {
        val ruta = reporte.imagenUrl?.trim().orEmpty()
        when {
            ruta.isEmpty() || ruta.endsWith(".tmp", ignoreCase = true) -> null
            ruta.startsWith("http://") || ruta.startsWith("https://") -> ruta
            else -> {
                val hostBase = ServidorConfig.BASE_URL
                    .removeSuffix("/")
                    .removeSuffix("/api")
                val rutaSinSlashes = ruta.trimStart('/')
                val rutaLimpia = if (rutaSinSlashes.startsWith("storage/")) {
                    rutaSinSlashes
                } else {
                    "storage/$rutaSinSlashes"
                }
                "$hostBase/$rutaLimpia"
            }
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reporte #${reporte.id}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = colorPrincipal
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = colorBadge.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, colorBadge.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = textoEstado,
                        color = colorBadge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = reporte.reporte,
                fontSize = 13.5.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            var errorCargaImagen by remember(urlImagenCompleta) { mutableStateOf(false) }

            if (!urlImagenCompleta.isNullOrBlank() && !errorCargaImagen) {
                AsyncImage(
                    model = urlImagenCompleta,
                    contentDescription = "Evidencia del reporte #${reporte.id}",
                    onError = {
                        errorCargaImagen = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = reporte.fechaEnvio,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}