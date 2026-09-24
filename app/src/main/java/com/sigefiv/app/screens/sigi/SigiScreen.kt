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
import androidx.compose.material.icons.outlined.AttachFile
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.viewmodel.ZoeMensaje
import com.sigefiv.app.viewmodel.ZoeViewModel
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import androidx.compose.material.icons.outlined.History
import androidx.compose.ui.text.style.TextAlign

/*
|--------------------------------------------------------------------------
| PALETA DE COLORES PROFESIONAL SIGEFIV
|--------------------------------------------------------------------------
*/

private val VerdeSuaveAvatar = Color(0xFFDCFCE7)
private val VerdeOnline = Color(0xFF22C55E)
private val VerdeBurbujaUsuario = Color(0xFFDCF8C6)
private val AzulCheck = Color(0xFF0284C7)

@Composable
fun SigiScreen(
    onInicioClick: () -> Unit = {},
    onMovimientosClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onMasClick: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    onOpenHistory: () -> Unit = {}
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val temaOscuro =
        MaterialTheme.colorScheme.background.luminance() < 0.5f

    val colorBarraTitulo =
        if (temaOscuro) {
            MaterialTheme.colorScheme.surface
        } else {
            Color.White
        }

    val colorContenidoBarra =
        if (temaOscuro) {
            MaterialTheme.colorScheme.onSurface
        } else {
            Color(0xFF1F2937)
        }

    val context = LocalContext.current
    val viewModel = remember(context) {
        ZoeViewModel(context.applicationContext)
    }

    val mensajes by viewModel.mensajes.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var consulta by remember { mutableStateOf("") }
    val listaEstado = rememberLazyListState()

    fun enviarConsulta() {
        val texto = consulta.trim()
        if (texto.isEmpty() || cargando) return
        viewModel.enviarConsulta(texto)
        // Limpiamos únicamente el texto.
        // Conservamos el foco para mantener el teclado visible.
        consulta = ""
    }

    LaunchedEffect(mensajes.size) {
        if (mensajes.isNotEmpty()) {
            listaEstado.animateScrollToItem(mensajes.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                                tint = colorPrincipal,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "ZOE Asistente",
                                color = MaterialTheme.colorScheme.onSurface,
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
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
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
                            tint = colorContenidoBarra
                        )
                    }
                },
                actions = {

                    IconButton(
                        onClick = onOpenHistory
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "Historial de conversaciones",
                            tint = colorContenidoBarra
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorBarraTitulo,
                    titleContentColor = colorContenidoBarra,
                    navigationIconContentColor = colorContenidoBarra,
                    actionIconContentColor = colorContenidoBarra
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
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val temaOscuro = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val colorBurbujaUsuario =
        if (temaOscuro) Color(0xFF075E3B) else Color(0xFFDCF8C6)

    val colorTextoUsuario =
        if (temaOscuro) Color.White else Color(0xFF14532D)

    val colorHoraUsuario =
        if (temaOscuro) Color.White.copy(alpha = 0.70f)
        else Color(0xFF45634D)

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
                colors = CardDefaults.cardColors(
                    containerColor = colorBurbujaUsuario
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.widthIn(min = 50.dp, max = 290.dp)
            ) {
                Row(
                    modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 8.dp, bottom = 6.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = mensaje.texto,
                        color = colorTextoUsuario,
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
                            color = colorHoraUsuario,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "✓✓",
                            color = if (temaOscuro) Color(0xFF86EFAC) else AzulCheck,
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
            verticalAlignment = Alignment.Top
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
                    tint = colorPrincipal,
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
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ZoeRespuestaFormateada(
                        texto = mensaje.texto,
                        tipoMovimiento = mensaje.tipoMovimiento,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = "1:35 PM",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                end = 8.dp,
                                top = 3.dp
                            ),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

/*
|--------------------------------------------------------------------------
| RESPUESTA FORMATEADA DE ZOE
|--------------------------------------------------------------------------
|
| Las respuestas normales se muestran como texto.
| Las listas de ingresos/egresos se presentan visualmente
| como tarjetas compactas, sin modificar la respuesta del backend.
|
*/

@Composable
private fun ZoeRespuestaFormateada(
    texto: String,
    tipoMovimiento: String? = null,
    modifier: Modifier = Modifier
){

    /*
     * ZOE actualmente devuelve los movimientos en texto plano, por ejemplo:
     *
     * • 2025-01-31 — S/ 12.00 — reflectores — usuario — Efectivo
     *
     * Por eso no debemos depender de un título como "📈 INGRESOS".
     * Detectamos directamente las líneas que contienen:
     * fecha + S/ monto + datos separados por "—".
     */

    val lineas = texto.lines()

    val regexMovimiento = Regex(
        """^\s*(?:•|-|\d+\.)\s*(\d{2}-\d{2}-\d{4})\s*—\s*S/\s*([\d,]+(?:\.\d{1,2})?)\s*(?:—\s*(.*))?$"""
    )

    val movimientos = mutableListOf<MovimientoVisual>()

    for (linea in lineas) {

        val match = regexMovimiento.find(linea)

        if (match != null) {

            val fecha = match.groupValues[1]
            val monto = match.groupValues[2]
            val resto = match.groupValues
                .getOrNull(3)
                ?.trim()
                .orEmpty()

            val partes = resto
                .split(" — ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            /*
             * Formato actual observado:
             * fecha — monto — categoría — usuario — forma de pago
             *
             * Mostramos todos los datos sin inventar etiquetas.
             */
            val categoria = partes.getOrNull(0).orEmpty()
            val usuario = partes.getOrNull(1).orEmpty()
            val formaPago = partes.drop(2).joinToString(" — ")

            movimientos.add(
                MovimientoVisual(
                    fecha = fecha,
                    monto = "S/ $monto",
                    categoria = categoria,
                    usuario = usuario,
                    formaPago = formaPago
                )
            )
        }
    }

    /*
     * Si no hay líneas de movimientos, ZOE conserva exactamente
     * su presentación de texto normal.
     */
    if (movimientos.isEmpty()) {

        Text(
            text = texto,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.5.sp,
            lineHeight = 19.sp,
            modifier = modifier
        )

        return
    }
    val esEgreso =
        tipoMovimiento == "Egreso"

    val esIngreso =
        tipoMovimiento == "Ingreso"
    val colorTipo =
        when {
            esEgreso -> Color(0xFFDC2626)
            esIngreso -> Color(0xFF16A34A)
            else -> MaterialTheme.colorScheme.primary
        }

    val fechaReferencia = movimientos.firstOrNull()?.fecha.orEmpty()

    val tituloBase =
        when {
            esIngreso -> "📈 INGRESOS"
            esEgreso -> "📉 EGRESOS"
            else -> "📋 MOVIMIENTOS"
        }

    val titulo =
        if (fechaReferencia.length >= 10) {
            val anio = fechaReferencia.substring(6, 10)
            val mes = fechaReferencia.substring(3, 5)
            "$tituloBase — ${nombreMesZoe(mes)} $anio"
        } else {
            tituloBase
        }

    val icono =
        when {
            esIngreso -> "🟢"
            esEgreso -> "🔴"
            else -> "🔹"
        }

    val resumen = lineas.firstOrNull {
        it.startsWith("Encontré ", ignoreCase = true)
    }

    val totalTexto = lineas.lastOrNull {
        it.trim().startsWith("💰 Total:", ignoreCase = true)
    }

    val totalCalculado =
        movimientos.sumOf { movimiento ->
            movimiento.monto
                .removePrefix("S/")
                .replace(",", "")
                .trim()
                .toDoubleOrNull() ?: 0.0
        }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = titulo,
            color = colorTipo,
            fontSize = 14.5.sp,
            fontWeight = FontWeight.Bold
        )

        if (resumen != null) {

            Text(
                text = resumen,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }

        movimientos.forEach { movimiento ->

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(
                        horizontal = 11.dp,
                        vertical = 9.dp
                    )
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = "$icono ${movimiento.fecha}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = movimiento.monto,
                            color = colorTipo,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End
                        )
                    }

                    if (movimiento.categoria.isNotBlank()) {

                        Spacer(
                            modifier = Modifier.height(3.dp)
                        )

                        Text(
                            text = movimiento.categoria,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (movimiento.usuario.isNotBlank()) {

                        Text(
                            text = "👤 ${movimiento.usuario}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    }

                    if (movimiento.formaPago.isNotBlank()) {

                        Text(
                            text = "💳 ${movimiento.formaPago}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorTipo.copy(alpha = 0.10f)
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 14.dp,
                        vertical = 10.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Total",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "💰 ${if (totalTexto != null) {
                            totalTexto.substringAfter(":").trim()
                        } else {
                            "S/ %.2f".format(totalCalculado)
                        }}",
                        color = colorTipo,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${movimientos.size} movimientos",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.5.sp
                )
            }
        }
    }
}

private fun nombreMesZoe(mes: String): String {
    return when (mes) {
        "01" -> "ENERO"
        "02" -> "FEBRERO"
        "03" -> "MARZO"
        "04" -> "ABRIL"
        "05" -> "MAYO"
        "06" -> "JUNIO"
        "07" -> "JULIO"
        "08" -> "AGOSTO"
        "09" -> "SEPTIEMBRE"
        "10" -> "OCTUBRE"
        "11" -> "NOVIEMBRE"
        "12" -> "DICIEMBRE"
        else -> ""
    }
}

private data class MovimientoVisual(
    val fecha: String,
    val monto: String,
    val categoria: String,
    val usuario: String,
    val formaPago: String
)

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
            .background(MaterialTheme.colorScheme.surface)
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
                onClick = {
                    onPregunta("¿Cuál es el saldo disponible del período actual?")
                }
            )
        }
        item {
            SugerenciaChip(
                icon = Icons.Outlined.BarChart,
                texto = "Resumen del mes",
                onClick = {
                    onPregunta(
                        "RESUMEN_PERIODO_ACTIVO"
                    )
                }
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
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colorPrincipal,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = texto,
                color = MaterialTheme.colorScheme.onSurface,
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
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

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
                tint = colorPrincipal,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(13.dp),
                    color = colorPrincipal,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ZOE está redactando...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    val puedeEnviar = consulta.trim().isNotEmpty() && !cargando

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .imePadding(),
        color = MaterialTheme.colorScheme.surface,
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
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                // El campo permanece habilitado para seguir escribiendo
                // mientras ZOE procesa la consulta.
                enabled = true,
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.AttachFile,
                        contentDescription = "Adjuntar archivo",
                        tint = colorPrincipal,
                        modifier = Modifier.size(20.dp)
                    )
                },
                placeholder = {
                    Text(
                        text = "Pregúntale a ZOE...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                },
                maxLines = 1,
                shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onEnviar() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorPrincipal,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = colorPrincipal
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onEnviar,
                enabled = puedeEnviar,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = if (puedeEnviar) colorPrincipal else MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar",
                    tint = if (puedeEnviar) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}
