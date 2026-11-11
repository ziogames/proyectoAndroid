@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.sigefiv.app.screens.perfil

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.ui.components.AppBottomBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

/*
|--------------------------------------------------------------------------
| COLORES SEMÁNTICOS Y DE ACENTO
|--------------------------------------------------------------------------
*/

private val VerdeSemantico = Color(0xFF16A34A)
private val AzulSemantico = Color(0xFF2563EB)
private val NaranjaSemantico = Color(0xFFD97706)
private val MoradoSemantico = Color(0xFF7C3AED)
private val FacebookBlue = Color(0xFF1877F2)
private val GoogleRed = Color(0xFFEA4335)

@Composable
fun PerfilScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onInicioClick: () -> Unit = {},
    onAsambleasClick: () -> Unit = {},
    onEmprendimientosClick: () -> Unit = {},
    onPeriodosClick: () -> Unit = {},
    onMiCuentaClick: () -> Unit = {},
    onMisEmprendimientosClick: () -> Unit = {},

    nombre: String = "Freddy Antayhua Saavedra",
    seudonimo: String? = null,
    email: String = "admin@sigefiv.com",
    telefono: String? = null,
    dni: String? = null,
    direccion: String? = null,
    rol: String? = "Administrador",
    foto: String? = null,
    metodoAcceso: String? = "Google",

    onGuardarPerfil: (seudonimo: String?) -> Unit = {},
    onSeleccionarFoto: (Uri) -> Unit = {},
    onActualizarPassword: (String, String, String, () -> Unit) -> Unit = { _, _, _, _ -> },
    cargandoPassword: Boolean = false,

    guardando: Boolean = false,
    subiendoFoto: Boolean = false
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    var modoEdicion by remember { mutableStateOf(false) }
    var seudonimoEditado by remember(seudonimo) { mutableStateOf(seudonimo ?: "") }
    var fotoSeleccionada by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val selectorYCortadorFoto = rememberLauncherForActivityResult(
        contract = CropImageContract()
    ) { result ->
        if (result.isSuccessful) {
            val uriRecortada = result.uriContent
            if (uriRecortada != null) {
                fotoSeleccionada = uriRecortada
            }
        }
    }

    fun lanzarSelectorFoto(incluirCamara: Boolean) {
        val opciones = CropImageContractOptions(
            uri = null,
            cropImageOptions = CropImageOptions(
                imageSourceIncludeCamera = incluirCamara,
                imageSourceIncludeGallery = true,
                cropShape = CropImageView.CropShape.OVAL,
                fixAspectRatio = true,
                aspectRatioX = 1,
                aspectRatioY = 1,
                guidelines = CropImageView.Guidelines.ON,
                activityTitle = "Ajustar foto",
                toolbarColor = android.graphics.Color.DKGRAY,
                activityMenuIconColor = android.graphics.Color.WHITE,
                activityMenuTextColor = android.graphics.Color.WHITE,
                backgroundColor = android.graphics.Color.argb(200, 0, 0, 0)
            )
        )
        selectorYCortadorFoto.launch(opciones)
    }

    val permisoCamaraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        lanzarSelectorFoto(incluirCamara = concedido)
    }

    var mostrarDialogoPassword by remember { mutableStateOf(false) }
    var mostrarAvisoGoogle by remember { mutableStateOf(false) }
    var mostrarAvisoFacebook by remember { mutableStateOf(false) }
    var passwordActual by remember { mutableStateOf("") }
    var nuevaPassword by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var errorPassword by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(seudonimo, modoEdicion) {
        if (!modoEdicion) {
            seudonimoEditado = seudonimo ?: ""
        }
    }

    val telefonoMostrar = telefono?.takeIf { it.isNotBlank() } ?: "No registrado"
    val dniMostrar = dni?.takeIf { it.isNotBlank() } ?: "No registrado"
    val direccionMostrar = direccion?.takeIf { it.isNotBlank() } ?: "No registrada"
    val rolMostrar = rol?.takeIf { it.isNotBlank() } ?: "Consulta"
    val metodoMostrar = metodoAcceso?.takeIf { it.isNotBlank() } ?: "Correo y contraseña"
    val esGoogle = metodoMostrar.equals("Google", ignoreCase = true)
    val esFacebook = metodoMostrar.equals("Facebook", ignoreCase = true)
    val inicialNombre = nombre.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "U"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Mi Cuenta",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "Información de tu cuenta SIGEFIV",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Regresar",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        onClick = {
                            if (!modoEdicion) {
                                seudonimoEditado = seudonimo ?: ""
                                fotoSeleccionada = null
                                modoEdicion = true
                                onEditClick()
                            } else {
                                modoEdicion = false
                                fotoSeleccionada = null
                                seudonimoEditado = seudonimo ?: ""
                            }
                        },
                        shape = CircleShape,
                        color = primaryColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f)),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (!modoEdicion) Icons.Outlined.Edit else Icons.Outlined.Close,
                                contentDescription = if (!modoEdicion) "Editar perfil" else "Cancelar",
                                tint = primaryColor,
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
            AppBottomBar(
                pantallaActual = "MiCuenta",
                onInicioClick = onInicioClick,
                onAsambleasClick = onAsambleasClick,
                onEmprendimientosClick = onEmprendimientosClick,
                onPeriodosClick = onPeriodosClick,
                onMiCuentaClick = { /* Ya estás aquí */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            /*
            |--------------------------------------------------------------------------
            | TARJETA PRINCIPAL DE PERFIL (Estilo Tarjeta de Identidad Moderna)
            |--------------------------------------------------------------------------
            */
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(75.dp)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        primaryColor.copy(alpha = 0.22f),
                                        primaryColor.copy(alpha = 0.03f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(6.dp))

                        // Avatar destacado
                        Box {
                            Surface(
                                modifier = Modifier.size(88.dp),
                                shape = CircleShape,
                                color = primaryColor.copy(alpha = 0.15f),
                                border = BorderStroke(2.dp, primaryColor.copy(alpha = 0.35f))
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when {
                                        fotoSeleccionada != null -> {
                                            ImagenDesdeUri(
                                                uri = fotoSeleccionada!!,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                            )
                                        }
                                        !foto.isNullOrBlank() -> {
                                            ImagenPerfil(
                                                foto = foto,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(CircleShape)
                                            )
                                        }
                                        else -> {
                                            Text(
                                                text = inicialNombre,
                                                color = primaryColor,
                                                fontSize = 32.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                        }
                                    }
                                }
                            }

                            // Indicador en línea
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(VerdeSemantico)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = nombre,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )

                        if (!seudonimo.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "@$seudonimo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryColor,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = email,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Badges informativos
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = primaryColor.copy(alpha = 0.12f),
                                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Security,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = rolMostrar,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = when {
                                    esFacebook -> FacebookBlue.copy(alpha = 0.10f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                },
                                border = BorderStroke(
                                    1.dp,
                                    when {
                                        esFacebook -> FacebookBlue.copy(alpha = 0.30f)
                                        else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                                    }
                                ),
                                modifier = Modifier.clickable {
                                    when {
                                        esGoogle -> mostrarAvisoGoogle = true
                                        esFacebook -> mostrarAvisoFacebook = true
                                        else -> mostrarDialogoPassword = true
                                    }
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    when {
                                        esGoogle -> {
                                            Text(
                                                text = "G",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GoogleRed
                                            )
                                        }
                                        esFacebook -> {
                                            Box(
                                                modifier = Modifier
                                                    .size(15.dp)
                                                    .clip(CircleShape)
                                                    .background(FacebookBlue),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "f",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color.White
                                                )
                                            }
                                        }
                                        else -> {
                                            Icon(
                                                imageVector = Icons.Outlined.Key,
                                                contentDescription = null,
                                                tint = primaryColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = when {
                                            esGoogle -> "Google Auth"
                                            esFacebook -> "Facebook Auth"
                                            else -> "Cambiar Clave"
                                        },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (esFacebook) FacebookBlue else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }

            /*
            |--------------------------------------------------------------------------
            | MODO EDICIÓN FORMULARIO
            |--------------------------------------------------------------------------
            */
            if (modoEdicion) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Editar perfil",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Actualiza tu seudónimo y fotografía pública.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = seudonimoEditado,
                            onValueChange = { if (it.length <= 100) seudonimoEditado = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Seudónimo") },
                            placeholder = { Text("Ejemplo: Freddy") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = primaryColor)
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                focusedLabelColor = primaryColor,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val tienePermisoCamara = ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.CAMERA
                                ) == PackageManager.PERMISSION_GRANTED

                                if (tienePermisoCamara) {
                                    lanzarSelectorFoto(incluirCamara = true)
                                } else {
                                    permisoCamaraLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                        ) {
                            Icon(imageVector = Icons.Outlined.PhotoCamera, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = if (fotoSeleccionada != null) "Cambiar foto seleccionada" else "Seleccionar nueva foto")
                        }

                        if (fotoSeleccionada != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "✓ Foto lista para actualizar",
                                fontSize = 12.sp,
                                color = VerdeSemantico,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    modoEdicion = false
                                    fotoSeleccionada = null
                                    seudonimoEditado = seudonimo ?: ""
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                enabled = !guardando && !subiendoFoto
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    onGuardarPerfil(
                                        seudonimoEditado.trim().takeIf { it.isNotEmpty() }
                                    )
                                    fotoSeleccionada?.let { onSeleccionarFoto(it) }
                                    fotoSeleccionada = null
                                    modoEdicion = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                enabled = !guardando && !subiendoFoto
                            ) {
                                Text(if (guardando || subiendoFoto) "Guardando..." else "Guardar")
                            }
                        }
                    }
                }
            }

            /*
            |--------------------------------------------------------------------------
            | SECCIÓN 1: INFORMACIÓN PERSONAL
            |--------------------------------------------------------------------------
            */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PerfilSeccionTitulo(
                    icon = Icons.Outlined.Person,
                    iconBg = primaryColor.copy(alpha = 0.12f),
                    iconTint = primaryColor,
                    titulo = "Información personal",
                    subtitulo = "Datos registrados en SIGEFIV"
                )

                if (!modoEdicion) {
                    Surface(
                        onClick = {
                            seudonimoEditado = seudonimo ?: ""
                            fotoSeleccionada = null
                            modoEdicion = true
                            onEditClick()
                        },
                        shape = RoundedCornerShape(12.dp),
                        color = primaryColor.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Editar",
                                color = primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    PerfilDato(icon = Icons.Outlined.Person, iconBg = primaryColor.copy(alpha = 0.12f), iconTint = primaryColor, titulo = "Nombre completo", valor = nombre)
                    PerfilSeparador()
                    PerfilDato(icon = Icons.Outlined.Person, iconBg = primaryColor.copy(alpha = 0.12f), iconTint = primaryColor, titulo = "Seudónimo", valor = seudonimo?.takeIf { it.isNotBlank() }?.let { "@$it" } ?: "No establecido")
                    PerfilSeparador()
                    PerfilDato(icon = Icons.Outlined.Email, iconBg = AzulSemantico.copy(alpha = 0.12f), iconTint = AzulSemantico, titulo = "Correo electrónico", valor = email)
                    PerfilSeparador()
                    PerfilDato(icon = Icons.Outlined.Phone, iconBg = VerdeSemantico.copy(alpha = 0.12f), iconTint = VerdeSemantico, titulo = "Teléfono", valor = telefonoMostrar)
                    PerfilSeparador()
                    PerfilDato(icon = Icons.Outlined.Badge, iconBg = NaranjaSemantico.copy(alpha = 0.12f), iconTint = NaranjaSemantico, titulo = "DNI", valor = dniMostrar)
                    PerfilSeparador()
                    PerfilDato(icon = Icons.Outlined.Home, iconBg = MoradoSemantico.copy(alpha = 0.12f), iconTint = MoradoSemantico, titulo = "Dirección", valor = direccionMostrar)
                }
            }

            /*
            |--------------------------------------------------------------------------
            | SECCIÓN 2: MI EMPRENDIMIENTO
            |--------------------------------------------------------------------------
            */
            PerfilSeccionTitulo(
                icon = Icons.Outlined.Storefront,
                iconBg = MoradoSemantico.copy(alpha = 0.12f),
                iconTint = MoradoSemantico,
                titulo = "Mi Emprendimiento",
                subtitulo = "Promociona tus servicios a la comunidad"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onMisEmprendimientosClick),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PerfilIcono(
                        icon = Icons.Outlined.Storefront,
                        bg = MoradoSemantico.copy(alpha = 0.12f),
                        tint = MoradoSemantico
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gestionar mis emprendimientos",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Crea y administra tus negocios vecinales",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            /*
            |--------------------------------------------------------------------------
            | SECCIÓN 3: SEGURIDAD DE LA CUENTA
            |--------------------------------------------------------------------------
            */
            PerfilSeccionTitulo(
                icon = Icons.Outlined.Lock,
                iconBg = primaryColor.copy(alpha = 0.12f),
                iconTint = primaryColor,
                titulo = "Seguridad de la cuenta",
                subtitulo = "Información de acceso y credenciales"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PerfilIcono(icon = Icons.Outlined.Security, bg = primaryColor.copy(alpha = 0.12f), tint = primaryColor)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Estado de la cuenta", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "Cuenta SIGEFIV activa", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = VerdeSemantico.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, VerdeSemantico.copy(alpha = 0.25f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Outlined.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = VerdeSemantico)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Activa", color = VerdeSemantico, fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    PerfilSeparador()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when {
                                    esGoogle -> mostrarAvisoGoogle = true
                                    esFacebook -> mostrarAvisoFacebook = true
                                    else -> mostrarDialogoPassword = true
                                }
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PerfilIcono(icon = Icons.Outlined.Lock, bg = AzulSemantico.copy(alpha = 0.12f), tint = AzulSemantico)
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Contraseña", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = when {
                                    esGoogle -> "Gestionada por Google"
                                    esFacebook -> "Gestionada por Facebook"
                                    else -> "Protegida"
                                },
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    PerfilSeparador()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PerfilIcono(icon = Icons.Outlined.Key, bg = NaranjaSemantico.copy(alpha = 0.12f), tint = NaranjaSemantico)
                        Spacer(modifier = Modifier.width(14.dp))
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "Método de acceso", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = when {
                                        esGoogle -> "Cuenta Google"
                                        esFacebook -> "Cuenta Facebook"
                                        else -> "Correo y contraseña"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (esFacebook) FacebookBlue.copy(alpha = 0.12f) else primaryColor.copy(alpha = 0.12f),
                            border = BorderStroke(
                                1.dp,
                                if (esFacebook) FacebookBlue.copy(alpha = 0.30f) else primaryColor.copy(alpha = 0.25f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (esFacebook) {
                                    Box(
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clip(CircleShape)
                                            .background(FacebookBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "f",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(5.dp))
                                } else if (esGoogle) {
                                    Text(
                                        text = "G",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = GoogleRed
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                }
                                Text(
                                    text = when {
                                        esGoogle -> "Google"
                                        esFacebook -> "Facebook"
                                        else -> "Correo"
                                    },
                                    color = if (esFacebook) FacebookBlue else primaryColor,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            /*
            |--------------------------------------------------------------------------
            | SECCIÓN 4: SOBRE SIGEFIV
            |--------------------------------------------------------------------------
            */
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PerfilIcono(icon = Icons.Outlined.Info, bg = primaryColor.copy(alpha = 0.15f), tint = primaryColor)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "SIGEFIV", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "Sistema de Gestión Financiera", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = "Grupo Residencial 21", fontSize = 11.5.sp, color = primaryColor, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Versión 1.0.0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Gestión transparente", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    /*
    |--------------------------------------------------------------------------
    | DIÁLOGO PARA CAMBIAR CONTRASEÑA
    |--------------------------------------------------------------------------
    */
    if (mostrarDialogoPassword) {
        AlertDialog(
            onDismissRequest = {
                if (!cargandoPassword) {
                    mostrarDialogoPassword = false
                    passwordActual = ""
                    nuevaPassword = ""
                    confirmarPassword = ""
                    errorPassword = null
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(text = "Cambiar contraseña", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Introduce tu contraseña actual y la nueva clave de acceso.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = passwordActual,
                        onValueChange = { passwordActual = it },
                        label = { Text("Contraseña actual") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = nuevaPassword,
                        onValueChange = { nuevaPassword = it },
                        label = { Text("Nueva contraseña") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    val tieneMinimo8 = nuevaPassword.length >= 8
                    val tieneMayuscula = nuevaPassword.any { it.isUpperCase() }
                    val tieneEspecial = nuevaPassword.any { "!@#$%^&*()_+-=[]{}|;':\",./<>?".contains(it) }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        ItemValidacionRequisito(texto = "Mínimo 8 caracteres", cumplido = tieneMinimo8)
                        ItemValidacionRequisito(texto = "Al menos una letra mayúscula", cumplido = tieneMayuscula)
                        ItemValidacionRequisito(texto = "Al menos un carácter especial (!@#$%^&*)", cumplido = tieneEspecial)
                    }

                    OutlinedTextField(
                        value = confirmarPassword,
                        onValueChange = { confirmarPassword = it },
                        label = { Text("Confirmar nueva contraseña") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!errorPassword.isNullOrBlank()) {
                        Text(text = errorPassword!!, color = Color(0xFFDC2626), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val tieneMinimo8 = nuevaPassword.length >= 8
                        val tieneMayuscula = nuevaPassword.any { it.isUpperCase() }
                        val tieneEspecial = nuevaPassword.any { "!@#$%^&*()_+-=[]{}|;':\",./<>?".contains(it) }

                        if (!tieneMinimo8 || !tieneMayuscula || !tieneEspecial) {
                            errorPassword = "La contraseña debe cumplir con todos los requisitos de seguridad."
                            return@Button
                        }
                        if (nuevaPassword != confirmarPassword) {
                            errorPassword = "Las contraseñas nuevas no coinciden."
                            return@Button
                        }
                        errorPassword = null

                        onActualizarPassword(passwordActual, nuevaPassword, confirmarPassword) {
                            mostrarDialogoPassword = false
                            passwordActual = ""
                            nuevaPassword = ""
                            confirmarPassword = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !cargandoPassword
                ) {
                    Text(if (cargandoPassword) "Actualizando..." else "Actualizar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoPassword = false
                        passwordActual = ""
                        nuevaPassword = ""
                        confirmarPassword = ""
                        errorPassword = null
                    },
                    enabled = !cargandoPassword
                ) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    /*
    |--------------------------------------------------------------------------
    | DIÁLOGO AVISO GOOGLE
    |--------------------------------------------------------------------------
    */
    if (mostrarAvisoGoogle) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoGoogle = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(text = "Acceso con Google", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    text = "Tu cuenta está vinculada directamente con Google. No es necesario gestionar una contraseña local en SIGEFIV.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarAvisoGoogle = false },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    /*
    |--------------------------------------------------------------------------
    | DIÁLOGO AVISO FACEBOOK
    |--------------------------------------------------------------------------
    */
    if (mostrarAvisoFacebook) {
        AlertDialog(
            onDismissRequest = { mostrarAvisoFacebook = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text(text = "Acceso con Facebook", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    text = "Tu cuenta está vinculada directamente con Facebook. No es necesario gestionar una contraseña local en SIGEFIV.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarAvisoFacebook = false },
                    colors = ButtonDefaults.buttonColors(containerColor = FacebookBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Entendido", color = Color.White)
                }
            }
        )
    }
}

/*
|--------------------------------------------------------------------------
| COMPONENTES AUXILIARES
|--------------------------------------------------------------------------
*/

@Composable
private fun ImagenDesdeUri(uri: Uri, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var bitmap by remember(uri) { mutableStateOf<ImageBitmap?>(null) }

    LaunchedEffect(uri) {
        bitmap = try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input)?.asImageBitmap()
            }
        } catch (_: Exception) {
            null
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = "Foto de perfil",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun PerfilSeccionTitulo(icon: ImageVector, iconBg: Color, iconTint: Color, titulo: String, subtitulo: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        PerfilIcono(icon = icon, bg = iconBg, tint = iconTint)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = titulo, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitulo, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PerfilDato(icon: ImageVector, iconBg: Color, iconTint: Color, titulo: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PerfilIcono(icon = icon, bg = iconBg, tint = iconTint)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = titulo, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = valor, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun PerfilIcono(icon: ImageVector, bg: Color, tint: Color) {
    Surface(
        modifier = Modifier.size(36.dp),
        shape = RoundedCornerShape(10.dp),
        color = bg
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = tint)
        }
    }
}

@Composable
private fun PerfilSeparador() {
    HorizontalDivider(modifier = Modifier.padding(start = 50.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f))
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
                    val urlCorregida = foto.replace(
                        "localhost",
                        ServidorConfig.API_BASE_URL
                            .substringBefore("/api/")
                            .removePrefix("http://")
                            .removePrefix("https://")
                    )

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
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ItemValidacionRequisito(texto: String, cumplido: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (cumplido) Icons.Outlined.CheckCircle else Icons.Outlined.Check,
            contentDescription = null,
            tint = if (cumplido) Color(0xFF16A34A) else Color(0xFF94A3B8),
            modifier = Modifier.size(15.dp)
        )
        Text(
            text = texto,
            fontSize = 11.sp,
            color = if (cumplido) Color(0xFF16A34A) else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (cumplido) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}