package com.sigefiv.app.screens.notificaciones

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Notificacion
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.FcmPreferenciaViewModel
import com.sigefiv.app.viewmodel.NotificacionViewModel
import java.text.SimpleDateFormat
import java.util.Locale


// ====================================================================
// COLORES
// ====================================================================

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
                top = 9.dp,
                bottom = 9.dp
            ),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = titulo,

                color =
                    if (enabled) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },

                fontSize = 14.sp,

                fontWeight =
                    FontWeight.Medium
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = descripcion,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant,

                fontSize = 12.sp,

                maxLines = 1,

                overflow =
                    TextOverflow.Ellipsis
            )
        }

        Switch(
            checked = checked,

            onCheckedChange =
                onCheckedChange,

            enabled = enabled
        )
    }
}


// ====================================================================
// PANTALLA DE NOTIFICACIONES
// ====================================================================

@Composable
fun NotificacionesScreen(

    viewModel: NotificacionViewModel,

    fcmPreferenciaViewModel:
    FcmPreferenciaViewModel,

    onNuevaNotificacionClick:
        () -> Unit,

    onBackClick:
        () -> Unit,

    rol: String? = null,

    onNotificacionClick:
        (Notificacion) -> Unit,

    mascotaVisible: Boolean,

    onMascotaVisibleChange:
        (Boolean) -> Unit,

    ) {

    // =================================================================
    // NOTIFICACIONES
    // =================================================================

    val notificaciones by
    viewModel.notificaciones.collectAsState()

    val noLeidas by
    viewModel.noLeidas.collectAsState()

    val cargando by
    viewModel.cargando.collectAsState()

    val mensaje by
    viewModel.mensaje.collectAsState()


    // =================================================================
    // COLOR PRINCIPAL
    // =================================================================

    val colorPrincipal =
        SeasonalColors.primary(
            SeasonalTheme.getSeason()
        )


    // =================================================================
    // PREFERENCIAS FCM
    // =================================================================

    val notificacionesActivadas by
    fcmPreferenciaViewModel
        .notificacionesActivadas
        .collectAsState()

    val ingresos by
    fcmPreferenciaViewModel
        .ingresos
        .collectAsState()

    val egresos by
    fcmPreferenciaViewModel
        .egresos
        .collectAsState()

    val zoe by
    fcmPreferenciaViewModel
        .zoe
        .collectAsState()

    val avisos by
    fcmPreferenciaViewModel
        .avisos
        .collectAsState()

    val guardandoPreferencia by
    fcmPreferenciaViewModel
        .guardando
        .collectAsState()

    val mensajePreferencia by
    fcmPreferenciaViewModel
        .mensaje
        .collectAsState()


    // =================================================================
    // ESTADO DEL PANEL DE PREFERENCIAS
    // =================================================================

    var preferenciasExpandida by
    remember {
        mutableStateOf(false)
    }


    // =================================================================
    // CONTADOR DE OPCIONES ACTIVAS
    // =================================================================

    val opcionesActivas = listOf(
        ingresos,
        egresos,
        zoe,
        avisos,
        mascotaVisible
    ).count { it }


    // =================================================================
    // PERMISO PARA CREAR NOTIFICACIONES
    // =================================================================

    val puedeCrearNotificacion =
        !rol.equals(
            "Consulta",
            ignoreCase = true
        )


    // =================================================================
    // CARGAR DATOS
    // =================================================================

    LaunchedEffect(Unit) {

        viewModel.cargarNotificaciones()

        fcmPreferenciaViewModel
            .cargarPreferencias()
    }


    // =================================================================
    // CONTENIDO
    // =================================================================

    LazyColumn(

        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme
                    .colorScheme
                    .background
            )
            .statusBarsPadding(),

        contentPadding =
            PaddingValues(
                bottom = 28.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(0.dp)
    ) {


        // =============================================================
        // ENCABEZADO
        // =============================================================

        item {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme
                            .colorScheme
                            .surface
                    )
                    .padding(
                        start = 8.dp,
                        end = 16.dp,
                        top = 6.dp,
                        bottom = 10.dp
                    ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                IconButton(

                    onClick =
                        onBackClick,

                    modifier =
                        Modifier.size(44.dp)
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Volver",

                        tint =
                            MaterialTheme
                                .colorScheme
                                .onSurface
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )


                Column(

                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(

                        text =
                            "Notificaciones",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        fontSize = 21.sp,

                        fontWeight =
                            FontWeight.Bold
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

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,

                        fontSize = 12.sp
                    )
                }


                // =====================================================
                // ICONO SUPERIOR
                // =====================================================

                Box(

                    modifier =
                        Modifier
                            .size(42.dp)
                            .background(

                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer,

                                CircleShape
                            ),

                    contentAlignment =
                        Alignment.Center
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.Notifications,

                        contentDescription =
                            null,

                        tint =
                            colorPrincipal,

                        modifier =
                            Modifier.size(21.dp)
                    )
                }
            }


            HorizontalDivider(

                color =
                    MaterialTheme
                        .colorScheme
                        .outlineVariant,

                thickness = 1.dp
            )
        }


        // =============================================================
        // PANEL DE PREFERENCIAS
        // =============================================================

        item {

            Card(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 14.dp,
                            end = 14.dp,
                            top = 12.dp,
                            bottom = 6.dp
                        ),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surface
                    ),

                border =
                    BorderStroke(

                        width = 1.dp,

                        color =
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
            ) {

                Column(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {


                    // =================================================
                    // INTERRUPTOR GENERAL
                    // =================================================

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 14.dp,
                                    vertical = 11.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Box(

                            modifier =
                                Modifier
                                    .size(42.dp)
                                    .background(

                                        if (
                                            notificacionesActivadas
                                        ) {

                                            MaterialTheme
                                                .colorScheme
                                                .secondaryContainer

                                        } else {

                                            MaterialTheme
                                                .colorScheme
                                                .surfaceVariant
                                        },

                                        CircleShape
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Notifications,

                                contentDescription =
                                    null,

                                tint =
                                    if (
                                        notificacionesActivadas
                                    ) {

                                        colorPrincipal

                                    } else {

                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                    },

                                modifier =
                                    Modifier.size(21.dp)
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )


                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "Activar notificaciones",

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface,

                                fontSize = 15.sp,

                                fontWeight =
                                    FontWeight.SemiBold
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )


                            Text(

                                text =
                                    if (
                                        notificacionesActivadas
                                    ) {

                                        "Recibir notificaciones en este dispositivo"

                                    } else {

                                        "Las notificaciones están desactivadas"
                                    },

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,

                                fontSize = 11.sp,

                                maxLines = 1,

                                overflow =
                                    TextOverflow.Ellipsis
                            )
                        }


                        Switch(

                            checked =
                                notificacionesActivadas,

                            onCheckedChange = {
                                    activo ->

                                fcmPreferenciaViewModel
                                    .cambiarEstado(
                                        activo
                                    )
                            },

                            enabled =
                                !guardandoPreferencia
                        )
                    }


                    HorizontalDivider(
                        color =
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                    )


                    // =================================================
                    // CABECERA DE PREFERENCIAS
                    // =================================================

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable {

                                    preferenciasExpandida =
                                        !preferenciasExpandida
                                }
                                .padding(
                                    horizontal = 16.dp,
                                    vertical = 11.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {


                        Column(

                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(

                                text =
                                    "Preferencias de notificaciones",

                                fontSize = 14.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )


                            Text(

                                text =
                                    "$opcionesActivas de 5 opciones activas",

                                fontSize = 11.sp,

                                color =
                                    if (
                                        notificacionesActivadas
                                    ) {

                                        colorPrincipal

                                    } else {

                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                    }
                            )
                        }


                        Box(

                            modifier =
                                Modifier
                                    .size(34.dp)
                                    .background(

                                        MaterialTheme
                                            .colorScheme
                                            .surfaceVariant,

                                        CircleShape
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(

                                imageVector =
                                    if (
                                        preferenciasExpandida
                                    ) {

                                        Icons.Default.ExpandLess

                                    } else {

                                        Icons.Default.ExpandMore
                                    },

                                contentDescription =
                                    if (
                                        preferenciasExpandida
                                    ) {

                                        "Ocultar preferencias"

                                    } else {

                                        "Mostrar preferencias"
                                    },

                                tint =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,

                                modifier =
                                    Modifier.size(20.dp)
                            )
                        }
                    }


                    // =================================================
                    // OPCIONES EXPANDIDAS
                    // =================================================

                    if (preferenciasExpandida) {


                        HorizontalDivider(

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .outlineVariant
                        )


                        // =================================================
                        // INGRESOS
                        // =================================================

                        PreferenciaItem(

                            titulo =
                                "Nuevos ingresos",

                            descripcion =
                                "Avisarme cuando se registre un ingreso",

                            checked =
                                ingresos,

                            enabled =
                                notificacionesActivadas &&
                                        !guardandoPreferencia,

                            onCheckedChange = {

                                fcmPreferenciaViewModel
                                    .cambiarIngresos(
                                        it
                                    )
                            }
                        )


                        HorizontalDivider(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .outlineVariant
                        )


                        // =================================================
                        // EGRESOS
                        // =================================================

                        PreferenciaItem(

                            titulo =
                                "Nuevos egresos",

                            descripcion =
                                "Avisarme cuando se registre un egreso",

                            checked =
                                egresos,

                            enabled =
                                notificacionesActivadas &&
                                        !guardandoPreferencia,

                            onCheckedChange = {

                                fcmPreferenciaViewModel
                                    .cambiarEgresos(
                                        it
                                    )
                            }
                        )


                        HorizontalDivider(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .outlineVariant
                        )


                        // =================================================
                        // ZOE
                        // =================================================

                        PreferenciaItem(

                            titulo =
                                "Cierre de período por Zoe",

                            descripcion =
                                "Avisarme cuando Zoe cierre un período",

                            checked =
                                zoe,

                            enabled =
                                notificacionesActivadas &&
                                        !guardandoPreferencia,

                            onCheckedChange = {

                                fcmPreferenciaViewModel
                                    .cambiarZoe(
                                        it
                                    )
                            }
                        )


                        HorizontalDivider(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .outlineVariant
                        )


                        // =================================================
                        // AVISOS VECINALES
                        // =================================================

                        PreferenciaItem(

                            titulo =
                                "Avisos vecinales",

                            descripcion =
                                "Recibir avisos enviados a los vecinos",

                            checked =
                                avisos,

                            enabled =
                                notificacionesActivadas &&
                                        !guardandoPreferencia,

                            onCheckedChange = {

                                fcmPreferenciaViewModel
                                    .cambiarAvisos(
                                        it
                                    )
                            }
                        )


                        HorizontalDivider(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .outlineVariant
                        )


                        // =================================================
                        // MASCOTA ZOE
                        // =================================================

                        PreferenciaItem(

                            titulo =
                                "Mascota ZOE",

                            descripcion =
                                "Mostrar la mascota flotante en la aplicación",

                            checked =
                                mascotaVisible,

                            enabled =
                                !guardandoPreferencia,

                            onCheckedChange = {

                                onMascotaVisibleChange(
                                    it
                                )
                            }
                        )
                    }
                }
            }
        }


        // =============================================================
        // MENSAJE DE PREFERENCIAS
        // =============================================================

        item {

            if (
                mensajePreferencia != null
            ) {

                Text(

                    text =
                        mensajePreferencia ?: "",

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 18.dp,
                                vertical = 4.dp
                            ),

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    fontSize = 11.sp
                )
            }
        }


        // =============================================================
        // ACCIONES
        // =============================================================

        item {

            if (
                puedeCrearNotificacion ||
                noLeidas > 0
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            ),

                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    // =================================================
                    // NUEVA NOTIFICACIÓN
                    // =================================================

                    if (
                        puedeCrearNotificacion
                    ) {

                        Button(

                            onClick =
                                onNuevaNotificacionClick,

                            modifier =
                                Modifier.weight(1f),

                            shape =
                                RoundedCornerShape(13.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        colorPrincipal
                                ),

                            contentPadding =
                                PaddingValues(
                                    horizontal = 10.dp,
                                    vertical = 9.dp
                                )
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.Notifications,

                                contentDescription =
                                    null,

                                modifier =
                                    Modifier.size(17.dp)
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )


                            Text(

                                text =
                                    "Nueva notificación",

                                fontSize = 12.sp,

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
                    }


                    // =================================================
                    // MARCAR TODAS
                    // =================================================

                    if (
                        noLeidas > 0
                    ) {

                        Button(

                            onClick = {

                                viewModel
                                    .marcarTodasComoLeidas()
                            },

                            modifier =
                                Modifier.weight(1f),

                            shape =
                                RoundedCornerShape(13.dp),

                            colors =
                                ButtonDefaults.buttonColors(

                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .secondaryContainer,

                                    contentColor =
                                        colorPrincipal
                                ),

                            contentPadding =
                                PaddingValues(
                                    horizontal = 8.dp,
                                    vertical = 9.dp
                                )
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default.DoneAll,

                                contentDescription =
                                    null,

                                modifier =
                                    Modifier.size(17.dp)
                            )


                            Spacer(
                                modifier =
                                    Modifier.width(6.dp)
                            )


                            Text(

                                text =
                                    "Marcar todas",

                                fontSize = 12.sp,

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }


        // =============================================================
        // TÍTULO NOTIFICACIONES
        // =============================================================

        item {

            if (
                notificaciones.isNotEmpty() &&
                mensaje == null &&
                !cargando
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                start = 18.dp,
                                end = 18.dp,
                                top = 6.dp,
                                bottom = 4.dp
                            ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Text(

                        text =
                            "Notificaciones recientes",

                        fontSize = 17.sp,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        modifier =
                            Modifier.weight(1f)
                    )


                    if (
                        noLeidas > 0
                    ) {

                        Box(

                            modifier =
                                Modifier
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .secondaryContainer,

                                        RoundedCornerShape(
                                            20.dp
                                        )
                                    )
                                    .padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    )
                        ) {

                            Text(

                                text =
                                    "$noLeidas pendientes",

                                fontSize = 10.sp,

                                fontWeight =
                                    FontWeight.SemiBold,

                                color =
                                    colorPrincipal
                            )
                        }
                    }
                }
            }
        }


        // =============================================================
        // CONTENIDO
        // =============================================================

        when {


            // =========================================================
            // CARGANDO
            // =========================================================

            cargando -> {

                item {

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(260.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            CircularProgressIndicator(

                                color =
                                    colorPrincipal,

                                strokeWidth = 3.dp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(12.dp)
                            )


                            Text(

                                text =
                                    "Cargando notificaciones...",

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,

                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }


            // =========================================================
            // ERROR
            // =========================================================

            mensaje != null -> {

                item {

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .padding(24.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Text(

                                text =
                                    mensaje
                                        ?: "No se pudieron cargar las notificaciones.",

                                color =
                                    Rojo,

                                fontSize = 14.sp
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(16.dp)
                            )


                            Button(

                                onClick = {

                                    viewModel
                                        .limpiarMensaje()

                                    viewModel
                                        .cargarNotificaciones()
                                },

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            colorPrincipal
                                    ),

                                shape =
                                    RoundedCornerShape(10.dp)
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.Refresh,

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier.size(18.dp)
                                )


                                Spacer(
                                    modifier =
                                        Modifier.width(6.dp)
                                )


                                Text(
                                    "Reintentar"
                                )
                            }
                        }
                    }
                }
            }


            // =========================================================
            // SIN NOTIFICACIONES
            // =========================================================

            notificaciones.isEmpty() -> {

                item {

                    Box(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(300.dp),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Column(

                            horizontalAlignment =
                                Alignment.CenterHorizontally
                        ) {

                            Box(

                                modifier =
                                    Modifier
                                        .size(72.dp)
                                        .background(
                                            MaterialTheme
                                                .colorScheme
                                                .secondaryContainer,

                                            CircleShape
                                        ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.Notifications,

                                    contentDescription =
                                        null,

                                    tint =
                                        colorPrincipal,

                                    modifier =
                                        Modifier.size(36.dp)
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.height(16.dp)
                            )


                            Text(

                                text =
                                    "No tienes notificaciones",

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface,

                                fontSize = 17.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(6.dp)
                            )


                            Text(

                                text =
                                    "Aquí aparecerán los avisos de SIGEFIV.",

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,

                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }


            // =========================================================
            // NOTIFICACIONES
            // =========================================================

            else -> {

                items(

                    items =
                        notificaciones,

                    key = {
                        it.id
                    }

                ) { notificacion ->


                    NotificacionItem(

                        notificacion =
                            notificacion,

                        onClick = {

                            if (
                                !notificacion.leida
                            ) {

                                viewModel
                                    .marcarComoLeida(
                                        notificacion.id
                                    )
                            }


                            onNotificacionClick(
                                notificacion
                            )
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

    notificacion:
    Notificacion,

    onClick:
        () -> Unit
) {


    val pendiente =
        !notificacion.leida


    val colorPrincipal =
        SeasonalColors.primary(
            SeasonalTheme.getSeason()
        )


    Card(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                )
                .clickable(
                    onClick = onClick
                ),

        shape =
            RoundedCornerShape(16.dp),

        colors =
            CardDefaults.cardColors(

                containerColor =
                    if (pendiente) {

                        MaterialTheme
                            .colorScheme
                            .surfaceVariant

                    } else {

                        MaterialTheme
                            .colorScheme
                            .surface
                    }
            ),

        border =
            BorderStroke(

                width = 1.dp,

                color =
                    if (pendiente) {

                        Color(0xFFBBF7D0)

                    } else {

                        MaterialTheme
                            .colorScheme
                            .outlineVariant
                    }
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {


        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(14.dp),

            verticalAlignment =
                Alignment.Top
        ) {


            // =========================================================
            // ICONO
            // =========================================================

            Box(

                modifier =
                    Modifier
                        .size(44.dp)
                        .background(

                            color =
                                if (pendiente) {

                                    AzulIcono

                                } else {

                                    MaterialTheme
                                        .colorScheme
                                        .surfaceVariant
                                },

                            shape =
                                CircleShape
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        Icons.Default.Notifications,

                    contentDescription =
                        null,

                    tint =
                        if (pendiente) {

                            MaterialTheme
                                .colorScheme
                                .surface

                        } else {

                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                        },

                    modifier =
                        Modifier.size(22.dp)
                )
            }


            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )


            // =========================================================
            // INFORMACIÓN
            // =========================================================

            Column(

                modifier =
                    Modifier.weight(1f)
            ) {


                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {


                    Text(

                        text =
                            notificacion.titulo,

                        modifier =
                            Modifier.weight(1f),

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        fontSize = 15.sp,

                        fontWeight =
                            if (pendiente) {

                                FontWeight.Bold

                            } else {

                                FontWeight.SemiBold
                            },

                        maxLines = 2,

                        overflow =
                            TextOverflow.Ellipsis
                    )


                    if (
                        pendiente
                    ) {

                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )


                        Box(

                            modifier =
                                Modifier
                                    .size(8.dp)
                                    .background(
                                        colorPrincipal,
                                        CircleShape
                                    )
                        )
                    }
                }


                Spacer(
                    modifier =
                        Modifier.height(5.dp)
                )


                Text(

                    text =
                        notificacion.mensaje,

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    fontSize = 13.sp,

                    lineHeight = 18.sp
                )


                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )


                Text(

                    text =
                        formatearFecha(
                            notificacion.created_at
                        ),

                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant,

                    fontSize = 12.sp
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

    if (
        fecha.isNullOrBlank()
    ) {

        return ""
    }


    val formatosEntrada =
        listOf(

            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",

            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",

            "yyyy-MM-dd'T'HH:mm:ss'Z'",

            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",

            "yyyy-MM-dd'T'HH:mm:ss"
        )


    for (
    formato in formatosEntrada
    ) {

        try {

            val entrada =
                SimpleDateFormat(
                    formato,
                    Locale.US
                )


            val salida =
                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                )


            val fechaParseada =
                entrada.parse(
                    fecha
                )


            if (
                fechaParseada != null
            ) {

                return salida.format(
                    fechaParseada
                )
            }

        } catch (
            _: Exception
        ) {

            // Intentar siguiente formato
        }
    }


    return fecha
}