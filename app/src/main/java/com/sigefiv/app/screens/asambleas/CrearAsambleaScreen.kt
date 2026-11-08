@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.asambleas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Style
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Asamblea
import com.sigefiv.app.data.model.CrearAsambleaRequest
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val FondoSuperficie = Color(0xFFF8FAFC)
private val Blanco = Color.White
private val GrisBorde = Color(0xFFE2E8F0)
private val TextoOscuro = Color(0xFF0F172A)
private val TextoSecundario = Color(0xFF64748B)

@Composable
fun CrearAsambleaScreen(
    creando: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    onGuardar: (CrearAsambleaRequest) -> Unit,
    asamblea: Asamblea? = null
) {
    val colorPrincipal = SeasonalColors.primary(SeasonalTheme.getSeason())
    val calendario = remember { Calendar.getInstance() }
    val snackbarHostState = remember { SnackbarHostState() }
    val modoEdicion = asamblea != null

    var tipo by remember { mutableStateOf(asamblea?.tipo?.takeIf { it.isNotBlank() } ?: "Ordinaria") }
    var titulo by remember { mutableStateOf(asamblea?.titulo ?: "") }
    var convoca by remember { mutableStateOf(asamblea?.convoca ?: "") }
    var sector by remember { mutableStateOf(asamblea?.sector ?: "") }
    var grupo by remember { mutableStateOf(asamblea?.grupo ?: "") }
    var manzana by remember { mutableStateOf(asamblea?.manzana ?: "") }
    var lote by remember { mutableStateOf(asamblea?.lote ?: "") }

    var fecha by remember {
        mutableStateOf(
            asamblea?.fecha?.substringBefore("T")?.takeIf { it.isNotBlank() }
                ?: SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendario.time)
        )
    }

    var hora by remember { mutableStateOf(asamblea?.hora?.substringBefore(".")?.substringBefore("Z") ?: "17:00") }
    var primeraCitacion by remember { mutableStateOf(asamblea?.primera_citacion?.substringBefore(".")?.substringBefore("Z") ?: "17:00") }
    var segundaCitacion by remember { mutableStateOf(asamblea?.segunda_citacion?.substringBefore(".")?.substringBefore("Z") ?: "17:30") }

    var lugar by remember { mutableStateOf(asamblea?.lugar ?: "") }
    var descripcion by remember { mutableStateOf(asamblea?.descripcion ?: "") }
    var importancia by remember { mutableStateOf(asamblea?.importancia?.takeIf { it.isNotBlank() } ?: "normal") }
    var plantilla by remember { mutableStateOf(asamblea?.plantilla_citacion ?: 1) }

    val agenda = remember {
        mutableStateListOf<String>().apply {
            if (asamblea != null && asamblea.agendas.isNotEmpty()) {
                addAll(
                    asamblea.agendas
                        .sortedBy { it.numero }
                        .mapNotNull { it.descripcion?.trim() }
                        .filter { it.isNotBlank() }
                )
            }
            if (isEmpty()) add("")
        }
    }

    var mostrarTipoMenu by remember { mutableStateOf(false) }
    var mostrarPlantillaMenu by remember { mutableStateOf(false) }
    var mostrarErrorValidacion by remember { mutableStateOf<String?>(null) }

    val errorVisible = error ?: mostrarErrorValidacion

    LaunchedEffect(errorVisible) {
        errorVisible?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        containerColor = FondoSuperficie,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (modoEdicion) "Editar asamblea" else "Nueva asamblea",
                            color = Blanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (modoEdicion) "Modificar convocatoria" else "Convocatoria vecinal",
                            color = Blanco.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !creando) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver", tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorPrincipal)
            )
        },
        bottomBar = {
            // Barra inferior fija con sombra y botones siempre al alcance
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Blanco,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, GrisBorde)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        enabled = !creando,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, GrisBorde)
                    ) {
                        Text("Cancelar", color = TextoSecundario, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            mostrarErrorValidacion = null
                            val agendaFinal = agenda.map { it.trim() }.filter { it.isNotBlank() }

                            val mensaje = validarFormulario(
                                titulo = titulo,
                                convoca = convoca,
                                fecha = fecha,
                                primeraCitacion = primeraCitacion,
                                segundaCitacion = segundaCitacion,
                                lugar = lugar
                            )

                            if (mensaje != null) {
                                mostrarErrorValidacion = mensaje
                                return@Button
                            }

                            val request = CrearAsambleaRequest(
                                tipo = tipo,
                                titulo = titulo.trim(),
                                convoca = convoca.trim(),
                                sector = sector.trim().ifBlank { null },
                                grupo = grupo.trim().ifBlank { null },
                                manzana = manzana.trim().ifBlank { null },
                                lote = lote.trim().ifBlank { null },
                                fecha = fecha,
                                hora = hora.trim().ifBlank { null },
                                primera_citacion = primeraCitacion,
                                segunda_citacion = segundaCitacion.trim().ifBlank { null },
                                lugar = lugar.trim(),
                                descripcion = descripcion.trim().ifBlank { null },
                                importancia = importancia,
                                agenda = agendaFinal,
                                plantilla_citacion = plantilla
                            )
                            onGuardar(request)
                        },
                        enabled = !creando,
                        modifier = Modifier
                            .weight(1.3f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                    ) {
                        if (creando) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Blanco, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(if (modoEdicion) "Guardar cambios" else "Guardar borrador", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // SECCIÓN 1: DATOS CLAVE
            item {
                TarjetaContenedor(titulo = "Información de convocatoria", icono = Icons.Outlined.NotificationsActive, colorPrincipal = colorPrincipal) {
                    CampoDesplegable(
                        label = "Tipo de asamblea",
                        valor = tipo,
                        opciones = listOf("Ordinaria", "Extraordinaria"),
                        expandido = mostrarTipoMenu,
                        onExpandChange = { mostrarTipoMenu = it },
                        onSeleccionar = { tipo = it; mostrarTipoMenu = false },
                        colorPrincipal = colorPrincipal
                    )

                    Spacer(Modifier.height(10.dp))

                    CampoTexto(
                        label = "Título de la asamblea",
                        valor = titulo,
                        onValorChange = { titulo = it },
                        obligatorio = true,
                        placeholder = "Ej. Aprobación del presupuesto comunal",
                        colorPrincipal = colorPrincipal
                    )

                    Spacer(Modifier.height(10.dp))

                    CampoTexto(
                        label = "Convoca",
                        valor = convoca,
                        onValorChange = { convoca = it },
                        obligatorio = true,
                        placeholder = "Ej. Junta Directiva Central",
                        colorPrincipal = colorPrincipal
                    )

                    Spacer(Modifier.height(12.dp))

                    Text("Nivel de importancia", fontSize = 12.sp, color = TextoSecundario, fontWeight = FontWeight.Medium)
                    Spacer(Modifier.height(6.dp))

                    // Selector interactivo con chips de importancia
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("normal" to "Normal", "importante" to "Importante", "urgente" to "Urgente").forEach { (clave, nombre) ->
                            val seleccionado = importancia == clave
                            val colorChip = when (clave) {
                                "urgente" -> Color(0xFFEF4444)
                                "importante" -> Color(0xFFF59E0B)
                                else -> colorPrincipal
                            }

                            FilterChip(
                                selected = seleccionado,
                                onClick = { importancia = clave },
                                label = { Text(nombre, fontSize = 12.sp, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal) },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorChip.copy(alpha = 0.15f),
                                    selectedLabelColor = colorChip
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = if (seleccionado) colorChip else GrisBorde,
                                    selectedBorderColor = colorChip,
                                    borderWidth = if (seleccionado) 1.5.dp else 1.dp,
                                    enabled = true,
                                    selected = seleccionado
                                )
                            )
                        }
                    }
                }
            }

            // SECCIÓN 2: FECHA Y CITACIONES COMPACTAS
            item {
                TarjetaContenedor(titulo = "Fecha y horarios", icono = Icons.Outlined.CalendarToday, colorPrincipal = colorPrincipal) {
                    CampoFecha(fecha = fecha, onSeleccionar = { fecha = it }, colorPrincipal = colorPrincipal)

                    Spacer(Modifier.height(12.dp))

                    // Horas en fila compacta para no saturar verticalmente
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampoHoraCompacta(
                            label = "1ra Citación *",
                            valor = primeraCitacion,
                            onSeleccionar = { primeraCitacion = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = colorPrincipal
                        )
                        CampoHoraCompacta(
                            label = "2da Citación",
                            valor = segundaCitacion,
                            onSeleccionar = { segundaCitacion = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = colorPrincipal
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    CampoHoraCompacta(
                        label = "Hora oficial de asamblea",
                        valor = hora,
                        onSeleccionar = { hora = it },
                        modifier = Modifier.fillMaxWidth(),
                        colorPrincipal = colorPrincipal
                    )
                }
            }

            // SECCIÓN 3: UBICACIÓN Y LUGAR
            item {
                TarjetaContenedor(titulo = "Lugar y coordenadas vecinales", icono = Icons.Outlined.Place, colorPrincipal = colorPrincipal) {
                    CampoTexto(
                        label = "Lugar de encuentro",
                        valor = lugar,
                        onValorChange = { lugar = it },
                        obligatorio = true,
                        placeholder = "Ej. Local Comunal / Cancha deportiva",
                        leadingIcon = { Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = colorPrincipal) },
                        colorPrincipal = colorPrincipal
                    )

                    Spacer(Modifier.height(12.dp))

                    // Grid compacto de coordenadas vecinales (2x2 limpio)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CampoTexto(label = "Sector", valor = sector, onValorChange = { sector = it }, modifier = Modifier.weight(1f), colorPrincipal = colorPrincipal)
                        CampoTexto(label = "Grupo", valor = grupo, onValorChange = { grupo = it }, modifier = Modifier.weight(1f), colorPrincipal = colorPrincipal)
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CampoTexto(label = "Manzana", valor = manzana, onValorChange = { manzana = it }, modifier = Modifier.weight(1f), colorPrincipal = colorPrincipal)
                        CampoTexto(label = "Lote", valor = lote, onValorChange = { lote = it }, modifier = Modifier.weight(1f), colorPrincipal = colorPrincipal)
                    }
                }
            }

            // SECCIÓN 4: AGENDA DE PUNTOS
            item {
                TarjetaContenedor(titulo = "Puntos de agenda", icono = Icons.Outlined.FormatListNumbered, colorPrincipal = colorPrincipal) {
                    Text("Detalla los temas que se discutirán con los vecinos.", fontSize = 12.sp, color = TextoSecundario)
                    Spacer(Modifier.height(10.dp))

                    agenda.forEachIndexed { index, punto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(colorPrincipal.copy(alpha = 0.12f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = colorPrincipal,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            OutlinedTextField(
                                value = punto,
                                onValueChange = { agenda[index] = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("Tema o moción ${index + 1}...") },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorPrincipal,
                                    unfocusedBorderColor = GrisBorde
                                )
                            )

                            if (agenda.size > 1) {
                                IconButton(onClick = { agenda.removeAt(index) }, modifier = Modifier.size(36.dp)) {
                                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "Eliminar", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { agenda.add("") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Añadir otro punto", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // SECCIÓN 5: DESCRIPCIÓN Y PLANTILLA
            item {
                TarjetaContenedor(titulo = "Detalle y diseño", icono = Icons.Outlined.Style, colorPrincipal = colorPrincipal) {
                    CampoTexto(
                        label = "Descripción o notas adicionales",
                        valor = descripcion,
                        onValorChange = { descripcion = it },
                        singleLine = false,
                        minLines = 3,
                        placeholder = "Agrega instrucciones sobre puntualidad, material necesario, etc.",
                        colorPrincipal = colorPrincipal
                    )

                    Spacer(Modifier.height(12.dp))

                    CampoPlantilla(
                        plantilla = plantilla,
                        expandido = mostrarPlantillaMenu,
                        onExpandChange = { mostrarPlantillaMenu = it },
                        onSeleccionar = { plantilla = it; mostrarPlantillaMenu = false },
                        colorPrincipal = colorPrincipal
                    )
                }
            }

            // Espaciador final para que el último elemento no quede oculto detrás de la barra fija
            item { Spacer(Modifier.height(20.dp)) }
        }
    }
}

/*
|--------------------------------------------------------------------------
| COMPONENTES MODULARES Y ESTILIZADOS
|--------------------------------------------------------------------------
*/

@Composable
private fun TarjetaContenedor(
    titulo: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorPrincipal: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, GrisBorde)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(colorPrincipal.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icono, contentDescription = null, tint = colorPrincipal, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(text = titulo, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextoOscuro)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun CampoTexto(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    colorPrincipal: Color,
    modifier: Modifier = Modifier,
    obligatorio: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    placeholder: String? = null,
    leadingIcon: (@Composable (() -> Unit))? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.fillMaxWidth(),
        label = { Text(if (obligatorio) "$label *" else label) },
        placeholder = placeholder?.let { { Text(it, color = Color.Gray, fontSize = 13.sp) } },
        singleLine = singleLine,
        minLines = minLines,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = colorPrincipal,
            focusedLabelColor = colorPrincipal,
            unfocusedBorderColor = GrisBorde
        )
    )
}

@Composable
private fun CampoFecha(
    fecha: String,
    onSeleccionar: (String) -> Unit,
    colorPrincipal: Color
) {
    val context = LocalContext.current
    val calendario = remember { Calendar.getInstance() }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val cal = Calendar.getInstance().apply { set(y, m, d) }
                        onSeleccionar(formato.format(cal.time))
                    },
                    calendario.get(Calendar.YEAR),
                    calendario.get(Calendar.MONTH),
                    calendario.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GrisBorde),
        color = Blanco
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = colorPrincipal, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Fecha programada *", fontSize = 11.sp, color = TextoSecundario)
                Text(formatearFechaVisible(fecha), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextoOscuro)
            }
        }
    }
}

