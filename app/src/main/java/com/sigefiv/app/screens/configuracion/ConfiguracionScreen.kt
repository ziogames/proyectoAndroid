package com.sigefiv.app.screens.configuracion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Configuracion
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    configuracionInicial: Configuracion = Configuracion(),
    cargando: Boolean = false,
    onGuardarConfiguracion: (Configuracion) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())
    val colors = MaterialTheme.colorScheme

    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf("General", "Directiva", "Finanzas", "Seguridad")

    // Estados mutables para la edición
    var organizacion by remember { mutableStateOf(configuracionInicial.organizacion ?: "") }
    var ruc by remember { mutableStateOf(configuracionInicial.ruc ?: "") }
    var direccion by remember { mutableStateOf(configuracionInicial.direccion ?: "") }
    var telefono by remember { mutableStateOf(configuracionInicial.telefono ?: "") }
    var correo by remember { mutableStateOf(configuracionInicial.correo ?: "") }

    var presidente by remember { mutableStateOf(configuracionInicial.presidente ?: "") }
    var tesorero by remember { mutableStateOf(configuracionInicial.tesorero ?: "") }
    var secretario by remember { mutableStateOf(configuracionInicial.secretario ?: "") }

    var simboloMoneda by remember { mutableStateOf(configuracionInicial.simbolo_moneda ?: "S/") }
    var saldoApertura by remember { mutableStateOf(configuracionInicial.saldo_apertura?.toString() ?: "0.00") }

    var bitacoraActiva by remember { mutableStateOf(configuracionInicial.bitacora_activa) }
    var sesionUnica by remember { mutableStateOf(configuracionInicial.sesion_unica) }
    var bloqueoIntentos by remember { mutableStateOf(configuracionInicial.bloqueo_intentos) }
    var intentosLogin by remember { mutableStateOf(configuracionInicial.intentos_login.toString()) }
    var tiempoSesion by remember { mutableStateOf(configuracionInicial.tiempo_sesion.toString()) }

    // Sincronización automática de estados al llegar la respuesta del servidor
    LaunchedEffect(configuracionInicial) {
        organizacion = configuracionInicial.organizacion ?: ""
        ruc = configuracionInicial.ruc ?: ""
        direccion = configuracionInicial.direccion ?: ""
        telefono = configuracionInicial.telefono ?: ""
        correo = configuracionInicial.correo ?: ""

        presidente = configuracionInicial.presidente ?: ""
        tesorero = configuracionInicial.tesorero ?: ""
        secretario = configuracionInicial.secretario ?: ""

        simboloMoneda = configuracionInicial.simbolo_moneda ?: "S/"
        saldoApertura = configuracionInicial.saldo_apertura?.toString() ?: "0.00"

        bitacoraActiva = configuracionInicial.bitacora_activa
        sesionUnica = configuracionInicial.sesion_unica
        bloqueoIntentos = configuracionInicial.bloqueo_intentos
        intentosLogin = configuracionInicial.intentos_login.toString()
        tiempoSesion = configuracionInicial.tiempo_sesion.toString()
    }

    Scaffold(
        containerColor = colors.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Configuración del Sistema",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = colors.onPrimary
                        )
                        Text(
                            text = "Ajustes globales y directiva",
                            fontSize = 12.sp,
                            color = colors.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = colors.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorPrincipal)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TABS DE NAVEGACIÓN
            TabRow(
                selectedTabIndex = tabSeleccionado,
                containerColor = colors.surface,
                contentColor = colorPrincipal
            ) {
                tabs.forEachIndexed { index, titulo ->
                    Tab(
                        selected = tabSeleccionado == index,
                        onClick = { tabSeleccionado = index },
                        text = {
                            Text(
                                text = titulo,
                                fontSize = 13.sp,
                                fontWeight = if (tabSeleccionado == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // TARJETA HERO DE CABECERA
                CardHeroConfiguracion(
                    nombreOrg = organizacion.ifBlank { "Comunidad Vecinal" },
                    subtitulo = "Parámetros de la base de datos activa",
                    colorPrincipal = colorPrincipal
                )

                when (tabSeleccionado) {
                    0 -> {
                        // GENERAL & INSTITUCIONAL
                        CardSeccionConfiguracion(
                            titulo = "DATOS INSTITUCIONALES",
                            subtitulo = "Información oficial de la residencial",
                            icono = Icons.Outlined.HomeWork,
                            colorPrincipal = colorPrincipal
                        ) {
                            OutlinedTextField(
                                value = organizacion,
                                onValueChange = { organizacion = it },
                                label = { Text("Organización / Residencial") },
                                leadingIcon = { Icon(Icons.Outlined.Apartment, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = ruc,
                                onValueChange = { ruc = it },
                                label = { Text("RUC o Registro") },
                                leadingIcon = { Icon(Icons.Outlined.Badge, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = direccion,
                                onValueChange = { direccion = it },
                                label = { Text("Dirección / Ubicación") },
                                leadingIcon = { Icon(Icons.Outlined.Place, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = telefono,
                                onValueChange = { telefono = it },
                                label = { Text("Teléfono de Contacto") },
                                leadingIcon = { Icon(Icons.Outlined.Phone, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = correo,
                                onValueChange = { correo = it },
                                label = { Text("Correo Electrónico") },
                                leadingIcon = { Icon(Icons.Outlined.Email, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }

                    1 -> {
                        // DIRECTIVA ACTUAL
                        CardSeccionConfiguracion(
                            titulo = "MIEMBROS DE LA DIRECTIVA",
                            subtitulo = "Cargos oficiales para actas y balances",
                            icono = Icons.Outlined.Groups,
                            colorPrincipal = colorPrincipal
                        ) {
                            OutlinedTextField(
                                value = presidente,
                                onValueChange = { presidente = it },
                                label = { Text("Presidente / Administrador") },
                                leadingIcon = { Icon(Icons.Outlined.Person, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = tesorero,
                                onValueChange = { tesorero = it },
                                label = { Text("Tesorero / Encargado de Cobranzas") },
                                leadingIcon = { Icon(Icons.Outlined.AccountBalanceWallet, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                            OutlinedTextField(
                                value = secretario,
                                onValueChange = { secretario = it },
                                label = { Text("Secretario de Actas") },
                                leadingIcon = { Icon(Icons.Outlined.Description, null, tint = colorPrincipal) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            )
                        }
                    }

                    2 -> {
                        // FINANZAS Y MONEDA
                        CardSeccionConfiguracion(
                            titulo = "PARÁMETROS CONTABLES",
                            subtitulo = "Símbolos y apertura del ejercicio",
                            icono = Icons.Outlined.AccountBalance,
                            colorPrincipal = colorPrincipal
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = simboloMoneda,
                                    onValueChange = { simboloMoneda = it },
                                    label = { Text("Símbolo") },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = saldoApertura,
                                    onValueChange = { saldoApertura = it },
                                    label = { Text("Saldo Apertura") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(2f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = colorPrincipal.copy(alpha = 0.08f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = colorPrincipal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (configuracionInicial.contabilidad_iniciada) {
                                            "Contabilidad iniciada para el ejercicio ${configuracionInicial.anio_inicio ?: 2026}"
                                        } else {
                                            "Contabilidad pendiente de apertura inicial"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorPrincipal
                                    )
                                }
                            }
                        }
                    }

                    3 -> {
                        // SEGURIDAD DEL SISTEMA
                        CardSeccionConfiguracion(
                            titulo = "SEGURIDAD Y AUDITORÍA",
                            subtitulo = "Reglas de inicio de sesión y registro",
                            icono = Icons.Outlined.Security,
                            colorPrincipal = colorPrincipal
                        ) {
                            FilaSwitchSimple(
                                titulo = "Bitácora Activa",
                                subtitulo = "Registra todas las acciones y logins de usuarios",
                                checked = bitacoraActiva,
                                colorAcento = colorPrincipal,
                                onCheckedChange = { bitacoraActiva = it }
                            )

                            HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))

                            FilaSwitchSimple(
                                titulo = "Sesión Única",
                                subtitulo = "Cierra sesiones anteriores al abrir en otro dispositivo",
                                checked = sesionUnica,
                                colorAcento = colorPrincipal,
                                onCheckedChange = { sesionUnica = it }
                            )

                            HorizontalDivider(color = colors.outlineVariant.copy(alpha = 0.25f))

                            FilaSwitchSimple(
                                titulo = "Bloqueo por Intentos",
                                subtitulo = "Bloquea cuenta tras fallos repetidos",
                                checked = bloqueoIntentos,
                                colorAcento = Color(0xFFEF4444),
                                onCheckedChange = { bloqueoIntentos = it }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = intentosLogin,
                                    onValueChange = { intentosLogin = it },
                                    label = { Text("Máx. Intentos") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                OutlinedTextField(
                                    value = tiempoSesion,
                                    onValueChange = { tiempoSesion = it },
                                    label = { Text("Tiempo Sesión (min)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // BOTÓN DE GUARDAR
                Button(
                    onClick = {
                        val actualizado = configuracionInicial.copy(
                            organizacion = organizacion,
                            ruc = ruc,
                            direccion = direccion,
                            telefono = telefono,
                            correo = correo,
                            presidente = presidente,
                            tesorero = tesorero,
                            secretario = secretario,
                            simbolo_moneda = simboloMoneda,
                            saldo_apertura = saldoApertura.toDoubleOrNull() ?: 0.0,
                            bitacora_activa = bitacoraActiva,
                            sesion_unica = sesionUnica,
                            bloqueo_intentos = bloqueoIntentos,
                            intentos_login = intentosLogin.toIntOrNull() ?: 5,
                            tiempo_sesion = tiempoSesion.toIntOrNull() ?: 120
                        )
                        onGuardarConfiguracion(actualizado)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !cargando,
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            color = colors.onPrimary,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar Configuración",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun CardHeroConfiguracion(
    nombreOrg: String,
    subtitulo: String,
    colorPrincipal: Color
) {
    val colors = MaterialTheme.colorScheme

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .clip(RoundedCornerShape(22.dp))
            ) {
                val cx = size.width + 15.dp.toPx()
                val cy = -5.dp.toPx()

                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(colorPrincipal.copy(alpha = 0.25f), Color.Transparent),
                        center = Offset(cx - 50.dp.toPx(), 30.dp.toPx()),
                        radius = 150.dp.toPx()
                    ),
                    radius = 150.dp.toPx(),
                    center = Offset(cx - 50.dp.toPx(), 30.dp.toPx())
                )
                drawCircle(
                    color = colorPrincipal.copy(alpha = 0.12f),
                    radius = 100.dp.toPx(),
                    center = Offset(cx, cy),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(colorPrincipal, colorPrincipal.copy(alpha = 0.75f))
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tune,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = nombreOrg,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitulo,
                        fontSize = 12.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CardSeccionConfiguracion(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    colorPrincipal: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = BorderStroke(1.dp, colors.outlineVariant.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(colorPrincipal.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = colorPrincipal,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = titulo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface
                    )
                    Text(
                        text = subtitulo,
                        fontSize = 11.5.sp,
                        color = colors.onSurfaceVariant
                    )
                }
            }
            content()
        }
    }
}

@Composable
private fun FilaSwitchSimple(
    titulo: String,
    subtitulo: String,
    checked: Boolean,
    colorAcento: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = titulo,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface
            )
            Text(
                text = subtitulo,
                fontSize = 11.5.sp,
                color = colors.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colorAcento
            )
        )
    }
}