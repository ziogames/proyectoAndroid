package com.sigefiv.app

import android.os.Bundle
import android.content.Intent

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle

import androidx.compose.runtime.rememberCoroutineScope

import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException

import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

import com.sigefiv.app.navigation.AppNavigation
import com.sigefiv.app.screens.bienvenida.BienvenidaScreen
import com.sigefiv.app.ui.theme.SIGEFIVTheme
import com.sigefiv.app.viewmodel.DashboardViewModel
import com.sigefiv.app.viewmodel.DashboardViewModelFactory
import com.sigefiv.app.viewmodel.LoginViewModel
import com.sigefiv.app.viewmodel.LoginViewModelFactory
import com.sigefiv.app.viewmodel.MovimientosViewModel
import com.sigefiv.app.viewmodel.MovimientosViewModelFactory

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
|--------------------------------------------------------------------------
| COLORES SIGEFIV (ESTILO DASHBOARD / MODERNO)
|--------------------------------------------------------------------------
*/

private val FondoSIGEFIV = Color(0xFFF8FAFC)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val VerdePrincipal = Color(0xFF15803D)
private val VerdeSuave = Color(0xFFDCFCE7)
private val Blanco = Color(0xFFFFFFFF)
private val TextoPrincipal = Color(0xFF0F172A)
private val GrisClaro = Color(0xFF64748B)
private val GrisBorde = Color(0xFFCBD5E1)
private val Rojo = Color(0xFFDC2626)

private const val GOOGLE_WEB_CLIENT_ID =
    "129407987989-9fb6ecp2f8n8fdpfjo9easo5vpsb7s15.apps.googleusercontent.com"

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

/*
|--------------------------------------------------------------------------
| ACTIVITY PRINCIPAL
|--------------------------------------------------------------------------
*/

class MainActivity : ComponentActivity() {

    private var asambleaIdNotificacion = mutableStateOf<Int?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

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
            intent?.getStringExtra(
                SIGEFIVFirebaseMessagingService.EXTRA_ASAMBLEA_ID
            )?.toIntOrNull()

