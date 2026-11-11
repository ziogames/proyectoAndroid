@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.configuracion

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Apartment
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.HomeWork
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Configuracion

@Composable
fun ConfiguracionScreen(
    configuracionInicial: Configuracion = Configuracion(),
    cargando: Boolean = false,
    onGuardarConfiguracion: (Configuracion) -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val primaryColor = MaterialTheme.colorScheme.primary

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Configuración del Sistema",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ajustes globales y directiva",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Regresar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = primaryColor,
                indicator = { tabPositions ->
                    if (tabSeleccionado < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[tabSeleccionado]),
                            color = primaryColor,
                            height = 3.dp
                        )
                    }
                },
                divider = {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        thickness = 0.8.dp
                    )
                }
            ) {
                tabs.forEachIndexed { index, titulo ->
                    val seleccionado = tabSeleccionado == index
                    Tab(
                        selected = seleccionado,
                        onClick = { tabSeleccionado = index },
                        text = {
                            Text(
                                text = titulo,
                                fontSize = 13.sp,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium,
                                color = if (seleccionado) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // TARJETA HERO DE CABECERA
                CardHeroConfiguracion(
                    nombreOrg = organizacion.ifBlank { "Comunidad Vecinal" },
                    subtitulo = "Parámetros de la base de datos activa",
                    primaryColor = primaryColor
                )

                when (tabSeleccionado) {
                    0 -> {
                        // GENERAL & INSTITUCIONAL
                        CardSeccionConfiguracion(
                            titulo = "DATOS INSTITUCIONALES",
                            subtitulo = "Información oficial de la residencial",
                            icono = Icons.Outlined.HomeWork,
                            primaryColor = primaryColor
                        ) {
                            CampoConfiguracion(
                                valor = organizacion,
                                onValueChange = { organizacion = it },
                                etiqueta = "Organización / Residencial",
                                icono = Icons.Outlined.Apartment,
                                primaryColor = primaryColor
                            )
                            CampoConfiguracion(
                                valor = ruc,
                                onValueChange = { ruc = it },
                                etiqueta = "RUC o Registro institucional",
                                icono = Icons.Outlined.Badge,
                                primaryColor = primaryColor
                            )
                            CampoConfiguracion(
                                valor = direccion,
                                onValueChange = { direccion = it },
                                etiqueta = "Dirección / Ubicación",
                                icono = Icons.Outlined.Place,
                                primaryColor = primaryColor
                            )
                            CampoConfiguracion(
                                valor = telefono,
                                onValueChange = { telefono = it },
                                etiqueta = "Teléfono de contacto",
                                icono = Icons.Outlined.Phone,
                                primaryColor = primaryColor,
                                keyboardType = KeyboardType.Phone
                            )
                            CampoConfiguracion(
                                valor = correo,
                                onValueChange = { correo = it },
                                etiqueta = "Correo electrónico institucional",
                                icono = Icons.Outlined.Email,
                                primaryColor = primaryColor,
                                keyboardType = KeyboardType.Email
                            )
                        }
                    }

                    1 -> {
                        // DIRECTIVA ACTUAL
                        CardSeccionConfiguracion(
                            titulo = "MIEMBROS DE LA DIRECTIVA",
                            subtitulo = "Cargos oficiales para actas y balances",
                            icono = Icons.Outlined.Groups,
                            primaryColor = primaryColor
                        ) {
                            CampoConfiguracion(
                                valor = presidente,
                                onValueChange = { presidente = it },
                                etiqueta = "Presidente / Administrador",
                                icono = Icons.Outlined.Person,
                                primaryColor = primaryColor
                            )
                            CampoConfiguracion(
                                valor = tesorero,
                                onValueChange = { tesorero = it },
                                etiqueta = "Tesorero / Encargado de Cobranzas",
                                icono = Icons.Outlined.AccountBalanceWallet,
                                primaryColor = primaryColor
                            )
                            CampoConfiguracion(
                                valor = secretario,
                                onValueChange = { secretario = it },
                                etiqueta = "Secretario de Actas y Archivo",
                                icono = Icons.Outlined.Description,
                                primaryColor = primaryColor
                            )
                        }
                    }

                    2 -> {
                        // FINANZAS Y MONEDA
                        CardSeccionConfiguracion(
                            titulo = "PARÁMETROS CONTABLES",
                            subtitulo = "Símbolos y apertura del ejercicio",
                            icono = Icons.Outlined.AccountBalance,
                            primaryColor = primaryColor
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
                                    shape = RoundedCornerShape(12.dp),
                                    colors = coloresCampo(primaryColor)
                                )
                                OutlinedTextField(
                                    value = saldoApertura,
                                    onValueChange = { saldoApertura = it },
                                    label = { Text("Saldo de apertura") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    modifier = Modifier.weight(2f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = coloresCampo(primaryColor)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = primaryColor.copy(alpha = 0.08f),
                                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = if (configuracionInicial.contabilidad_iniciada) {
                                            "Contabilidad iniciada para el ejercicio ${configuracionInicial.anio_inicio ?: 2026}"
                                        } else {
                                            "Contabilidad pendiente de apertura inicial"
                                        },
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = primaryColor
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
                            primaryColor = primaryColor
                        ) {
                            FilaSwitchSimple(
                                titulo = "Bitácora activa",
                                subtitulo = "Registra todas las acciones y logins de usuarios",
                                checked = bitacoraActiva,
                                colorAcento = primaryColor,
                                onCheckedChange = { bitacoraActiva = it }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                thickness = 0.8.dp
                            )

                            FilaSwitchSimple(
                                titulo = "Sesión única",
                                subtitulo = "Cierra sesiones anteriores al abrir en otro dispositivo",
                                checked = sesionUnica,
                                colorAcento = primaryColor,
                                onCheckedChange = { sesionUnica = it }
                            )

                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                                thickness = 0.8.dp
                            )

                            FilaSwitchSimple(
                                titulo = "Bloqueo por intentos",
                                subtitulo = "Bloquea cuenta tras fallos repetidos",
                                checked = bloqueoIntentos,
                                colorAcento = MaterialTheme.colorScheme.error,
                                onCheckedChange = { bloqueoIntentos = it }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                OutlinedTextField(
                                    value = intentosLogin,
                                    onValueChange = { intentosLogin = it },
                                    label = { Text("Máx. intentos") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = coloresCampo(primaryColor)
                                )
                                OutlinedTextField(
                                    value = tiempoSesion,
                                    onValueChange = { tiempoSesion = it },
                                    label = { Text("Sesión (min)") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = coloresCampo(primaryColor)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

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
                        .height(50.dp),
                    enabled = !cargando,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    )
                ) {
                    if (cargando) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Save,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar configuración",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CardHeroConfiguracion(
    nombreOrg: String,
    subtitulo: String,
    primaryColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Tune,
                    contentDescription = null,
                    tint = primaryColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = nombreOrg,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitulo,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun CardSeccionConfiguracion(
    titulo: String,
    subtitulo: String,
    icono: ImageVector,
    primaryColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(primaryColor.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(19.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = titulo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitulo,
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                thickness = 0.8.dp
            )

            content()
        }
    }
}

@Composable
private fun CampoConfiguracion(
    valor: String,
    onValueChange: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    primaryColor: Color,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValueChange,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, null, tint = primaryColor) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = coloresCampo(primaryColor)
    )
}

@Composable
private fun FilaSwitchSimple(
    titulo: String,
    subtitulo: String,
    checked: Boolean,
    colorAcento: Color,
    onCheckedChange: (Boolean) -> Unit
) {
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
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitulo,
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colorAcento,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun coloresCampo(primaryColor: Color) = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = primaryColor,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f),
    focusedLabelColor = primaryColor,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = primaryColor
)