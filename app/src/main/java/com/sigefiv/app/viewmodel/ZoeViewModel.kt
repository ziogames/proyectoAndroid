package com.sigefiv.app.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ConversacionItem
import com.sigefiv.app.data.model.ZoeEstadistica
import com.sigefiv.app.data.repository.ZoeRepository
import com.sigefiv.app.screens.mascota.MascotaControlador
import com.sigefiv.app.screens.mascota.MascotaEvento
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

data class ZoeMensaje(
    val texto: String,
    val esUsuario: Boolean,
    val tipoMovimiento: String? = null,
    val estadistica: ZoeEstadistica? = null,
    val imagenUri: Uri? = null,
    val hora: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val tipo: String? = null,
    val asambleaId: Long? = null,
    val asambleaEstadisticas: com.sigefiv.app.screens.chat.model.AsambleaEstadisticasDto? = null
)

data class AsambleaActivaReporte(
    val tieneAsamblea: Boolean = false,
    val asambleaId: Long? = null,
    val titulo: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val lugar: String? = null,
    val miVoto: String? = null,
    val asistiran: Int = 0,
    val enDuda: Int = 0,
    val noAsistiran: Int = 0,
    val total: Int = 0
)

class ZoeViewModel(
    context: Context
) : ViewModel() {

    private val appContext = context.applicationContext
    private val api = ApiClient.create(appContext)
    private val wsClient = OkHttpClient()
    private var zoeWebSocket: WebSocket? = null

    private var reproductorAudio: MediaPlayer? = null
    private val audioHandler = Handler(Looper.getMainLooper())
    private val zoeRepository = ZoeRepository(appContext)

    /*
    |--------------------------------------------------------------------------
    | HISTORIAL DE CONVERSACIONES
    |--------------------------------------------------------------------------
    */
    private val _historialConversaciones = MutableStateFlow<List<ConversacionItem>>(emptyList())
    val historialConversaciones: StateFlow<List<ConversacionItem>> = _historialConversaciones.asStateFlow()

    fun cargarHistorial() {
        viewModelScope.launch {
            try {
                val respuesta = zoeRepository.obtenerHistorial()
                if (respuesta.success) {
                    _historialConversaciones.value = respuesta.historial
                }
            } catch (e: Exception) {
                Log.e("ZOE_HISTORIAL", "Error al cargar historial: ${e.message}", e)
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | REPORTE Y ESTADÍSTICAS DE ASAMBLEA ACTIVA (BOTÓN Y BOTTOMSHEET)
    |--------------------------------------------------------------------------
    */
    private val _reporteAsamblea = MutableStateFlow<AsambleaActivaReporte?>(null)
    val reporteAsamblea: StateFlow<AsambleaActivaReporte?> = _reporteAsamblea.asStateFlow()

    fun actualizarEstadisticasAsamblea() {
        viewModelScope.launch {
            try {
                val response = zoeRepository.obtenerEstadisticasAsambleaActiva()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    if (data.tiene_asamblea && data.asamblea_id != null) {
                        val stats = data.estadisticas
                        val asist = stats?.asistiran ?: 0
                        val duda = stats?.en_duda ?: 0
                        val no = stats?.no_asistiran ?: 0
                        _reporteAsamblea.value = AsambleaActivaReporte(
                            tieneAsamblea = true,
                            asambleaId = data.asamblea_id,
                            titulo = data.titulo,
                            fecha = data.fecha,
                            hora = data.hora,
                            lugar = data.lugar,
                            miVoto = data.mi_voto,
                            asistiran = asist,
                            enDuda = duda,
                            noAsistiran = no,
                            total = asist + duda + no
                        )
                    } else {
                        _reporteAsamblea.value = null
                    }
                }
            } catch (e: Exception) {
                Log.e("ZOE_ASAMBLEA", "Error cargando estadísticas activas: ${e.message}")
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | CAPTURA DE VOZ — AUDIO ANDROID → ZOE GATEWAY
    |--------------------------------------------------------------------------
    */
    private var audioRecord: AudioRecord? = null

    @Volatile
    private var grabando = false

    private val _escuchando = MutableStateFlow(false)
    val escuchando: StateFlow<Boolean> = _escuchando.asStateFlow()

    private companion object {
        const val ZOE_SAMPLE_RATE = 16_000
        const val ZOE_CHANNEL = AudioFormat.CHANNEL_IN_MONO
        const val ZOE_ENCODING = AudioFormat.ENCODING_PCM_16BIT
        const val ZOE_AUDIO_BUFFER = 2048
    }

    /*
    |--------------------------------------------------------------------------
    | VOZ DE ZOE (TTS NATIVO)
    |--------------------------------------------------------------------------
    */
    private var tts: TextToSpeech? = null
    private var ttsListo = false

    private val _hablando = MutableStateFlow(false)
    val hablando: StateFlow<Boolean> = _hablando.asStateFlow()

    init {
        inicializarVoz()
        conectarZoeWebSocket()
        cargarHistorial()
    }

    /*
    |--------------------------------------------------------------------------
    | MENSAJES Y ESTADO DE ENTRADA
    |--------------------------------------------------------------------------
    */
    private val _mensajes = MutableStateFlow<List<ZoeMensaje>>(emptyList())
    val mensajes: StateFlow<List<ZoeMensaje>> = _mensajes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _archivoAdjuntoUri = MutableStateFlow<Uri?>(null)
    val archivoAdjuntoUri: StateFlow<Uri?> = _archivoAdjuntoUri

    fun seleccionarArchivo(uri: Uri?) {
        _archivoAdjuntoUri.value = uri
    }

    fun limpiarAdjunto() {
        _archivoAdjuntoUri.value = null
    }

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private fun inicializarVoz() {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val resultadoIdioma = tts?.setLanguage(Locale("es", "PE"))
                tts?.setSpeechRate(0.95f)
                tts?.setPitch(1.0f)
                tts?.setOnUtteranceProgressListener(
                    object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _hablando.value = true
                            MascotaControlador.feliz()
                        }

                        override fun onDone(utteranceId: String?) {
                            _hablando.value = false
                            MascotaControlador.normal()
                        }

                        override fun onError(utteranceId: String?) {
                            _hablando.value = false
                            MascotaControlador.normal()
                        }
                    }
                )

                ttsListo = resultadoIdioma != TextToSpeech.LANG_MISSING_DATA &&
                        resultadoIdioma != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

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

        val moneda = Regex("""S/\s*([0-9][0-9,]*(?:\.[0-9]{1,2})?)""", RegexOption.IGNORE_CASE)
        resultado = moneda.replace(resultado) { match ->
            val valor = match.groupValues[1]
            val partes = valor.split(".", limit = 2)
            montoEnPalabras(partes[0], partes.getOrNull(1))
        }

        resultado = Regex("""(\d[\d,]*(?:\.\d+)?)\s*%""").replace(resultado) { match ->
            val valor = match.groupValues[1]
            val numero = valor.replace(",", "").toDoubleOrNull()
            if (numero != null && numero % 1.0 == 0.0) {
                "${numeroEnPalabras(numero.toLong())} por ciento"
            } else {
                valor.replace(",", "") + " por ciento"
            }
        }

        resultado = Regex("""\b\d[\d,.]*\b""").replace(resultado) { match ->
            val valor = match.value
            val normalizado = valor.replace(",", "")
            val partes = normalizado.split(".", limit = 2)
            val entero = partes[0].toLongOrNull()

            if (entero == null) {
                valor
            } else if (partes.size == 2) {
                val decimal = partes[1]
                val digitosPronunciados = decimal.map { numeroEnPalabras((it - '0').toLong()) }.joinToString(" ")
                "${numeroEnPalabras(entero)} punto $digitosPronunciados"
            } else {
                numeroEnPalabras(entero)
            }
        }

        return resultado
            .replace(Regex("\\s*\\+\\s*"), " más ")
            .replace(Regex("\\s*=\\s*"), " igual a ")
            .replace(Regex("\\s-\\s"), " menos ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private fun esListaParaVoz(texto: String): Boolean {
        val cantidadFechas = Regex("""\b\d{1,2}[-/]\d{1,2}[-/]\d{4}\b""").findAll(texto).count()
        val cantidadImportes = Regex("""S/\s*[0-9][0-9,.]*""", RegexOption.IGNORE_CASE).findAll(texto).count()
        val tieneIndicadorLista = texto.contains("lista de", ignoreCase = true) ||
                texto.contains("detalle de", ignoreCase = true) ||
                texto.contains("movimientos de", ignoreCase = true)

        return cantidadFechas >= 2 || cantidadImportes >= 3 || tieneIndicadorLista
    }

    private fun resumenListaParaVoz(texto: String, tipoMovimiento: String?): String {
        val cantidad = Regex("""\b\d{1,2}[-/]\d{1,2}[-/]\d{4}\b""").findAll(texto).count()
        val periodo = Regex("""(?i)\b(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre)\s+\d{4}\b""")
            .find(texto)?.value

        val tipo = when (tipoMovimiento?.lowercase(Locale.getDefault())) {
            "ingreso" -> "ingresos"
            "egreso" -> "egresos"
            else -> "movimientos"
        }

        val cantidadTexto = if (cantidad > 0) {
            if (cantidad == 1) "un registro de $tipo" else "$cantidad $tipo"
        } else {
            "varios $tipo"
        }

        return if (periodo != null) {
            "He encontrado $cantidadTexto correspondientes a $periodo. Te muestro el detalle en pantalla."
        } else {
            "He encontrado $cantidadTexto. Te muestro el detalle en pantalla."
        }
    }

    private fun textoParaVoz(texto: String, tipoMovimiento: String? = null): String {
        val limpio = texto
            .replace(Regex("^\\s*(?:ZOE|Zoe)\\s*:\\s*"), "")
            .replace(Regex("[\\p{So}\\p{Cn}]"), " ")
            .replace("**", "")
            .replace("__", "")
            .replace("SIGEFIV", "sigefiv", ignoreCase = true)
            .replace(Regex("\\s+"), " ")
            .trim()

        if (esListaParaVoz(limpio)) {
            return convertirNumerosParaVoz(resumenListaParaVoz(limpio, tipoMovimiento))
        }

        return convertirNumerosParaVoz(limpio)
    }

    /*
    |--------------------------------------------------------------------------
    | EVENTO SILENCIOSO
    |--------------------------------------------------------------------------
    */
    fun enviarEventoSilencioso(mensaje: String) {
        val consulta = mensaje.trim()
        if (consulta.isEmpty() || _cargando.value) return

        _error.value = null

        viewModelScope.launch {
            _cargando.value = true
            MascotaControlador.evento(MascotaEvento.ZOE_PENSANDO)

            try {
                val respuesta = zoeRepository.consultar(consulta)

                if (respuesta.success) {
                    val respuestaTexto = respuesta.respuesta
                        ?.trim()
                        ?.replace("**", "")
                        ?.replace("__", "")
                        ?.takeIf { it.isNotEmpty() }
                        ?: "ZOE recibió la consulta, pero no devolvió un mensaje."

                    val estadisticaZoe: ZoeEstadistica? = respuesta.estadistica
                        ?.takeIf { it.isJsonObject }
                        ?.let {
                            try {
                                com.google.gson.Gson().fromJson(it, ZoeEstadistica::class.java)
                            } catch (e: Exception) {
                                null
                            }
                        }

                    _mensajes.value = _mensajes.value + ZoeMensaje(
                        texto = respuestaTexto,
                        esUsuario = false,
                        tipoMovimiento = null,
                        estadistica = estadisticaZoe
                    )

                    _cargando.value = false
                    MascotaControlador.evento(MascotaEvento.ZOE_RESPONDIENDO)
                    delay(1500)
                    MascotaControlador.normal()
                    cargarHistorial()
                } else {
                    MascotaControlador.evento(MascotaEvento.ERROR)
                    val mensajeError = respuesta.message?.trim()?.takeIf { it.isNotEmpty() }
                        ?: respuesta.mensaje?.trim()?.takeIf { it.isNotEmpty() }
                        ?: "No fue posible procesar el inicio de sesión."

                    _error.value = mensajeError
                    _mensajes.value = _mensajes.value + ZoeMensaje(texto = mensajeError, esUsuario = false)
                    delay(1500)
                    MascotaControlador.normal()
                }
            } catch (e: Exception) {
                Log.e("ZOE_DEBUG", "ERROR EVENTO SILENCIOSO: ${e.message}", e)
                MascotaControlador.evento(MascotaEvento.ERROR)

                val mensajeError = "No se pudo conectar con ZOE. Verifica que el servidor SIGEFIV esté funcionando."
                _error.value = mensajeError
                _mensajes.value = _mensajes.value + ZoeMensaje(texto = mensajeError, esUsuario = false)

                delay(1500)
                MascotaControlador.normal()
            } finally {
                _cargando.value = false
            }
        }
    }

    /*
    |--------------------------------------------------------------------------
    | CONSULTA PRINCIPAL
    |--------------------------------------------------------------------------
    */
    fun enviarConsulta(
        mensaje: String,
        hablarRespuesta: Boolean = false,
        responderPorGateway: Boolean = false
    ) {
        val consulta = mensaje.trim()
        if (consulta.isEmpty() || _cargando.value) return

        val tipoMovimiento = when {
            consulta.contains("egreso", ignoreCase = true) || consulta.contains("egresos", ignoreCase = true) -> "Egreso"
            consulta.contains("ingreso", ignoreCase = true) || consulta.contains("ingresos", ignoreCase = true) -> "Ingreso"
            else -> null
        }

        _error.value = null
        _mensajes.value = _mensajes.value + ZoeMensaje(texto = consulta, esUsuario = true)

        viewModelScope.launch {
            _cargando.value = true
            MascotaControlador.evento(MascotaEvento.ZOE_PENSANDO)

            try {
                val respuesta = zoeRepository.consultar(consulta)

                if (respuesta.success) {
                    val respuestaTexto = respuesta.respuesta
                        ?.trim()
                        ?.replace("**", "")
                        ?.replace("__", "")
                        ?.takeIf { it.isNotEmpty() }
                        ?: "ZOE recibió la consulta, pero no devolvió un mensaje."

                    val estadisticaZoe: ZoeEstadistica? = respuesta.estadistica
                        ?.takeIf { it.isJsonObject }
                        ?.let {
                            try {
                                com.google.gson.Gson().fromJson(it, ZoeEstadistica::class.java)
                            } catch (e: Exception) {
                                Log.e("ZOE_DEBUG", "Error convirtiendo estadística", e)
                                null
                            }
                        }

                    _mensajes.value = _mensajes.value + ZoeMensaje(
                        texto = respuestaTexto,
                        esUsuario = false,
                        tipoMovimiento = tipoMovimiento,
                        estadistica = estadisticaZoe
                    )

                    if (responderPorGateway) {
                        zoeWebSocket?.send(
                            org.json.JSONObject()
                                .put("type", "response_text")
                                .put("text", textoParaVoz(respuestaTexto, tipoMovimiento))
                                .toString()
                        )
                    } else if (hablarRespuesta) {
                        hablar(textoParaVoz(respuestaTexto, tipoMovimiento))
                    }

                    _cargando.value = false
                    MascotaControlador.evento(MascotaEvento.ZOE_RESPONDIENDO)
                    delay(1500)
                    MascotaControlador.normal()
                    cargarHistorial()
                } else {
                    val mensajeError = respuesta.message?.trim()?.takeIf { it.isNotEmpty() }
                        ?: respuesta.mensaje?.trim()?.takeIf { it.isNotEmpty() }
                        ?: "No fue posible procesar la consulta."

                    MascotaControlador.evento(MascotaEvento.ERROR)
                    _error.value = mensajeError
                    _mensajes.value = _mensajes.value + ZoeMensaje(
                        texto = mensajeError,
                        esUsuario = false,
                        tipoMovimiento = tipoMovimiento
                    )

                    if (hablarRespuesta) hablar(mensajeError)
                    delay(1500)
                    MascotaControlador.normal()
                }
            } catch (e: Exception) {
                Log.e("ZOE_DEBUG", "ERROR CONSULTANDO ZOE: ${e.message}", e)
                MascotaControlador.evento(MascotaEvento.ERROR)

                val mensajeError = when {
                    e.message?.contains("401") == true -> "La sesión no es válida. Inicia sesión nuevamente."
                    e.message?.contains("timeout", ignoreCase = true) == true -> "ZOE tardó demasiado en responder. Inténtalo nuevamente."
                    else -> "No se pudo conectar con ZOE. Verifica que el servidor SIGEFIV esté funcionando."
                }

                _error.value = mensajeError
                _mensajes.value = _mensajes.value + ZoeMensaje(
                    texto = mensajeError,
                    esUsuario = false,
                    tipoMovimiento = tipoMovimiento
                )

                if (hablarRespuesta) hablar(mensajeError)
                delay(1500)
                MascotaControlador.normal()
            } finally {
                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    override fun onCleared() {
        tts?.stop()
        try { reproductorAudio?.stop() } catch (_: Exception) {}
        try { reproductorAudio?.release() } catch (_: Exception) {}
        reproductorAudio = null
        _hablando.value = false
        tts?.shutdown()
        tts = null
        ttsListo = false

        grabando = false
        try { audioRecord?.stop() } catch (_: Exception) {}
        audioRecord?.release()
        audioRecord = null
        _escuchando.value = false

        zoeWebSocket?.close(1000, "ViewModel destruido")
        zoeWebSocket = null
        wsClient.dispatcher.executorService.shutdown()

        super.onCleared()
    }

    fun enviarAudio(file: File) {
        if (_cargando.value) return

        viewModelScope.launch {
            _cargando.value = true
            try {
                val requestBody = file.asRequestBody("audio/wav".toMediaType())
                val part = MultipartBody.Part.createFormData("audio", file.name, requestBody)
                val respuesta = api.enviarVoz(part)
                val texto = respuesta.texto?.trim()?.takeIf { it.isNotEmpty() }

                if (respuesta.success && texto != null) {
                    file.delete()
                    _cargando.value = false
                    enviarConsulta(texto, hablarRespuesta = true)
                    return@launch
                }

                val mensajeError = respuesta.message?.trim()?.takeIf { it.isNotEmpty() } ?: "No pude transcribir el audio."
                _error.value = mensajeError
                _mensajes.value = _mensajes.value + ZoeMensaje(texto = mensajeError, esUsuario = false)
                hablar(mensajeError)
            } catch (e: Exception) {
                val mensajeError = "No se pudo procesar el audio. Verifica la conexión con SIGEFIV."
                _error.value = mensajeError
                _mensajes.value = _mensajes.value + ZoeMensaje(texto = mensajeError, esUsuario = false)
                hablar(mensajeError)
            } finally {
                _cargando.value = false
                if (file.exists()) file.delete()
            }
        }
    }

    fun iniciarEscucha() {
        if (grabando) return

        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            _error.value = "Permiso de micrófono no concedido."
            return
        }

        val socket = zoeWebSocket
        if (socket == null) {
            _error.value = "ZOE todavía no está conectada."
            return
        }

        val minBuffer = AudioRecord.getMinBufferSize(ZOE_SAMPLE_RATE, ZOE_CHANNEL, ZOE_ENCODING)
        if (minBuffer <= 0) {
            _error.value = "No se pudo inicializar el micrófono."
            return
        }

        val bufferSize = maxOf(minBuffer, ZOE_AUDIO_BUFFER * 2)

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
            record.startRecording()

            viewModelScope.launch(Dispatchers.IO) {
                val buffer = ByteArray(ZOE_AUDIO_BUFFER)
                try {
                    while (grabando) {
                        val leidos = record.read(buffer, 0, buffer.size)
                        if (leidos > 0 && grabando) {
                            val datos = buffer.copyOf(leidos)
                            socket.send(okio.ByteString.of(*datos))
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ZOE_WS", "Error capturando audio: ${e.message}", e)
                    _error.value = "No se pudo capturar el audio."
                } finally {
                    try {
                        if (record.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                            record.stop()
                        }
                    } catch (_: Exception) {}
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
            _error.value = "No se pudo iniciar el micrófono: ${e.message}"
        }
    }

    fun detenerEscucha() {
        if (!grabando) return
        grabando = false
        try { audioRecord?.stop() } catch (_: Exception) {}
        _escuchando.value = false
        zoeWebSocket?.send("{\"type\":\"end_audio\"}")
    }

    fun detenerVoz() {
        tts?.stop()
        _hablando.value = false
    }

    private fun reproducirAudioZoe(bytes: ByteArray) {
        if (bytes.isEmpty()) return

        audioHandler.post {
            try { reproductorAudio?.stop() } catch (_: Exception) {}
            try { reproductorAudio?.release() } catch (_: Exception) {}
            reproductorAudio = null

            val archivo = File(appContext.cacheDir, "zoe_respuesta_${System.currentTimeMillis()}.wav")

            try {
                archivo.writeBytes(bytes)
                val player = MediaPlayer()
                player.setDataSource(archivo.absolutePath)
                player.setOnPreparedListener {
                    _hablando.value = true
                    MascotaControlador.feliz()
                    it.start()
                }

                player.setOnCompletionListener {
                    _hablando.value = false
                    MascotaControlador.normal()
                    try { it.release() } catch (_: Exception) {}
                    reproductorAudio = null
                    archivo.delete()
                }

                player.setOnErrorListener { mp, _, _ ->
                    _hablando.value = false
                    MascotaControlador.normal()
                    try { mp.release() } catch (_: Exception) {}
                    reproductorAudio = null
                    archivo.delete()
                    true
                }

                reproductorAudio = player
                player.prepareAsync()
            } catch (e: Exception) {
                _hablando.value = false
                MascotaControlador.normal()
                reproductorAudio = null
                archivo.delete()
            }
        }
    }

    private fun obtenerUrlZoeWebSocket(): String {
        return ServidorConfig.ZOE_WEBSOCKET_URL
    }

    private fun conectarZoeWebSocket() {
        val request = Request.Builder().url(obtenerUrlZoeWebSocket()).build()

        zoeWebSocket = wsClient.newWebSocket(
            request,
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    Log.d("ZOE_WS", "ZOE WS conectado")
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    try {
                        val datos = org.json.JSONObject(text)
                        when (datos.optString("type")) {
                            "transcription" -> {
                                val consulta = datos.optString("text").trim()
                                if (consulta.isNotEmpty()) {
                                    enviarConsulta(consulta, hablarRespuesta = false, responderPorGateway = true)
                                }
                            }
                        }
                    } catch (_: Exception) {}
                }

                override fun onMessage(webSocket: WebSocket, bytes: okio.ByteString) {
                    reproducirAudioZoe(bytes.toByteArray())
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    Log.e("ZOE_WS", "Error WebSocket: ${t.message}", t)
                }
            }
        )
    }

    fun cargarConversacion(conversacionId: String) {
        viewModelScope.launch {
            _cargando.value = true
            try {
                val respuesta = zoeRepository.obtenerDetalle(conversacionId)
                if (respuesta.success) {
                    val regexId = Regex("""\[ASAMBLEA_ID:(\d+)\]""")

                    _mensajes.value = respuesta.mensajes
                        .filter { dto ->
                            val textoTrim = dto.texto.trim()
                            textoTrim.isNotEmpty() &&
                                    !textoTrim.startsWith("evento_") &&
                                    !textoTrim.contains("bienvenido de nuevo a SIGEFIV", ignoreCase = true)
                        }
                        .map { dto ->
                            val matchId = regexId.find(dto.texto)
                            val asambleaIdDetectado = matchId?.groupValues?.get(1)?.toLongOrNull()
                            val esEncuesta = asambleaIdDetectado != null || dto.texto.contains("¿Contaremos con su asistencia?", ignoreCase = true)
                            val textoLimpio = regexId.replace(dto.texto.replace("[REPORTE_DIRECTIVA] ", ""), "").trim()

                            ZoeMensaje(
                                texto = textoLimpio,
                                esUsuario = dto.esUsuario,
                                hora = dto.hora,
                                tipo = if (esEncuesta) "encuesta_asamblea" else null,
                                asambleaId = asambleaIdDetectado
                            )
                        }
                }
            } catch (e: Exception) {
                Log.e("ZOE_HISTORIAL", "Error al cargar detalle de conversación: ${e.message}", e)
            } finally {
                _cargando.value = false
            }
        }
    }

    fun iniciarNuevaConversacion() {
        _mensajes.value = emptyList()
        enviarEventoSilencioso("evento_inicio_sesion")
    }

    fun enviarMensajeConMultimedia(context: Context, uri: Uri?, textoMensaje: String) {
        val mensajeTextoLimpio = textoMensaje.trim()
        if (mensajeTextoLimpio.isBlank() && uri == null) return

        viewModelScope.launch {
            try {
                _cargando.value = true
                MascotaControlador.evento(MascotaEvento.ZOE_PENSANDO)

                _mensajes.value = _mensajes.value + ZoeMensaje(
                    texto = if (mensajeTextoLimpio.isBlank()) "[Imagen adjunta]" else mensajeTextoLimpio,
                    esUsuario = true,
                    imagenUri = uri
                )

                var parteArchivo: MultipartBody.Part? = null
                var bodyTexto: RequestBody? = null

                if (uri != null) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val archivoTemporal = File(context.cacheDir, "zoe_upload_${System.currentTimeMillis()}.tmp")

                    inputStream?.use { input ->
                        archivoTemporal.outputStream().use { output -> input.copyTo(output) }
                    }

                    val tipoMime = context.contentResolver.getType(uri) ?: "image/jpeg"
                    val requestFile = archivoTemporal.asRequestBody(tipoMime.toMediaTypeOrNull())
                    parteArchivo = MultipartBody.Part.createFormData("archivo", archivoTemporal.name, requestFile)
                }

                if (mensajeTextoLimpio.isNotBlank()) {
                    bodyTexto = mensajeTextoLimpio.toRequestBody("text/plain".toMediaTypeOrNull())
                }

                val respuestaArchivo = zoeRepository.enviarMensajeConArchivo(parteArchivo, bodyTexto)

                if (respuestaArchivo != null && respuestaArchivo.success) {
                    val detalleReporte = if (mensajeTextoLimpio.isBlank()) {
                        "Adjunté una fotografía como reporte para la directiva."
                    } else {
                        mensajeTextoLimpio
                    }

                    val consultaParaN8n = "[REPORTE_DIRECTIVA] $detalleReporte"
                    val respuestaZoe = zoeRepository.consultar(consultaParaN8n)

                    val textoRespuestaZoe = if (respuestaZoe.success && !respuestaZoe.respuesta.isNullOrBlank()) {
                        respuestaZoe.respuesta!!
                            .trim()
                            .replace("**", "")
                            .replace("__", "")
                    } else {
                        "¡Muchas gracias! He recibido tu fotografía y el reporte, y ya fueron enviados con éxito a la directiva."
                    }

                    _mensajes.value = _mensajes.value + ZoeMensaje(
                        texto = textoRespuestaZoe,
                        esUsuario = false
                    )
                } else {
                    _mensajes.value = _mensajes.value + ZoeMensaje(
                        texto = "El servidor no pudo procesar el archivo adjunto.",
                        esUsuario = false
                    )
                }

                _archivoAdjuntoUri.value = null
                MascotaControlador.evento(MascotaEvento.ZOE_RESPONDIENDO)
                delay(1500)
                MascotaControlador.normal()
                cargarHistorial()
            } catch (e: Exception) {
                Log.e("ZOE_VM", "Error al enviar contenido multimedia: ${e.message}", e)
                MascotaControlador.evento(MascotaEvento.ERROR)
                _mensajes.value = _mensajes.value + ZoeMensaje(
                    texto = "No se pudo conectar con el servidor para enviar el archivo.",
                    esUsuario = false
                )
                delay(1500)
                MascotaControlador.normal()
            } finally {
                _cargando.value = false
            }
        }
    }

    fun enviarConsultaOAdjunto(context: Context, texto: String) {
        val uriActual = _archivoAdjuntoUri.value
        if (uriActual != null) {
            enviarMensajeConMultimedia(context, uriActual, texto)
        } else {
            enviarConsulta(texto)
        }
    }

    fun verificarAvisosAsamblea() {
        viewModelScope.launch {
            try {
                Log.d("ZOE_ASAMBLEA", "1. Iniciando llamada a verificarAvisosPendientes()...")
                val response = zoeRepository.verificarAvisosPendientes()
                Log.d("ZOE_ASAMBLEA", "2. Código HTTP recibido: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d("ZOE_ASAMBLEA", "3. Body recibido: success=${data.success}, tiene_asamblea=${data.tiene_asamblea}, id=${data.asamblea_id}")

                    if (data.tiene_asamblea && data.asamblea_id != null && data.texto != null) {
                        val yaExiste = _mensajes.value.any { it.asambleaId == data.asamblea_id }
                        if (!yaExiste) {
                            _mensajes.value = _mensajes.value + ZoeMensaje(
                                texto = data.texto,
                                esUsuario = false,
                                tipo = "encuesta_asamblea",
                                asambleaId = data.asamblea_id
                            )
                            Log.d("ZOE_ASAMBLEA", "4. Mensaje agregado a _mensajes con éxito.")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZOE_ASAMBLEA", "Excepción de red al verificar: ${e.message}", e)
            }
        }
    }

    fun confirmarAsistenciaAsamblea(asambleaId: Long, opcion: String) {
        viewModelScope.launch {
            try {
                _cargando.value = true
                val response = zoeRepository.confirmarAsistencia(asambleaId, opcion)

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    _mensajes.value = _mensajes.value.map { msg ->
                        if (msg.asambleaId == asambleaId) {
                            msg.copy(asambleaEstadisticas = body.estadisticas)
                        } else {
                            msg
                        }
                    }

                    _mensajes.value = _mensajes.value + ZoeMensaje(
                        texto = body.mensaje_zoe,
                        esUsuario = false
                    )

                    // Sincronizar el BottomSheet superior con el nuevo voto emitido
                    actualizarEstadisticasAsamblea()

                    MascotaControlador.evento(MascotaEvento.ZOE_RESPONDIENDO)
                    delay(1500)
                    MascotaControlador.normal()

                    cargarHistorial()
                } else {
                    _error.value = "No se pudo registrar la respuesta a la asamblea."
                }
            } catch (e: Exception) {
                Log.e("ZOE_ASAMBLEA", "Error al confirmar asistencia: ${e.message}", e)
                _error.value = "Error de conexión al registrar asistencia."
            } finally {
                _cargando.value = false
            }
        }
    }
}