@Composable
private fun CampoHoraCompacta(
    label: String,
    valor: String,
    onSeleccionar: (String) -> Unit,
    colorPrincipal: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Tomar la hora guardada o iniciar por defecto a las 5:00 PM (17:00)
    val partes = valor.split(":")
    val horaInicial = partes.getOrNull(0)?.toIntOrNull() ?: 17 // 👈 17 = 5:00 PM
    val minutoInicial = partes.getOrNull(1)?.toIntOrNull() ?: 0

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                TimePickerDialog(
                    context,
                    { _, h, m ->
                        // Guarda internamente en 24h ("17:00") para que PostgreSQL no falle
                        onSeleccionar(String.format(Locale.getDefault(), "%02d:%02d", h, m))
                    },
                    horaInicial,
                    minutoInicial,
                    false // 👈 'false' activa el reloj de 12 horas con botones AM / PM
                ).show()
            },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GrisBorde),
        color = Blanco
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.AccessTime,
                contentDescription = null,
                tint = colorPrincipal,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(label, fontSize = 10.sp, color = TextoSecundario, maxLines = 1)
                // Se formatea visualmente a 12 horas con AM/PM (Ej: 05:00 PM)
                Text(
                    text = if (valor.isBlank()) "--:--" else formatearHora12(valor),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextoOscuro
                )
            }
        }
    }
}

