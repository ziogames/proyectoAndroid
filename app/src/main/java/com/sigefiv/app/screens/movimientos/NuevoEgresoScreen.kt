@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import android.content.ContentValues
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.sigefiv.app.data.model.Categoria
import com.sigefiv.app.viewmodel.CategoriasViewModel
import com.sigefiv.app.viewmodel.PeriodoViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val ColorEgreso = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoEgresoScreen(
    categorias: List<Categoria>,
    cargandoCategorias: Boolean,
    periodoNombre: String?,
    periodoAnio: Int?,
    cargandoPeriodo: Boolean,
    guardando: Boolean,
    mensaje: String?,
    fechaInicial: String? = null,
    personaInicial: String? = null,
    comprobanteUri: Uri? = null,
    formaPagoInicial: String? = null,
    montoInicial: Double? = null,
    referenciaInicial: String? = null,
    onCerrarClick: () -> Unit,
    onGuardar: (
        fecha: String,
        categoriaId: Int,
        concepto: String,
        persona: String?,
        formaPago: String,
        monto: Double,
        referencia: String?,
        observaciones: String?,
        comprobanteUri: Uri?
    ) -> Unit
) {
    val context = LocalContext.current

    var comprobanteSeleccionado by remember { mutableStateOf(comprobanteUri) }
    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) comprobanteSeleccionado = uri }

    var uriCamara by remember { mutableStateOf<Uri?>(null) }
    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) uriCamara?.let { comprobanteSeleccionado = it }
    }

    fun abrirCamara() {
        val valores = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "comprobante_egreso_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/SIGEFIV")
        }
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, valores)
        if (uri != null) {
            uriCamara = uri
            camaraLauncher.launch(uri)
        }
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
    }

    val categoriasVM = remember { CategoriasViewModel(context) }
    val periodoVM = remember { PeriodoViewModel(context) }

    LaunchedEffect(Unit) {
        categoriasVM.cargarCategoriasParaMovimientos()
        periodoVM.cargarPeriodoAbierto()
    }

    val categoriasCargadasVM by categoriasVM.categorias.collectAsState()
    val cargandoCategoriasVM by categoriasVM.cargando.collectAsState()
    val periodoCargadoVM by periodoVM.periodo.collectAsState()
    val cargandoPeriodoVM by periodoVM.cargando.collectAsState()

    val hoy = remember { LocalDate.now() }

    val nombrePeriodoEfectivo = periodoCargadoVM?.nombre ?: periodoNombre ?: "Junio"
    val anioPeriodoEfectivo = periodoCargadoVM?.anio ?: periodoAnio ?: 2026

    val mesNumero = remember(nombrePeriodoEfectivo) {
        when (nombrePeriodoEfectivo.lowercase().trim()) {
            "enero" -> 1
            "febrero" -> 2
            "marzo" -> 3
            "abril" -> 4
            "mayo" -> 5
            "junio" -> 6
            "julio" -> 7
            "agosto" -> 8
            "septiembre", "setiembre" -> 9
            "octubre" -> 10
            "noviembre" -> 11
            "diciembre" -> 12
            else -> 6
        }
    }

    val periodoInicio = remember(mesNumero, anioPeriodoEfectivo) {
        LocalDate.of(anioPeriodoEfectivo, mesNumero, 1)
    }

    val periodoFin = remember(periodoInicio) {
        periodoInicio.withDayOfMonth(periodoInicio.lengthOfMonth())
    }

    val fechaPredeterminada = remember(periodoInicio, periodoFin, fechaInicial, hoy) {
        fechaInicial ?: run {
            if (periodoInicio.year == hoy.year && periodoInicio.monthValue == hoy.monthValue) {
                hoy.toString()
            } else {
                periodoFin.toString()
            }
        }
    }

    var fecha by remember(periodoInicio, fechaInicial, fechaPredeterminada) {
        mutableStateOf(fechaPredeterminada)
    }
    var concepto by remember { mutableStateOf("") }
    var persona by remember(personaInicial) { mutableStateOf(personaInicial ?: "") }
    var formaPago by remember(formaPagoInicial) { mutableStateOf(formaPagoInicial ?: "Efectivo") }
    var monto by remember(montoInicial) { mutableStateOf(montoInicial?.let { "%.2f".format(it) } ?: "") }
    var referencia by remember(referenciaInicial) { mutableStateOf(referenciaInicial ?: "") }
    var observaciones by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var mostrarCategorias by remember { mutableStateOf(false) }
    var mostrarFormaPagoMenu by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    val fechaPredeterminadaDate = remember(fechaPredeterminada) { LocalDate.parse(fechaPredeterminada) }
    val initialMonthMillis = remember(periodoInicio) {
        periodoInicio.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }
    val initialSelectedDateMillis = remember(fechaPredeterminadaDate) {
        fechaPredeterminadaDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        initialDisplayedMonthMillis = initialMonthMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis).atZone(ZoneOffset.UTC).toLocalDate()
                return !date.isBefore(periodoInicio) && !date.isAfter(periodoFin)
            }
        }
    )

    LaunchedEffect(initialMonthMillis, initialSelectedDateMillis) {
        datePickerState.displayedMonthMillis = initialMonthMillis
        datePickerState.selectedDateMillis = initialSelectedDateMillis
    }

    val todasLasCategorias = remember(categorias, categoriasCargadasVM) {
        if (categorias.isNotEmpty()) categorias else categoriasCargadasVM
    }
    val estaCargandoCategorias = cargandoCategorias || cargandoCategoriasVM
    val categoriasEgreso = remember(todasLasCategorias) {
        todasLasCategorias.filter { cat ->
            val t = cat.tipo.trim().lowercase()
            t == "egreso" || t == "egresos"
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nuevo egreso",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Registrar un gasto u operación",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCerrarClick, enabled = !guardando) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .imePadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // Header informativo del período activo
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(ColorEgreso.copy(alpha = 0.12f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Período",
                            tint = ColorEgreso,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Período contable activo",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (cargandoPeriodo || cargandoPeriodoVM) "Cargando..." else "$nombrePeriodoEfectivo $anioPeriodoEfectivo",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Bloque destacado para el Monto
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                border = BorderStroke(1.dp, ColorEgreso.copy(alpha = 0.35f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Monto del egreso",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = monto,
                        onValueChange = { nuevoValor ->
                            if (nuevoValor.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                                monto = nuevoValor
                            }
                        },
                        enabled = !guardando,
                        placeholder = { Text("0.00", fontSize = 24.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)) },
                        prefix = {
                            Text(
                                "S/ ",
                                color = ColorEgreso,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        },
                        textStyle = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = coloresCampoGenerico()
                    )
                }
            }

            // Selector de Fecha
            OutlinedTextField(
                value = runCatching {
                    LocalDate.parse(fecha).let { "%02d-%02d-%04d".format(it.dayOfMonth, it.monthValue, it.year) }
                }.getOrElse { fecha },
                onValueChange = {},
                readOnly = true,
                enabled = !guardando,
                label = { Text("Fecha de egreso") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !guardando) { mostrarCalendario = true },
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = { mostrarCalendario = true }, enabled = !guardando) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = ColorEgreso
                        )
                    }
                },
                colors = coloresCampoGenerico()
            )

            // Selector de Categoría
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = !estaCargandoCategorias && !guardando,
                    label = { Text("Categoría") },
                    placeholder = {
                        Text(if (estaCargandoCategorias) "Cargando categorías..." else "Seleccionar categoría")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Text("▼", color = ColorEgreso, fontSize = 12.sp) },
                    colors = coloresCampoGenerico()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(enabled = !estaCargandoCategorias && !guardando) {
                            mostrarCategorias = true
                        }
                )

                DropdownMenu(
                    expanded = mostrarCategorias,
                    onDismissRequest = { mostrarCategorias = false },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    if (estaCargandoCategorias) {
                        Text(
                            text = "Obteniendo categorías...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp
                        )
                    } else if (categoriasEgreso.isEmpty()) {
                        Text(
                            text = "No se encontraron categorías de egreso.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(14.dp),
                            fontSize = 13.sp
                        )
                    } else {
                        categoriasEgreso.forEach { categoria ->
                            val esSeleccionada = categoriaSeleccionada?.id == categoria.id
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = categoria.nombre,
                                            color = if (esSeleccionada) ColorEgreso else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (esSeleccionada) FontWeight.Bold else FontWeight.Normal
                                        )
                                        if (esSeleccionada) {
                                            Icon(Icons.Outlined.Check, contentDescription = null, tint = ColorEgreso, modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                onClick = {
                                    categoriaSeleccionada = categoria
                                    mostrarCategorias = false
                                }
                            )
                        }
                    }
                }
            }

            // Concepto
            OutlinedTextField(
                value = concepto,
                onValueChange = { concepto = it },
                enabled = !guardando,
                label = { Text("Concepto") },
                placeholder = { Text("Ej. Compra de materiales de limpieza") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoGenerico()
            )

            // Proveedor / Persona
            OutlinedTextField(
                value = persona,
                onValueChange = { persona = it },
                enabled = !guardando,
                label = { Text("Proveedor o persona (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoGenerico()
            )

            // Forma de Pago
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = formaPago,
                    onValueChange = {},
                    readOnly = true,
                    enabled = !guardando,
                    label = { Text("Forma de pago") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Text("▼", color = ColorEgreso, fontSize = 12.sp) },
                    colors = coloresCampoGenerico()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(enabled = !guardando) { mostrarFormaPagoMenu = true }
                )

                DropdownMenu(
                    expanded = mostrarFormaPagoMenu,
                    onDismissRequest = { mostrarFormaPagoMenu = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    listOf("Efectivo", "Yape", "Plin", "Transferencia", "Depósito", "Otro").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                formaPago = opcion
                                mostrarFormaPagoMenu = false
                            }
                        )
                    }
                }
            }

            // Referencia
            OutlinedTextField(
                value = referencia,
                onValueChange = { referencia = it },
                enabled = !guardando,
                label = { Text("N° de operación / Comprobante (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoGenerico()
            )

            // Sección de Comprobante / Evidencia
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Comprobante / Recibo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (comprobanteSeleccionado != null) {
                            TextButton(onClick = { comprobanteSeleccionado = null }, enabled = !guardando) {
                                Text("Quitar", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (comprobanteSeleccionado != null) {
                        AsyncImage(
                            model = comprobanteSeleccionado,
                            contentDescription = "Comprobante",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                onClick = { selectorImagen.launch("image/*") },
                                enabled = !guardando,
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.PhotoLibrary, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Galería", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }

                            Surface(
                                onClick = { permisoCamaraLauncher.launch(android.Manifest.permission.CAMERA) },
                                enabled = !guardando,
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Outlined.PhotoCamera, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cámara", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                        }
                    }
                }
            }

            // Observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                enabled = !guardando,
                label = { Text("Observaciones (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                colors = coloresCampoGenerico()
            )

            if (!mensaje.isNullOrBlank()) {
                Text(
                    text = mensaje,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Botones de Acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextButton(
                    onClick = onCerrarClick,
                    enabled = !guardando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = {
                        val montoNumerico = monto.replace(",", ".").toDoubleOrNull()
                        if (categoriaSeleccionada != null && concepto.isNotBlank() && montoNumerico != null && montoNumerico > 0.0) {
                            onGuardar(
                                fecha,
                                categoriaSeleccionada!!.id,
                                concepto.trim(),
                                persona.trim().ifBlank { null },
                                formaPago,
                                montoNumerico,
                                referencia.trim().ifBlank { null },
                                observaciones.trim().ifBlank { null },
                                comprobanteSeleccionado
                            )
                        }
                    },
                    enabled = !guardando &&
                            categoriaSeleccionada != null &&
                            concepto.isNotBlank() &&
                            (monto.replace(",", ".").toDoubleOrNull() ?: 0.0) > 0.0,
                    modifier = Modifier.weight(1.3f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorEgreso,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (guardando) "Guardando..." else "Guardar egreso",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Modal Calendario
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val nuevaFecha = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                            fecha = nuevaFecha.toString()
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text("Aceptar", color = ColorEgreso, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Días de $nombrePeriodoEfectivo $anioPeriodoEfectivo",
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                showModeToggle = false
            )
        }
    }
}

@Composable
private fun coloresCampoGenerico() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ColorEgreso,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
    focusedLabelColor = ColorEgreso,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = ColorEgreso
)