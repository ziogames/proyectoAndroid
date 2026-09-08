@file:OptIn(ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.asambleas

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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

private val FondoSIGEFIV = Color(0xFFF1F5F9)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdePrincipal = Color(0xFF0F766E)
private val VerdeSuave = Color(0xFFCCFBF1)
private val VerdeTexto = Color(0xFF0D9488)
private val Blanco = Color.White
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisSecundario = Color(0xFF475569)
private val GrisClaro = Color(0xFF94A3B8)
private val GrisBorde = Color(0xFFE2E8F0)
private val Rojo = Color(0xFFE11D48)
private val RojoFondo = Color(0xFFFFE4E6)

@Composable
fun CrearAsambleaScreen(
    creando: Boolean,
    error: String?,
    onBackClick: () -> Unit,
    onGuardar: (CrearAsambleaRequest) -> Unit,
    asamblea: Asamblea? = null
) {
    val calendario = remember { Calendar.getInstance() }
    val snackbarHostState = remember { SnackbarHostState() }

    val modoEdicion = asamblea != null

    var tipo by remember {
        mutableStateOf(
            asamblea?.tipo?.takeIf { it.isNotBlank() } ?: "Ordinaria"
        )
    }

    var titulo by remember {
        mutableStateOf(
            asamblea?.titulo ?: ""
        )
    }

    var convoca by remember {
        mutableStateOf(
            asamblea?.convoca ?: ""
        )
    }

    var sector by remember {
        mutableStateOf(
            asamblea?.sector ?: ""
        )
    }

    var grupo by remember {
        mutableStateOf(
            asamblea?.grupo ?: ""
        )
    }

    var manzana by remember {
        mutableStateOf(
            asamblea?.manzana ?: ""
        )
    }

    var lote by remember {
        mutableStateOf(
            asamblea?.lote ?: ""
        )
    }

    var fecha by remember {
        mutableStateOf(
            asamblea?.fecha
                ?.substringBefore("T")
                ?.takeIf { it.isNotBlank() }
                ?: SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(calendario.time)
        )
    }

    var hora by remember {
        mutableStateOf(
            asamblea?.hora
                ?.substringBefore(".")
                ?.substringBefore("Z")
                ?: ""
        )
    }

    var primeraCitacion by remember {
        mutableStateOf(
            asamblea?.primera_citacion
                ?.substringBefore(".")
                ?.substringBefore("Z")
                ?: ""
        )
    }

    var segundaCitacion by remember {
        mutableStateOf(
            asamblea?.segunda_citacion
                ?.substringBefore(".")
                ?.substringBefore("Z")
                ?: ""
        )
    }

    var lugar by remember {
        mutableStateOf(
            asamblea?.lugar ?: ""
        )
    }

    var descripcion by remember {
        mutableStateOf(
            asamblea?.descripcion ?: ""
        )
    }

    var importancia by remember {
        mutableStateOf(
            asamblea?.importancia
                ?.takeIf { it.isNotBlank() }
                ?: "normal"
        )
    }

    var plantilla by remember {
        mutableStateOf(
            asamblea?.plantilla_citacion ?: 1
        )
    }

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

            if (isEmpty()) {
                add("")
            }
        }
    }

    var mostrarTipoMenu by remember {
        mutableStateOf(false)
    }

    var mostrarImportanciaMenu by remember {
        mutableStateOf(false)
    }

    var mostrarPlantillaMenu by remember {
        mutableStateOf(false)
    }

    var mostrarErrorValidacion by remember {
        mutableStateOf<String?>(null)
    }

    val errorVisible = error ?: mostrarErrorValidacion

    LaunchedEffect(errorVisible) {
        errorVisible?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        containerColor = FondoSIGEFIV,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            ) { data ->
                Surface(
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = RojoFondo,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Rojo.copy(alpha = 0.3f)
                    ),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = 16.dp,
                                vertical = 14.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ErrorOutline,
                            contentDescription = null,
                            tint = Rojo,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(10.dp)
                        )

                        Text(
                            text = data.visuals.message,
                            color = Rojo,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (modoEdicion) {
                                "Editar asamblea"
                            } else {
                                "Nueva asamblea"
                            },
                            color = Blanco,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (modoEdicion) {
                                "Modificar convocatoria"
                            } else {
                                "Crear convocatoria"
                            },
                            color = Blanco.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        enabled = !creando
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Volver",
                            tint = Blanco
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdePrincipal
                )
            )
        }
    ) { innerPadding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                horizontal = 16.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Información general",
                        subtitulo = "Datos principales de la asamblea."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    CampoDesplegable(
                        label = "Tipo de asamblea",
                        valor = tipo,
                        opciones = listOf(
                            "Ordinaria",
                            "Extraordinaria"
                        ),
                        expandido = mostrarTipoMenu,
                        onExpandChange = {
                            mostrarTipoMenu = it
                        },
                        onSeleccionar = {
                            tipo = it
                            mostrarTipoMenu = false
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoTexto(
                        label = "Título",
                        valor = titulo,
                        onValorChange = {
                            titulo = it
                        },
                        obligatorio = true
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoTexto(
                        label = "Convoca",
                        valor = convoca,
                        onValorChange = {
                            convoca = it
                        },
                        obligatorio = true
                    )
                }
            }

            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Ubicación vecinal",
                        subtitulo = "Completa solo los datos que correspondan."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampoTexto(
                            label = "Sector",
                            valor = sector,
                            onValorChange = {
                                sector = it
                            },
                            modifier = Modifier.weight(1f)
                        )

                        CampoTexto(
                            label = "Grupo",
                            valor = grupo,
                            onValorChange = {
                                grupo = it
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CampoTexto(
                            label = "Manzana",
                            valor = manzana,
                            onValorChange = {
                                manzana = it
                            },
                            modifier = Modifier.weight(1f)
                        )

                        CampoTexto(
                            label = "Lote",
                            valor = lote,
                            onValorChange = {
                                lote = it
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoTexto(
                        label = "Lugar",
                        valor = lugar,
                        onValorChange = {
                            lugar = it
                        },
                        obligatorio = true,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = VerdePrincipal
                            )
                        }
                    )
                }
            }

            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Fecha y horarios",
                        subtitulo = "Define la fecha de la asamblea y sus citaciones."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    CampoFecha(
                        fecha = fecha,
                        onSeleccionar = {
                            fecha = it
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoHora(
                        label = "Hora de la asamblea",
                        valor = hora,
                        onSeleccionar = {
                            hora = it
                        },
                        opcional = true
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoHora(
                        label = "Primera citación",
                        valor = primeraCitacion,
                        onSeleccionar = {
                            primeraCitacion = it
                        },
                        obligatorio = true
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoHora(
                        label = "Segunda citación",
                        valor = segundaCitacion,
                        onSeleccionar = {
                            segundaCitacion = it
                        },
                        opcional = true
                    )
                }
            }

            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Características",
                        subtitulo = "Define la importancia de la convocatoria."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    CampoDesplegable(
                        label = "Importancia",
                        valor = when (importancia) {
                            "urgente" -> "Urgente"
                            "importante" -> "Importante"
                            else -> "Normal"
                        },
                        opciones = listOf(
                            "Normal",
                            "Importante",
                            "Urgente"
                        ),
                        expandido = mostrarImportanciaMenu,
                        onExpandChange = {
                            mostrarImportanciaMenu = it
                        },
                        onSeleccionar = {
                            importancia = when (it) {
                                "Urgente" -> "urgente"
                                "Importante" -> "importante"
                                else -> "normal"
                            }

                            mostrarImportanciaMenu = false
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    CampoTexto(
                        label = "Descripción",
                        valor = descripcion,
                        onValorChange = {
                            descripcion = it
                        },
                        singleLine = false,
                        minLines = 3
                    )
                }
            }

            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Agenda",
                        subtitulo = "Agrega los puntos que serán tratados."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        agenda.forEachIndexed { index, punto ->

                            AgendaCampo(
                                numero = index + 1,
                                valor = punto,
                                puedeEliminar = agenda.size > 1,
                                onValorChange = {
                                    agenda[index] = it
                                },
                                onEliminar = {
                                    agenda.removeAt(index)
                                }
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                agenda.add("")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = VerdePrincipal
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                "Agregar punto de agenda",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            item {
                FormCard {
                    SeccionTitulo(
                        titulo = "Plantilla de citación",
                        subtitulo = "Selecciona el diseño que utilizará la convocatoria."
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    CampoPlantilla(
                        plantilla = plantilla,
                        expandido = mostrarPlantillaMenu,
                        onExpandChange = {
                            mostrarPlantillaMenu = it
                        },
                        onSeleccionar = {
                            plantilla = it
                            mostrarPlantillaMenu = false
                        }
                    )
                }
            }

            item {
                FormCard {
                    Text(
                        text = if (modoEdicion) {
                            "Estado actual"
                        } else {
                            "Estado inicial"
                        },
                        fontSize = 11.sp,
                        color = GrisClaro,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = when {
                            modoEdicion &&
                                    asamblea?.estado.equals(
                                        "publicada",
                                        ignoreCase = true
                                    ) -> "Publicada"

                            modoEdicion &&
                                    asamblea?.estado.equals(
                                        "cancelada",
                                        ignoreCase = true
                                    ) -> "Cancelada"

                            else -> "Borrador"
                        },
                        fontSize = 16.sp,
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = if (modoEdicion) {
                            "Puedes modificar los datos de la convocatoria y guardar los cambios."
                        } else {
                            "La asamblea se guardará como borrador y podrá revisarse antes de publicarla."
                        },
                        fontSize = 12.sp,
                        color = GrisSecundario
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBackClick,
                        enabled = !creando,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            GrisBorde
                        )
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = GrisSecundario
                        )
                    }

                    Button(
                        onClick = {
                            mostrarErrorValidacion = null

                            val agendaFinal =
                                agenda
                                    .map { it.trim() }
                                    .filter { it.isNotBlank() }

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
                                sector = sector
                                    .trim()
                                    .ifBlank { null },
                                grupo = grupo
                                    .trim()
                                    .ifBlank { null },
                                manzana = manzana
                                    .trim()
                                    .ifBlank { null },
                                lote = lote
                                    .trim()
                                    .ifBlank { null },
                                fecha = fecha,
                                hora = hora
                                    .trim()
                                    .ifBlank { null },
                                primera_citacion =
                                    primeraCitacion,
                                segunda_citacion =
                                    segundaCitacion
                                        .trim()
                                        .ifBlank { null },
                                lugar = lugar.trim(),
                                descripcion =
                                    descripcion
                                        .trim()
                                        .ifBlank { null },
                                importancia = importancia,
                                agenda = agendaFinal,
                                plantilla_citacion = plantilla
                            )

                            onGuardar(request)
                        },
                        enabled = !creando,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdePrincipal,
                            contentColor = Blanco
                        )
                    ) {
                        if (creando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Blanco,
                                strokeWidth = 2.dp
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                if (modoEdicion) {
                                    "Guardando..."
                                } else {
                                    "Guardando..."
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Save,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text(
                                if (modoEdicion) {
                                    "Guardar cambios"
                                } else {
                                    "Guardar borrador"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                Spacer(
                    modifier = Modifier.height(16.dp)
                )
            }
        }
    }
}

@Composable
private fun FormCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = FondoTarjeta
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GrisBorde
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            content = content
        )
    }
}

@Composable
private fun SeccionTitulo(
    titulo: String,
    subtitulo: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = titulo,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        Text(
            text = subtitulo,
            fontSize = 12.sp,
            color = GrisClaro
        )
    }
}

@Composable
private fun CampoTexto(
    label: String,
    valor: String,
    onValorChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    obligatorio: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    leadingIcon: (@Composable (() -> Unit))? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onValorChange,
        modifier = modifier.fillMaxWidth(),
        label = {
            Text(
                if (obligatorio) {
                    "$label *"
                } else {
                    label
                }
            )
        },
        singleLine = singleLine,
        minLines = minLines,
        leadingIcon = leadingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VerdePrincipal,
            focusedLabelColor = VerdePrincipal,
            unfocusedBorderColor = GrisBorde
        )
    )
}

