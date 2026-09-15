@file:OptIn(ExperimentalMaterial3Api::class)
package com.sigefiv.app.screens.perfil

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/*
|--------------------------------------------------------------------------
| COLORES CORPORATIVOS SIGEFIV
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdeSuave = Color(0xFFDCFCE7)
private val Blanco = Color(0xFFFFFFFF)
private val TextoOscuro = Color(0xFF0F172A)
private val TextoGris = Color(0xFF64748B)

private val FondoIconoVerde = Color(0xFFDCFCE7)
private val FondoIconoAzul = Color(0xFFDBEAFE)
private val FondoIconoNaranja = Color(0xFFFFEDD5)
private val FondoIconoMorado = Color(0xFFF3E8FF)

private val IconoVerde = Color(0xFF15803D)
private val IconoAzul = Color(0xFF2563EB)
private val IconoNaranja = Color(0xFFEA580C)
private val IconoMorado = Color(0xFF9333EA)

@Composable
fun PerfilScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {},

    nombre: String = "Freddy Antayhua Saavedra",
    seudonimo: String? = null,
    email: String = "admin@sigefiv.com",
    telefono: String? = null,
    dni: String? = null,
    direccion: String? = null,
    rol: String? = "Administrador",
    foto: String? = null,
    metodoAcceso: String? = "Google",

    /*
     * Callbacks para conectar con PerfilViewModel.
     */
    onGuardarPerfil: (
        seudonimo: String?
    ) -> Unit = {},

    onSeleccionarFoto: (
        Uri
    ) -> Unit = {},

    guardando: Boolean = false,
    subiendoFoto: Boolean = false
) {
    val colorPrincipal = SeasonalColors.primary(
        SeasonalTheme.getSeason()
    )

    var modoEdicion by remember {
        mutableStateOf(false)
    }

    var seudonimoEditado by remember(
        seudonimo
    ) {
        mutableStateOf(seudonimo ?: "")
    }

    var fotoSeleccionada by remember {
        mutableStateOf<Uri?>(null)
    }

    val selectorFoto =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->

            if (uri != null) {
                fotoSeleccionada = uri
            }
        }

    LaunchedEffect(
        seudonimo,
        modoEdicion
    ) {
        if (!modoEdicion) {
            seudonimoEditado =
                seudonimo ?: ""
        }
    }

    val telefonoMostrar =
        telefono
            ?.takeIf { it.isNotBlank() }
            ?: "No registrado"

    val dniMostrar =
        dni
            ?.takeIf { it.isNotBlank() }
            ?: "No registrado"

    val direccionMostrar =
        direccion
            ?.takeIf { it.isNotBlank() }
            ?: "No registrada"

    val rolMostrar =
        rol
            ?.takeIf { it.isNotBlank() }
            ?: "Consulta"

    val metodoMostrar =
        metodoAcceso
            ?.takeIf { it.isNotBlank() }
            ?: "Correo y contraseña"

    val esGoogle =
        metodoMostrar.equals(
            "Google",
            ignoreCase = true
        )

    val inicialNombre =
        nombre
            .trim()
            .firstOrNull()
            ?.uppercaseChar()
            ?.toString()
            ?: "U"

    Scaffold(
        containerColor = FondoSIGEFIV,

        topBar = {

            TopAppBar(

                title = {
                    Column {

                        Text(
                            text = "Mi Cuenta",
                            color = Blanco,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )

                        Text(
                            text = "Información de tu cuenta SIGEFIV",
                            color = Blanco.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Blanco
                        )
                    }
                },

                actions = {

                    if (!modoEdicion) {

                        IconButton(
                            onClick = {
                                seudonimoEditado =
                                    seudonimo ?: ""

                                fotoSeleccionada = null

                                modoEdicion = true

                                onEditClick()
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Edit,
                                contentDescription =
                                    "Editar Perfil",
                                tint = Blanco
                            )
                        }

                    } else {

                        IconButton(
                            onClick = {

                                modoEdicion = false
                                fotoSeleccionada = null

                                seudonimoEditado =
                                    seudonimo ?: ""
                            }
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.Close,
                                contentDescription =
                                    "Cancelar edición",
                                tint = Blanco
                            )
                        }
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = colorPrincipal
                    )
            )
        },

        bottomBar = {

            BarraInferiorPerfil(
                onInicioClick =
                    onInicioClick,

                onAsambleasClick =
                    onAsambleasClick,

                onPeriodosClick =
                    onPeriodosClick,

                onMiCuentaClick =
                    onMiCuentaClick
            )
        }

    ) { innerPadding ->

        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .background(FondoSIGEFIV)
                    .padding(innerPadding)
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
        ) {

            /*
            |--------------------------------------------------------------------------
            | TARJETA PRINCIPAL DE PERFIL
            |--------------------------------------------------------------------------
            */

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(24.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.Transparent
                    )
            ) {

                Box(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(
                                brush =
                                    Brush.horizontalGradient(
                                        colors =
                                            listOf(
                                                colorPrincipal,
                                                colorPrincipal.copy(
                                                    alpha = 0.85f
                                                )
                                            )
                                    )
                            )
                            .padding(20.dp)
                ) {

                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        /*
                        |--------------------------------------------------------------------------
                        | AVATAR
                        |--------------------------------------------------------------------------
                        */

                        Box {

                            Surface(

                                modifier =
                                    Modifier.size(80.dp),

                                shape =
                                    CircleShape,

                                color =
                                    Color.Transparent,

                                border =
                                    BorderStroke(
                                        3.dp,
                                        Blanco
                                    )
                            ) {

                                Box(
                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .background(
                                                Color(0xFF14532D)
                                            ),

                                    contentAlignment =
                                        Alignment.Center
                                ) {

                                    when {
                                        fotoSeleccionada != null -> {

                                            ImagenDesdeUri(
                                                uri =
                                                    fotoSeleccionada!!,
                                                modifier =
                                                    Modifier
                                                        .fillMaxSize()
                                                        .clip(
                                                            CircleShape
                                                        )
                                            )
                                        }

                                        !foto.isNullOrBlank() -> {

                                            ImagenPerfil(
                                                foto = foto,
                                                modifier =
                                                    Modifier
                                                        .fillMaxSize()
                                                        .clip(
                                                            CircleShape
                                                        )
                                            )
                                        }

                                        else -> {

                                            Text(
                                                text =
                                                    inicialNombre,

                                                color =
                                                    Blanco,

                                                fontSize =
                                                    34.sp,

                                                fontWeight =
                                                    FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            /*
                            |--------------------------------------------------------------------------
                            | INDICADOR ACTIVO
                            |--------------------------------------------------------------------------
                            */

                            Box(

                                modifier =
                                    Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(Blanco)
                                        .padding(3.dp)
                                        .align(
                                            Alignment.BottomEnd
                                        )
                            ) {

                                Box(

                                    modifier =
                                        Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(
                                                Color(0xFF22C55E)
                                            )
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.width(16.dp)
                        )

                        /*
                        |--------------------------------------------------------------------------
                        | DATOS DEL PERFIL
                        |--------------------------------------------------------------------------
                        */

                        Column {

                            Text(
                                text = nombre,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Blanco
                            )

                            if (
                                !seudonimo
                                    .isNullOrBlank()
                            ) {

                                Spacer(
                                    modifier =
                                        Modifier.height(2.dp)
                                )

                                Text(
                                    text =
                                        "@$seudonimo",

                                    fontSize =
                                        13.sp,

                                    color =
                                        Blanco.copy(
                                            alpha = 0.9f
                                        )
                                )
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(2.dp)
                            )

                            Text(
                                text = email,
                                fontSize = 13.sp,
                                color =
                                    Blanco.copy(
                                        alpha = 0.9f
                                    )
                            )

                            Spacer(
                                modifier =
                                    Modifier.height(10.dp)
                            )

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Surface(

                                    shape =
                                        RoundedCornerShape(20.dp),

                                    color =
                                        Blanco.copy(
                                            alpha = 0.95f
                                        )
                                ) {

                                    Row(

                                        modifier =
                                            Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 4.dp
                                            ),

                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Outlined.Security,

                                            contentDescription =
                                                null,

                                            tint =
                                                colorPrincipal,

                                            modifier =
                                                Modifier.size(14.dp)
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(4.dp)
                                        )

                                        Text(
                                            text =
                                                rolMostrar,

                                            fontSize =
                                                12.sp,

                                            fontWeight =
                                                FontWeight.SemiBold,

                                            color =
                                                colorPrincipal
                                        )
                                    }
                                }

                                Spacer(
                                    modifier =
                                        Modifier.width(8.dp)
                                )

                                Surface(

                                    shape =
                                        RoundedCornerShape(20.dp),

                                    color = Blanco
                                ) {

                                    Row(

                                        modifier =
                                            Modifier.padding(
                                                horizontal = 10.dp,
                                                vertical = 4.dp
                                            ),

                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        if (esGoogle) {

                                            Text(
                                                text = "G",
                                                fontSize = 13.sp,
                                                fontWeight =
                                                    FontWeight.Bold,
                                                color =
                                                    Color(0xFFEA4335)
                                            )

                                            Spacer(
                                                modifier =
                                                    Modifier.width(4.dp)
                                            )
                                        }

                                        Text(
                                            text =
                                                if (esGoogle)
                                                    "Google"
                                                else
                                                    "Clave",

                                            fontSize = 12.sp,

                                            fontWeight =
                                                FontWeight.SemiBold,

                                            color =
                                                TextoOscuro
                                        )
                                    }
                                }
                            }

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Box(

                                    modifier =
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Color(0xFF4ADE80)
                                            )
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(6.dp)
                                )

                                Text(
                                    text =
                                        "USUARIO ACTIVO",

                                    fontSize =
                                        11.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Blanco.copy(
                                            alpha = 0.95f
                                        )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            /*
            |--------------------------------------------------------------------------
            | EDICIÓN DEL PERFIL
            |--------------------------------------------------------------------------
            */

            if (modoEdicion) {

                Card(

                    modifier =
                        Modifier.fillMaxWidth(),

                    shape =
                        RoundedCornerShape(20.dp),

                    colors =
                        CardDefaults.cardColors(
                            containerColor =
                                FondoTarjeta
                        ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                ) {

                    Column(

                        modifier =
                            Modifier.padding(16.dp)
                    ) {

                        Text(
                            text =
                                "Editar perfil",

                            fontSize =
                                17.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoOscuro
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Personaliza cómo apareces públicamente en SIGEFIV.",

                            fontSize =
                                12.sp,

                            color =
                                TextoGris
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        /*
                        |--------------------------------------------------------------------------
                        | SEUDÓNIMO
                        |--------------------------------------------------------------------------
                        */

                        OutlinedTextField(

                            value =
                                seudonimoEditado,

                            onValueChange = {
                                if (it.length <= 100) {
                                    seudonimoEditado = it
                                }
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            label = {
                                Text("Seudónimo")
                            },

                            placeholder = {
                                Text("Ejemplo: Freddy")
                            },

                            singleLine = true,

                            leadingIcon = {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.Person,
                                    contentDescription =
                                        null
                                )
                            }
                        )

                        Spacer(
                            modifier =
                                Modifier.height(6.dp)
                        )

                        Text(
                            text =
                                "Será el nombre que verán los vecinos en las áreas públicas.",

                            fontSize =
                                11.sp,

                            color =
                                TextoGris
                        )

                        Spacer(
                            modifier =
                                Modifier.height(16.dp)
                        )

                        /*
                        |--------------------------------------------------------------------------
                        | FOTO
                        |--------------------------------------------------------------------------
                        */

                        Button(

                            onClick = {
                                selectorFoto.launch(
                                    "image/*"
                                )
                            },

                            modifier =
                                Modifier.fillMaxWidth(),

                            shape =
                                RoundedCornerShape(12.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        colorPrincipal
                                )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Outlined.PhotoCamera,

                                contentDescription =
                                    null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(8.dp)
                            )

                            Text(
                                text =
                                    if (
                                        fotoSeleccionada != null
                                    )
                                        "Cambiar foto"
                                    else
                                        "Seleccionar foto"
                            )
                        }

                        if (
                            fotoSeleccionada != null
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(8.dp)
                            )

                            Text(
                                text =
                                    "Nueva foto seleccionada",

                                fontSize =
                                    12.sp,

                                color =
                                    colorPrincipal,

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }

                        Spacer(
                            modifier =
                                Modifier.height(20.dp)
                        )

                        /*
                        |--------------------------------------------------------------------------
                        | BOTONES
                        |--------------------------------------------------------------------------
                        */

                        Row(
                            modifier =
                                Modifier.fillMaxWidth(),

                            horizontalArrangement =
                                Arrangement.spacedBy(10.dp)
                        ) {

                            Button(

                                onClick = {

                                    modoEdicion =
                                        false

                                    fotoSeleccionada =
                                        null

                                    seudonimoEditado =
                                        seudonimo ?: ""
                                },

                                modifier =
                                    Modifier.weight(1f),

                                shape =
                                    RoundedCornerShape(12.dp),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            Color(0xFFE2E8F0),
                                        contentColor =
                                            TextoOscuro
                                    ),

                                enabled =
                                    !guardando &&
                                            !subiendoFoto
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.Close,
                                    contentDescription =
                                        null
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(5.dp)
                                )

                                Text("Cancelar")
                            }

                            Button(

                                onClick = {

                                    onGuardarPerfil(
                                        seudonimoEditado
                                            .trim()
                                            .takeIf {
                                                it.isNotEmpty()
                                            }
                                    )

                                    fotoSeleccionada?.let {
                                        onSeleccionarFoto(it)
                                    }

                                    fotoSeleccionada =
                                        null

                                    modoEdicion =
                                        false
                                },

                                modifier =
                                    Modifier.weight(1f),

                                shape =
                                    RoundedCornerShape(12.dp),

                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            colorPrincipal
                                    ),

                                enabled =
                                    !guardando &&
                                            !subiendoFoto
                            ) {

                                if (
                                    guardando ||
                                    subiendoFoto
                                ) {

                                    Text(
                                        text =
                                            "Guardando..."
                                    )

                                } else {

                                    Icon(
                                        imageVector =
                                            Icons.Outlined.Check,
                                        contentDescription =
                                            null
                                    )

                                    Spacer(
                                        modifier =
                                            Modifier.width(5.dp)
                                    )

                                    Text(
                                        text =
                                            "Guardar"
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(
                    modifier =
                        Modifier.height(18.dp)
                )
            }

            /*
            |--------------------------------------------------------------------------
            | INFORMACIÓN PERSONAL
            |--------------------------------------------------------------------------
            */

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                PerfilSeccionTitulo(
                    icon =
                        Icons.Outlined.Person,

                    iconBg =
                        VerdeSuave,

                    iconTint =
                        colorPrincipal,

                    titulo =
                        "Información personal",

                    subtitulo =
                        "Datos registrados en SIGEFIV"
                )

                if (!modoEdicion) {

                    Button(

                        onClick = {

                            seudonimoEditado =
                                seudonimo ?: ""

                            fotoSeleccionada =
                                null

                            modoEdicion =
                                true

                            onEditClick()
                        },

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    VerdeSuave
                            ),

                        shape =
                            RoundedCornerShape(12.dp),

                        contentPadding =
                            PaddingValues(
                                horizontal = 12.dp,
                                vertical = 4.dp
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Outlined.Edit,

                            contentDescription =
                                null,

                            tint =
                                colorPrincipal,

                            modifier =
                                Modifier.size(14.dp)
                        )

                        Spacer(
                            modifier =
                                Modifier.width(4.dp)
                        )

                        Text(
                            text =
                                "Editar",

                            color =
                                colorPrincipal,

                            fontSize =
                                13.sp,

                            fontWeight =
                                FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            FondoTarjeta
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
            ) {

                Column(

                    modifier =
                        Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
                ) {

                    PerfilDato(
                        icon =
                            Icons.Outlined.Person,

                        iconBg =
                            VerdeSuave,

                        iconTint =
                            colorPrincipal,

                        titulo =
                            "Nombre completo",

                        valor =
                            nombre
                    )

                    PerfilSeparador()

                    PerfilDato(
                        icon =
                            Icons.Outlined.Person,

                        iconBg =
                            VerdeSuave,

                        iconTint =
                            colorPrincipal,

                        titulo =
                            "Seudónimo",

                        valor =
                            seudonimo
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?.let {
                                    "@$it"
                                }
                                ?: "No establecido"
                    )

                    PerfilSeparador()

                    PerfilDato(
                        icon =
                            Icons.Outlined.Email,

                        iconBg =
                            FondoIconoAzul,

                        iconTint =
                            IconoAzul,

                        titulo =
                            "Correo electrónico",

                        valor =
                            email
                    )

                    PerfilSeparador()

                    PerfilDato(
                        icon =
                            Icons.Outlined.Phone,

                        iconBg =
                            FondoIconoVerde,

                        iconTint =
                            IconoVerde,

                        titulo =
                            "Teléfono",

                        valor =
                            telefonoMostrar
                    )

                    PerfilSeparador()

                    PerfilDato(
                        icon =
                            Icons.Outlined.Badge,

                        iconBg =
                            FondoIconoNaranja,

                        iconTint =
                            IconoNaranja,

                        titulo =
                            "DNI",

                        valor =
                            dniMostrar
                    )

                    PerfilSeparador()

                    PerfilDato(
                        icon =
                            Icons.Outlined.Home,

                        iconBg =
                            FondoIconoMorado,

                        iconTint =
                            IconoMorado,

                        titulo =
                            "Dirección",

                        valor =
                            direccionMostrar
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            /*
            |--------------------------------------------------------------------------
            | SEGURIDAD DE LA CUENTA
            |--------------------------------------------------------------------------
            */

            PerfilSeccionTitulo(
                icon =
                    Icons.Outlined.Lock,

                iconBg =
                    VerdeSuave,

                iconTint =
                    colorPrincipal,

                titulo =
                    "Seguridad de la cuenta",

                subtitulo =
                    "Información de acceso y seguridad"
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            FondoTarjeta
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 1.dp
                    )
            ) {

                Column(

                    modifier =
                        Modifier.padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 12.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        PerfilIcono(
                            icon =
                                Icons.Outlined.Security,

                            bg =
                                VerdeSuave,

                            tint =
                                colorPrincipal
                        )

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Estado de la cuenta",

                                fontSize =
                                    12.sp,

                                color =
                                    TextoGris
                            )

                            Text(
                                text =
                                    "Cuenta SIGEFIV activa",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    colorPrincipal
                            )
                        }

                        Surface(

                            shape =
                                RoundedCornerShape(12.dp),

                            color =
                                VerdeSuave
                        ) {

                            Row(

                                modifier =
                                    Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    ),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Outlined.CheckCircle,

                                    contentDescription =
                                        null,

                                    modifier =
                                        Modifier.size(15.dp),

                                    tint =
                                        colorPrincipal
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(4.dp)
                                )

                                Text(
                                    text =
                                        "Activa",

                                    color =
                                        colorPrincipal,

                                    fontSize =
                                        12.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )
                            }
                        }
                    }

                    PerfilSeparador()

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 12.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        PerfilIcono(
                            icon =
                                Icons.Outlined.Lock,

                            bg =
                                FondoIconoAzul,

                            tint =
                                IconoAzul
                        )

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {

                            Text(
                                text =
                                    "Contraseña",

                                fontSize =
                                    12.sp,

                                color =
                                    TextoGris
                            )

                            Text(
                                text =
                                    if (esGoogle)
                                        "Gestionada por Google"
                                    else
                                        "Protegida",

                                fontSize =
                                    14.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    TextoOscuro
                            )
                        }

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,

                            contentDescription =
                                null,

                            tint =
                                TextoGris,

                            modifier =
                                Modifier.size(20.dp)
                        )
                    }

                    PerfilSeparador()

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    vertical = 12.dp
                                ),

                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {

                        PerfilIcono(
                            icon =
                                Icons.Outlined.Key,

                            bg =
                                FondoIconoNaranja,

                            tint =
                                IconoNaranja
                        )

                        Spacer(
                            modifier =
                                Modifier.width(14.dp)
                        )

                        Row(

                            modifier =
                                Modifier.weight(1f),

                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            if (esGoogle) {

                                Text(
                                    text =
                                        "G",

                                    fontSize =
                                        16.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        Color(0xFFEA4335)
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(6.dp)
                                )
                            }

                            Column {

                                Text(
                                    text =
                                        "Método de acceso",

                                    fontSize =
                                        12.sp,

                                    color =
                                        TextoGris
                                )

                                Text(
                                    text =
                                        if (esGoogle)
                                            "Cuenta Google"
                                        else
                                            "Correo y contraseña",

                                    fontSize =
                                        14.sp,

                                    fontWeight =
                                        FontWeight.Bold,

                                    color =
                                        TextoOscuro
                                )
                            }
                        }

                        Surface(

                            shape =
                                RoundedCornerShape(12.dp),

                            color =
                                VerdeSuave
                        ) {

                            Row(

                                modifier =
                                    Modifier.padding(
                                        horizontal = 10.dp,
                                        vertical = 5.dp
                                    ),

                                verticalAlignment =
                                    Alignment.CenterVertically
                            ) {

                                Text(
                                    text =
                                        if (esGoogle)
                                            "Google"
                                        else
                                            "Correo",

                                    color =
                                        colorPrincipal,

                                    fontSize =
                                        12.sp,

                                    fontWeight =
                                        FontWeight.Bold
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(4.dp)
                                )

                                Icon(
                                    imageVector =
                                        Icons.AutoMirrored.Filled.KeyboardArrowRight,

                                    contentDescription =
                                        null,

                                    tint =
                                        colorPrincipal,

                                    modifier =
                                        Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            /*
            |--------------------------------------------------------------------------
            | SOBRE SIGEFIV
            |--------------------------------------------------------------------------
            */

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(20.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            VerdeSuave.copy(
                                alpha = 0.5f
                            )
                    )
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    PerfilIcono(
                        icon =
                            Icons.Outlined.Info,

                        bg =
                            colorPrincipal,

                        tint =
                            Blanco
                    )

                    Spacer(
                        modifier =
                            Modifier.width(14.dp)
                    )

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "Sobre SIGEFIV",

                            fontSize =
                                15.sp,

                            fontWeight =
                                FontWeight.Bold,

                            color =
                                TextoOscuro
                        )

                        Text(
                            text =
                                "Sistema de Gestión Financiera",

                            fontSize =
                                12.sp,

                            color =
                                TextoGris
                        )

                        Text(
                            text =
                                "Grupo Residencial 21",

                            fontSize =
                                12.sp,

                            color =
                                colorPrincipal,

                            fontWeight =
                                FontWeight.Medium
                        )
                    }

                    Column(
                        horizontalAlignment =
                            Alignment.End
                    ) {

                        Text(
                            text =
                                "Versión 1.0.0",

                            fontSize =
                                12.sp,

                            color =
                                TextoGris
                        )

                        Spacer(
                            modifier =
                                Modifier.height(2.dp)
                        )

                        Text(
                            text =
                                "Juntos por una mejor gestión",

                            fontSize =
                                10.sp,

                            color =
                                TextoGris
                        )
                    }
                }
            }

            Spacer(
                modifier =
                    Modifier.height(24.dp)
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| IMAGEN DESDE URI
|--------------------------------------------------------------------------
*/

@Composable
private fun ImagenDesdeUri(
    uri: Uri,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var bitmap by remember(uri) {
        mutableStateOf<ImageBitmap?>(null)
    }

    LaunchedEffect(uri) {

        bitmap = try {

            context.contentResolver
                .openInputStream(uri)
                ?.use { input ->
                    BitmapFactory
                        .decodeStream(input)
                        ?.asImageBitmap()
                }

        } catch (_: Exception) {

            null
        }
    }

    if (bitmap != null) {

        androidx.compose.foundation.Image(
            bitmap = bitmap!!,
            contentDescription = "Foto de perfil",
            modifier = modifier
        )
    }
}

/*
|--------------------------------------------------------------------------
| BARRA INFERIOR
|--------------------------------------------------------------------------
*/

@Composable
private fun BarraInferiorPerfil(
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {}
) {
    val colorPrincipal =
        SeasonalColors.primary(
            SeasonalTheme.getSeason()
        )

    NavigationBar(
        containerColor =
            colorPrincipal,

        tonalElevation =
            0.dp
    ) {

        NavigationBarItem(
            selected = false,
            onClick = onInicioClick,
            icon = {
                Icon(
                    Icons.Outlined.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text("Inicio")
            },
            colors =
                NavigationBarItemDefaults.colors(
                    unselectedIconColor =
                        Blanco.copy(alpha = 0.75f),

                    unselectedTextColor =
                        Blanco.copy(alpha = 0.75f)
                )
        )

        NavigationBarItem(
            selected = false,
            onClick = onAsambleasClick,
            icon = {
                Icon(
                    Icons.Outlined.Groups,
                    contentDescription = "Asamblea"
                )
            },
            label = {
                Text("Asamblea")
            },
            colors =
                NavigationBarItemDefaults.colors(
                    unselectedIconColor =
                        Blanco.copy(alpha = 0.75f),

                    unselectedTextColor =
                        Blanco.copy(alpha = 0.75f)
                )
        )

        NavigationBarItem(
            selected = false,
            onClick = onPeriodosClick,
            icon = {
                Icon(
                    Icons.Outlined.CalendarMonth,
                    contentDescription = "Periodos"
                )
            },
            label = {
                Text("Periodos")
            },
            colors =
                NavigationBarItemDefaults.colors(
                    unselectedIconColor =
                        Blanco.copy(alpha = 0.75f),

                    unselectedTextColor =
                        Blanco.copy(alpha = 0.75f)
                )
        )

        NavigationBarItem(
            selected = true,
            onClick = onMiCuentaClick,
            icon = {
                Icon(
                    Icons.Outlined.Person,
                    contentDescription = "Mi cuenta"
                )
            },
            label = {
                Text("Mi cuenta")
            },
            colors =
                NavigationBarItemDefaults.colors(
                    selectedIconColor =
                        colorPrincipal,

                    selectedTextColor =
                        Blanco,

                    indicatorColor =
                        Blanco,

                    unselectedIconColor =
                        Blanco.copy(alpha = 0.75f),

                    unselectedTextColor =
                        Blanco.copy(alpha = 0.75f)
                )
        )
    }
}

/*
|--------------------------------------------------------------------------
| TÍTULO DE SECCIÓN
|--------------------------------------------------------------------------
*/

@Composable
private fun PerfilSeccionTitulo(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    titulo: String,
    subtitulo: String
) {
    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        PerfilIcono(
            icon = icon,
            bg = iconBg,
            tint = iconTint
        )

        Spacer(
            modifier =
                Modifier.width(12.dp)
        )

        Column {

            Text(
                text = titulo,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )

            Text(
                text = subtitulo,
                fontSize = 12.sp,
                color = TextoGris
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| DATO DEL PERFIL
|--------------------------------------------------------------------------
*/

@Composable
private fun PerfilDato(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    titulo: String,
    valor: String
) {
    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        PerfilIcono(
            icon = icon,
            bg = iconBg,
            tint = iconTint
        )

        Spacer(
            modifier =
                Modifier.width(14.dp)
        )

        Column(
            modifier =
                Modifier.weight(1f)
        ) {

            Text(
                text = titulo,
                fontSize = 11.sp,
                color = TextoGris
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text = valor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TextoOscuro
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| ICONO
|--------------------------------------------------------------------------
*/

@Composable
private fun PerfilIcono(
    icon: ImageVector,
    bg: Color,
    tint: Color
) {
    Box(

        modifier =
            Modifier
                .size(38.dp)
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(bg),

        contentAlignment =
            Alignment.Center
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = tint
        )
    }
}

/*
|--------------------------------------------------------------------------
| SEPARADOR
|--------------------------------------------------------------------------
*/

@Composable
private fun PerfilSeparador() {
    HorizontalDivider(
        modifier =
            Modifier.padding(
                start = 52.dp
            ),

        color =
            Color(0xFFF1F5F9)
    )
}
@Composable
private fun ImagenPerfil(
    foto: String?,
    modifier: Modifier = Modifier
) {
    var bitmap by remember(foto) {
        mutableStateOf<Bitmap?>(null)
    }

    LaunchedEffect(foto) {
        bitmap = null

        if (!foto.isNullOrBlank()) {
            bitmap = withContext(Dispatchers.IO) {
                try {
                    val urlCorregida = foto
                        .replace("localhost", "192.168.1.34")

                    BitmapFactory.decodeStream(
                        URL(urlCorregida).openStream()
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "Foto de perfil",
            modifier = modifier
        )
    }
}

