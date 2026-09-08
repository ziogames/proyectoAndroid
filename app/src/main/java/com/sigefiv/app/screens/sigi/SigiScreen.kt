@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.sigi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.SmartToy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.viewmodel.ZoeMensaje
import com.sigefiv.app.viewmodel.ZoeViewModel

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES PROFESIONAL SIGEFIV
|--------------------------------------------------------------------------
*/

private val VerdeCorporativo = Color(0xFF15803D)
private val VerdeSuaveAvatar = Color(0xFFDCFCE7)
private val VerdeOnline = Color(0xFF22C55E)
private val VerdeBurbujaUsuario = Color(0xFFDCF8C6)
private val FondoChat = Color(0xFFF1F5F9)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val TextoSecundario = Color(0xFF64748B)
private val BordeSutil = Color(0xFFE2E8F0)
private val AzulCheck = Color(0xFF0284C7)

@Composable
fun SigiScreen(
    onInicioClick: () -> Unit = {},
    onMovimientosClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onMasClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember(context) {
        ZoeViewModel(context.applicationContext)
    }

    val mensajes by viewModel.mensajes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var consulta by remember { mutableStateOf("") }
    val listaEstado = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    fun enviarConsulta() {
        val texto = consulta.trim()
        if (texto.isEmpty() || cargando) return
        viewModel.enviarConsulta(texto)
        consulta = ""
        focusManager.clearFocus()
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listaEstado.animateScrollToItem(mensajes.size - 1)
        }
    }

    Scaffold(
        containerColor = FondoChat,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(VerdeSuaveAvatar, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.SmartToy,
                                contentDescription = "ZOE",
                                tint = VerdeCorporativo,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "ZOE Asistente",
                                color = Blanco,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(VerdeOnline, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (cargando) "Escribiendo..." else "En línea",
                                    color = Blanco.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdeCorporativo
                )
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. BARRA DE ACCIONES RÁPIDAS (CHIPS DESLIZABLES TIPO GOOGLE/TELEGRAM)
            SugerenciasChipsBar(
                onPregunta = { pregunta ->
                    if (!cargando) viewModel.enviarConsulta(pregunta)
                }
            )

            // 2. HISTORIAL DE CONVERSACIÓN
            LazyColumn(
                state = listaEstado,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(
                    count = mensajes.size,
                    key = { index ->
                        // Combinamos el índice único con el hash del mensaje para garantizar unicidad absoluta
                        val mensaje = mensajes[index]
                        "$index-${mensaje.esUsuario}-${mensaje.texto.hashCode()}"
                    }
                ) { index ->
                    BurbujaChat(mensajes[index])
                }

                if (cargando) {
                    item(key = "loading_indicator") {
                        IndicadorCargaMensaje()
                    }
                }
            }

            // 3. BARRA DE ENTRADA CON ESTILO FLOTANTE
            BarraEntradaModerna(
                consulta = consulta,
                onConsultaChange = { consulta = it },
                onEnviar = { enviarConsulta() },
                cargando = cargando
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| BURBUJA DE MENSAJE REFINADA (TIPO WHATSAPP)
|--------------------------------------------------------------------------
*/

@Composable
private fun BurbujaChat(mensaje: ZoeMensaje) {
    if (mensaje.esUsuario) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 3.dp
                ),
                colors = CardDefaults.cardColors(containerColor = VerdeBurbujaUsuario),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.widthIn(min = 50.dp, max = 290.dp)
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = mensaje.texto,
                        color = TextoPrincipal,
                        fontSize = 14.5.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1:35 PM",
                            color = TextoSecundario,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "✓✓",
                            color = AzulCheck,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(VerdeSuaveAvatar, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SmartToy,
                    contentDescription = "ZOE",
                    tint = VerdeCorporativo,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = 3.dp,
                    bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.widthIn(min = 50.dp, max = 290.dp)
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp, end = 10.dp, top = 8.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = mensaje.texto,
                        color = TextoPrincipal,
                        fontSize = 14.5.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "1:35 PM",
                        color = TextoSecundario,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| SUGERENCIAS DESLIZABLES TIPO CHIPS HORIZONTALES
|--------------------------------------------------------------------------
*/

@Composable
private fun SugerenciasChipsBar(
    onPregunta: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .background(Blanco)
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SugerenciaChip(
                icon = Icons.Outlined.CalendarMonth,
                texto = "¿Período actual?",
                onClick = { onPregunta("¿Cuál es el período actual?") }
            )
        }
        item {
            SugerenciaChip(
                icon = Icons.Outlined.Payments,
                texto = "¿Saldo disponible?",
                onClick = { onPregunta("¿Cuánto dinero hay disponible?") }
            )
        }
        item {
            SugerenciaChip(
                icon = Icons.Outlined.BarChart,
                texto = "Resumen del mes",
                onClick = { onPregunta("Mostrar resumen del mes") }
            )
        }
    }
}

@Composable
private fun SugerenciaChip(
    icon: ImageVector,
    texto: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = FondoChat,
        border = androidx.compose.foundation.BorderStroke(1.dp, BordeSutil)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VerdeCorporativo,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = texto,
                color = TextoPrincipal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| INDICADOR DE CARGA DINÁMICO
|--------------------------------------------------------------------------
*/

@Composable
private fun IndicadorCargaMensaje() {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(VerdeSuaveAvatar, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SmartToy,
                contentDescription = "ZOE",
                tint = VerdeCorporativo,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Blanco),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(13.dp),
                    color = VerdeCorporativo,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ZOE está redactando...",
                    color = TextoSecundario,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| BARRA DE ENTRADA MODERNA
|--------------------------------------------------------------------------
*/

@Composable
private fun BarraEntradaModerna(
    consulta: String,
    onConsultaChange: (String) -> Unit,
    onEnviar: () -> Unit,
    cargando: Boolean
) {
    val puedeEnviar = consulta.trim().isNotEmpty() && !cargando

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = Blanco,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = consulta,
                onValueChange = onConsultaChange,
                modifier = Modifier.weight(1f),
                enabled = !cargando,
                placeholder = {
                    Text(
                        text = "Pregúntale a ZOE...",
                        color = TextoSecundario,
                        fontSize = 14.sp
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onEnviar() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = VerdeCorporativo,
                    unfocusedBorderColor = BordeSutil,
                    focusedTextColor = TextoPrincipal,
                    unfocusedTextColor = TextoPrincipal,
                    cursorColor = VerdeCorporativo
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onEnviar,
                enabled = puedeEnviar,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (puedeEnviar) VerdeCorporativo else Color(0xFFE2E8F0),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = if (puedeEnviar) Blanco else TextoSecundario,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}