package com.sigefiv.app.screens.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Notificacion
import com.sigefiv.app.viewmodel.FcmPreferenciaViewModel
import com.sigefiv.app.viewmodel.NotificacionViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

private val Fondo = Color(0xFFF8FAFC)
private val Blanco = Color.White
private val VerdeSuave = Color(0xFFDCFCE7)
private val VerdePendiente = Color(0xFFF0FDF4)
private val TextoPrincipal = Color(0xFF0F172A)
private val Gris = Color(0xFF64748B)
private val GrisFecha = Color(0xFF94A3B8)
private val GrisBorde = Color(0xFFE2E8F0)
private val AzulIcono = Color(0xFF334E8C)
private val Rojo = Color(0xFFDC2626)

// ====================================================================
// ITEM DE PREFERENCIA
// ====================================================================

@Composable
private fun PreferenciaItem(
    titulo: String,
    descripcion: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                end = 10.dp,
                top = 10.dp,
                bottom = 10.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                color = if (enabled) {
                    TextoPrincipal
                } else {
                    GrisFecha
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = descripcion,
                color = if (enabled) {
                    Gris
                } else {
                    GrisFecha
                },
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
    }
}


@Composable
fun NotificacionesScreen(
    viewModel: NotificacionViewModel,
    fcmPreferenciaViewModel: FcmPreferenciaViewModel,
    onNuevaNotificacionClick: () -> Unit,
    onBackClick: () -> Unit,
    rol: String? = null,
    onNotificacionClick: (Notificacion) -> Unit,
) {

    val notificaciones by viewModel.notificaciones.collectAsState()
    val noLeidas by viewModel.noLeidas.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    // ============================================================
    // PREFERENCIAS FCM
    // ============================================================

    val notificacionesActivadas by
    fcmPreferenciaViewModel.notificacionesActivadas.collectAsState()

    val ingresos by
    fcmPreferenciaViewModel.ingresos.collectAsState()

    val egresos by
    fcmPreferenciaViewModel.egresos.collectAsState()

    val zoe by
    fcmPreferenciaViewModel.zoe.collectAsState()

    val avisos by
    fcmPreferenciaViewModel.avisos.collectAsState()

    val guardandoPreferencia by
    fcmPreferenciaViewModel.guardando.collectAsState()

    val mensajePreferencia by
    fcmPreferenciaViewModel.mensaje.collectAsState()

    // ============================================================
    // PERMISO PARA CREAR NOTIFICACIONES
    // ============================================================

    val puedeCrearNotificacion =
        !rol.equals("Consulta", ignoreCase = true)

    // ============================================================
    // CARGAR DATOS
    // ============================================================

    LaunchedEffect(Unit) {

        viewModel.cargarNotificaciones()

        fcmPreferenciaViewModel.cargarPreferencias()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding(),
        contentPadding = PaddingValues(bottom = 28.dp)
    ) {

        item {

            // ============================================================
            // ENCABEZADO
            // ============================================================

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Blanco)
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 6.dp,
                        bottom = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(44.dp)
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextoPrincipal
                    )
                }

                Spacer(
                    modifier = Modifier.width(4.dp)
                )

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = "Notificaciones",
                        color = TextoPrincipal,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = when {

                            noLeidas == 0 ->
                                "Todas las notificaciones están leídas"

                            noLeidas == 1 ->
                                "Tienes 1 notificación pendiente"

                            else ->
                                "Tienes $noLeidas notificaciones pendientes"
                        },
                        color = Gris,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            VerdeSuave,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = colorPrincipal,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            HorizontalDivider(
                color = GrisBorde,
                thickness = 1.dp
            )

            // ============================================================
            // PREFERENCIAS DE NOTIFICACIONES
            // ============================================================

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 14.dp,
                        end = 14.dp,
                        top = 12.dp,
                        bottom = 4.dp
                    ),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Blanco
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = GrisBorde
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // ====================================================
                    // INTERRUPTOR GENERAL
                    // ====================================================

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 12.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    if (notificacionesActivadas) {
                                        VerdeSuave
                                    } else {
                                        Color(0xFFF1F5F9)
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {

                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = if (notificacionesActivadas) {
                                    colorPrincipal
                                } else {
                                    Gris
                                },
                                modifier = Modifier.size(21.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(12.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Activar notificaciones",
                                color = TextoPrincipal,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(
                                modifier = Modifier.height(2.dp)
                            )

                            Text(
                                text = if (notificacionesActivadas) {
                                    "Recibir notificaciones en este dispositivo"
                                } else {
                                    "Las notificaciones están desactivadas"
                                },
                                color = Gris,
                                fontSize = 12.sp
                            )
                        }

                        Switch(
                            checked = notificacionesActivadas,
                            onCheckedChange = { activo ->
                                fcmPreferenciaViewModel.cambiarEstado(
                                    activo
                                )
                            },
                            enabled = !guardandoPreferencia
                        )
                    }

                    HorizontalDivider(
                        color = GrisBorde,
                        thickness = 1.dp
                    )

                    // ====================================================
                    // NUEVOS INGRESOS
                    // ====================================================

                    PreferenciaItem(
                        titulo = "Nuevos ingresos",
                        descripcion = "Avisarme cuando se registre un ingreso",
                        checked = ingresos,
                        enabled = notificacionesActivadas &&
                                !guardandoPreferencia,
                        onCheckedChange = {
                            fcmPreferenciaViewModel.cambiarIngresos(it)
                        }
                    )

                    HorizontalDivider(
                        color = GrisBorde,
                        thickness = 1.dp
                    )

                    // ====================================================
                    // NUEVOS EGRESOS
                    // ====================================================

                    PreferenciaItem(
                        titulo = "Nuevos egresos",
                        descripcion = "Avisarme cuando se registre un egreso",
                        checked = egresos,
                        enabled = notificacionesActivadas &&
                                !guardandoPreferencia,
                        onCheckedChange = {
                            fcmPreferenciaViewModel.cambiarEgresos(it)
                        }
                    )

                    HorizontalDivider(
                        color = GrisBorde,
                        thickness = 1.dp
                    )

                    // ====================================================
                    // ZOE
                    // ====================================================

                    PreferenciaItem(
                        titulo = "Cierre de período por Zoe",
                        descripcion = "Avisarme cuando Zoe cierre un período",
                        checked = zoe,
                        enabled = notificacionesActivadas &&
                                !guardandoPreferencia,
                        onCheckedChange = {
                            fcmPreferenciaViewModel.cambiarZoe(it)
                        }
                    )

                    HorizontalDivider(
                        color = GrisBorde,
                        thickness = 1.dp
                    )

                    // ====================================================
                    // AVISOS VECINALES
                    // ====================================================

                    PreferenciaItem(
                        titulo = "Avisos vecinales",
                        descripcion = "Recibir avisos enviados a los vecinos",
                        checked = avisos,
                        enabled = notificacionesActivadas &&
                                !guardandoPreferencia,
                        onCheckedChange = {
                            fcmPreferenciaViewModel.cambiarAvisos(it)
                        }
                    )
                }
            }
        }

        item {

            // ============================================================
            // MENSAJE DE LA PREFERENCIA FCM
            // ============================================================

            if (mensajePreferencia != null) {

                Text(
                    text = mensajePreferencia ?: "",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 18.dp,
                            end = 18.dp,
                            top = 4.dp,
                            bottom = 4.dp
                        ),
                    color = Gris,
                    fontSize = 11.sp
                )
            }

            // ============================================================
            // NUEVA NOTIFICACIÓN
            // SOLO ROLES DIFERENTES DE CONSULTA
            // ============================================================

            if (puedeCrearNotificacion) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 14.dp,
                            end = 14.dp,
                            top = 8.dp,
                            bottom = 4.dp
                        ),
                    horizontalArrangement = Arrangement.End
                ) {

                    Button(
                        onClick = onNuevaNotificacionClick,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorPrincipal
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = "Nueva notificación",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ============================================================
            // MARCAR TODAS COMO LEÍDAS
            // ============================================================

            if (noLeidas > 0) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Blanco)
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 10.dp,
                            bottom = 10.dp
                        ),
                    horizontalArrangement = Arrangement.End
                ) {

                    Button(
                        onClick = {
                            viewModel.marcarTodasComoLeidas()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdeSuave,
                            contentColor = colorPrincipal
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 14.dp,
                            vertical = 8.dp
                        )
                    ) {

                        Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(7.dp)
                        )

                        Text(
                            text = "Marcar todas como leídas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

        }

        // ============================================================
        // CONTENIDO
        // ============================================================

        when {

            cargando -> {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            CircularProgressIndicator(
                                color = colorPrincipal,
                                strokeWidth = 3.dp
                            )

                            Spacer(
                                modifier = Modifier.height(12.dp)
                            )

                            Text(
                                text = "Cargando notificaciones...",
                                color = Gris,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            mensaje != null -> {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Text(
                                text = mensaje
                                    ?: "No se pudieron cargar las notificaciones.",
                                color = Rojo,
                                fontSize = 14.sp
                            )

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Button(
                                onClick = {
                                    viewModel.limpiarMensaje()
                                    viewModel.cargarNotificaciones()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = colorPrincipal
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )

                                Spacer(
                                    modifier = Modifier.width(6.dp)
                                )

                                Text("Reintentar")
                            }
                        }
                    }
                }
            }

            notificaciones.isEmpty() -> {

                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(
                                        VerdeSuave,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = colorPrincipal,
                                    modifier = Modifier.size(36.dp)
                                )
                            }

                            Spacer(
                                modifier = Modifier.height(16.dp)
                            )

                            Text(
                                text = "No tienes notificaciones",
                                color = TextoPrincipal,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(6.dp)
                            )

                            Text(
                                text = "Aquí aparecerán los avisos de SIGEFIV.",
                                color = Gris,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            else -> {

                items(
                    items = notificaciones,
                    key = { it.id }
                ) { notificacion ->

                    NotificacionItem(
                        notificacion = notificacion,
                        onClick = {
                            if (!notificacion.leida) {
                                viewModel.marcarComoLeida(
                                    notificacion.id
                                )
                            }

                            onNotificacionClick(notificacion)
                        }
                    )
                }
            }
        }
    }
}

