package com.sigefiv.app.screens.chat.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.sigefiv.app.data.model.ChatArchivoPayload
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import java.util.Locale

// ============================================================
// COLORES COMPARTIDOS PARA EL CHAT
// ============================================================

val VerdeMonto = Color(0xFF047857)
val RojoMonto = Color(0xFFDC2626)

@Composable
fun colorPrincipal(): Color {
    return SeasonalColors.primary(SeasonalTheme.getSeason())
}

@Composable
fun coloresChat(): ChatColors {
    val oscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f

    return if (oscuro) {
        ChatColors(
            fondo = Color(0xFF121A16),
            burbujaPropia = Color(0xFF14532D),
            burbujaAjena = Color(0xFF26332C),
            textoPrincipal = Color(0xFFF1F5F2),
            textoSecundario = Color(0xFFB7C8BC),
            nombreEmisor = Color(0xFF4ADE80),
            fondoTarjeta = Color(0xFF1D3A29),
            bordeTarjeta = Color(0xFF356B49),
            fondoInput = Color(0xFF24342B),
            textoInput = Color(0xFFF1F5F2),
            placeholderInput = Color(0xFFB7C8BC)
        )
    } else {
        ChatColors(
            fondo = Color(0xFFF9FBF9),
            burbujaPropia = Color(0xFFD9FDD3),
            burbujaAjena = Color(0xFFF0F4F1),
            textoPrincipal = Color(0xFF172B1A),
            textoSecundario = Color(0xFF5F6B61),
            nombreEmisor = Color(0xFF15803D),
            fondoTarjeta = Color(0xFFE8F5E9),
            bordeTarjeta = Color(0xFFA5D6A7),
            fondoInput = Color.White,
            textoInput = Color(0xFF172B1A),
            placeholderInput = Color(0xFF667085)
        )
    }
}

data class ChatColors(
    val fondo: Color, val burbujaPropia: Color, val burbujaAjena: Color,
    val textoPrincipal: Color, val textoSecundario: Color, val nombreEmisor: Color,
    val fondoTarjeta: Color, val bordeTarjeta: Color, val fondoInput: Color,
    val textoInput: Color, val placeholderInput: Color
)

// ============================================================
// TARJETAS VISUALES
// ============================================================