        setContent {
            SIGEFIVTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FondoSIGEFIV
                ) {
                    AppSIGEFIV(
                        loginViewModel = loginViewModel,
                        dashboardViewModel = dashboardViewModel,
                        movimientosViewModel = movimientosViewModel,
                        asambleaIdNotificacion = asambleaIdNotificacion.value
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        asambleaIdNotificacion.value =
            intent?.getStringExtra(
                SIGEFIVFirebaseMessagingService.EXTRA_ASAMBLEA_ID
            )?.toIntOrNull()
    }
}

/*
|--------------------------------------------------------------------------
| CONTROLADOR DEL FLUJO PRINCIPAL
|--------------------------------------------------------------------------
*/

@Composable
fun AppSIGEFIV(
    loginViewModel: LoginViewModel,
    dashboardViewModel: DashboardViewModel,
    movimientosViewModel: MovimientosViewModel,
    asambleaIdNotificacion: Int? = null
) {
    var pantalla by remember { mutableStateOf(PantallaInicial.SPLASH) }

    val loginCorrecto by loginViewModel.loginCorrecto.collectAsStateWithLifecycle()
    val bienvenidaVista by loginViewModel.bienvenidaVista.collectAsStateWithLifecycle()
    val mensaje by loginViewModel.mensaje.collectAsStateWithLifecycle()
    val cargando by loginViewModel.cargando.collectAsStateWithLifecycle()

    // Sincroniza cambios de estado de login y onboarding
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

    // Retardo inicial del Splash
    LaunchedEffect(Unit) {
        delay(2000)
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

    when (pantalla) {
        PantallaInicial.SPLASH -> {
            PantallaSplash()
        }

        PantallaInicial.LOGIN -> {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(450)) +
                        slideInVertically(
                            initialOffsetY = { 40 },
                            animationSpec = tween(450)
                        )
            ) {
                PantallaLogin(
                    cargando = cargando,
                    mensaje = mensaje,
                    onIniciarSesion = { email, password ->
                        loginViewModel.iniciarSesion(
                            email = email,
                            password = password
                        )
                    },
                    onIniciarSesionGoogle = { idToken ->
                        loginViewModel.iniciarSesionConGoogle(idToken)
                    },
                    onIniciarSesionFacebook = {
                        // Reservado para futura implementación
                    },
                    onLimpiarMensaje = {
                        loginViewModel.limpiarMensaje()
                    }
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
            AppNavigation(
                context = LocalContext.current,
                dashboardViewModel = dashboardViewModel,
                movimientosViewModel = movimientosViewModel,
                loginViewModel = loginViewModel,
                asambleaIdNotificacion = asambleaIdNotificacion
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| PANTALLA SPLASH DE BIENVENIDA
|--------------------------------------------------------------------------
*/

@Composable
fun PantallaSplash() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoSIGEFIV)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.logo_grupo_residencial),
                contentDescription = "Logo Grupo Residencial 21",
                modifier = Modifier.size(160.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tu comunidad,",
                    color = TextoPrincipal,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "siempre conectada",
                    color = VerdePrincipal,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Caracteristica(icono = Icons.Outlined.Info, titulo = "Información clara")
                Caracteristica(icono = Icons.Outlined.VerifiedUser, titulo = "Gestión transparente")
                Caracteristica(icono = Icons.Outlined.AccountBalance, titulo = "Comunicación directa")
                Caracteristica(icono = Icons.Outlined.Groups, titulo = "Comunidad unida")
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Sistema de Gestión Financiera Vecinal",
                color = GrisClaro,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "SIGEFIV • Grupo Residencial 21",
                color = VerdePrincipal,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

/*
|--------------------------------------------------------------------------
| CARACTERÍSTICA (SPLASH)
|--------------------------------------------------------------------------
*/

@Composable
fun Caracteristica(
    icono: ImageVector,
    titulo: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(VerdeSuave, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = titulo,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = titulo,
                color = TextoPrincipal,
                fontSize = 15.sp,
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
    onIniciarSesion: (String, String) -> Unit,
    onIniciarSesionGoogle: (String) -> Unit,
    onIniciarSesionFacebook: () -> Unit,
    onLimpiarMensaje: () -> Unit
) {
    var usuario by remember { mutableStateOf("admin@sigefiv.com") }
    var password by remember { mutableStateOf("12345678") }
    var mostrarPassword by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = remember(context) { CredentialManager.create(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FondoSIGEFIV)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_grupo_residencial),
                contentDescription = "Logo Grupo Residencial 21",
                modifier = Modifier.size(130.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "¡Bienvenido!",
                color = TextoPrincipal,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ingresa tus credenciales para continuar",
                color = GrisClaro,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    /*
                    --------------------------------------------------------
                    | USUARIO
                    --------------------------------------------------------
                    */
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
                                tint = VerdePrincipal
                            )
                        },
                        colors = coloresCampoLogin()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    /*
                    --------------------------------------------------------
                    | CONTRASEÑA
                    --------------------------------------------------------
                    */
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
                                tint = VerdePrincipal
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { mostrarPassword = !mostrarPassword },
                                enabled = !cargando
                            ) {
                                Icon(
                                    imageVector = if (mostrarPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (mostrarPassword) "Ocultar contraseña" else "Mostrar contraseña",
                                    tint = GrisClaro
                                )
                            }
                        },
                        visualTransformation = if (mostrarPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        colors = coloresCampoLogin()
                    )

                    /*
                    --------------------------------------------------------
                    | MENSAJE DE ERROR
                    --------------------------------------------------------
                    */
                    if (!mensaje.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = mensaje,
                            color = Rojo,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    /*
                    --------------------------------------------------------
                    | OLVIDASTE TU CONTRASEÑA
                    --------------------------------------------------------
                    */
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { },
                            enabled = !cargando
                        ) {
                            Text(
                                text = "¿Olvidaste tu contraseña?",
                                color = VerdePrincipal,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    /*
                    --------------------------------------------------------
                    | BOTÓN INICIAR SESIÓN
                    --------------------------------------------------------
                    */
                    Button(
                        onClick = {
                            onIniciarSesion(usuario.trim(), password)
                        },
                        enabled = !cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VerdePrincipal,
                            contentColor = Blanco
                        )
                    ) {
                        if (cargando) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Blanco,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Iniciar sesión",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Separador
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(GrisBorde)
                        )
                        Text(
                            text = "  o  ",
                            color = GrisClaro,
                            fontSize = 13.sp
                        )
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(GrisBorde)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    /*
                    --------------------------------------------------------
                    | GOOGLE (CORREGIDO CON GetGoogleIdOption)
                    --------------------------------------------------------
                    */
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    // Se desactiva filterByAuthorizedAccounts para que no falle al primer clic
                                    val googleIdOption = GetGoogleIdOption.Builder()
                                        .setFilterByAuthorizedAccounts(false)
                                        .setServerClientId(GOOGLE_WEB_CLIENT_ID)
                                        .setAutoSelectEnabled(false)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(
                                        context = context,
                                        request = request
                                    )

                                    val credential = result.credential

                                    if (
                                        credential is CustomCredential &&
                                        credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                                    ) {
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
                                } catch (e: GetCredentialException) {
                                    e.printStackTrace()
                                    onLimpiarMensaje()
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    onLimpiarMensaje()
                                }
                            }
                        },
                        enabled = !cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Blanco,
                            contentColor = TextoPrincipal
                        )
                    ) {
                        Text(
                            text = "G   Continuar con Google",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    /*
                    --------------------------------------------------------
                    | FACEBOOK
                    --------------------------------------------------------
                    */
                    OutlinedButton(
                        onClick = { onIniciarSesionFacebook() },
                        enabled = !cargando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Blanco,
                            contentColor = TextoPrincipal
                        )
                    ) {
                        Text(
                            text = "f   Continuar con Facebook",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sistema de Gestión Financiera Vecinal",
                color = GrisClaro,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "SIGEFIV • Grupo Residencial 21",
                color = VerdePrincipal,
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
    focusedBorderColor = VerdePrincipal,
    unfocusedBorderColor = GrisBorde,
    focusedLabelColor = VerdePrincipal,
    unfocusedLabelColor = GrisClaro,
    focusedTextColor = TextoPrincipal,
    unfocusedTextColor = TextoPrincipal,
    cursorColor = VerdePrincipal
)