@Composable
private fun CampoDesplegable(
    label: String,
    valor: String,
    opciones: List<String>,
    expandido: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSeleccionar: (String) -> Unit,
    colorPrincipal: Color
) {
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = onExpandChange) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorPrincipal,
                focusedLabelColor = colorPrincipal,
                unfocusedBorderColor = GrisBorde
            )
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { onExpandChange(false) }) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(opcion, fontSize = 14.sp) },
                    onClick = { onSeleccionar(opcion) }
                )
            }
        }
    }
}

@Composable
private fun CampoPlantilla(
    plantilla: Int,
    expandido: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSeleccionar: (Int) -> Unit,
    colorPrincipal: Color
) {
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = onExpandChange) {
        OutlinedTextField(
            value = "Plantilla $plantilla (Diseño oficial)",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = { Text("Plantilla de citación") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) },
            leadingIcon = { Icon(Icons.Outlined.Style, contentDescription = null, tint = colorPrincipal) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorPrincipal,
                focusedLabelColor = colorPrincipal,
                unfocusedBorderColor = GrisBorde
            )
        )
        ExposedDropdownMenu(expanded = expandido, onDismissRequest = { onExpandChange(false) }) {
            (1..7).forEach { numero ->
                DropdownMenuItem(
                    text = { Text("Plantilla $numero") },
                    onClick = { onSeleccionar(numero) }
                )
            }
        }
    }
}