// ====================================================================
// TARJETA DE NOTIFICACIÓN
// ====================================================================

@Composable
private fun NotificacionItem(
    notificacion: Notificacion,
    onClick: () -> Unit
) {

    val pendiente = !notificacion.leida

    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(

                onClick = onClick
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (pendiente) {
                VerdePendiente
            } else {
                Blanco
            }
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (pendiente) {
                Color(0xFFBBF7D0)
            } else {
                GrisBorde
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {

            // ========================================================
            // ICONO
            // ========================================================

            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (pendiente) {
                            AzulIcono
                        } else {
                            Color(0xFFE2E8F0)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = if (pendiente) {
                        Blanco
                    } else {
                        Gris
                    },
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            // ========================================================
            // INFORMACIÓN
            // ========================================================

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = notificacion.titulo,
                        modifier = Modifier.weight(1f),
                        color = TextoPrincipal,
                        fontSize = 15.sp,
                        fontWeight = if (pendiente) {
                            FontWeight.Bold
                        } else {
                            FontWeight.SemiBold
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (pendiente) {

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(
                                    colorPrincipal,
                                    CircleShape
                                )
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = notificacion.mensaje,
                    color = Gris,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = formatearFecha(
                        notificacion.created_at
                    ),
                    color = GrisFecha,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ====================================================================
// FORMATEAR FECHA
// ====================================================================

private fun formatearFecha(
    fecha: String?
): String {

    if (fecha.isNullOrBlank()) {
        return ""
    }

    val formatosEntrada = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
        "yyyy-MM-dd'T'HH:mm:ss"
    )

    for (formato in formatosEntrada) {

        try {

            val entrada = SimpleDateFormat(
                formato,
                Locale.US
            )

            val salida = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            )

            val fechaParseada = entrada.parse(fecha)

            if (fechaParseada != null) {
                return salida.format(fechaParseada)
            }

        } catch (_: Exception) {
            // Intentar siguiente formato
        }
    }

    return fecha
}

