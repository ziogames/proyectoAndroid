package com.sigefiv.app

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.DisposableEffect
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.NetworkErrorManager
import com.sigefiv.app.navigation.AppNavigation
import com.sigefiv.app.notifications.NotificacionEventBus
import com.sigefiv.app.screens.bienvenida.BienvenidaScreen
import com.sigefiv.app.ui.theme.AppSeason
import com.sigefiv.app.ui.theme.SIGEFIVTheme
import com.sigefiv.app.ui.theme.SeasonalColors
import com.sigefiv.app.ui.theme.SeasonalTheme
import com.sigefiv.app.viewmodel.DashboardViewModel
import com.sigefiv.app.viewmodel.DashboardViewModelFactory
import com.sigefiv.app.viewmodel.LoginViewModel
import com.sigefiv.app.viewmodel.LoginViewModelFactory
import com.sigefiv.app.viewmodel.MovimientosViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
|--------------------------------------------------------------------------
| PANTALLAS DE FLUJO INICIAL
|--------------------------------------------------------------------------
*/

private enum class PantallaInicial {
    SPLASH,
    LOGIN,
    ONBOARDING_BIENVENIDA,
    APLICACION
}

class MainActivity : ComponentActivity() {

    private val solicitarPermisoNotificaciones =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { concedido ->
            Log.d("SIGEFIV_NOTIF", "Permiso de notificaciones: $concedido")
        }

    private var asambleaIdNotificacion = mutableStateOf<Int?>(null)
    private var notificacionIdNotificacion = mutableStateOf<Int?>(null)
    private var comprobanteCompartido = mutableStateOf<Uri?>(null)
    val callbackManager: CallbackManager = CallbackManager.Factory.create()

    @Deprecated("Requerido por el SDK de Facebook")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (intent?.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            comprobanteCompartido.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            }
        }

        FirebaseTokenTest.obtenerToken()

        val loginViewModel = ViewModelProvider(
            this,
            LoginViewModelFactory(applicationContext)
        )[LoginViewModel::class.java]

        val dashboardViewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(applicationContext)
        )[DashboardViewModel::class.java]

        val movimientosViewModel = ViewModelProvider(
            this,
            MovimientosViewModelFactory(applicationContext)
        )[MovimientosViewModel::class.java]

        asambleaIdNotificacion.value =
            intent?.getStringExtra(SIGEFIVFirebaseMessagingService.EXTRA_ASAMBLEA_ID)?.toIntOrNull()

        notificacionIdNotificacion.value =
            intent?.getStringExtra(SIGEFIVFirebaseMessagingService.EXTRA_NOTIFICACION_ID)?.toIntOrNull()

        setContent {
            val preferenciasTema = getSharedPreferences("sigefiv_config", Context.MODE_PRIVATE)
            var darkTheme by rememberSaveable {
                mutableStateOf(preferenciasTema.getBoolean("tema_oscuro", false))
            }

            SIGEFIVTheme(darkTheme = darkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppSIGEFIV(
                        loginViewModel = loginViewModel,
                        dashboardViewModel = dashboardViewModel,
                        movimientosViewModel = movimientosViewModel,
                        callbackManager = callbackManager,
                        asambleaIdNotificacion = asambleaIdNotificacion.value,
                        notificacionIdNotificacion = notificacionIdNotificacion.value,
                        solicitarPermisoNotificaciones = solicitarPermisoNotificaciones,
                        comprobanteCompartido = comprobanteCompartido.value,
                        darkTheme = darkTheme,
                        onThemeToggle = {
                            darkTheme = !darkTheme
                            preferenciasTema.edit().putBoolean("tema_oscuro", darkTheme).apply()
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
            comprobanteCompartido.value = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            }
        }

        asambleaIdNotificacion.value =
            intent.getStringExtra(SIGEFIVFirebaseMessagingService.EXTRA_ASAMBLEA_ID)?.toIntOrNull()

        notificacionIdNotificacion.value =
            intent.getStringExtra(SIGEFIVFirebaseMessagingService.EXTRA_NOTIFICACION_ID)?.toIntOrNull()
    }
}

