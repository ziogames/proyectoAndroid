package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sigefiv.app.FcmTokenManager
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.repository.AuthRepository
import com.sigefiv.app.data.repository.UsuarioRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.gms.tasks.Tasks
import retrofit2.HttpException
import java.io.IOException

class LoginViewModel(
    private val sessionManager: SessionManager,
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext

    private val authRepository = AuthRepository(
        ApiClient.create(appContext)
    )

    private val usuarioRepository = UsuarioRepository(
        ApiClient.usuarioApi(appContext)
    )

    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje

    /*
    --------------------------------------------------------------------------
    | LOGIN CORRECTO Y ESTADO DE BIENVENIDA
    --------------------------------------------------------------------------
    */
    private val _loginCorrecto = MutableStateFlow(false)
    val loginCorrecto: StateFlow<Boolean> = _loginCorrecto

    private val _bienvenidaVista = MutableStateFlow(true)
    val bienvenidaVista: StateFlow<Boolean> = _bienvenidaVista

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    init {
        verificarSesionExistente()
    }

    /*
    |--------------------------------------------------------------------------
    | VERIFICAR SESIÓN EXISTENTE
    |--------------------------------------------------------------------------
    */
    private fun verificarSesionExistente() {
        viewModelScope.launch {
            try {
                val tokenGuardado = sessionManager.token.first()
                val bienvenidaGuardada = sessionManager.bienvenidaVista.first()

                if (!tokenGuardado.isNullOrBlank()) {
                    _token.value = tokenGuardado
                    _bienvenidaVista.value = bienvenidaGuardada
                    _loginCorrecto.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | INICIO DE SESIÓN CON CORREO Y CONTRASEÑA
    |--------------------------------------------------------------------------
    */
    fun iniciarSesion(
        email: String,
        password: String
    ) {
        if (email.isBlank() || password.isBlank()) {
            _mensaje.value = "Ingresa tu correo y contraseña."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null
            _loginCorrecto.value = false

            try {
                val respuesta = authRepository.login(
                    email = email,
                    password = password
                )

                if (respuesta.success) {
                    val tokenRecibido = respuesta.token
                    val usuario = respuesta.usuario

                    if (!tokenRecibido.isNullOrBlank() && usuario != null) {
                        _token.value = tokenRecibido
                        _bienvenidaVista.value = usuario.bienvenidaVista

                        sessionManager.guardarSesion(
                            userId = usuario.id,
                            token = tokenRecibido,
                            email = usuario.email,
                            nombre = usuario.name,
                            seudonimo = usuario.seudonimo,
                            rol = usuario.rol,
                            bienvenidaVista = usuario.bienvenidaVista
                        )

                        FcmTokenManager.registrarToken(appContext)
                        _loginCorrecto.value = true
                    } else {
                        _mensaje.value = "El servidor no devolvió todos los datos necesarios."
                    }
                } else {
                    _mensaje.value = respuesta.message
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    401 -> _mensaje.value = "No se encontró el usuario o contraseña incorrecta."
                    403 -> _mensaje.value = "Tu cuenta se encuentra bloqueada. Contacta al administrador."
                    422 -> _mensaje.value = "Los datos ingresados no son válidos."
                    else -> _mensaje.value = "Error de servidor (${e.code()}). Inténtalo nuevamente."
                }
            } catch (e: IOException) {
                _mensaje.value = "Sin conexión con el servidor. Revisa tu acceso a internet."
            } catch (e: Exception) {
                e.printStackTrace()
                _mensaje.value = "No se pudo iniciar sesión. Verifica tus datos."
            } finally {
                _cargando.value = false
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | INICIO DE SESIÓN CON GOOGLE + FIREBASE
    |--------------------------------------------------------------------------
    |
    | Flujo:
    |
    | Google ID Token
    |       ↓
    | Firebase Authentication
    |       ↓
    | Firebase ID Token
    |       ↓
    | Laravel
    |
    |--------------------------------------------------------------------------
    */
    fun iniciarSesionConGoogle(
        idToken: String
    ) {
        if (idToken.isBlank()) {
            _mensaje.value = "No se pudo obtener la identificación de Google."
            return
        }

        viewModelScope.launch {
            _cargando.value = true
            _mensaje.value = null
            _loginCorrecto.value = false

            try {

                /*
                 * 1. Convertimos el Google ID Token en credencial Firebase.
                 */
                val credential = GoogleAuthProvider.getCredential(
                    idToken,
                    null
                )

                /*
                 * 2. Iniciamos sesión en Firebase.
                 */
                withContext(Dispatchers.IO) {
                    Tasks.await(
                        firebaseAuth.signInWithCredential(credential)
                    )
                }

                /*
                 * 3. Obtenemos el usuario directamente desde FirebaseAuth.
                 */
                val firebaseUser = firebaseAuth.currentUser

                if (firebaseUser == null) {
                    _mensaje.value =
                        "Firebase autenticó la cuenta pero no devolvió el usuario."
                    return@launch
                }

                println(
                    "SIGEFIV FIREBASE UID: ${firebaseUser.uid}"
                )

                println(
                    "SIGEFIV FIREBASE EMAIL: ${firebaseUser.email}"
                )

                /*
                 * 4. Solicitamos el Firebase ID Token.
                 *
                 * Primero intentamos obtener un token actualizado.
                 */
                val firebaseTokenResult = withContext(Dispatchers.IO) {
                    Tasks.await(
                        firebaseUser.getIdToken(true)
                    )
                }

                val firebaseIdToken = firebaseTokenResult.token

                println(
                    "SIGEFIV FIREBASE TOKEN RESULT: " +
                            "presente=${!firebaseIdToken.isNullOrBlank()} " +
                            "longitud=${firebaseIdToken?.length ?: 0}"
                )

                /*
                 * Nunca mostramos el token completo por seguridad.
                 */
                if (firebaseIdToken.isNullOrBlank()) {
                    _mensaje.value =
                        "Firebase autenticó la cuenta, pero no pudo generar el token de sesión."

                    println(
                        "SIGEFIV FIREBASE ERROR: getIdToken() devolvió token vacío"
                    )

                    return@launch
                }

                /*
                 * 5. Enviamos el Firebase ID Token a Laravel.
                 */
                println(
                    "SIGEFIV AUTH: enviando Firebase ID Token a Laravel"
                )

                val respuesta = authRepository.loginConGoogle(
                    idToken = firebaseIdToken
                )

                /*
                 * 6. Laravel devuelve el token Sanctum.
                 */
                if (respuesta.success) {

                    val tokenRecibido = respuesta.token
                    val usuario = respuesta.usuario

                    if (!tokenRecibido.isNullOrBlank() && usuario != null) {

                        _token.value = tokenRecibido
                        _bienvenidaVista.value = usuario.bienvenidaVista

                        sessionManager.guardarSesion(
                            userId = usuario.id,
                            token = tokenRecibido,
                            email = usuario.email,
                            nombre = usuario.name,
                            seudonimo = usuario.seudonimo,
                            rol = usuario.rol,
                            bienvenidaVista = usuario.bienvenidaVista
                        )

                        FcmTokenManager.registrarToken(appContext)

                        _loginCorrecto.value = true

                    } else {

                        _mensaje.value =
                            "El servidor no devolvió todos los datos necesarios."
                    }

                } else {

                    _mensaje.value = respuesta.message
                }

            } catch (e: HttpException) {

                when (e.code()) {

                    401 -> _mensaje.value =
                        "No se pudo autenticar la cuenta de Google."

                    403 -> _mensaje.value =
                        "Tu cuenta de SIGEFIV está bloqueada."

                    422 -> _mensaje.value =
                        "Los datos de autenticación no son válidos."

                    else -> _mensaje.value =
                        "Error al autenticar con Google (${e.code()})."
                }

            } catch (e: IOException) {

                _mensaje.value =
                    "Sin conexión con el servidor. Revisa tu internet."

            } catch (e: Exception) {

                e.printStackTrace()

                _mensaje.value =
                    "No fue posible autenticar la cuenta de Google con Firebase."

                println(
                    "SIGEFIV FIREBASE EXCEPTION: " +
                            "${e.javaClass.simpleName}: ${e.message}"
                )

            } finally {

                _cargando.value = false
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | MARCAR BIENVENIDA VISTA
    |--------------------------------------------------------------------------
    */
    fun marcarBienvenidaVista(onCompletado: () -> Unit = {}) {
        viewModelScope.launch {
            sessionManager.marcarBienvenidaVistaLocal()
            _bienvenidaVista.value = true

            try {
                usuarioRepository.marcarBienvenidaVista()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            onCompletado()
        }
    }

    /*
    |--------------------------------------------------------------------------
    | CERRAR SESIÓN
    |--------------------------------------------------------------------------
    */
    fun cerrarSesion() {
        viewModelScope.launch {
            _cargando.value = true

            try {
                try {
                    authRepository.cerrarSesion()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                /*
                 * Cerramos también la sesión de Firebase.
                 */
                firebaseAuth.signOut()

            } finally {
                sessionManager.cerrarSesion()

                _token.value = null
                _loginCorrecto.value = false
                _bienvenidaVista.value = true
                _mensaje.value = null
                _cargando.value = false
            }
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
