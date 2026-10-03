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
import androidx.compose.material.icons.outlined.AttachFile

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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.res.painterResource

import androidx.compose.foundation.Image
import com.sigefiv.app.R
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.KeyboardArrowDown
/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV
|--------------------------------------------------------------------------
*/

private val FondoClaro = Color(0xFFF8FAFC)
private val FondoOscuro = Color(0xFF0F172A)
private val TarjetaClaro = Color(0xFFFFFFFF)
private val TarjetaOscuro = Color(0xFF1E293B)
private val CampoClaro = Color(0xFFF8FAFC)
private val CampoOscuro = Color(0xFF172033)
private val VerdeSuaveClaro = Color(0xFFDCFCE7)
private val VerdeSuaveOscuro = Color(0xFF163B2A)
private val Blanco = Color(0xFFFFFFFF)
private val TextoClaro = Color(0xFF0F172A)
private val TextoOscuro = Color(0xFFF1F5F9)
private val GrisClaro = Color(0xFF64748B)
private val GrisOscuro = Color(0xFF94A3B8)
private val Verde = Color(0xFF15803D)
private val Morado = Color(0xFF7C3AED)
private val Naranja = Color(0xFFF59E0B)
private val Azul = Color(0xFF2563EB)
private val Rojo = Color(0xFFDC2626)