private fun esDispositivoXiaomi(): Boolean {
    val fabricante = Build.MANUFACTURER.lowercase()
    val marca = Build.BRAND.lowercase()
    return fabricante.contains("xiaomi") || fabricante.contains("redmi") || fabricante.contains("poco") ||
            marca.contains("xiaomi") || marca.contains("redmi") || marca.contains("poco")
}

private fun abrirAutostartXiaomi(context: Context) {
    try {
        val intent = Intent().apply {
            action = "miui.intent.action.OP_AUTO_START"
            addCategory(Intent.CATEGORY_DEFAULT)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            val intent = Intent(
                android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:${context.packageName}")
            )
            context.startActivity(intent)
        }
    }
}

@Composable
fun AppSIGEFIV(
    loginViewModel: LoginViewModel,
    dashboardViewModel: DashboardViewModel,
    movimientosViewModel: MovimientosViewModel,
    callbackManager: CallbackManager,
    asambleaIdNotificacion: Int? = null,
    notificacionIdNotificacion: Int? = null,
    solicitarPermisoNotificaciones: androidx.activity.result.ActivityResultLauncher<String>,
    comprobanteCompartido: Uri? = null,
    darkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    var pantalla by remember { mutableStateOf(PantallaInicial.SPLASH) }
    var mostrarBanner by remember { mutableStateOf(false) }
    var mostrarDialogoAutostart by remember { mutableStateOf(false) }
    var tituloBanner by remember { mutableStateOf("") }
    var mensajeBanner by remember { mutableStateOf("") }

    var mostrarAlertaExpulsion by remember { mutableStateOf(false) }
    var tituloExpulsion by remember { mutableStateOf("") }
    var mensajeExpulsionDialogo by remember { mutableStateOf("") }

    val primaryColor = MaterialTheme.colorScheme.primary

    LaunchedEffect(Unit) {
        NotificacionEventBus.eventoExpulsion.collect { evento ->
            tituloExpulsion = evento.titulo
            mensajeExpulsionDialogo = evento.mensaje
            mostrarAlertaExpulsion = true
        }
    }

    val loginCorrecto by loginViewModel.loginCorrecto.collectAsStateWithLifecycle()
    val bienvenidaVista by loginViewModel.bienvenidaVista.collectAsStateWithLifecycle()
    val mensaje by loginViewModel.mensaje.collectAsStateWithLifecycle()
    val cargando by loginViewModel.cargando.collectAsStateWithLifecycle()
    val errorConexion by NetworkErrorManager.error.collectAsState()

    LaunchedEffect(loginCorrecto, bienvenidaVista) {
        if (loginCorrecto) {
            pantalla = if (!bienvenidaVista) {
                PantallaInicial.ONBOARDING_BIENVENIDA
            } else {
                PantallaInicial.APLICACION
            }
        } else if (pantalla == PantallaInicial.APLICACION || pantalla == PantallaInicial.ONBOARDING_BIENVENIDA) {
            pantalla = PantallaInicial.LOGIN
        }
    }

    val context = LocalContext.current
    val ocrService = remember { OcrService(context) }
    var textoOcr by remember { mutableStateOf<String?>(null) }
    var comprobanteResultado by remember { mutableStateOf<ComprobanteInterpretado?>(null) }
    var comprobanteConfirmado by remember { mutableStateOf<ComprobanteInterpretado?>(null) }
    var comprobanteUriConfirmado by remember { mutableStateOf<Uri?>(null) }
    var procesandoOcr by remember { mutableStateOf(false) }

    LaunchedEffect(loginCorrecto) {
        if (loginCorrecto && esDispositivoXiaomi()) {
            val preferencias = context.getSharedPreferences("sigefiv_config", Context.MODE_PRIVATE)
            val autostartMostrado = preferencias.getBoolean("autostart_mostrado", false)
            if (!autostartMostrado) {
                mostrarDialogoAutostart = true
            }
        }
    }

    LaunchedEffect(loginCorrecto) {
        if (loginCorrecto && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permiso = Manifest.permission.POST_NOTIFICATIONS
            if (androidx.core.content.ContextCompat.checkSelfPermission(context, permiso) != PackageManager.PERMISSION_GRANTED) {
                solicitarPermisoNotificaciones.launch(permiso)
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        if (loginCorrecto) {
            pantalla = if (!bienvenidaVista) {
                PantallaInicial.ONBOARDING_BIENVENIDA
            } else {
                PantallaInicial.APLICACION
            }
        } else {
            pantalla = PantallaInicial.LOGIN
        }
    }

    var mostrarComprobante by remember { mutableStateOf(comprobanteCompartido != null) }

    LaunchedEffect(comprobanteCompartido) {
        mostrarComprobante = comprobanteCompartido != null
    }

    if (mostrarComprobante && comprobanteCompartido != null) {
        AlertDialog(
            onDismissRequest = { mostrarComprobante = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Comprobante recibido", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                val bitmap = remember(comprobanteCompartido) {
                    comprobanteCompartido?.let { uri ->
                        context.contentResolver.openInputStream(uri)?.use { input ->
                            BitmapFactory.decodeStream(input)
                        }
                    }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Comprobante recibido",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentScale = ContentScale.Fit
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val uri = comprobanteCompartido
                        if (uri != null) {
                            procesandoOcr = true
                            ocrService.reconocerTexto(
                                uri = uri,
                                onSuccess = { texto ->
                                    val resultado = ComprobanteInterpreter.interpretar(texto)
                                    comprobanteResultado = resultado
                                    procesandoOcr = false
                                    mostrarComprobante = false
                                },
                                onError = { error ->
                                    textoOcr = "Error OCR: ${error.message}"
                                    procesandoOcr = false
                                    mostrarComprobante = false
                                }
                            )
                        }
                    },
                    enabled = !procesandoOcr
                ) {
                    Text(if (procesandoOcr) "Leyendo..." else "Continuar", color = primaryColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarComprobante = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    if (comprobanteResultado != null) {
        val resultado = comprobanteResultado!!
        AlertDialog(
            onDismissRequest = { comprobanteResultado = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Revisar comprobante", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Tipo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = when (resultado.tipo) {
                            TipoMovimientoDetectado.INGRESO -> "Ingreso"
                            TipoMovimientoDetectado.EGRESO -> "Egreso"
                            TipoMovimientoDetectado.INDETERMINADO -> "No determinado"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Monto", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = resultado.monto?.let { "S/ %.2f".format(it) } ?: "No detectado",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Medio de pago", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = resultado.medioPago ?: "No detectado", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "N.º de operación", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = resultado.numeroOperacion ?: "No detectado", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Persona", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = resultado.persona ?: "No detectada", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "Fecha", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = resultado.fecha ?: "No detectada", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = "¿Los datos son correctos?", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (resultado.tipo != TipoMovimientoDetectado.INDETERMINADO) {
                            comprobanteUriConfirmado = comprobanteCompartido
                            comprobanteConfirmado = resultado
                            comprobanteResultado = null
                        }
                    }
                ) {
                    Text("Confirmar", color = primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { comprobanteResultado = null }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    when (pantalla) {
        PantallaInicial.SPLASH -> {
            PantallaSplash()
        }

        PantallaInicial.LOGIN -> {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(450)) + slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(450)
                )
            ) {
                PantallaLogin(
                    cargando = cargando,
                    mensaje = mensaje,
                    callbackManager = callbackManager,
                    onIniciarSesion = { email, password ->
                        loginViewModel.iniciarSesion(email = email, password = password)
                    },
                    onIniciarSesionGoogle = { idToken ->
                        loginViewModel.iniciarSesionConGoogle(idToken)
                    },
                    onIniciarSesionFacebook = { accessToken ->
                        loginViewModel.iniciarSesionConFacebook(accessToken)
                    },
                    onLimpiarMensaje = { loginViewModel.limpiarMensaje() }
                )
            }
        }

        PantallaInicial.ONBOARDING_BIENVENIDA -> {
            BienvenidaScreen(
                onComenzarClick = {
                    loginViewModel.marcarBienvenidaVista {
                        pantalla = PantallaInicial.APLICACION
                    }
                }
            )
        }

        PantallaInicial.APLICACION -> {
            Box(modifier = Modifier.fillMaxSize()) {
                AppNavigation(
                    context = LocalContext.current,
                    dashboardViewModel = dashboardViewModel,
                    movimientosViewModel = movimientosViewModel,
                    loginViewModel = loginViewModel,
                    asambleaIdNotificacion = asambleaIdNotificacion,
                    notificacionIdNotificacion = notificacionIdNotificacion,
                    comprobanteConfirmado = comprobanteConfirmado,
                    comprobanteUri = comprobanteUriConfirmado,
                    onComprobanteProcesado = { comprobanteConfirmado = null },
                    darkTheme = darkTheme,
                    onThemeToggle = onThemeToggle
                )

                AnimatedVisibility(
                    visible = mostrarBanner,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(animationSpec = tween(300)),
                    exit = fadeOut(animationSpec = tween(300)),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 12.dp, start = 12.dp, end = 12.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = primaryColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Filled.Notifications,
                                        contentDescription = "Nueva notificación",
                                        tint = Color.White,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tituloBanner,
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = mensajeBanner,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarDialogoAutostart) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAutostart = false },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("🔔 Notificaciones de SIGEFIV", color = MaterialTheme.colorScheme.onSurface) },
            text = {
                Text(
                    "Para recibir las notificaciones de SIGEFIV aunque la aplicación esté cerrada, te recomendamos activar el inicio automático.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoAutostart = false
                        context.getSharedPreferences("sigefiv_config", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("autostart_mostrado", true)
                            .apply()
                        abrirAutostartXiaomi(context)
                    }
                ) {
                    Text("Activar ahora", color = primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoAutostart = false
                        context.getSharedPreferences("sigefiv_config", Context.MODE_PRIVATE)
                            .edit()
                            .putBoolean("autostart_mostrado", true)
                            .apply()
                    }
                ) {
                    Text("Ahora no", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    if (errorConexion != null) {
        AlertDialog(
            onDismissRequest = { NetworkErrorManager.limpiarError() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = { Text("Problema de conexión", color = MaterialTheme.colorScheme.onSurface) },
            text = { Text(text = errorConexion ?: "", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            confirmButton = {
                Button(
                    onClick = { NetworkErrorManager.limpiarError() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    if (mostrarAlertaExpulsion) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = tituloExpulsion,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    text = mensajeExpulsionDialogo,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarAlertaExpulsion = false
                        val sessionManager = SessionManager(context)
                        GlobalScope.launch(Dispatchers.IO) {
                            sessionManager.cerrarSesion()
                        }
                        pantalla = PantallaInicial.LOGIN
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Entendido e iniciar sesión", fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@Composable
fun logoGrupo21PorTemporada(): Int {
    return when (SeasonalTheme.getSeason()) {
        AppSeason.NORMAL -> R.drawable.logo_grupo_residencial
        AppSeason.FIESTAS_PATRIAS -> R.drawable.logo_grupo_21_fiestas_patrias
        AppSeason.NAVIDAD -> R.drawable.logo_grupo_21_navidad
    }
}

/*
|--------------------------------------------------------------------------
| PANTALLA SPLASH
|--------------------------------------------------------------------------
*/

@Composable
fun PantallaSplash() {
    val primaryColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Logo central con anillo suave de elevación
            Surface(
                modifier = Modifier.size(140.dp),
                shape = CircleShape,
                color = primaryColor.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.2f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Image(
                        painter = painterResource(id = logoGrupo21PorTemporada()),
                        contentDescription = "Logo Grupo Residencial 21",
                        modifier = Modifier.size(115.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "SIGEFIV",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Gestión Vecinal y Financiera",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Indicador sutil de carga
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = primaryColor,
                strokeWidth = 2.5.dp
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Grupo Residencial 21",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = primaryColor
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Versión oficial del sistema",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun Caracteristica(
    icono: ImageVector,
    titulo: String,
    primaryColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(38.dp),
                shape = RoundedCornerShape(10.dp),
                color = primaryColor.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, primaryColor.copy(alpha = 0.22f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icono,
                        contentDescription = titulo,
                        tint = primaryColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| PANTALLA LOGIN
|--------------------------------------------------------------------------
*/

@Composable
fun PantallaLogin(
    cargando: Boolean,
    mensaje: String?,
    callbackManager: CallbackManager,
    onIniciarSesion: (String, String) -> Unit,
    onIniciarSesionGoogle: (String) -> Unit,
    onIniciarSesionFacebook: (String) -> Unit,
    onLimpiarMensaje: () -> Unit
) {
    var usuario by remember { mutableStateOf("admin@sigefiv.com") }
    var password by remember { mutableStateOf("12345678") }
    var mostrarPassword by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }
    val primaryColor = MaterialTheme.colorScheme.primary

    val activity = context as? Activity

    DisposableEffect(callbackManager) {
        val loginManager = LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d("SIGEFIV_FB", "Token Facebook recibido, iniciando Firebase...")
                onIniciarSesionFacebook(result.accessToken.token)
            }

            override fun onCancel() {
                onLimpiarMensaje()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(
                    context,
                    "Error al conectar con Facebook: ${error.localizedMessage ?: "Intenta nuevamente"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        onDispose {
            loginManager.unregisterCallback(callbackManager)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = logoGrupo21PorTemporada()),
                contentDescription = "Logo Grupo Residencial 21",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Bienvenido!",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ingresa tus credenciales para continuar",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    OutlinedTextField(
                        value = usuario,
                        onValueChange = {
                            usuario = it
                            if (mensaje != null) onLimpiarMensaje()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !cargando,
                        label = { Text("Usuario o correo") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.MailOutline,
                                contentDescription = "Usuario o correo",
                                tint = primaryColor
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = coloresCampoLogin()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            if (mensaje != null) onLimpiarMensaje()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        enabled = !cargando,
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = "Contraseña",
                                tint = primaryColor
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { mostrarPassword = !mostrarPassword },
                                enabled = !cargando
                            ) {
                                Icon(
                                    imageVector = if (mostrarPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (mostrarPassword) "Ocultar" else "Mostrar",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = coloresCampoLogin()
                    )

                    if (!mensaje.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mensaje,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {}, enabled = !cargando) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                color = primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onIniciarSesion(usuario.trim(), password) },
                        enabled = !cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Iniciar sesión",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        )
                        Text(
                            text = "  o  ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(context.getString(R.string.default_web_client_id))
                                        .setAutoSelectEnabled(false)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(context = context, request = request)
                                    val credential = result.credential

                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        try {
                                            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                            onIniciarSesionGoogle(googleCredential.idToken)
                                        } catch (e: GoogleIdTokenParsingException) {
                                            e.printStackTrace()
                                            onLimpiarMensaje()
                                        }
                                    } else {
                                        onLimpiarMensaje()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    onLimpiarMensaje()
                                }
                            }
                        },
                        enabled = !cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "G   Continuar con Google",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            onLimpiarMensaje()
                            if (activity != null) {
                                LoginManager.getInstance().logInWithReadPermissions(
                                    activity,
                                    listOf("email", "public_profile")
                                )
                            }
                        },
                        enabled = !cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "f   Continuar con Facebook",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Sistema de Gestión Financiera Vecinal",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "SIGEFIV • Grupo Residencial 21",
                color = primaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| COLORES DE CAMPOS DE LOGIN
|--------------------------------------------------------------------------
*/

@Composable
private fun coloresCampoLogin() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f),
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface
)