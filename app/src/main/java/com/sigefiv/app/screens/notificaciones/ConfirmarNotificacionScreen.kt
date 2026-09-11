package com.sigefiv.app.screens.notificaciones

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES PROFESIONAL SIGEFIV
|--------------------------------------------------------------------------
*/
private val FondoPantalla = Color(0xFFF8FAFC)
private val Blanco = Color.White
private val VerdePrincipal = Color(0xFF15803D)      // Color principal de tu sistema
private val VerdeSuave = Color(0xFFDCFCE7)        // Fondo del icono de éxito
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisTexto = Color(0xFF64748B)
private val GrisBorde = Color(0xFFE2E8F0)

@Composable
fun ConfirmarNotificacionScreen(
    titulo: String,
    mensaje: String,
    tipo: String,
    destinatario: String,
    cantidadDestinatarios: Int,
    onBackClick: () -> Unit,
    onConfirmarEnvio: () -> Unit
) {
    // ============================================================
    // CONFIGURAR COLOR DE LA BARRA DE ESTADO DE FORMA NATIVA
    // ============================================================
    val view = LocalView.current
    val context = LocalContext.current
    SideEffect {
        val window = (context as? Activity)?.window
        window?.let {
            it.statusBarColor = android.graphics.Color.parseColor("#15803D")
            WindowInsetsControllerCompat(it, view).isAppearanceLightStatusBars = false
        }
    }

    val fechaHora = SimpleDateFormat(
        "dd MMM. yyyy - HH:mm",
        Locale("es", "PE")
    ).format(Date())

    val textoDestinatarios = when {
        cantidadDestinatarios > 0 -> "a $cantidadDestinatarios destinatarios."
        destinatario == "Todos los vecinos" -> "a todos los vecinos."
        destinatario == "Miembros de la directiva" -> "a los miembros de la directiva."
        else -> "a los usuarios seleccionados."
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
    ) {

        // ============================================================
        // ENCABEZADO CON VERDE INSTITUCIONAL
        // ============================================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = VerdePrincipal,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // <-- AQUÍ ESTÁ LA CLAVE: Empuja el contenido debajo de la hora/batería sin perder el fondo verde
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Blanco,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Column {
                    Text(
                        text = "SIGEFIV",
                        color = Blanco,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Confirmación de Envío",
                        color = Blanco.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }
            }
        }

        // ============================================================
        // CONTENIDO CON SCROLL
        // ============================================================
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Icono de Éxito con Anillo Suave
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .background(VerdeSuave, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Notificación enviada!",
                color = TextoPrincipal,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "El mensaje se ha enviado correctamente\n$textoDestinatarios",
                color = GrisTexto,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ====================================================
            // TARJETA DE RESUMEN EJECUTIVO
            // ====================================================
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    // Cabecera de la tarjeta
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = VerdePrincipal,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Detalles del comunicado",
                            color = TextoPrincipal,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Línea divisoria elegante
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(GrisBorde)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    DatoNotificacion(etiqueta = "Título:", valor = titulo)
                    Spacer(modifier = Modifier.height(12.dp))

                    DatoNotificacion(etiqueta = "Mensaje:", valor = mensaje)
                    Spacer(modifier = Modifier.height(12.dp))

                    DatoConIcono(
                        etiqueta = "Tipo:",
                        valor = tipo.replaceFirstChar { it.uppercase() },
                        icono = Icons.Default.Campaign
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DatoConIcono(
                        etiqueta = "Destinatario:",
                        valor = destinatario,
                        icono = Icons.Default.Groups
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DatoConIcono(
                        etiqueta = "Fecha:",
                        valor = fechaHora,
                        icono = Icons.Default.CalendarToday
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ============================================================
        // BARRA INFERIOR / BOTÓN DE ACCIÓN CON EL VERDE DEL SISTEMA
        // ============================================================
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Blanco,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onConfirmarEnvio,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VerdePrincipal,
                        contentColor = Blanco
                    )
                ) {
                    Text(
                        text = "Volver al inicio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ====================================================================
// COMPONENTES AUXILIARES
// ====================================================================

@Composable
private fun DatoNotificacion(
    etiqueta: String,
    valor: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = etiqueta,
            color = GrisTexto,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(100.dp)
        )
        Text(
            text = valor,
            color = TextoPrincipal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DatoConIcono(
    etiqueta: String,
    valor: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            color = GrisTexto,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(100.dp)
        )

        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = VerdePrincipal,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = valor,
            color = TextoPrincipal,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
    }
}