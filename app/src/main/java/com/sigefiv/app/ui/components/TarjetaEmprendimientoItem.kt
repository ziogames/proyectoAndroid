package com.sigefiv.app.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.screens.chat.model.ChatUIComponent

@Composable
fun TarjetaEmprendimientoItem(
    emprendimiento: Any,
    colorPrincipal: Color,
    temaOscuro: Boolean
) {
    val context = LocalContext.current

    val colorFondoTarjeta = if (temaOscuro) Color(0xFF1E242B) else Color.White
    val colorBorde = if (temaOscuro) Color.White.copy(alpha = 0.09f) else Color(0xFFE2E8F0)

    // Lectura flexible según venga de ChatUIComponent.Emprendimiento o del modelo de ZOE
    val nombreNegocio: String
    val descripcionNegocio: String?
    val rubroEtiqueta: String?
    val urlOriginal: String?
    val zonaAtencion: String?
    val horarioAtencion: String?
    val telefonoLimpio: String
    val mostrarWhatsapp: Boolean

    if (emprendimiento is ChatUIComponent.Emprendimiento) {
        nombreNegocio = emprendimiento.nombre.ifBlank { "Emprendimiento Vecinal" }
        rubroEtiqueta = emprendimiento.categoria.takeIf { it.isNotBlank() }
        descripcionNegocio = emprendimiento.descripcion.takeIf { it.isNotBlank() }
        urlOriginal = emprendimiento.imagenUrl // ✅ Lee la URL de la imagen
        zonaAtencion = emprendimiento.zona       // ✅ Lee la zona
        horarioAtencion = emprendimiento.horario // ✅ Lee el horario
        telefonoLimpio = emprendimiento.contacto.trim()
        mostrarWhatsapp = telefonoLimpio.isNotEmpty()
    } else {
        nombreNegocio = leerPropiedadTexto(emprendimiento, "nombre", "name")
            ?: "Emprendimiento Vecinal"
        descripcionNegocio = leerPropiedadTexto(emprendimiento, "descripcion", "description")
        rubroEtiqueta = leerPropiedadTexto(emprendimiento, "categoria", "modalidad")
        urlOriginal = leerPropiedadTexto(emprendimiento, "imagen", "imagen_url")
            ?: leerPrimeraFotoUrl(emprendimiento, "fotos", "photos")
        zonaAtencion = leerPropiedadTexto(emprendimiento, "zona_atencion", "attention_zone")
        horarioAtencion = leerPropiedadTexto(emprendimiento, "horario", "schedule")
        telefonoLimpio = leerPropiedadTexto(emprendimiento, "telefono", "phone").orEmpty().trim()
        mostrarWhatsapp = leerPropiedadBooleana(emprendimiento, "show_whatsapp") ?: true
    }

    val fotoUrl = ServidorConfig.resolverUrlImagen(urlOriginal)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorFondoTarjeta),
        border = BorderStroke(1.dp, colorBorde),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. 📸 FOTO PRINCIPAL DEL NEGOCIO
            if (!fotoUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = fotoUrl,
                        contentDescription = "Foto de $nombreNegocio",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        onError = { estado ->
                            android.util.Log.e("ZOE_COIL", "Error cargando foto [$fotoUrl]: ${estado.result.throwable.message}")
                        },
                        onSuccess = {
                            android.util.Log.d("ZOE_COIL", "Foto cargada exitosamente: $fotoUrl")
                        }
                    )

                    if (rubroEtiqueta != null) {
                        Surface(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Text(
                                text = rubroEtiqueta.uppercase(),
                                color = Color(0xFF4ADE80),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            // 2. 📝 CUERPO DE DETALLES
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (fotoUrl.isNullOrBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    colorPrincipal.copy(alpha = 0.12f),
                                    RoundedCornerShape(12.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Storefront,
                                contentDescription = null,
                                tint = colorPrincipal,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = nombreNegocio,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            if (rubroEtiqueta != null) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = rubroEtiqueta.uppercase(),
                                    color = colorPrincipal,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        text = nombreNegocio,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (!descripcionNegocio.isNullOrBlank()) {
                    Text(
                        text = descripcionNegocio,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                val tieneZona = !zonaAtencion.isNullOrBlank()
                val tieneHorario = !horarioAtencion.isNullOrBlank()

                if (tieneZona || tieneHorario) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (tieneZona) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = "Zona",
                                    tint = colorPrincipal,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Zona: $zonaAtencion",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        if (tieneHorario) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.AccessTime,
                                    contentDescription = "Horario",
                                    tint = colorPrincipal,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Horario: $horarioAtencion",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (telefonoLimpio.isNotEmpty()) {
                    HorizontalDivider(color = colorBorde, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { llamarVecino(context, telefonoLimpio) },
                            shape = RoundedCornerShape(12.dp),
                            color = colorPrincipal.copy(alpha = 0.10f),
                            border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .background(colorPrincipal, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Phone,
                                        contentDescription = "Llamar",
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Contactar al vecino",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = formatearTelefonoPeru(telefonoLimpio),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        if (mostrarWhatsapp) {
                            Spacer(modifier = Modifier.width(10.dp))

                            IconButton(
                                onClick = { abrirWhatsApp(context, telefonoLimpio) },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFF25D366), CircleShape)
                            ) {
                                IconoWhatsAppVector(
                                    modifier = Modifier.size(22.dp),
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun leerPropiedadTexto(objeto: Any, vararg nombres: String): String? {
    for (nombre in nombres) {
        try {
            val field = objeto.javaClass.getDeclaredField(nombre).apply { isAccessible = true }
            val valor = field.get(objeto) as? String
            if (!valor.isNullOrBlank()) return valor
        } catch (_: Exception) {}
    }
    return null
}

private fun leerPropiedadBooleana(objeto: Any, nombre: String): Boolean? {
    return try {
        val field = objeto.javaClass.getDeclaredField(nombre).apply { isAccessible = true }
        field.get(objeto) as? Boolean
    } catch (_: Exception) {
        null
    }
}

private fun leerPrimeraFotoUrl(objeto: Any, vararg nombresListas: String): String? {
    for (nombre in nombresListas) {
        try {
            val field = objeto.javaClass.getDeclaredField(nombre).apply { isAccessible = true }
            val lista = field.get(objeto) as? Iterable<*>
            val primerItem = lista?.firstOrNull() ?: continue
            val url = leerPropiedadTexto(primerItem, "url")
            if (!url.isNullOrBlank()) return url
        } catch (_: Exception) {}
    }
    return null
}

@Composable
private fun IconoWhatsAppVector(
    modifier: Modifier = Modifier,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val bubblePath = Path().apply {
            moveTo(w * 0.15f, h * 0.85f)
            lineTo(w * 0.22f, h * 0.68f)
            cubicTo(
                w * 0.10f, h * 0.50f,
                w * 0.18f, h * 0.20f,
                w * 0.45f, h * 0.15f
            )
            cubicTo(
                w * 0.75f, h * 0.10f,
                w * 0.92f, h * 0.35f,
                w * 0.85f, h * 0.62f
            )
            cubicTo(
                w * 0.78f, h * 0.85f,
                w * 0.50f, h * 0.90f,
                w * 0.33f, h * 0.79f
            )
            close()
        }

        drawPath(
            path = bubblePath,
            color = tint,
            style = Stroke(
                width = w * 0.085f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        val phonePath = Path().apply {
            moveTo(w * 0.38f, h * 0.36f)
            quadraticTo(w * 0.34f, h * 0.45f, w * 0.44f, h * 0.56f)
            quadraticTo(w * 0.55f, h * 0.66f, w * 0.64f, h * 0.62f)
        }

        drawPath(
            path = phonePath,
            color = tint,
            style = Stroke(
                width = w * 0.095f,
                cap = StrokeCap.Round
            )
        )
    }
}

private fun formatearTelefonoPeru(telefono: String): String {
    val digitos = telefono.filter { it.isDigit() }
    val local = if (digitos.startsWith("51") && digitos.length == 11) {
        digitos.substring(2)
    } else {
        digitos
    }
    return if (local.length == 9) {
        "${local.substring(0, 3)} ${local.substring(3, 6)} ${local.substring(6, 9)}"
    } else {
        telefono
    }
}

private fun llamarVecino(context: Context, telefono: String) {
    val numero = telefono.filter { it.isDigit() || it == '+' }
    if (numero.isBlank()) return
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numero"))
    context.startActivity(intent)
}

private fun abrirWhatsApp(context: Context, telefono: String) {
    val numero = telefono.filter { it.isDigit() }
    if (numero.isBlank()) return
    val numeroPeru = if (numero.startsWith("51")) numero else "51$numero"
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$numeroPeru"))
    context.startActivity(intent)
}