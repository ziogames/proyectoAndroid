package com.sigefiv.app.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.Build
import androidx.core.content.ContextCompat
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ZoeConsultaRequest
import com.sigefiv.app.data.model.ZoeEstadistica
import com.sigefiv.app.screens.mascota.MascotaControlador
import com.sigefiv.app.screens.mascota.MascotaEvento
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

data class ZoeMensaje(
    val texto: String,
    val esUsuario: Boolean,
    val tipoMovimiento: String? = null,
    val estadistica: ZoeEstadistica? = null,
    val hora: String = SimpleDateFormat(
        "h:mm a",
        Locale.getDefault()
    ).format(Date())
)


class ZoeViewModel(
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext

    private val api = ApiClient.create(appContext)

    private val wsClient = OkHttpClient()

    private var zoeWebSocket: WebSocket? = null

    // Reproductor del WAV generado por Supertonic en el Gateway.
    private var reproductorAudio: MediaPlayer? = null
    private val audioHandler = Handler(Looper.getMainLooper())

    /*
     * ============================================================
     * CAPTURA DE VOZ — AUDIO ANDROID → ZOE GATEWAY
     * ============================================================
     *
     * PCM:
     * - 16 kHz
     * - mono
     * - 16 bits
     */

    private var audioRecord: AudioRecord? = null

    @Volatile
    private var grabando = false

    private val _escuchando = MutableStateFlow(false)

    val escuchando: StateFlow<Boolean> =
        _escuchando.asStateFlow()

    private companion object {
        const val ZOE_SAMPLE_RATE = 16_000
        const val ZOE_CHANNEL = AudioFormat.CHANNEL_IN_MONO
        const val ZOE_ENCODING = AudioFormat.ENCODING_PCM_16BIT
        const val ZOE_AUDIO_BUFFER = 2048
    }


    /*
     * ============================================================
     * VOZ DE ZOE
     * ============================================================
     *
     * Usamos el TextToSpeech nativo de Android.
     *
     * No requiere servidor adicional, n8n, Laravel ni otra
     * dependencia.
     */

    private var tts: TextToSpeech? = null

    private var ttsListo = false

    private val _hablando = MutableStateFlow(false)

    val hablando: StateFlow<Boolean> =
        _hablando.asStateFlow()


    init {
        inicializarVoz()
        conectarZoeWebSocket()
    }

    /*
     * ============================================================
     * MENSAJES
     * ============================================================
     */

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


    /*
     * ============================================================
     * ESTADO DE CARGA
     * ============================================================
     */

    private val _cargando = MutableStateFlow(false)

    val cargando: StateFlow<Boolean> =
        _cargando.asStateFlow()


    /*
     * ============================================================
     * ERRORES
     * ============================================================
     */

    private val _error = MutableStateFlow<String?>(null)

    val error: StateFlow<String?> =
        _error.asStateFlow()


    /*
     * ============================================================
     * INICIALIZAR VOZ
     * ============================================================
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

                        override fun onStart(
                            utteranceId: String?
                        ) {
                            _hablando.value = true

                            // ZOE está hablando
                            MascotaControlador.feliz()
                        }

                        override fun onDone(
                            utteranceId: String?
                        ) {
                            _hablando.value = false

                            // ZOE terminó de hablar
                            MascotaControlador.normal()
                        }

                        override fun onError(
                            utteranceId: String?
                        ) {
                            _hablando.value = false

                            // Volvemos al estado normal
                            MascotaControlador.normal()
                        }
                    }
                )

                ttsListo =
                    resultadoIdioma !=
                            TextToSpeech.LANG_MISSING_DATA &&
                            resultadoIdioma !=
                            TextToSpeech.LANG_NOT_SUPPORTED

                /*
                 * Primera frase hablada de ZOE.
                 *
                 * Se mantiene sin ejecución automática.
                 */
            }
        }
    }


    /*
     * ============================================================
     * HACER HABLAR A ZOE
     * ============================================================
     */

    private fun hablar(texto: String) {

        if (!ttsListo || texto.isBlank()) {
            return
        }

        val textoVoz = texto
            .replace(Regex("[\\p{So}\\p{Cn}]"), " ")
            .replace(
                "SIGEFIV",
                "sigefiv",
                ignoreCase = true
            )
            .replace("**", "")
            .replace("__", "")
            .replace(Regex("\\s+"), " ")
            .trim()

        if (textoVoz.isBlank()) {
            return
        }

        tts?.speak(
            textoVoz,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "ZOE_${System.currentTimeMillis()}"
        )
    }


    /*
     * ============================================================
     * PREPARAR TEXTO PARA VOZ
     * ============================================================
     */

    private fun numeroEnPalabras(numero: Long): String {
        if (numero == 0L) return "cero"
        if (numero < 0L) return "menos ${numeroEnPalabras(-numero)}"

        fun centenas(n: Int): String {
            if (n < 100) {
                val unidades = arrayOf(
                    "cero", "uno", "dos", "tres", "cuatro", "cinco",
                    "seis", "siete", "ocho", "nueve", "diez", "once",
                    "doce", "trece", "catorce", "quince", "dieciséis",
                    "diecisiete", "dieciocho", "diecinueve", "veinte",
                    "veintiuno", "veintidós", "veintitrés", "veinticuatro",
                    "veinticinco", "veintiséis", "veintisiete", "veintiocho",
                    "veintinueve"
                )
                if (n <= 29) return unidades[n]

                val decenas = arrayOf(
                    "", "", "", "treinta", "cuarenta", "cincuenta",
                    "sesenta", "setenta", "ochenta", "noventa"
                )
                val d = n / 10
                val u = n % 10
                return if (u == 0) decenas[d] else "${decenas[d]} y ${unidades[u]}"
            }

            if (n == 100) return "cien"

            val centenas = arrayOf(
                "", "ciento", "doscientos", "trescientos", "cuatrocientos",
                "quinientos", "seiscientos", "setecientos", "ochocientos",
                "novecientos"
            )
            val c = n / 100
            val resto = n % 100
            return if (resto == 0) centenas[c] else "${centenas[c]} ${centenas(resto)}"
        }

        val partes = mutableListOf<String>()
        var resto = numero

        if (resto >= 1_000_000_000L) {
            val milesDeMillones = resto / 1_000_000_000L
            partes += if (milesDeMillones == 1L) "mil millones"
            else "${numeroEnPalabras(milesDeMillones)} mil millones"
            resto %= 1_000_000_000L
        }

        if (resto >= 1_000_000L) {
            val millones = resto / 1_000_000L
            partes += if (millones == 1L) "un millón"
            else "${numeroEnPalabras(millones)} millones"
            resto %= 1_000_000L
        }

        if (resto >= 1_000L) {
            val miles = resto / 1_000L
            partes += if (miles == 1L) "mil"
            else "${centenas(miles.toInt())} mil"
            resto %= 1_000L
        }

        if (resto > 0) {
            partes += centenas(resto.toInt())
        }

        return partes.joinToString(" ")
    }

    private fun montoEnPalabras(enteroTexto: String, centimosTexto: String?): String {
        val entero = enteroTexto.replace(",", "").replace(".", "").toLongOrNull() ?: return enteroTexto
        val soles = numeroEnPalabras(entero)
        val resultadoSoles = if (entero == 1L) "$soles sol" else "$soles soles"

        val centimos = centimosTexto?.toIntOrNull() ?: 0
        if (centimos == 0) return resultadoSoles

        val palabraCentimos = numeroEnPalabras(centimos.toLong())
        val nombreCentimos = if (centimos == 1) "céntimo" else "céntimos"
        return "$resultadoSoles con $palabraCentimos $nombreCentimos"
    }

    private fun convertirNumerosParaVoz(texto: String): String {
        var resultado = texto

        // Primero convertimos importes en soles, por ejemplo:
        // S/ 1,567.40 -> mil quinientos sesenta y siete soles con cuarenta céntimos.
        val moneda = Regex(
            """S/\s*([0-9][0-9,]*(?:\.[0-9]{1,2})?)""",
            RegexOption.IGNORE_CASE
        )

        resultado = moneda.replace(resultado) { match ->
            val valor = match.groupValues[1]
            val partes = valor.split(".", limit = 2)
            montoEnPalabras(
                partes[0],
                partes.getOrNull(1)
            )
        }

        // Porcentajes: 25% -> veinticinco por ciento.
        resultado = Regex("""(\d[\d,]*(?:\.\d+)?)\s*%""").replace(resultado) { match ->
            val valor = match.groupValues[1]
            val numero = valor.replace(",", "").toDoubleOrNull()
            if (numero != null && numero % 1.0 == 0.0) {
                "${numeroEnPalabras(numero.toLong())} por ciento"
            } else {
                valor.replace(",", "") + " por ciento"
            }
        }

        // Números que todavía queden: 683.30 -> seiscientos ochenta y tres punto treinta.
        resultado = Regex("""\b\d[\d,.]*\b""").replace(resultado) { match ->
            val valor = match.value
            val normalizado = valor.replace(",", "")
            val partes = normalizado.split(".", limit = 2)
            val entero = partes[0].toLongOrNull()

            if (entero == null) {
                valor
            } else if (partes.size == 2) {
                val decimal = partes[1]
                "${numeroEnPalabras(entero)} punto ${decimal.map { numeroEnPalabras((it - '0').toLong()) }.joinToString(" ")}"
            } else {
                numeroEnPalabras(entero)
            }
        }

        // Operadores habituales de los cálculos financieros.
        resultado = resultado
            .replace(Regex("\\s*\\+\\s*"), " más ")
            .replace(Regex("\\s*=\\s*"), " igual a ")
            .replace(Regex("\\s-\\s"), " menos ")
            .replace(Regex("\\s+"), " ")
            .trim()

        return resultado
    }

    /*
     * Detecta respuestas que contienen una lista de movimientos.
     *
     * La lista completa sigue mostrándose en pantalla,
     * pero NO debe ser leída completa por la voz de ZOE.
     */
    private fun esListaParaVoz(
        texto: String
    ): Boolean {

        val cantidadFechas =
            Regex("""\b\d{1,2}[-/]\d{1,2}[-/]\d{4}\b""")
                .findAll(texto)
                .count()

        val cantidadImportes =
            Regex("""S/\s*[0-9][0-9,.]*""", RegexOption.IGNORE_CASE)
                .findAll(texto)
                .count()

        val tieneIndicadorLista =
            texto.contains("lista de", ignoreCase = true) ||
                    texto.contains("detalle de", ignoreCase = true) ||
                    texto.contains("movimientos de", ignoreCase = true)

        return cantidadFechas >= 2 ||
                cantidadImportes >= 3 ||
                tieneIndicadorLista
    }

    /*
     * Genera una frase corta para la voz cuando la respuesta
     * contiene una lista. El detalle completo permanece en pantalla.
     */
    private fun resumenListaParaVoz(
        texto: String,
        tipoMovimiento: String?
    ): String {

        val cantidad =
            Regex("""\b\d{1,2}[-/]\d{1,2}[-/]\d{4}\b""")
                .findAll(texto)
                .count()

        val periodo =
            Regex(
                """(?i)\b(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\s+\d{4}\b"""
            )
                .find(texto)
                ?.value

        val tipo = when (tipoMovimiento?.lowercase(Locale.getDefault())) {
            "ingreso" -> "ingresos"
            "egreso" -> "egresos"
            else -> "movimientos"
        }

        val cantidadTexto =
            if (cantidad > 0) {
                if (cantidad == 1) {
                    "un registro de $tipo"
                } else {
                    "$cantidad $tipo"
                }
            } else {
                "varios $tipo"
            }

        return if (periodo != null) {
            "He encontrado $cantidadTexto correspondientes a $periodo. Te muestro el detalle en pantalla."
        } else {
            "He encontrado $cantidadTexto. Te muestro el detalle en pantalla."
        }
    }

    private fun textoParaVoz(
        texto: String,
        tipoMovimiento: String? = null
    ): String {

        var limpio = texto
            .replace(
                Regex("^\\s*(?:ZOE|Zoe)\\s*:\\s*"),
                ""
            )
            .replace(
                Regex("[\\p{So}\\p{Cn}]"),
                " "
            )
            .replace("**", "")
            .replace("__", "")
            .replace(
                "SIGEFIV",
                "sigefiv",
                ignoreCase = true
            )
            .replace(Regex("\\s+"), " ")
            .trim()

        /*
         * IMPORTANTE:
         * La respuesta completa se conserva para la interfaz.
         * Solo la versión enviada a voz se convierte en resumen
         * cuando detectamos una lista.
         */
        if (esListaParaVoz(limpio)) {
            return convertirNumerosParaVoz(
                resumenListaParaVoz(limpio, tipoMovimiento)
            )
        }

        return convertirNumerosParaVoz(limpio)
    }


    /*
     * ============================================================
     * CONSULTA PRINCIPAL DE ZOE
     * ============================================================
     */

    fun enviarConsulta(
        mensaje: String,
        hablarRespuesta: Boolean = false,
        responderPorGateway: Boolean = false
    ) {

        val consulta = mensaje.trim()

        if (
            consulta.isEmpty() ||
            _cargando.value
        ) {
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

        _mensajes.value =
            _mensajes.value +
                    ZoeMensaje(
                        texto = consulta,
                        esUsuario = true
                    )


        viewModelScope.launch {

            _cargando.value = true


            /*
             * ====================================================
             * ZOE ESTÁ PENSANDO
             * ====================================================
             *
             * IMPORTANTE:
             *
             * Usamos evento() y no pensando(), porque evento()
             * también genera la burbuja visual.
             */

            MascotaControlador.evento(
                MascotaEvento.ZOE_PENSANDO
            )


            try {

                /*
                 * =================================================
                 * FLUJO ACTUAL DE ZOE
                 * =================================================
                 *
                 * NO modificamos la comunicación existente.
                 */

                val respuesta =
                    api.consultarZoeN8n(
                        ZoeConsultaRequest(
                            mensaje = consulta
                        )
                    )


                /*
                 * =================================================
                 * RESPUESTA CORRECTA
                 * =================================================
                 */

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

                    _mensajes.value =
                        _mensajes.value +
                                ZoeMensaje(
                                    texto = respuestaTexto,
                                    esUsuario = false,
                                    tipoMovimiento = tipoMovimiento,
                                    estadistica =
                                        respuesta.estadistica
                                )


                    /*
                     * ZOE habla exactamente la misma
                     * respuesta que muestra en pantalla.
                     */

                    if (responderPorGateway) {

                        zoeWebSocket?.send(
                            org.json.JSONObject()
                                .put("type", "response_text")
                                .put("text", textoParaVoz(respuestaTexto, tipoMovimiento))
                                .toString()
                        )

                        android.util.Log.d(
                            "ZOE_WS",
                            "Respuesta de SIGEFIV enviada al Gateway para voz"
                        )

                    } else if (hablarRespuesta) {

                        hablar(
                            textoParaVoz(
                                respuestaTexto,
                                tipoMovimiento
                            )
                        )
                    }


                    /*
                     * =================================================
                     * ZOE RESPONDIENDO
                     * =================================================
                     *
                     * Este evento genera la burbuja:
                     *
                     * "Aquí tienes la respuesta."
                     */

                    // ============================================================
// LA RESPUESTA YA FUE MOSTRADA
// Dejamos de mostrar "ZOE está redactando..."
// ============================================================

                    _cargando.value = false

// ============================================================
// AHORA LA MASCOTA PUEDE HACER SU REACCIÓN
// sin mantener activo el indicador de escritura
// ============================================================

                    MascotaControlador.evento(
                        MascotaEvento.ZOE_RESPONDIENDO
                    )

                    delay(1500)

                    MascotaControlador.normal()


                } else {

                    /*
                     * =================================================
                     * ERROR DEVUELTO POR EL SERVIDOR
                     * =================================================
                     */

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


                    /*
                     * Generamos UNA SOLA reacción de error.
                     */

                    MascotaControlador.evento(
                        MascotaEvento.ERROR
                    )


                    _error.value = mensajeError


                    _mensajes.value =
                        _mensajes.value +
                                ZoeMensaje(
                                    texto = mensajeError,
                                    esUsuario = false,
                                    tipoMovimiento =
                                        tipoMovimiento
                                )


                    if (hablarRespuesta) {

                        hablar(
                            mensajeError
                        )
                    }


                    delay(1500)


                    MascotaControlador.normal()
                }


            } catch (e: Exception) {

                /*
                 * =================================================
                 * ERROR DE CONEXIÓN / EXCEPCIÓN
                 * =================================================
                 */

                MascotaControlador.evento(
                    MascotaEvento.ERROR
                )


                val mensajeError =
                    when {

                        e.message?.contains(
                            "401"
                        ) == true ->

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


                _mensajes.value =
                    _mensajes.value +
                            ZoeMensaje(
                                texto = mensajeError,
                                esUsuario = false,
                                tipoMovimiento =
                                    tipoMovimiento
                            )


                if (hablarRespuesta) {

                    hablar(
                        mensajeError
                    )
                }


                delay(1500)


                MascotaControlador.normal()


            } finally {

                _cargando.value = false
            }
        }
    }


    /*
     * ============================================================
     * LIMPIAR ERROR
     * ============================================================
     */

    fun limpiarError() {

        _error.value = null
    }


    /*
     * ============================================================
     * DESTRUIR VIEWMODEL
     * ============================================================
     */

    override fun onCleared() {

        /*
         * Liberamos el motor TTS cuando se destruye
         * el ViewModel.
         */

        tts?.stop()

        try {
            reproductorAudio?.stop()
        } catch (_: Exception) {
        }

        try {
            reproductorAudio?.release()
        } catch (_: Exception) {
        }

        reproductorAudio = null

        _hablando.value = false

        tts?.shutdown()

        tts = null

        ttsListo = false

        grabando = false

        try {
            audioRecord?.stop()
        } catch (_: Exception) {
        }

        audioRecord?.release()
        audioRecord = null
        _escuchando.value = false

        zoeWebSocket?.close(
            1000,
            "ViewModel destruido"
        )

        zoeWebSocket = null
        wsClient.dispatcher.executorService.shutdown()

        super.onCleared()
    }


    /*
     * ============================================================
     * PROCESAR AUDIO
     * ============================================================
     */

    fun enviarAudio(
        file: File
    ) {

        if (_cargando.value) {
            return
        }


        viewModelScope.launch {

            _cargando.value = true


            try {

                val requestBody =
                    file.asRequestBody(
                        "audio/wav".toMediaType()
                    )


                val part =
                    MultipartBody.Part.createFormData(
                        "audio",
                        file.name,
                        requestBody
                    )


                val respuesta =
                    api.enviarVoz(part)


                val texto =
                    respuesta.texto
                        ?.trim()
                        ?.takeIf {
                            it.isNotEmpty()
                        }


                /*
                 * =================================================
                 * AUDIO RECONOCIDO CORRECTAMENTE
                 * =================================================
                 */

                if (
                    respuesta.success &&
                    texto != null
                ) {

                    file.delete()


                    _cargando.value = false


                    /*
                     * Entregamos la transcripción al flujo
                     * normal de ZOE.
                     *
                     * Esto significa que también utilizará
                     * las nuevas burbujas de ZOE.
                     */

                    enviarConsulta(
                        texto,
                        hablarRespuesta = true
                    )


                    return@launch
                }


                /*
                 * =================================================
                 * ERROR DE TRANSCRIPCIÓN
                 * =================================================
                 */

                val mensajeError =
                    respuesta.message
                        ?.trim()
                        ?.takeIf {
                            it.isNotEmpty()
                        }
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

                /*
                 * =================================================
                 * ERROR PROCESANDO AUDIO
                 * =================================================
                 */

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


    /*
     * ============================================================
     * INICIAR ESCUCHA
     * ============================================================
     */

    fun iniciarEscucha() {

        android.util.Log.d("ZOE_AUDIO", "iniciarEscucha() ejecutado")

        if (grabando) return

        if (
            ContextCompat.checkSelfPermission(
                appContext,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            _error.value = "Permiso de micrófono no concedido."
            return
        }

        val socket = zoeWebSocket

        if (socket == null) {
            _error.value = "ZOE todavía no está conectada."
            return
        }

        val minBuffer = AudioRecord.getMinBufferSize(
            ZOE_SAMPLE_RATE,
            ZOE_CHANNEL,
            ZOE_ENCODING
        )

        if (minBuffer <= 0) {
            _error.value = "No se pudo inicializar el micrófono."
            return
        }

        val bufferSize = maxOf(
            minBuffer,
            ZOE_AUDIO_BUFFER * 2
        )

        try {

            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                ZOE_SAMPLE_RATE,
                ZOE_CHANNEL,
                ZOE_ENCODING,
                bufferSize
            )

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                record.release()
                _error.value = "No se pudo inicializar el micrófono."
                return
            }

            audioRecord = record
            grabando = true
            _escuchando.value = true
            _error.value = null

            socket.send("{\"type\":\"start_audio\"}")
            android.util.Log.d("ZOE_AUDIO", "start_audio enviado")

            record.startRecording()

            viewModelScope.launch(Dispatchers.IO) {

                val buffer = ByteArray(ZOE_AUDIO_BUFFER)

                try {

                    while (grabando) {

                        val leidos = record.read(
                            buffer,
                            0,
                            buffer.size
                        )

                        if (leidos > 0 && grabando) {
                            android.util.Log.d("ZOE_AUDIO", "PCM capturado: $leidos bytes")
                            val datos = buffer.copyOf(leidos)

                            socket.send(
                                okio.ByteString.of(*datos)
                            )
                        }
                    }

                } catch (e: Exception) {

                    android.util.Log.e(
                        "ZOE_WS",
                        "Error capturando audio: ${e.message}",
                        e
                    )

                    _error.value =
                        "No se pudo capturar el audio."

                } finally {

                    try {
                        if (
                            record.recordingState ==
                            AudioRecord.RECORDSTATE_RECORDING
                        ) {
                            record.stop()
                        }
                    } catch (_: Exception) {
                    }

                    record.release()

                    audioRecord = null
                    grabando = false
                    _escuchando.value = false
                }
            }

        } catch (e: Exception) {

            audioRecord = null
            grabando = false
            _escuchando.value = false

            _error.value =
                "No se pudo iniciar el micrófono: ${e.message}"
        }
    }


    /*
     * ============================================================
     * DETENER ESCUCHA
     * ============================================================
     */

    fun detenerEscucha() {

        if (!grabando) return

        grabando = false

        try {
            audioRecord?.stop()
        } catch (_: Exception) {
        }

        _escuchando.value = false

        android.util.Log.d("ZOE_AUDIO", "end_audio enviado")
        zoeWebSocket?.send("{\"type\":\"end_audio\"}")
    }


    /*
     * ============================================================
     * DETENER VOZ
     * ============================================================
     */

    fun detenerVoz() {

        tts?.stop()

        _hablando.value = false
    }


    /*
     * ============================================================
     * REPRODUCIR VOZ DE ZOE
     * ============================================================
     *
     * El Gateway envía un WAV completo generado por Supertonic F4.
     * Lo guardamos temporalmente y MediaPlayer lo reproduce.
     */
    private fun reproducirAudioZoe(bytes: ByteArray) {

        if (bytes.isEmpty()) {
            android.util.Log.w(
                "ZOE_AUDIO",
                "WAV vacío recibido"
            )
            return
        }

        audioHandler.post {

            try {
                reproductorAudio?.stop()
            } catch (_: Exception) {
            }

            try {
                reproductorAudio?.release()
            } catch (_: Exception) {
            }

            reproductorAudio = null

            val archivo = File(
                appContext.cacheDir,
                "zoe_respuesta_${System.currentTimeMillis()}.wav"
            )

            try {
                archivo.writeBytes(bytes)

                val player = MediaPlayer()

                player.setDataSource(archivo.absolutePath)

                player.setOnPreparedListener {
                    _hablando.value = true
                    MascotaControlador.feliz()

                    android.util.Log.d(
                        "ZOE_AUDIO",
                        "Reproduciendo voz de ZOE"
                    )

                    it.start()
                }

                player.setOnCompletionListener {
                    _hablando.value = false
                    MascotaControlador.normal()

                    android.util.Log.d(
                        "ZOE_AUDIO",
                        "Voz de ZOE terminada"
                    )

                    try {
                        it.release()
                    } catch (_: Exception) {
                    }

                    reproductorAudio = null

                    archivo.delete()
                }

                player.setOnErrorListener { mp, what, extra ->
                    _hablando.value = false
                    MascotaControlador.normal()

                    android.util.Log.e(
                        "ZOE_AUDIO",
                        "Error reproduciendo WAV: what=$what extra=$extra"
                    )

                    try {
                        mp.release()
                    } catch (_: Exception) {
                    }

                    reproductorAudio = null
                    archivo.delete()

                    true
                }

                reproductorAudio = player
                player.prepareAsync()

            } catch (e: Exception) {

                _hablando.value = false
                MascotaControlador.normal()

                android.util.Log.e(
                    "ZOE_AUDIO",
                    "No se pudo reproducir WAV: ${e.message}",
                    e
                )

                reproductorAudio = null
                archivo.delete()
            }
        }
    }


    private fun esEmulador(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk", ignoreCase = true)
                || Build.MODEL.contains("Emulator", ignoreCase = true)
                || Build.MODEL.contains("Android SDK built for", ignoreCase = true)
                || Build.MANUFACTURER.contains("Genymotion", ignoreCase = true)
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
    }

    private fun obtenerUrlZoeWebSocket(): String {
        val host = if (esEmulador()) {
            "10.0.2.2"
        } else {
            "192.168.1.34"
        }

        android.util.Log.d(
            "ZOE_WS",
            "Conectando al Gateway ZOE en $host:8001 (emulador=${esEmulador()})"
        )

        return "ws://$host:8001/ws/zoe"
    }

    private fun conectarZoeWebSocket() {

        val request = Request.Builder()
            .url(obtenerUrlZoeWebSocket())
            .build()

        zoeWebSocket = wsClient.newWebSocket(
            request,
            object : WebSocketListener() {

                override fun onOpen(
                    webSocket: WebSocket,
                    response: Response
                ) {
                    android.util.Log.d(
                        "ZOE_WS",
                        "ZOE WS conectado"
                    )
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    text: String
                ) {
                    android.util.Log.d(
                        "ZOE_WS",
                        "Servidor: $text"
                    )

                    try {
                        val datos =
                            org.json.JSONObject(text)

                        when (datos.optString("type")) {

                            "transcription" -> {

                                val consulta =
                                    datos.optString("text")
                                        .trim()

                                if (consulta.isNotEmpty()) {

                                    android.util.Log.d(
                                        "ZOE_WS",
                                        "Transcripción recibida: $consulta"
                                    )

                                    // La consulta de voz entra al mismo
                                    // backend real de SIGEFIV que el chat.
                                    enviarConsulta(
                                        consulta,
                                        hablarRespuesta = false,
                                        responderPorGateway = true
                                    )
                                }
                            }

                            "awaiting_backend" -> {
                                android.util.Log.d(
                                    "ZOE_WS",
                                    "Esperando respuesta real de SIGEFIV"
                                )
                            }

                            "transcribing", "thinking", "synthesizing" -> {
                                // Estados informativos del Gateway.
                            }
                        }

                    } catch (e: Exception) {
                        android.util.Log.w(
                            "ZOE_WS",
                            "Mensaje no JSON o no procesable: ${e.message}"
                        )
                    }
                }

                override fun onMessage(
                    webSocket: WebSocket,
                    bytes: okio.ByteString
                ) {
                    android.util.Log.d(
                        "ZOE_WS",
                        "Audio WAV recibido: ${bytes.size} bytes"
                    )

                    reproducirAudioZoe(bytes.toByteArray())
                }

                override fun onFailure(
                    webSocket: WebSocket,
                    t: Throwable,
                    response: Response?
                ) {
                    android.util.Log.e(
                        "ZOE_WS",
                        "Error WebSocket: ${t.message}",
                        t
                    )
                }
            }
        )
    }
}
