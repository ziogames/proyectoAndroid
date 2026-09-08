package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.FcmTokenManager
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.repository.AuthRepository
import com.sigefiv.app.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
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
    | INICIO DE SESIÓN CON GOOGLE
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
                val respuesta = authRepository.loginConGoogle(
                    idToken = idToken
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
                    401 -> _mensaje.value = "No se pudo autenticar la cuenta de Google."
                    403 -> _mensaje.value = "Tu cuenta de SIGEFIV está bloqueada."
                    else -> _mensaje.value = "Error al autenticar con Google (${e.code()})."
                }
            } catch (e: IOException) {
                _mensaje.value = "Sin conexión con el servidor. Revisa tu internet."
            } catch (e: Exception) {
                e.printStackTrace()
                _mensaje.value = "No fue posible iniciar sesión con Google. Inténtalo nuevamente."
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
                FcmTokenManager.desactivarToken(appContext)
                try {
                    authRepository.cerrarSesion()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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