@Composable
fun TarjetaMovimientoInteligente(
    fecha: String,
    tipo: String,
    concepto: String,
    categoria: String,
    monto: String
) {
    val c = coloresChat()

    val esEgreso = tipo.contains("egreso", ignoreCase = true) ||
            tipo.contains("salida", ignoreCase = true) ||
            monto.startsWith("-")

    val colorMovimiento = if (esEgreso) RojoMonto else VerdeMonto
    val iconoMovimiento = if (esEgreso) Icons.Outlined.ArrowDownward else Icons.Outlined.ArrowUpward

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = c.fondoTarjeta,
        border = BorderStroke(1.dp, c.bordeTarjeta),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 9.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconoMovimiento,
                contentDescription = null,
                tint = colorMovimiento,
                modifier = Modifier.size(17.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                if (concepto.isNotBlank()) {
                    Text(
                        text = concepto,
                        color = c.textoPrincipal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }

                Text(
                    text = buildString {
                        append(fecha)
                        if (tipo.isNotBlank()) {
                            append(" · ")
                            append(tipo.replaceFirstChar { it.uppercase() })
                        }
                        if (categoria.isNotBlank() && categoria != "Sin categoría") {
                            append(" · ")
                            append(categoria)
                        }
                    },
                    color = c.textoSecundario,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (monto.isNotBlank()) {
                Text(
                    text = monto,
                    color = colorMovimiento,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TarjetaArchivoMensaje(archivo: ChatArchivoPayload) {
    val context = LocalContext.current
    var mostrarImagenAmpliada by remember { mutableStateOf(false) }

    val urlCorregida = archivo.url?.replace("localhost", "10.0.2.2")
    val esImagenOVid = archivo.mime?.startsWith("image/") == true || archivo.mime?.startsWith("video/") == true

    Column(modifier = Modifier.fillMaxWidth()) {
        if (esImagenOVid && !urlCorregida.isNullOrBlank()) {
            // Diseño profesional limpio para Fotos y Videos
            AsyncImage(
                model = urlCorregida,
                contentDescription = archivo.nombre ?: "Archivo adjunto",
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 300.dp) // Limita la altura para no romper el chat
                    .clip(RoundedCornerShape(8.dp))
                    .background(LocalContentColor.current.copy(alpha = 0.1f))
                    .clickable { mostrarImagenAmpliada = true },
                contentScale = ContentScale.Crop
            )

            // Leyenda justo debajo de la imagen, adopta el color del tema automáticamente
            if (!archivo.texto.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = archivo.texto,
                    color = LocalContentColor.current,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
        } else {
            // Diseño para documentos que no son imágenes (ej. PDFs, Word, sin URL válida)
            if (!archivo.texto.isNullOrBlank()) {
                Text(
                    text = archivo.texto,
                    color = LocalContentColor.current,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = LocalContentColor.current.copy(alpha = 0.08f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
                    .combinedClickable(
                        onClick = {
                            if (!urlCorregida.isNullOrBlank()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlCorregida))
                                context.startActivity(intent)
                            }
                        }
                    )
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AttachFile,
                        contentDescription = null,
                        tint = LocalContentColor.current.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = archivo.nombre ?: "Archivo adjunto",
                        color = LocalContentColor.current,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    // Modal de imagen ampliada a pantalla completa
    if (mostrarImagenAmpliada && !urlCorregida.isNullOrBlank()) {
        Dialog(onDismissRequest = { mostrarImagenAmpliada = false }) {
            Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
                Box(modifier = Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = urlCorregida,
                        contentDescription = archivo.nombre ?: "Imagen ampliada",
                        modifier = Modifier.fillMaxSize().padding(vertical = 35.dp),
                        contentScale = ContentScale.Fit
                    )
                    IconButton(
                        onClick = { mostrarImagenAmpliada = false },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                    ) {
                        Icon(Icons.Outlined.Close, contentDescription = "Cerrar imagen", tint = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun TarjetaTotalInteligente(totalSuma: Double?, lineaOriginal: String) {
    val c = coloresChat()
    val montoTextoExtraido = if (totalSuma != null) {
        val valAbsoluto = Math.abs(totalSuma)
        val strMonto = String.format(Locale.US, "%.2f", valAbsoluto)
        if (totalSuma < 0) "-S/ $strMonto" else "S/ $strMonto"
    } else {
        val partes = lineaOriginal.split(":")
        if (partes.size > 1 && partes[1].isNotBlank()) {
            partes[1].trim()
        } else {
            lineaOriginal.replace("Total", "", ignoreCase = true).replace("•", "").trim().takeIf { it.isNotBlank() } ?: "S/ 0.00"
        }
    }

    val esNegativo = (totalSuma ?: 0.0) < 0 || montoTextoExtraido.startsWith("-")
    val colorTotal = if (esNegativo) RojoMonto else VerdeMonto

    Surface(
        shape = RoundedCornerShape(10.dp),
        color = c.fondoTarjeta,
        border = BorderStroke(1.dp, c.bordeTarjeta),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Total", color = c.textoPrincipal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = montoTextoExtraido, color = colorTotal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ChatEmptyState() {
    val c = coloresChat()

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(80.dp),
            shape = CircleShape,
            color = colorPrincipal().copy(alpha = 0.12f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Outlined.Group,
                    contentDescription = null,
                    tint = colorPrincipal(),
                    modifier = Modifier.size(42.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Chat Vecinal", color = c.textoPrincipal, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Aún no hay mensajes.\nSé el primero en escribir.",
            color = c.textoSecundario,
            textAlign = TextAlign.Center,
            fontSize = 13.sp
        )
    }
}