@Composable
private fun IconoCampoIngreso(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    fondo: Color,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .background(fondo, RoundedCornerShape(14.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}

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
    fechaInicial: String? = null,
    personaInicial: String? = null,
    formaPagoInicial: String? = null,
    montoInicial: Double? = null,
    referenciaInicial: String? = null,
    comprobanteUri: Uri? = null,
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
    val esTemaOscuro = isSystemInDarkTheme()

    val fondoPantalla = if (esTemaOscuro) FondoOscuro else FondoClaro
    val fondoTarjeta = if (esTemaOscuro) TarjetaOscuro else TarjetaClaro
    val fondoCampo = if (esTemaOscuro) CampoOscuro else CampoClaro
    val textoPrincipal = if (esTemaOscuro) TextoOscuro else TextoClaro
    val textoSecundario = if (esTemaOscuro) GrisOscuro else GrisClaro
    var comprobanteSeleccionado by remember {
        mutableStateOf(comprobanteUri)
    }

    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            comprobanteSeleccionado = uri
        }
    }
    var uriCamara by remember {
        mutableStateOf<Uri?>(null)
    }

    val camaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito) {
            uriCamara?.let {
                comprobanteSeleccionado = it
            }
        }
    }
    fun abrirCamara() {
        val valores = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                "comprobante_${System.currentTimeMillis()}.jpg"
            )
            put(
                MediaStore.Images.Media.MIME_TYPE,
                "image/jpeg"
            )
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/SIGEFIV"
            )
        }

        val uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            valores
        )

        if (uri != null) {
            uriCamara = uri
            camaraLauncher.launch(uri)
        }
    }
    val permisoCamaraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { concedido ->
            if (concedido) {
                abrirCamara()
            }
        }
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )
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
    /*
    |--------------------------------------------------------------------------
    | FECHA INICIAL DEL MOVIMIENTO
    |--------------------------------------------------------------------------
    | Si el período abierto es el mes actual, usamos hoy.
    | Si el período abierto es anterior al mes actual, usamos el último día
    | de ese período. Internamente mantenemos yyyy-MM-dd para el backend.
    */
    val fechaPredeterminada = remember(periodoInicio, periodoFin, fechaInicial) {
        fechaInicial ?: run {
            when {
                hoy.year == anioPeriodoEfectivo && hoy.monthValue == mesNumero -> {
                    hoy.toString()
                }
                hoy.isAfter(periodoFin) -> {
                    periodoFin.toString()
                }
                else -> {
                    periodoInicio.toString()
                }
            }
        }
    }

    var fecha by remember(fechaPredeterminada) {
        mutableStateOf(fechaPredeterminada)
    }

    val formatoFechaVisible = remember {
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }

    val fechaVisible = remember(fecha) {
        runCatching {
            LocalDate.parse(fecha).format(formatoFechaVisible)
        }.getOrDefault(fecha)
    }

    var concepto by remember {
        mutableStateOf("")
    }

    var persona by remember(personaInicial) {
        mutableStateOf(
            personaInicial ?: ""
        )
    }

    var formaPago by remember(formaPagoInicial) {
        mutableStateOf(
            formaPagoInicial ?: "Efectivo"
        )
    }

    var monto by remember(montoInicial) {
        mutableStateOf(
            montoInicial?.let { "%.2f".format(it) } ?: ""
        )
    }

    var referencia by remember(referenciaInicial) {
        mutableStateOf(
            referenciaInicial ?: ""
        )
    }
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
    val fechaSeleccionadaInicial = remember(fecha) {
        runCatching { LocalDate.parse(fecha) }.getOrDefault(periodoInicio)
    }

    val initialSelectedDateMillis = remember(fechaSeleccionadaInicial) {
        fechaSeleccionadaInicial.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    }

    val initialMonthMillis = remember(fechaSeleccionadaInicial) {
        fechaSeleccionadaInicial.withDayOfMonth(1)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
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

    LaunchedEffect(initialSelectedDateMillis, initialMonthMillis) {
        datePickerState.displayedMonthMillis = initialMonthMillis
        datePickerState.selectedDateMillis = initialSelectedDateMillis
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
        containerColor = fondoPantalla,
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
                .background(fondoPantalla)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ){
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {

                    // Imagen diferente automáticamente para claro / oscuro
                    Image(
                        painter = painterResource(
                            id = R.drawable.ingreso_banner
                        ),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Contenido encima de la imagen
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = 18.dp,
                                end = 18.dp,
                                top = 18.dp,
                                bottom = 18.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // Icono izquierdo
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    color = Color.White.copy(alpha = 0.82f),
                                    shape = RoundedCornerShape(18.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Outlined.AccountBalanceWallet,
                                contentDescription = "Ingreso",
                                tint = colorPrincipal,
                                modifier = Modifier.size(34.dp)
                            )
                        }

                        Spacer(
                            modifier = Modifier.width(16.dp)
                        )

                        // Información del período
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Período contable activo",
                                color = if (esTemaOscuro)
                                    Color(0xFFCBD5E1)
                                else
                                    Color(0xFF64748B),
                                fontSize = 12.sp
                            )

                            Text(
                                text =
                                    if (cargandoPeriodo ||
                                        cargandoPeriodoVM
                                    ) {
                                        "Cargando..."
                                    } else {
                                        "$nombrePeriodoEfectivo $anioPeriodoEfectivo"
                                    },
                                color = colorPrincipal,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(
                                modifier = Modifier.height(3.dp)
                            )

                            Text(
                                text =
                                    "Registra un nuevo ingreso\n" +
                                            "de manera rápida y segura",
                                color = if (esTemaOscuro)
                                    Color(0xFFCBD5E1)
                                else
                                    Color(0xFF64748B),
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
            /*
            |--------------------------------------------------------------------------
            | FECHA DE INGRESO
            |--------------------------------------------------------------------------
            */
            OutlinedTextField(
                value = fechaVisible,
                onValueChange = {},
                readOnly = true,
                enabled = !guardando,
                label = { Text("Fecha de ingreso") },
                trailingIcon = {
                    IconButton(
                        onClick = { mostrarCalendario = true },
                        enabled = !guardando
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = "Seleccionar fecha",
                            tint = colorPrincipal,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoIngreso()
            )

            /*
            |--------------------------------------------------------------------------
            | SELECTOR DE CATEGORÍA
            |--------------------------------------------------------------------------

             SELECT CATEGORÍA CORREGIDO
            --------------------------------------------------------------------------
            */
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = categoriaSeleccionada?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    enabled = !estaCargandoCategorias && !guardando,
                    label = { Text("Categoría") },
                    placeholder = {
                        Text(
                            if (estaCargandoCategorias)
                                "Cargando categorías..."
                            else
                                "Seleccionar categoría"
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Seleccionar categoría",
                            tint = colorPrincipal,
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = coloresCampoIngreso()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable(
                            enabled = !estaCargandoCategorias && !guardando
                        ) {
                            mostrarCategorias = true
                        }
                )

                DropdownMenu(
                    expanded = mostrarCategorias,
                    onDismissRequest = { mostrarCategorias = false },
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .background(
                            fondoTarjeta,
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    if (estaCargandoCategorias) {
                        Text(
                            text = "Obteniendo categorías desde la base de datos...",
                            color = textoSecundario,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp
                        )
                    } else if (categoriasIngreso.isEmpty()) {
                        Text(
                            text = "No se encontraron categorías de tipo ingreso.",
                            color = textoSecundario,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 13.sp
                        )
                    } else {
                        Text(
                            text = "CATEGORÍAS DE INGRESO",
                            color = colorPrincipal,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(
                                horizontal = 16.dp,
                                vertical = 10.dp
                            )
                        )

                        categoriasIngreso.forEach { categoria ->
                            val esSeleccionada =
                                categoriaSeleccionada?.id == categoria.id

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = categoria.nombre,
                                        color = textoPrincipal,
                                        fontSize = 14.sp,
                                        fontWeight = if (esSeleccionada)
                                            FontWeight.SemiBold
                                        else
                                            FontWeight.Normal
                                    )
                                },
                                trailingIcon = {
                                    if (esSeleccionada) {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = colorPrincipal,
                                            modifier = Modifier.size(20.dp)
                                        )
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
                placeholder = { Text("Describe el ingreso") },
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
                placeholder = { Text("Nombre de la persona") },
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
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            contentDescription = "Seleccionar forma de pago",
                            tint = colorPrincipal,
                            modifier = Modifier.size(23.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
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
                    onDismissRequest = {
                        mostrarFormaPagoMenu = false
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .background(
                            fondoTarjeta,
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    listOf(
                        "Efectivo",
                        "Yape",
                        "Plin",
                        "Transferencia",
                        "Depósito",
                        "Otro"
                    ).forEach { opcion ->

                        val seleccionada = formaPago == opcion

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = opcion,
                                    color = textoPrincipal,
                                    fontSize = 14.sp,
                                    fontWeight = if (seleccionada)
                                        FontWeight.SemiBold
                                    else
                                        FontWeight.Normal
                                )
                            },
                            trailingIcon = {
                                if (seleccionada) {
                                    Icon(
                                        imageVector = Icons.Outlined.Check,
                                        contentDescription = null,
                                        tint = colorPrincipal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
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
                prefix = {
                    Text(
                        "S/ ",
                        color = colorPrincipal,
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                colors = coloresCampoIngreso()
            )

            // REFERENCIA
            OutlinedTextField(
                value = referencia,
                onValueChange = { referencia = it },
                enabled = !guardando,
                label = {
                    Text("N° de operación / Referencia (opcional)")
                },
                placeholder = {
                    Text("Ej. N° de operación, voucher, etc.")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = coloresCampoIngreso()
            )

            // COMPROBANTE

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (esTemaOscuro) Color(0xFF142B20) else Color(0xFFF1FBF4)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (esTemaOscuro) Color(0xFF2E7D52) else Color(0xFF86D8A6)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconoCampoIngreso(
                            Icons.Outlined.AttachFile,
                            if (esTemaOscuro) Color(0xFF1C5137) else VerdeSuaveClaro,
                            colorPrincipal
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Comprobante",
                                color = textoPrincipal,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Opcional · agrega una imagen",
                                color = textoSecundario,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { selectorImagen.launch("image/*") },
                            enabled = !guardando,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (esTemaOscuro) Color(0xFF1C5137) else VerdeSuaveClaro,
                                contentColor = colorPrincipal
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoLibrary,
                                contentDescription = null,
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text("Galería", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                permisoCamaraLauncher.launch(
                                    android.Manifest.permission.CAMERA
                                )
                            },
                            enabled = !guardando,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (esTemaOscuro) Color(0xFF1C5137) else VerdeSuaveClaro,
                                contentColor = colorPrincipal
                            )
                        ) {
                            Text("📷", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(7.dp))
                            Text("Cámara", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (comprobanteSeleccionado != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = fondoTarjeta
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Comprobante",
                                color = textoPrincipal,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(
                                onClick = {
                                    comprobanteSeleccionado = null
                                },
                                enabled = !guardando
                            ) {
                                Text(
                                    text = "✕",
                                    color = Rojo,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        AsyncImage(
                            model = comprobanteSeleccionado,
                            contentDescription = "Comprobante",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(10.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
            // OBSERVACIONES
            OutlinedTextField(
                value = observaciones,
                onValueChange = { observaciones = it.take(200) },
                enabled = !guardando,
                label = { Text("Observaciones (opcional)") },
                placeholder = {
                    Text("Escribe cualquier información adicional...")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(108.dp),
                minLines = 3,
                maxLines = 5,
                colors = coloresCampoIngreso()
            )

            Text(
                text = "${observaciones.length}/200",
                color = textoSecundario,
                fontSize = 11.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 4.dp),
                textAlign = TextAlign.End
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
                Button(
                    onClick = onCerrarClick,
                    enabled = !guardando,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = colorPrincipal
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, colorPrincipal)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        val montoNumerico = monto.replace(",", ".").toDoubleOrNull()
                        if (categoriaSeleccionada != null && concepto.isNotBlank() && montoNumerico != null && montoNumerico > 0.0) {
                            // fecha se mantiene internamente como yyyy-MM-dd para el backend.
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
                    Text(text = "Cancelar", color = textoSecundario)
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
private fun coloresCampoIngreso(): androidx.compose.material3.TextFieldColors {
    val oscuro = isSystemInDarkTheme()
    val texto = if (oscuro) TextoOscuro else TextoClaro
    val gris = if (oscuro) GrisOscuro else GrisClaro
    val fondo = if (oscuro) CampoOscuro else CampoClaro
    val principal = SeasonalColors.primary(SeasonalTheme.getSeason())

    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = principal,
        unfocusedBorderColor = if (oscuro)
            Color(0xFF64748B).copy(alpha = 0.55f)
        else
            Color(0xFF94A3B8).copy(alpha = 0.55f),
        focusedLabelColor = principal,
        unfocusedLabelColor = gris,
        focusedTextColor = texto,
        unfocusedTextColor = texto,
        focusedPlaceholderColor = gris.copy(alpha = 0.72f),
        unfocusedPlaceholderColor = gris.copy(alpha = 0.72f),
        cursorColor = principal,
        disabledTextColor = gris,
        disabledBorderColor = gris.copy(alpha = 0.20f),
        disabledLabelColor = gris.copy(alpha = 0.60f),
        focusedContainerColor = fondo,
        unfocusedContainerColor = fondo,
        disabledContainerColor = fondo
    )
}

