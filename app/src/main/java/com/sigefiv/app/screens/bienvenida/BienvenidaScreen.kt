package com.sigefiv.app.screens.bienvenida

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

private val AzulSubtitulo = Color(0xFF1D4ED8)
private val FondoPantalla = Color(0xFFF0F6FE)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val TextoOscuro = Color(0xFF0F172A)
private val TextoGris = Color(0xFF475569)
private val TextoMuted = Color(0xFF64748B)
private val GrisBorde = Color(0xFFE2E8F0)
private val VerdeTransparencia = Color(0xFF16A34A)
private val VerdeBgTransparencia = Color(0xFFDCFCE7)
private val AzulBurbujaSigi = Color(0xFFEFF6FF)

@Composable
fun BienvenidaScreen(
    onComenzarClick: () -> Unit
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoPantalla)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contenedor blanco tipo tarjeta central
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo y Marca
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colorPrincipal.copy(alpha = 0.12f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.HomeWork,
                                    contentDescription = null,
                                    tint = colorPrincipal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SIGEFIV",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextoOscuro,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Título y Subtítulo
                    Text(
                        text = "¡Bienvenido a SIGEFIV!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextoOscuro,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "El sistema de gestión financiera de tu junta vecinal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AzulSubtitulo,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SIGEFIV es una plataforma creada para facilitar la gestión, organización y transparencia de la información de nuestra junta vecinal. Aquí podrás consultar información financiera, movimientos, recibos, períodos, asambleas y otros datos de interés para los vecinos.",
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        color = TextoGris,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // Encabezado de sección
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "¿Qué puedes hacer en SIGEFIV?",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextoOscuro
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(colorPrincipal)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tarjetas en lista de 2 columnas o apiladas
                    ItemCaracteristica(
                        icono = Icons.Default.Savings,
                        colorIcono = Color(0xFFF59E0B),
                        colorFondoIcono = Color(0xFFFEF3C7),
                        titulo = "Ingresos y egresos",
                        descripcion = "Registra y consulta los movimientos económicos de la junta vecinal."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ItemCaracteristica(
                        icono = Icons.Default.ReceiptLong,
                        colorIcono = Color(0xFF0284C7),
                        colorFondoIcono = Color(0xFFE0F2FE),
                        titulo = "Recibos y comprobantes",
                        descripcion = "Mantén identificados y organizados los movimientos y sus comprobantes."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ItemCaracteristica(
                        icono = Icons.Default.BarChart,
                        colorIcono = Color(0xFF8B5CF6),
                        colorFondoIcono = Color(0xFFEDE9FE),
                        titulo = "Resúmenes financieros",
                        descripcion = "Consulta ingresos, egresos, saldo anterior, disponible y saldo de caja."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ItemCaracteristica(
                        icono = Icons.Default.CalendarMonth,
                        colorIcono = Color(0xFF3B82F6),
                        colorFondoIcono = Color(0xFFDBEAFE),
                        titulo = "Gestión de períodos",
                        descripcion = "Organiza la información por meses y períodos manteniendo la continuidad."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ItemCaracteristica(
                        icono = Icons.Default.Apartment,
                        colorIcono = Color(0xFFEA580C),
                        colorFondoIcono = Color(0xFFFFEDD5),
                        titulo = "Predios y propietarios",
                        descripcion = "Administra la información relacionada con los vecinos y sus predios."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ItemCaracteristica(
                        icono = Icons.Default.Groups,
                        colorIcono = Color(0xFF4F46E5),
                        colorFondoIcono = Color(0xFFEEF2FF),
                        titulo = "Asambleas y citaciones",
                        descripcion = "Gestiona información relacionada con las asambleas de vecinos."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ItemCaracteristica(
                        icono = Icons.Default.SmartToy,
                        colorIcono = Color(0xFFEC4899),
                        colorFondoIcono = Color(0xFFFCE7F3),
                        titulo = "ZOE, asistente inteligente",
                        descripcion = "Realiza consultas sobre la información del sistema utilizando lenguaje natural."
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    ItemCaracteristica(
                        icono = Icons.Default.LockPerson,
                        colorIcono = Color(0xFF10B981),
                        colorFondoIcono = Color(0xFFD1FAE5),
                        titulo = "Acceso seguro",
                        descripcion = "Cada usuario accede a las funciones e información que le corresponden."
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tarjeta destacada de SIGI
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🤖", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Conoce a ZOE",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoOscuro
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Nuestro asistente inteligente entiende el lenguaje natural y te ayuda a obtener información rápida.",
                                fontSize = 12.sp,
                                color = TextoGris
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Preguntas de ejemplo
                            listOf(
                                "¿Cuánto ingresó en enero?",
                                "¿Cuánto se gastó este mes?",
                                "¿Cuál es el saldo de caja?",
                                "Muéstrame los ingresos de febrero."
                            ).forEach { pregunta ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = AzulBurbujaSigi,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "💬 $pregunta",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AzulSubtitulo,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Banner de Transparencia
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VerdeBgTransparencia, RoundedCornerShape(12.dp))
                            .border(1.dp, VerdeTransparencia.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(VerdeTransparencia, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SIGEFIV promueve la transparencia",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextoOscuro
                            )
                            Text(
                                text = "La organización y la participación de todos los vecinos.",
                                fontSize = 11.sp,
                                color = TextoGris
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de acción principal
                    Button(
                        onClick = onComenzarClick,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Comenzar a usar SIGEFIV",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemCaracteristica(
    icono: ImageVector,
    colorIcono: Color,
    colorFondoIcono: Color,
    titulo: String,
    descripcion: String
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = FondoTarjeta,
        border = androidx.compose.foundation.BorderStroke(1.dp, GrisBorde),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(colorFondoIcono, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = colorIcono,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = titulo,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = descripcion,
                    fontSize = 11.5.sp,
                    color = TextoMuted,
                    lineHeight = 16.sp
                )
            }
        }
    }
}