@Composable
private fun CampoDesplegable(
    label: String,
    valor: String,
    opciones: List<String>,
    expandido: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onSeleccionar: (String) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = onExpandChange
    ) {
        OutlinedTextField(
            value = valor,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = {
                Text(label)
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandido
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                focusedLabelColor = VerdePrincipal,
                unfocusedBorderColor = GrisBorde
            )
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                onExpandChange(false)
            }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = {
                        Text(opcion)
                    },
                    onClick = {
                        onSeleccionar(opcion)
                    }
                )
            }
        }
    }
}

@Composable
private fun CampoFecha(
    fecha: String,
    onSeleccionar: (String) -> Unit
) {
    val context = LocalContext.current
    val calendario = remember {
        Calendar.getInstance()
    }

    OutlinedButton(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    val formato =
                        SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        )

                    val seleccion =
                        Calendar.getInstance().apply {
                            set(
                                year,
                                month,
                                day
                            )
                        }

                    onSeleccionar(
                        formato.format(
                            seleccion.time
                        )
                    )
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GrisBorde
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.CalendarMonth,
            contentDescription = null,
            tint = VerdePrincipal
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Fecha",
                fontSize = 10.sp,
                color = GrisClaro
            )

            Text(
                text = formatearFechaVisible(fecha),
                fontSize = 14.sp,
                color = TextoPrincipal,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CampoHora(
    label: String,
    valor: String,
    onSeleccionar: (String) -> Unit,
    obligatorio: Boolean = false,
    opcional: Boolean = false
) {
    val context = LocalContext.current
    val calendario = remember {
        Calendar.getInstance()
    }

    OutlinedButton(
        onClick = {
            TimePickerDialog(
                context,
                { _, hour, minute ->
                    onSeleccionar(
                        String.format(
                            Locale.getDefault(),
                            "%02d:%02d",
                            hour,
                            minute
                        )
                    )
                },
                calendario.get(Calendar.HOUR_OF_DAY),
                calendario.get(Calendar.MINUTE),
                true
            ).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            GrisBorde
        )
    ) {
        Icon(
            imageVector = Icons.Outlined.Schedule,
            contentDescription = null,
            tint = VerdePrincipal
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = when {
                    obligatorio -> "$label *"
                    opcional -> "$label (opcional)"
                    else -> label
                },
                fontSize = 10.sp,
                color = GrisClaro
            )

            Text(
                text = valor.ifBlank {
                    "Seleccionar hora"
                },
                fontSize = 14.sp,
                color = if (valor.isBlank()) {
                    GrisClaro
                } else {
                    TextoPrincipal
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AgendaCampo(
    numero: Int,
    valor: String,
    puedeEliminar: Boolean,
    onValorChange: (String) -> Unit,
    onEliminar: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    VerdeSuave,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = numero.toString(),
                color = VerdeTexto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        OutlinedTextField(
            value = valor,
            onValueChange = onValorChange,
            modifier = Modifier.weight(1f),
            label = {
                Text("Punto de agenda")
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                unfocusedBorderColor = GrisBorde
            )
        )

        if (puedeEliminar) {
            IconButton(
                onClick = onEliminar
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Eliminar punto",
                    tint = Rojo
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
    onSeleccionar: (Int) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expandido,
        onExpandedChange = onExpandChange
    ) {
        OutlinedTextField(
            value = "Plantilla $plantilla",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            label = {
                Text("Plantilla de citación")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expandido
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Event,
                    contentDescription = null,
                    tint = VerdePrincipal
                )
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VerdePrincipal,
                unfocusedBorderColor = GrisBorde
            )
        )

        ExposedDropdownMenu(
            expanded = expandido,
            onDismissRequest = {
                onExpandChange(false)
            }
        ) {
            (1..7).forEach { numero ->
                DropdownMenuItem(
                    text = {
                        Text("Plantilla $numero")
                    },
                    onClick = {
                        onSeleccionar(numero)
                    }
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

    if (titulo.isBlank()) {
        return "Ingresa el título de la asamblea."
    }

    if (convoca.isBlank()) {
        return "Ingresa quién convoca la asamblea."
    }

    if (fecha.isBlank()) {
        return "Selecciona la fecha de la asamblea."
    }

    if (primeraCitacion.isBlank()) {
        return "Selecciona la primera citación."
    }

    if (lugar.isBlank()) {
        return "Ingresa el lugar de la asamblea."
    }

    if (segundaCitacion.isNotBlank()) {
        val primera =
            convertirMinutos(primeraCitacion)

        val segunda =
            convertirMinutos(segundaCitacion)

        if (
            primera != null &&
            segunda != null &&
            segunda <= primera
        ) {
            return "La segunda citación debe ser posterior a la primera."
        }
    }

    return null
}

private fun convertirMinutos(
    hora: String
): Int? {
    return try {
        val partes = hora.split(":")

        if (partes.size != 2) {
            null
        } else {
            partes[0].toInt() * 60 +
                    partes[1].toInt()
        }
    } catch (e: Exception) {
        null
    }
}

private fun formatearFechaVisible(
    fecha: String
): String {
    return try {
        val entrada =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val salida =
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            )

        val date = entrada.parse(fecha)

        if (date != null) {
            salida.format(date)
        } else {
            fecha
        }
    } catch (e: Exception) {
        fecha
    }
}