private fun validarFormulario(
    titulo: String,
    convoca: String,
    fecha: String,
    primeraCitacion: String,
    segundaCitacion: String,
    lugar: String
): String? {
    if (titulo.isBlank()) return "Ingresa el título de la asamblea."
    if (convoca.isBlank()) return "Ingresa quién convoca la asamblea."
    if (fecha.isBlank()) return "Selecciona la fecha de la asamblea."
    if (primeraCitacion.isBlank()) return "Selecciona la primera citación."
    if (lugar.isBlank()) return "Ingresa el lugar de la asamblea."

    if (segundaCitacion.isNotBlank()) {
        val primera = convertirMinutos(primeraCitacion)
        val segunda = convertirMinutos(segundaCitacion)
        if (primera != null && segunda != null && segunda <= primera) {
            return "La segunda citación debe ser posterior a la primera."
        }
    }
    return null
}

private fun convertirMinutos(hora: String): Int? {
    return try {
        val partes = hora.split(":")
        if (partes.size != 2) null else partes[0].toInt() * 60 + partes[1].toInt()
    } catch (_: Exception) {
        null
    }
}

private fun formatearFechaVisible(fecha: String): String {
    return try {
        val entrada = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val salida = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val date = entrada.parse(fecha)
        if (date != null) salida.format(date) else fecha
    } catch (_: Exception) {
        fecha
    }
}
private fun formatearHora12(hora24: String): String {
    return try {
        val limpia = hora24.substringBefore(".").substringBefore("Z").trim()
        val entrada = SimpleDateFormat("HH:mm", Locale.getDefault())
        val salida = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = entrada.parse(limpia)
        if (date != null) salida.format(date).uppercase(Locale.getDefault()) else hora24
    } catch (_: Exception) {
        hora24
    }
}