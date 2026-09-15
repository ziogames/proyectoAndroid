@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.movimientos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sigefiv.app.data.model.Categoria
import com.sigefiv.app.viewmodel.CategoriasViewModel
import com.sigefiv.app.viewmodel.PeriodoViewModel
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdeSuave = Color(0xFFDCFCE7)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisClaro = Color(0xFF64748B)
private val Verde = Color(0xFF15803D)
private val Rojo = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoIngresoScreen(
    categorias: List<Categoria>,
    cargandoCategorias: Boolean,
    periodoNombre: String?,
    periodoAnio: Int?,
    cargandoPeriodo: Boolean,
    guardando: Boolean,
    mensaje: String?,
    onCerrarClick: () -> Unit,
    onGuardar: (
        fecha: String,
        categoriaId: Int,
        concepto: String,
        persona: String?,
        formaPago: String,
        monto: Double,
        referencia: String?,
        observaciones: String?
    ) -> Unit
) {
    val context = LocalContext.current
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
    val categoriasVM = remember { CategoriasViewModel(context) }
    val periodoVM = remember { PeriodoViewModel(context) }

    LaunchedEffect(Unit) {
        categoriasVM.cargarCategorias()
        periodoVM.cargarPeriodoAbierto()
    }

    val categoriasCargadasVM by categoriasVM.categorias.collectAsState()
    val cargandoCategoriasVM by categoriasVM.cargando.collectAsState()
    val periodoCargadoVM by periodoVM.periodo.collectAsState()
    val cargandoPeriodoVM by periodoVM.cargando.collectAsState()

    val hoy = remember { LocalDate.now() }

    /*
    |--------------------------------------------------------------------------
    | DETERMINACIÓN EXACTA DEL PERÍODO ACTIVO
    |--------------------------------------------------------------------------
    */
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

    var fecha by remember(periodoInicio) {
        mutableStateOf(periodoInicio.toString())
    }

    var concepto by remember { mutableStateOf("") }
    var persona by remember { mutableStateOf("") }
    var formaPago by remember { mutableStateOf("Efectivo") }
    var monto by remember { mutableStateOf("") }
    var referencia by remember { mutableStateOf("") }
    var observaciones by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf<Categoria?>(null) }
    var mostrarCategorias by remember { mutableStateOf(false) }
    var mostrarFormaPagoMenu by remember { mutableStateOf(false) }
    var mostrarCalendario by remember { mutableStateOf(false) }

    /*
    |--------------------------------------------------------------------------
    | DATEPICKER CONFIGURADO EN UTC
    |--------------------------------------------------------------------------
    */
    val initialMonthMillis = remember(periodoInicio) {
        periodoInicio.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMonthMillis,
        initialDisplayedMonthMillis = initialMonthMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant.ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

                return !date.isBefore(periodoInicio) && !date.isAfter(periodoFin)
            }
        }
    )

    LaunchedEffect(initialMonthMillis) {
        datePickerState.displayedMonthMillis = initialMonthMillis
        datePickerState.selectedDateMillis = initialMonthMillis
    }

    /*
    |--------------------------------------------------------------------------
    | LISTA UNIFICADA DE CATEGORÍAS
    |--------------------------------------------------------------------------
    */
    val todasLasCategorias = remember(categorias, categoriasCargadasVM) {
        if (categorias.isNotEmpty()) categorias else categoriasCargadasVM
    }

    val estaCargandoCategorias = cargandoCategorias || cargandoCategoriasVM

    val categoriasIngreso = remember(todasLasCategorias) {
        todasLasCategorias.filter { cat ->
            val tipoNormalizado = cat.tipo.trim().lowercase()
            tipoNormalizado == "ingreso" || tipoNormalizado == "ingresos"
        }
    }

    Scaffold(
        containerColor = FondoSIGEFIV,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Nuevo ingreso",
                            color = Blanco,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Registrar dinero recibido",
                            color = Blanco.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCerrarClick,
                        enabled = !guardando
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoSIGEFIV)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            /*
            |--------------------------------------------------------------------------
            | TARJETA INFORMATIVA DEL PERÍODO
            |--------------------------------------------------------------------------
            */
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(VerdeSuave, shape = RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Período",
                            tint = colorPrincipal
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Período contable activo",
                            color = GrisClaro,
                            fontSize = 11.sp
                        )
                        Text(
                            text = if (cargandoPeriodo || cargandoPeriodoVM) "Cargando..." else "$nombrePeriodoEfectivo $anioPeriodoEfectivo",
                            color = colorPrincipal,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            /*
            |--------------------------------------------------------------------------
            | CAMPO FECHA RESTRINGIDO
            |--------------------------------------------------------------------------
            */
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                readOnly = true,
                enabled = !guardando,
                label = { Text("Fecha de ingreso") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !guardando) {
                        mostrarCalendario = true
                    },
                singleLine = true,
                trailingIcon = {
                    IconButton(
                        onClick = { mostrarCalendario = true },
                        enabled = !guardando
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = colorPrincipal
                        )
                    }
                },
                colors = coloresCampoIngreso()
            )

            /*
            |--------------------------------------------------------------------------
            | SELECT CATEGORÍA CORREGIDO
            |--------------------------------------------------------------------------
            */
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
                    trailingIcon = { Text(text = "▼", color = colorPrincipal) },
                    colors = coloresCampoIngreso()
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
                        .background(FondoTarjeta)
                ) {
                    if (estaCargandoCategorias) {
                        Text(
                            text = "Obteniendo categorías desde la base de datos...",
                            color = GrisClaro,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp
                        )
                    } else if (categoriasIngreso.isEmpty()) {
                        Text(
                            text = "No se encontraron categorías de tipo ingreso.",
                            color = GrisClaro,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = "CATEGORÍAS DE INGRESO",
                            color = colorPrincipal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        categoriasIngreso.forEach { categoria ->
                            val esSeleccionada = categoriaSeleccionada?.id == categoria.id

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                if (esSeleccionada) VerdeSuave else Color.Transparent,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Outlined.Folder,
                                                contentDescription = null,
                                                tint = if (esSeleccionada) colorPrincipal else GrisClaro,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = categoria.nombre,
                                                color = if (esSeleccionada) colorPrincipal else TextoPrincipal,
                                                fontWeight = if (esSeleccionada) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                        }

                                        if (esSeleccionada) {
                                            Icon(
                                                imageVector = Icons.Outlined.Check,
                                                contentDescription = null,
                                                tint = colorPrincipal,
                                                modifier = Modifier.size(18.dp)
                                            )
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

            // CONCEPTO
            OutlinedTextField(
                value = concepto,
                onValueChange = { concepto = it },
                enabled = !guardando,
                label = { Text("Concepto") },
                placeholder = { Text("Ej. Cuota mantenimiento de junio") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoIngreso()
            )

            // PERSONA
            OutlinedTextField(
                value = persona,
                onValueChange = { persona = it },
                enabled = !guardando,
                label = { Text("Persona / Vecino (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoIngreso()
            )

            // FORMA DE PAGO
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = formaPago,
                    onValueChange = {},
                    readOnly = true,
                    enabled = !guardando,
                    label = { Text("Forma de pago") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = { Text(text = "▼", color = colorPrincipal) },
                    colors = coloresCampoIngreso()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(enabled = !guardando) {
                            mostrarFormaPagoMenu = true
                        }
                )

                DropdownMenu(
                    expanded = mostrarFormaPagoMenu,
                    onDismissRequest = { mostrarFormaPagoMenu = false }
                ) {
                    listOf("Efectivo", "Yape", "Plin", "Transferencia", "Depósito", "Otro").forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                formaPago = opcion
                                mostrarFormaPagoMenu = false
                            }
                        )
                    }
                }
            }

            // MONTO
            OutlinedTextField(
                value = monto,
                onValueChange = { nuevoValor ->
                    if (nuevoValor.matches(Regex("^\\d*(\\.\\d{0,2})?$"))) {
                        monto = nuevoValor
                    }
                },
                enabled = !guardando,
                label = { Text("Monto") },
                placeholder = { Text("0.00") },
                prefix = { Text("S/ ", color = colorPrincipal, fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                colors = coloresCampoIngreso()
            )

            // REFERENCIA
            OutlinedTextField(
                value = referencia,
                onValueChange = { referencia = it },
                enabled = !guardando,
                label = { Text("N° de operación / Referencia (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoIngreso()
            )

            // OBSERVACIONES
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it },
                enabled = !guardando,
                label = { Text("Observaciones (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                colors = coloresCampoIngreso()
            )

            if (!mensaje.isNullOrBlank()) {
                Text(
                    text = mensaje,
                    color = Rojo,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            /*
            |--------------------------------------------------------------------------
            | BOTONES ACCIÓN
            |--------------------------------------------------------------------------
            */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onCerrarClick,
                    enabled = !guardando,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Cancelar", color = GrisClaro)
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
                                observaciones.trim().ifBlank { null }
                            )
                        }
                    },
                    enabled = !guardando &&
                            categoriaSeleccionada != null &&
                            concepto.isNotBlank() &&
                            (monto.replace(",", ".").toDoubleOrNull() ?: 0.0) > 0.0,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorPrincipal,
                        contentColor = Blanco
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (guardando) "Guardando..." else "Guardar ingreso",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | DIÁLOGO DEL CALENDARIO
    |--------------------------------------------------------------------------
    */
    if (mostrarCalendario) {
        DatePickerDialog(
            onDismissRequest = { mostrarCalendario = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            val nuevaFecha = Instant.ofEpochMilli(it)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
                            fecha = nuevaFecha.toString()
                        }
                        mostrarCalendario = false
                    }
                ) {
                    Text(text = "Aceptar", color = colorPrincipal, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarCalendario = false }) {
                    Text(text = "Cancelar", color = GrisClaro)
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
                        color = colorPrincipal
                    )
                },
                showModeToggle = false
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| COLORES DE CAMPOS
|--------------------------------------------------------------------------
*/

@Composable
private fun coloresCampoIngreso() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
    unfocusedBorderColor = GrisClaro.copy(alpha = 0.35f),
    focusedLabelColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
    unfocusedLabelColor = GrisClaro,
    focusedTextColor = TextoPrincipal,
    unfocusedTextColor = TextoPrincipal,
    focusedPlaceholderColor = GrisClaro.copy(alpha = 0.65f),
    unfocusedPlaceholderColor = GrisClaro.copy(alpha = 0.65f),
    cursorColor = SeasonalColors.primary(SeasonalTheme.getSeason()),
    disabledTextColor = GrisClaro,
    disabledBorderColor = GrisClaro.copy(alpha = 0.2f),
    disabledLabelColor = GrisClaro.copy(alpha = 0.6f)
)