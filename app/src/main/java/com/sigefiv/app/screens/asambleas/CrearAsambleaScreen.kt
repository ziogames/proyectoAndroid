@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.asambleas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.FormatListNumbered
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun CrearAsambleaScreen(
    creando: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    onGuardar: (CrearAsambleaRequest) -> Unit,
    asamblea: Asamblea? = null
) {
    val primaryColor = MaterialTheme.colorScheme.primary
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
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (modoEdicion) "Editar asamblea" else "Nueva asamblea",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (modoEdicion) "Modificar convocatoria" else "Convocatoria vecinal",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = onBackClick,
                        enabled = !creando,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Text(
                            "Cancelar",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
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
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (creando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Outlined.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (modoEdicion) "Guardar cambios" else "Guardar borrador",
                                fontWeight = FontWeight.Bold
                            )
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
                TarjetaContenedor(
                    titulo = "Información de convocatoria",
                    icono = Icons.Outlined.NotificationsActive,
                    colorPrincipal = primaryColor
                ) {
                    CampoDesplegable(
                        label = "Tipo de asamblea",
                        valor = tipo,
                        opciones = listOf("Ordinaria", "Extraordinaria"),
                        expandido = mostrarTipoMenu,
                        onExpandChange = { mostrarTipoMenu = it },
                        onSeleccionar = { tipo = it; mostrarTipoMenu = false },
                        colorPrincipal = primaryColor
                    )

                    Spacer(Modifier.height(10.dp))

                    CampoTexto(
                        label = "Título de la asamblea",
                        valor = titulo,
                        onValorChange = { titulo = it },
                        obligatorio = true,
                        placeholder = "Ej. Aprobación del presupuesto comunal",
                        colorPrincipal = primaryColor
                    )

                    Spacer(Modifier.height(10.dp))

                    CampoTexto(
                        label = "Convoca",
                        valor = convoca,
                        onValorChange = { convoca = it },
                        obligatorio = true,
                        placeholder = "Ej. Junta Directiva Central",
                        colorPrincipal = primaryColor
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Nivel de importancia",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("normal" to "Normal", "importante" to "Importante", "urgente" to "Urgente").forEach { (clave, nombre) ->
                            val seleccionado = importancia == clave
                            val colorChip = when (clave) {
                                "urgente" -> Color(0xFFDC2626)
                                "importante" -> Color(0xFFD97706)
                                else -> primaryColor
                            }

                            FilterChip(
                                selected = seleccionado,
                                onClick = { importancia = clave },
                                label = {
                                    Text(
                                        text = nombre,
                                        fontSize = 12.sp,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                shape = RoundedCornerShape(20.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = colorChip.copy(alpha = 0.15f),
                                    selectedLabelColor = colorChip,
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    borderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
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

            // SECCIÓN 2: FECHA Y CITACIONES
            item {
                TarjetaContenedor(
                    titulo = "Fecha y horarios",
                    icono = Icons.Outlined.CalendarToday,
                    colorPrincipal = primaryColor
                ) {
                    CampoFecha(
                        fecha = fecha,
                        onSeleccionar = { fecha = it },
                        colorPrincipal = primaryColor
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampoHoraCompacta(
                            label = "1ra Citación *",
                            valor = primeraCitacion,
                            onSeleccionar = { primeraCitacion = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = primaryColor
                        )
                        CampoHoraCompacta(
                            label = "2da Citación",
                            valor = segundaCitacion,
                            onSeleccionar = { segundaCitacion = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = primaryColor
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    CampoHoraCompacta(
                        label = "Hora oficial de asamblea",
                        valor = hora,
                        onSeleccionar = { hora = it },
                        modifier = Modifier.fillMaxWidth(),
                        colorPrincipal = primaryColor
                    )
                }
            }

            // SECCIÓN 3: UBICACIÓN Y LUGAR
            item {
                TarjetaContenedor(
                    titulo = "Lugar y coordenadas vecinales",
                    icono = Icons.Outlined.Place,
                    colorPrincipal = primaryColor
                ) {
                    CampoTexto(
                        label = "Lugar de encuentro",
                        valor = lugar,
                        onValorChange = { lugar = it },
                        obligatorio = true,
                        placeholder = "Ej. Local Comunal / Cancha deportiva",
                        leadingIcon = {
                            Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = primaryColor)
                        },
                        colorPrincipal = primaryColor
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CampoTexto(
                            label = "Sector",
                            valor = sector,
                            onValorChange = { sector = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = primaryColor
                        )
                        CampoTexto(
                            label = "Grupo",
                            valor = grupo,
                            onValorChange = { grupo = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = primaryColor
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        CampoTexto(
                            label = "Manzana",
                            valor = manzana,
                            onValorChange = { manzana = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = primaryColor
                        )
                        CampoTexto(
                            label = "Lote",
                            valor = lote,
                            onValorChange = { lote = it },
                            modifier = Modifier.weight(1f),
                            colorPrincipal = primaryColor
                        )
                    }
                }
            }

            // SECCIÓN 4: AGENDA DE PUNTOS
            item {
                TarjetaContenedor(
                    titulo = "Puntos de agenda",
                    icono = Icons.Outlined.FormatListNumbered,
                    colorPrincipal = primaryColor
                ) {
                    Text(
                        "Detalla los temas que se discutirán con los vecinos.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))

                    agenda.forEachIndexed { index, punto ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = CircleShape,
                                color = primaryColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${index + 1}",
                                        color = primaryColor,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(Modifier.width(8.dp))

                            OutlinedTextField(
                                value = punto,
                                onValueChange = { agenda[index] = it },
                                modifier = Modifier.weight(1f),
                                placeholder = {
                                    Text(
                                        "Tema o moción ${index + 1}...",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                )
                            )

                            if (agenda.size > 1) {
                                IconButton(
                                    onClick = { agenda.removeAt(index) },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.DeleteOutline,
                                        contentDescription = "Eliminar",
                                        tint = MaterialTheme.colorScheme.error
                                    )
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
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.35f)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Añadir otro punto", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // SECCIÓN 5: DESCRIPCIÓN Y PLANTILLA
            item {
                TarjetaContenedor(
                    titulo = "Detalle y diseño",
                    icono = Icons.Outlined.Style,
                    colorPrincipal = primaryColor
                ) {
                    CampoTexto(
                        label = "Descripción o notas adicionales",
                        valor = descripcion,
                        onValorChange = { descripcion = it },
                        singleLine = false,
                        minLines = 3,
                        placeholder = "Agrega instrucciones sobre puntualidad, material necesario, etc.",
                        colorPrincipal = primaryColor
                    )

                    Spacer(Modifier.height(12.dp))

                    CampoPlantilla(
                        plantilla = plantilla,
                        expandido = mostrarPlantillaMenu,
                        onExpandChange = { mostrarPlantillaMenu = it },
                        onSeleccionar = { plantilla = it; mostrarPlantillaMenu = false },
                        colorPrincipal = primaryColor
                    )
                }
            }

            item { Spacer(Modifier.height(24.dp)) }
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(34.dp),
                    shape = RoundedCornerShape(9.dp),
                    color = colorPrincipal.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, colorPrincipal.copy(alpha = 0.25f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icono, contentDescription = null, tint = colorPrincipal, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = titulo,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
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
        placeholder = placeholder?.let {
            { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), fontSize = 13.sp) }
        },
        singleLine = singleLine,
        minLines = minLines,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedBorderColor = colorPrincipal,
            focusedLabelColor = colorPrincipal,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
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
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Outlined.CalendarToday,
                contentDescription = null,
                tint = colorPrincipal,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Fecha programada *",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    formatearFechaVisible(fecha),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
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

    val partes = valor.split(":")
    val horaInicial = partes.getOrNull(0)?.toIntOrNull() ?: 17
    val minutoInicial = partes.getOrNull(1)?.toIntOrNull() ?: 0

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                TimePickerDialog(
                    context,
                    { _, h, m ->
                        onSeleccionar(String.format(Locale.getDefault(), "%02d:%02d", h, m))
                    },
                    horaInicial,
                    minutoInicial,
                    false
                ).show()
            },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
        color = MaterialTheme.colorScheme.surface
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
                Text(
                    label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
                Text(
                    text = if (valor.isBlank()) "--:--" else formatearHora12(valor),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = colorPrincipal,
                focusedLabelColor = colorPrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcion,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
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
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = colorPrincipal,
                focusedLabelColor = colorPrincipal,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
            )
        )
        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = { onExpandChange(false) },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            (1..7).forEach { numero ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Plantilla $numero",
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
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