package com.sigefiv.app.viewmodel

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ZoeConsultaRequest
import com.sigefiv.app.data.model.ZoeEstadistica
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.File
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import android.speech.tts.UtteranceProgressListener

data class ZoeMensaje(
    val texto: String,
    val esUsuario: Boolean,
    val tipoMovimiento: String? = null,
    val estadistica: ZoeEstadistica? = null,
    val hora: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
)

class ZoeViewModel(
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext

    private val api = ApiClient.create(appContext)

    /*
     * Voz de ZOE
     *
     * Usamos el TextToSpeech nativo de Android.
     * No requiere servidor adicional, n8n, Laravel ni otra dependencia.
     */
    private var tts: TextToSpeech? = null
    private var ttsListo = false
    private val _hablando = MutableStateFlow(false)

    val hablando: StateFlow<Boolean> =
        _hablando.asStateFlow()

    init {
        inicializarVoz()
    }

    private val _mensajes = MutableStateFlow(
        listOf(
            ZoeMensaje(
                texto = "Hola, soy ZOE. Estoy lista para ayudarte con la información de SIGEFIV.",
                esUsuario = false
            )
        )
    )

    val mensajes: StateFlow<List<ZoeMensaje>> =
        _mensajes.asStateFlow()

    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()

    /*
     * Inicializa la voz de ZOE y hace que diga sus primeras palabras.
     */
    private fun inicializarVoz() {

        tts = TextToSpeech(appContext) { status ->

            if (status == TextToSpeech.SUCCESS) {

                val resultadoIdioma = tts?.setLanguage(
                    Locale("es", "PE")
                )

                tts?.setSpeechRate(0.95f)
                tts?.setPitch(1.0f)
                tts?.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {

                        override fun onStart(utteranceId: String?) {
                            _hablando.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _hablando.value = false
                        }

                        override fun onError(utteranceId: String?) {
                            _hablando.value = false
                        }
                    }
                )

                ttsListo =
                    resultadoIdioma != TextToSpeech.LANG_MISSING_DATA &&
                            resultadoIdioma != TextToSpeech.LANG_NOT_SUPPORTED

                /*
                 * Primera frase hablada de ZOE.
                 *
                 * Se ejecuta solamente cuando el motor TTS
                 * ya está listo.
                 */

            }
        }
    }

    /*
     * Hace que ZOE hable.
     *
     * QUEUE_FLUSH evita que una respuesta vieja quede
     * esperando detrás de otra.
     */
    private fun hablar(texto: String) {
        if (!ttsListo || texto.isBlank()) return

        val textoVoz = texto
            .replace(Regex("[\\p{So}\\p{Cn}]"), " ")
            .replace("SIGEFIV", "sigefiv", ignoreCase = true)
            .replace("**", "")
            .replace("__", "")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (textoVoz.isBlank()) return

        tts?.speak(
            textoVoz,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "ZOE_${System.currentTimeMillis()}"
        )
    }

    private fun textoParaVoz(texto: String): String {
        var limpio = texto
            .replace(Regex("[\\p{So}\\p{Cn}]"), " ")
            .replace("**", "")
            .replace("__", "")
            .replace("SIGEFIV", "sigefiv", ignoreCase = true)
            .replace(Regex("\\s+"), " ")
            .trim()

        val tieneLista =
            limpio.contains("lista de", ignoreCase = true) ||
                    limpio.contains("detalle de", ignoreCase = true) ||
                    limpio.contains("movimientos de", ignoreCase = true)

        if (tieneLista) {
            val posicionDosPuntos = limpio.indexOf(":")

            if (posicionDosPuntos > 0) {
                limpio = limpio.substring(0, posicionDosPuntos)
            }

            limpio = limpio
                .substringBefore("\n")
                .trim()
        }

        return limpio
    }
    fun enviarConsulta(
        mensaje: String,
        hablarRespuesta: Boolean = false
    ) {

        val consulta = mensaje.trim()

        if (consulta.isEmpty() || _cargando.value) {
            return
        }

        /*
         * Determinamos el tipo de movimiento.
         */
        val tipoMovimiento = when {

            consulta.contains(
                "egreso",
                ignoreCase = true
            ) ||
                    consulta.contains(
                        "egresos",
                        ignoreCase = true
                    ) -> "Egreso"

            consulta.contains(
                "ingreso",
                ignoreCase = true
            ) ||
                    consulta.contains(
                        "ingresos",
                        ignoreCase = true
                    ) -> "Ingreso"

            else -> null
        }

        _error.value = null

        /*
         * Pregunta del usuario.
         */
        _mensajes.value = _mensajes.value +
                ZoeMensaje(
                    texto = consulta,
                    esUsuario = true
                )

        viewModelScope.launch {

            _cargando.value = true

            try {

                /*
                 * Primero usamos el flujo actual de ZOE.
                 *
                 * NO modificamos la comunicación existente.
                 */
                val respuesta = api.consultarZoeN8n(
                    ZoeConsultaRequest(
                        mensaje = consulta
                    )
                )

                if (respuesta.success) {

                    val respuestaTexto =
                        respuesta.respuesta
                            ?.trim()
                            ?.replace("**", "")
                            ?.replace("__", "")
                            ?.takeIf {
                                it.isNotEmpty()
                            }
                            ?: "ZOE recibió la consulta, pero no devolvió un mensaje."

                    /*
                     * Si ZOE devuelve una estadística,
                     * la guardamos dentro del mensaje.
                     */
                    _mensajes.value = _mensajes.value +
                            ZoeMensaje(
                                texto = respuestaTexto,
                                esUsuario = false,
                                tipoMovimiento = tipoMovimiento,
                                estadistica = respuesta.estadistica
                            )

                    /*
                     * ZOE habla exactamente la misma respuesta
                     * que acaba de mostrar en pantalla.
                     */
                    if (hablarRespuesta) {
                        hablar(textoParaVoz(respuestaTexto))
                    }

                } else {

                    val mensajeError =
                        respuesta.message
                            ?.trim()
                            ?.takeIf {
                                it.isNotEmpty()
                            }
                            ?: respuesta.mensaje
                                ?.trim()
                                ?.takeIf {
                                    it.isNotEmpty()
                                }
                            ?: "No fue posible procesar la consulta."

                    _error.value = mensajeError

                    _mensajes.value = _mensajes.value +
                            ZoeMensaje(
                                texto = mensajeError,
                                esUsuario = false,
                                tipoMovimiento = tipoMovimiento
                            )

                    if (hablarRespuesta) {
                        hablar(mensajeError)
                    }
                }

            } catch (e: Exception) {

                val mensajeError = when {

                    e.message?.contains("401") == true ->
                        "La sesión no es válida. Inicia sesión nuevamente."

                    e.message?.contains(
                        "timeout",
                        ignoreCase = true
                    ) == true ->
                        "ZOE tardó demasiado en responder. Inténtalo nuevamente."

                    else ->
                        "No se pudo conectar con ZOE. Verifica que el servidor SIGEFIV esté funcionando."
                }

                _error.value = mensajeError

                _mensajes.value = _mensajes.value +
                        ZoeMensaje(
                            texto = mensajeError,
                            esUsuario = false,
                            tipoMovimiento = tipoMovimiento
                        )

                if (hablarRespuesta) {
                    hablar(mensajeError)
                }

            } finally {

                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    override fun onCleared() {
        /*
         * Liberamos el motor TTS cuando se destruye el ViewModel.
         */
        tts?.stop()
        _hablando.value = false
        tts?.shutdown()
        tts = null
        ttsListo = false

        super.onCleared()
    }
    fun enviarAudio(file: File) {
        if (_cargando.value) return

        viewModelScope.launch {
            _cargando.value = true

            try {
                val requestBody =
                    file.asRequestBody("audio/wav".toMediaType())

                val part =
                    MultipartBody.Part.createFormData(
                        "audio",
                        file.name,
                        requestBody
                    )

                val respuesta = api.enviarVoz(part)

                val texto =
                    respuesta.texto
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }

                if (respuesta.success && texto != null) {
                    file.delete()

                    _cargando.value = false

                    // Entregamos la transcripción al flujo normal de ZOE.
                    enviarConsulta(
                        texto,
                        hablarRespuesta = true
                    )

                    return@launch
                }

                val mensajeError =
                    respuesta.message
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }
                        ?: "No pude transcribir el audio."

                _error.value = mensajeError

                _mensajes.value =
                    _mensajes.value +
                            ZoeMensaje(
                                texto = mensajeError,
                                esUsuario = false
                            )

                hablar(mensajeError)

            } catch (e: Exception) {

                val mensajeError =
                    "No se pudo procesar el audio. Verifica la conexión con SIGEFIV."

                _error.value = mensajeError

                _mensajes.value =
                    _mensajes.value +
                            ZoeMensaje(
                                texto = mensajeError,
                                esUsuario = false
                            )

                hablar(mensajeError)

            } finally {
                _cargando.value = false

                if (file.exists()) {
                    file.delete()
                }
            }
        }
    }
    fun detenerVoz() {
        tts?.stop()
        _hablando.value = false
    }
}
