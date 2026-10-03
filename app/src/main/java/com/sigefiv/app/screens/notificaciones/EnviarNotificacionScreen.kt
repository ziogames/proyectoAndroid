package com.sigefiv.app.screens.notificaciones

import android.app.Activity

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.core.view.WindowInsetsControllerCompat

import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.EnviarNotificacionViewModel
import com.sigefiv.app.viewmodel.UsuarioViewModel


// ====================================================================
// PANTALLA ENVIAR NOTIFICACIÓN
// ====================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnviarNotificacionScreen(

    viewModel: EnviarNotificacionViewModel,

    usuarioViewModel:
    UsuarioViewModel,

    onBackClick:
        () -> Unit,

    onEnvioExitoso:
        (
        titulo: String,
        mensaje: String,
        tipo: String,
        destinatario: String,
        cantidad: Int
    ) -> Unit

) {

    // =================================================================
    // COLOR PRINCIPAL
    // =================================================================

    val colorPrincipal =
        SeasonalColors.primary(
            SeasonalTheme.getSeason()
        )


    // =================================================================
    // BARRA DE ESTADO
    // =================================================================

    val view =
        LocalView.current

    val context =
        LocalContext.current

    SideEffect {

        val window =
            (context as? Activity)?.window

        window?.let {

            it.statusBarColor =
                colorPrincipal.toArgb()

            WindowInsetsControllerCompat(
                it,
                view
            ).isAppearanceLightStatusBars = false
        }
    }


    // =================================================================
    // ESTADO DEL FORMULARIO
    // =================================================================

    var titulo by
    remember {
        mutableStateOf("")
    }

    var mensaje by
    remember {
        mutableStateOf("")
    }

    var tipo by
    remember {
        mutableStateOf("Aviso")
    }

    var destinatario by
    remember {
        mutableStateOf("Todos los vecinos")
    }

    var tipoExpandido by
    remember {
        mutableStateOf(false)
    }

    var destinatarioExpandido by
    remember {
        mutableStateOf(false)
    }

    var busquedaUsuario by
    remember {
        mutableStateOf("")
    }

    var usuariosSeleccionados by
    remember {
        mutableStateOf(setOf<Int>())
    }


    // =================================================================
    // ESTADOS DEL VIEWMODEL
    // =================================================================

    val enviando by
    viewModel.enviando.collectAsState()

    val mensajeEstado by
    viewModel.mensaje.collectAsState()

    val enviado by
    viewModel.enviado.collectAsState()

    val usuarioUiState by
    usuarioViewModel.uiState.collectAsState()


    // =================================================================
    // RESULTADO DEL ENVÍO
    // =================================================================

    LaunchedEffect(enviado) {

        if (enviado) {

            val cantidad =
                when (destinatario) {

                    "Seleccionar usuarios" ->
                        usuariosSeleccionados.size

                    else ->
                        0
                }

            onEnvioExitoso(
                titulo,
                mensaje,
                tipo,
                destinatario,
                cantidad
            )
        }
    }


    // =================================================================
    // CARGAR USUARIOS
    // =================================================================

    LaunchedEffect(destinatario) {

        if (
            destinatario ==
            "Seleccionar usuarios"
        ) {

            usuarioViewModel
                .cargarUsuarios()
        }
    }


    // =================================================================
    // FILTRAR USUARIOS
    // =================================================================

    val usuariosFiltrados =
        usuarioUiState.usuarios.filter { usuario ->

            if (
                busquedaUsuario.isBlank()
            ) {

                true

            } else {

                val texto =
                    busquedaUsuario
                        .trim()
                        .lowercase()

                usuario.name
                    .lowercase()
                    .contains(texto) ||

                        usuario.email
                            .lowercase()
                            .contains(texto)
            }
        }


    // =================================================================
    // ESTRUCTURA PRINCIPAL
    // =================================================================

    Column(

        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme
                        .colorScheme
                        .background
                )
    ) {


        // =============================================================
        // ENCABEZADO
        // =============================================================

        Surface(

            modifier =
                Modifier.fillMaxWidth(),

            color =
                colorPrincipal,

            shadowElevation = 4.dp
        ) {

            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(
                            horizontal = 8.dp,
                            vertical = 10.dp
                        ),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {


                IconButton(

                    onClick =
                        onBackClick,

                    modifier =
                        Modifier.size(44.dp)
                ) {

                    Icon(

                        imageVector =
                            Icons.Default.ArrowBack,

                        contentDescription =
                            "Volver",

                        tint =
                            MaterialTheme
                                .colorScheme
                                .onPrimary,

                        modifier =
                            Modifier.size(24.dp)
                    )
                }


                Spacer(
                    modifier =
                        Modifier.width(4.dp)
                )


                Column {

                    Text(

                        text =
                            "SIGEFIV",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimary,

                        fontSize = 19.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Text(

                        text =
                            "Centro de Notificaciones",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimary
                                .copy(
                                    alpha = 0.85f
                                ),

                        fontSize = 13.sp
                    )
                }
            }
        }


        // =============================================================
        // CONTENIDO
        // =============================================================

        Column(

            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(14.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {


            // =========================================================
            // TARJETA PRINCIPAL
            // =========================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(22.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surface
                    ),

                border =
                    BorderStroke(
                        1.dp,
                        MaterialTheme
                            .colorScheme
                            .outlineVariant
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 3.dp
                    )
            ) {

                Column(

                    modifier =
                        Modifier.padding(18.dp)
                ) {


                    // =================================================
                    // ENCABEZADO
                    // =================================================

                    Row(

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        Box(

                            modifier =
                                Modifier
                                    .size(50.dp)
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .secondaryContainer,
                                        RoundedCornerShape(15.dp)
                                    ),

                            contentAlignment =
                                Alignment.Center
                        ) {

                            Icon(

                                imageVector =
                                    Icons.Default
                                        .NotificationsActive,

                                contentDescription =
                                    null,

                                tint =
                                    colorPrincipal,

                                modifier =
                                    Modifier.size(27.dp)
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.width(13.dp)
                        )


                        Column {

                            Text(

                                text =
                                    "Nueva notificación",

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurface,

                                fontSize = 19.sp,

                                fontWeight =
                                    FontWeight.Bold
                            )


                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )


                            Text(

                                text =
                                    "Transmite avisos importantes a la comunidad",

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant,

                                fontSize = 12.sp
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(20.dp)
                    )


                    // =================================================
                    // TÍTULO
                    // =================================================

                    Text(

                        text =
                            "Título del comunicado *",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        fontSize = 13.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    OutlinedTextField(

                        value =
                            titulo,

                        onValueChange = {
                            titulo = it
                        },

                        modifier =
                            Modifier.fillMaxWidth(),

                        placeholder = {

                            Text(
                                "Ej. Reunión extraordinaria de vecinos"
                            )
                        },

                        singleLine = true,

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            textFieldColorsCustom()
                    )


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    // =================================================
                    // MENSAJE
                    // =================================================

                    Text(

                        text =
                            "Mensaje detallado *",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        fontSize = 13.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    OutlinedTextField(

                        value =
                            mensaje,

                        onValueChange = {
                            mensaje =
                                it.take(500)
                        },

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(140.dp),

                        placeholder = {

                            Text(
                                "Escribe aquí el contenido del mensaje..."
                            )
                        },

                        shape =
                            RoundedCornerShape(14.dp),

                        colors =
                            textFieldColorsCustom()
                    )


                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    top = 4.dp,
                                    end = 2.dp
                                ),

                        horizontalArrangement =
                            Arrangement.End
                    ) {

                        Text(

                            text =
                                "${mensaje.length}/500",

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant,

                            fontSize = 11.sp
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    // =================================================
                    // CATEGORÍA
                    // =================================================

                    Text(

                        text =
                            "Categoría o tipo *",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        fontSize = 13.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    ExposedDropdownMenuBox(

                        expanded =
                            tipoExpandido,

                        onExpandedChange = {

                            tipoExpandido =
                                !tipoExpandido
                        }
                    ) {

                        OutlinedTextField(

                            value =
                                tipo,

                            onValueChange = {},

                            readOnly = true,

                            leadingIcon = {

                                SelectorIcon(
                                    icono =
                                        when (tipo) {

                                            "Asamblea" ->
                                                Icons.Default.Groups

                                            "Financiero" ->
                                                Icons.Default
                                                    .AccountBalanceWallet

                                            "Sistema" ->
                                                Icons.Default
                                                    .NotificationsActive

                                            else ->
                                                Icons.Default.Campaign
                                        }
                                )
                            },

                            trailingIcon = {

                                Icon(

                                    imageVector =
                                        if (
                                            tipoExpandido
                                        ) {

                                            Icons.Default.ExpandLess

                                        } else {

                                            Icons.Default.ExpandMore
                                        },

                                    contentDescription =
                                        null,

                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                textFieldColorsCustom()
                        )


                        DropdownMenu(

                            expanded =
                                tipoExpandido,

                            onDismissRequest = {

                                tipoExpandido =
                                    false
                            },

                            modifier =
                                Modifier.width(
                                    320.dp
                                ),

                            shape =
                                RoundedCornerShape(
                                    17.dp
                                ),

                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surface,

                            tonalElevation =
                                3.dp,

                            shadowElevation =
                                8.dp
                        ) {

                            OpcionSelector(
                                texto = "Aviso",
                                seleccionado =
                                    tipo == "Aviso",
                                icono =
                                    Icons.Default.Campaign,
                                onClick = {
                                    tipo = "Aviso"
                                    tipoExpandido =
                                        false
                                }
                            )


                            OpcionSelector(
                                texto = "Asamblea",
                                seleccionado =
                                    tipo == "Asamblea",
                                icono =
                                    Icons.Default.Groups,
                                onClick = {
                                    tipo = "Asamblea"
                                    tipoExpandido =
                                        false
                                }
                            )


                            OpcionSelector(
                                texto = "Financiero",
                                seleccionado =
                                    tipo == "Financiero",
                                icono =
                                    Icons.Default
                                        .AccountBalanceWallet,
                                onClick = {
                                    tipo = "Financiero"
                                    tipoExpandido =
                                        false
                                }
                            )


                            OpcionSelector(
                                texto = "Sistema",
                                seleccionado =
                                    tipo == "Sistema",
                                icono =
                                    Icons.Default
                                        .NotificationsActive,
                                onClick = {
                                    tipo = "Sistema"
                                    tipoExpandido =
                                        false
                                }
                            )
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    // =================================================
                    // DESTINATARIO
                    // =================================================

                    Text(

                        text =
                            "Enviar a *",

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurface,

                        fontSize = 13.sp,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Spacer(
                        modifier =
                            Modifier.height(6.dp)
                    )


                    ExposedDropdownMenuBox(

                        expanded =
                            destinatarioExpandido,

                        onExpandedChange = {

                            destinatarioExpandido =
                                !destinatarioExpandido
                        }
                    ) {

                        OutlinedTextField(

                            value =
                                destinatario,

                            onValueChange = {},

                            readOnly = true,

                            leadingIcon = {

                                SelectorIcon(

                                    icono =
                                        when (
                                            destinatario
                                        ) {

                                            "Seleccionar usuarios" ->
                                                Icons.Default.Person

                                            else ->
                                                Icons.Default.Groups
                                        }
                                )
                            },

                            trailingIcon = {

                                Icon(

                                    imageVector =
                                        if (
                                            destinatarioExpandido
                                        ) {

                                            Icons.Default.ExpandLess

                                        } else {

                                            Icons.Default.ExpandMore
                                        },

                                    contentDescription =
                                        null,

                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            },

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                textFieldColorsCustom()
                        )


                        DropdownMenu(

                            expanded =
                                destinatarioExpandido,

                            onDismissRequest = {

                                destinatarioExpandido =
                                    false
                            },

                            modifier =
                                Modifier.width(
                                    320.dp
                                ),

                            shape =
                                RoundedCornerShape(
                                    17.dp
                                ),

                            containerColor =
                                MaterialTheme
                                    .colorScheme
                                    .surface,

                            tonalElevation =
                                3.dp,

                            shadowElevation =
                                8.dp
                        ) {


                            OpcionSelector(

                                texto =
                                    "Todos los vecinos",

                                seleccionado =
                                    destinatario ==
                                            "Todos los vecinos",

                                icono =
                                    Icons.Default.Groups,

                                onClick = {

                                    destinatario =
                                        "Todos los vecinos"

                                    destinatarioExpandido =
                                        false

                                    busquedaUsuario =
                                        ""

                                    usuariosSeleccionados =
                                        emptySet()
                                }
                            )


                            OpcionSelector(

                                texto =
                                    "Miembros de la directiva",

                                seleccionado =
                                    destinatario ==
                                            "Miembros de la directiva",

                                icono =
                                    Icons.Default.Groups,

                                onClick = {

                                    destinatario =
                                        "Miembros de la directiva"

                                    destinatarioExpandido =
                                        false

                                    busquedaUsuario =
                                        ""

                                    usuariosSeleccionados =
                                        emptySet()
                                }
                            )


                            OpcionSelector(

                                texto =
                                    "Seleccionar usuarios",

                                seleccionado =
                                    destinatario ==
                                            "Seleccionar usuarios",

                                icono =
                                    Icons.Default.Person,

                                onClick = {

                                    destinatario =
                                        "Seleccionar usuarios"

                                    destinatarioExpandido =
                                        false
                                }
                            )
                        }
                    }


                    // =================================================
                    // SELECCIÓN DE USUARIOS
                    // =================================================

                    if (
                        destinatario ==
                        "Seleccionar usuarios"
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )


                        Text(

                            text =
                                "Lista de destinatarios",

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurface,

                            fontSize = 13.sp,

                            fontWeight =
                                FontWeight.Bold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )


                        OutlinedTextField(

                            value =
                                busquedaUsuario,

                            onValueChange = {
                                busquedaUsuario =
                                    it
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            placeholder = {

                                Text(
                                    "Buscar por nombre o correo..."
                                )
                            },

                            leadingIcon = {

                                Icon(

                                    imageVector =
                                        Icons.Default.Search,

                                    contentDescription =
                                        null,

                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurfaceVariant
                                )
                            },

                            singleLine = true,

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                textFieldColorsCustom()
                        )


                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        Text(

                            text =
                                "${usuariosSeleccionados.size} usuario(s) seleccionado(s)",

                            color =
                                colorPrincipal,

                            fontSize = 12.sp,

                            fontWeight =
                                FontWeight.SemiBold
                        )


                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )


                        Card(

                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(210.dp),

                            shape =
                                RoundedCornerShape(14.dp),

                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .background
                                ),

                            border =
                                BorderStroke(
                                    1.dp,
                                    MaterialTheme
                                        .colorScheme
                                        .outlineVariant
                                )
                        ) {

                            when {

                                // =====================================
                                // CARGANDO
                                // =====================================

                                usuarioUiState.cargando -> {

                                    Box(

                                        modifier =
                                            Modifier.fillMaxSize(),

                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        CircularProgressIndicator(

                                            color =
                                                colorPrincipal
                                        )
                                    }
                                }


                                // =====================================
                                // ERROR
                                // =====================================

                                usuarioUiState.error != null -> {

                                    Box(

                                        modifier =
                                            Modifier.fillMaxSize(),

                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Text(

                                            text =
                                                usuarioUiState.error
                                                    ?: "Error al cargar usuarios.",

                                            color =
                                                MaterialTheme
                                                    .colorScheme
                                                    .error,

                                            fontSize = 13.sp
                                        )
                                    }
                                }


                                // =====================================
                                // SIN RESULTADOS
                                // =====================================

                                usuariosFiltrados.isEmpty() -> {

                                    Box(

                                        modifier =
                                            Modifier.fillMaxSize(),

                                        contentAlignment =
                                            Alignment.Center
                                    ) {

                                        Column(

                                            horizontalAlignment =
                                                Alignment.CenterHorizontally
                                        ) {

                                            Icon(

                                                imageVector =
                                                    Icons.Default.Person,

                                                contentDescription =
                                                    null,

                                                tint =
                                                    MaterialTheme
                                                        .colorScheme
                                                        .onSurfaceVariant,

                                                modifier =
                                                    Modifier.size(28.dp)
                                            )


                                            Spacer(
                                                modifier =
                                                    Modifier.height(6.dp)
                                            )


                                            Text(

                                                text =
                                                    "No se encontraron usuarios.",

                                                color =
                                                    MaterialTheme
                                                        .colorScheme
                                                        .onSurfaceVariant,

                                                fontSize = 13.sp
                                            )
                                        }
                                    }
                                }


                                // =====================================
                                // USUARIOS
                                // =====================================

                                else -> {

                                    LazyColumn(

                                        modifier =
                                            Modifier.fillMaxSize()
                                    ) {

                                        items(

                                            items =
                                                usuariosFiltrados,

                                            key = {
                                                it.id
                                            }

                                        ) { usuario ->

                                            val seleccionado =
                                                usuariosSeleccionados
                                                    .contains(
                                                        usuario.id
                                                    )


                                            Row(

                                                modifier =
                                                    Modifier
                                                        .fillMaxWidth()
                                                        .clickable {

                                                            usuariosSeleccionados =
                                                                if (
                                                                    seleccionado
                                                                ) {

                                                                    usuariosSeleccionados -
                                                                            usuario.id

                                                                } else {

                                                                    usuariosSeleccionados +
                                                                            usuario.id
                                                                }
                                                        }
                                                        .padding(
                                                            horizontal = 8.dp,
                                                            vertical = 5.dp
                                                        ),

                                                verticalAlignment =
                                                    Alignment.CenterVertically
                                            ) {


                                                Checkbox(

                                                    checked =
                                                        seleccionado,

                                                    onCheckedChange = {
                                                            marcado ->

                                                        usuariosSeleccionados =
                                                            if (
                                                                marcado
                                                            ) {

                                                                usuariosSeleccionados +
                                                                        usuario.id

                                                            } else {

                                                                usuariosSeleccionados -
                                                                        usuario.id
                                                            }
                                                    },

                                                    colors =
                                                        CheckboxDefaults.colors(

                                                            checkedColor =
                                                                colorPrincipal
                                                        )
                                                )


                                                Column(
                                                    modifier =
                                                        Modifier.weight(
                                                            1f
                                                        )
                                                ) {

                                                    Text(

                                                        text =
                                                            usuario.name,

                                                        color =
                                                            MaterialTheme
                                                                .colorScheme
                                                                .onSurface,

                                                        fontSize = 14.sp,

                                                        fontWeight =
                                                            FontWeight.Medium
                                                    )


                                                    Text(

                                                        text =
                                                            usuario.email,

                                                        color =
                                                            MaterialTheme
                                                                .colorScheme
                                                                .onSurfaceVariant,

                                                        fontSize = 11.sp
                                                    )


                                                    Text(

                                                        text =
                                                            usuario.rolPrincipal,

                                                        color =
                                                            colorPrincipal,

                                                        fontSize = 10.sp,

                                                        fontWeight =
                                                            FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }


                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )


                    // =================================================
                    // INFORMACIÓN
                    // =================================================

                    Card(

                        modifier =
                            Modifier.fillMaxWidth(),

                        shape =
                            RoundedCornerShape(15.dp),

                        colors =
                            CardDefaults.cardColors(

                                containerColor =
                                    MaterialTheme
                                        .colorScheme
                                        .tertiaryContainer
                            )
                    ) {

                        Row(

                            modifier =
                                Modifier.padding(14.dp),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Box(

                                modifier =
                                    Modifier
                                        .size(38.dp)
                                        .background(
                                            MaterialTheme
                                                .colorScheme
                                                .surface
                                                .copy(
                                                    alpha = 0.65f
                                                ),
                                            CircleShape
                                        ),

                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(

                                    imageVector =
                                        Icons.Default.Info,

                                    contentDescription =
                                        null,

                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .onTertiaryContainer,

                                    modifier =
                                        Modifier.size(21.dp)
                                )
                            }


                            Spacer(
                                modifier =
                                    Modifier.width(12.dp)
                            )


                            Text(

                                text =

                                    when (
                                        destinatario
                                    ) {

                                        "Miembros de la directiva" ->

                                            "Se enviará la notificación exclusivamente a los directivos con alertas activas."

                                        "Seleccionar usuarios" ->

                                            "Se enviará la notificación de forma personalizada a los destinatarios marcados."

                                        else ->

                                            "Se enviará la notificación de difusión masiva a todos los vecinos registrados."
                                    },

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onTertiaryContainer,

                                fontSize = 12.sp,

                                lineHeight = 18.sp
                            )
                        }
                    }


                    // =================================================
                    // MENSAJE DE ESTADO
                    // =================================================

                    if (
                        mensajeEstado != null
                    ) {

                        Spacer(
                            modifier =
                                Modifier.height(8.dp)
                        )


                        Text(

                            text =
                                mensajeEstado ?: "",

                            color =
                                if (enviado)
                                    colorPrincipal
                                else
                                    MaterialTheme
                                        .colorScheme
                                        .error,

                            fontSize = 13.sp,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )
        }


        // =============================================================
        // BARRA INFERIOR
        // =============================================================

        Surface(

            modifier =
                Modifier.fillMaxWidth(),

            color =
                MaterialTheme
                    .colorScheme
                    .surface,

            shadowElevation = 8.dp
        ) {

            val puedeEnviar =
                !enviando &&
                        titulo.isNotBlank() &&
                        mensaje.isNotBlank() &&
                        (
                                destinatario !=
                                        "Seleccionar usuarios" ||
                                        usuariosSeleccionados.isNotEmpty()
                                )


            Row(

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 14.dp,
                            vertical = 10.dp
                        ),

                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {


                // =====================================================
                // CANCELAR
                // =====================================================

                OutlinedButton(

                    onClick =
                        onBackClick,

                    modifier =
                        Modifier
                            .weight(0.38f)
                            .height(50.dp),

                    enabled =
                        !enviando,

                    shape =
                        RoundedCornerShape(14.dp),

                    border =
                        BorderStroke(
                            1.5.dp,
                            MaterialTheme
                                .colorScheme
                                .outlineVariant
                        ),

                    colors =
                        ButtonDefaults
                            .outlinedButtonColors(
                                contentColor =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                ) {

                    Text(

                        text =
                            "Cancelar",

                        fontSize = 14.sp,

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }


                // =====================================================
                // ENVIAR
                // =====================================================

                Button(

                    onClick = {

                        val destino =

                            when (
                                destinatario
                            ) {

                                "Todos los vecinos" ->
                                    "todos"

                                "Miembros de la directiva" ->
                                    "directiva"

                                else ->
                                    "usuarios"
                            }


                        viewModel.enviar(

                            titulo =
                                titulo,

                            mensaje =
                                mensaje,

                            tipo =
                                tipo.lowercase(),

                            destinatario =
                                destino,

                            usuarioIds =
                                usuariosSeleccionados
                                    .toList()
                        )
                    },

                    modifier =
                        Modifier
                            .weight(0.62f)
                            .height(50.dp),

                    enabled =
                        puedeEnviar,

                    shape =
                        RoundedCornerShape(14.dp),

                    colors =
                        ButtonDefaults.buttonColors(

                            containerColor =
                                colorPrincipal,

                            disabledContainerColor =
                                colorPrincipal
                                    .copy(
                                        alpha = 0.4f
                                    ),

                            contentColor =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary
                        )
                ) {

                    if (
                        enviando
                    ) {

                        CircularProgressIndicator(

                            modifier =
                                Modifier.size(20.dp),

                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimary,

                            strokeWidth = 2.dp
                        )


                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )


                        Text(

                            text =
                                "Enviando...",

                            fontSize = 14.sp
                        )

                    } else {

                        Icon(

                            imageVector =
                                Icons.Default.Send,

                            contentDescription =
                                null,

                            modifier =
                                Modifier.size(18.dp)
                        )


                        Spacer(
                            modifier =
                                Modifier.width(8.dp)
                        )


                        Text(

                            text =
                                "Enviar aviso",

                            fontSize = 14.sp,

                            fontWeight =
                                FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


// ====================================================================
// ICONO DEL SELECTOR
// ====================================================================

@Composable
private fun SelectorIcon(
    icono: ImageVector
) {

    val colorPrincipal =
        SeasonalColors.primary(
            SeasonalTheme.getSeason()
        )


    Box(

        modifier =
            Modifier
                .size(34.dp)
                .background(

                    MaterialTheme
                        .colorScheme
                        .secondaryContainer,

                    RoundedCornerShape(9.dp)
                ),

        contentAlignment =
            Alignment.Center
    ) {

        Icon(

            imageVector =
                icono,

            contentDescription =
                null,

            tint =
                colorPrincipal,

            modifier =
                Modifier.size(19.dp)
        )
    }
}


// ====================================================================
// OPCIÓN DEL SELECTOR
// ====================================================================

@Composable
private fun OpcionSelector(

    texto: String,

    seleccionado: Boolean,

    icono: ImageVector,

    onClick: () -> Unit
) {

    val colorPrincipal =
        SeasonalColors.primary(
            SeasonalTheme.getSeason()
        )


    DropdownMenuItem(

        text = {

            Text(

                text =
                    texto,

                fontSize = 14.sp,

                fontWeight =
                    if (seleccionado)
                        FontWeight.SemiBold
                    else
                        FontWeight.Normal,

                color =
                    if (seleccionado)
                        colorPrincipal
                    else
                        MaterialTheme
                            .colorScheme
                            .onSurface
            )
        },


        leadingIcon = {

            Box(

                modifier =
                    Modifier
                        .size(36.dp)
                        .background(

                            if (seleccionado)

                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer

                            else

                                MaterialTheme
                                    .colorScheme
                                    .surfaceVariant,

                            RoundedCornerShape(10.dp)
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(

                    imageVector =
                        icono,

                    contentDescription =
                        null,

                    tint =
                        if (seleccionado)

                            colorPrincipal

                        else

                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant,

                    modifier =
                        Modifier.size(19.dp)
                )
            }
        },


        trailingIcon = {

            if (
                seleccionado
            ) {

                Icon(

                    imageVector =
                        Icons.Default.Check,

                    contentDescription =
                        "Seleccionado",

                    tint =
                        colorPrincipal,

                    modifier =
                        Modifier.size(20.dp)
                )
            }
        },


        onClick =
            onClick,


        modifier =
            Modifier
                .padding(
                    horizontal = 6.dp,
                    vertical = 2.dp
                )
                .background(

                    if (seleccionado)

                        MaterialTheme
                            .colorScheme
                            .secondaryContainer

                    else

                        Color.Transparent,

                    RoundedCornerShape(12.dp)
                )
    )
}


// ====================================================================
// ESTILOS PERSONALIZADOS
// ====================================================================

@Composable
private fun textFieldColorsCustom() =

    OutlinedTextFieldDefaults.colors(

        focusedTextColor =
            MaterialTheme
                .colorScheme
                .onSurface,

        unfocusedTextColor =
            MaterialTheme
                .colorScheme
                .onSurface,

        focusedPlaceholderColor =
            MaterialTheme
                .colorScheme
                .onSurfaceVariant,

        unfocusedPlaceholderColor =
            MaterialTheme
                .colorScheme
                .onSurfaceVariant,

        focusedLabelColor =
            SeasonalColors.primary(
                SeasonalTheme.getSeason()
            ),

        unfocusedLabelColor =
            MaterialTheme
                .colorScheme
                .onSurfaceVariant,

        focusedBorderColor =
            SeasonalColors.primary(
                SeasonalTheme.getSeason()
            ),

        unfocusedBorderColor =
            MaterialTheme
                .colorScheme
                .outlineVariant,

        cursorColor =
            SeasonalColors.primary(
                SeasonalTheme.getSeason()
            )
    )