package com.sigefiv.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.model.ChatMessage
import com.sigefiv.app.data.model.ChatTypingUser
import com.sigefiv.app.data.repository.ChatRepository
import com.sigefiv.app.data.repository.ZoeException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

data class ChatUiState(
    val cargando: Boolean = false,
    val enviando: Boolean = false,
    val mensajes: List<ChatMessage> = emptyList(),
    val personas: Int = 0,
    val personasEnLinea: Int = 0,
    val usuariosEscribiendo: List<ChatTypingUser> = emptyList(),
    val error: String? = null,
    val mensaje: String? = null,
    val esBloqueoPermanente: Boolean = false,
    val mensajeRespondido: ChatMessage? = null,

    // 📖 Estado de lectura
    val ultimoLeidoMessageId: Int? = null,
    val primerNoLeidoId: Int? = null,
    val mensajesNoLeidos: Int = 0,

    // ❤️ ID de los mensajes que están procesando una reacción
    val reaccionandoMensajes: Set<Int> = emptySet()
)

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ChatUiState())

    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()

    private var actualizacionJob: Job? = null
    private var ultimoMensajeId: Int = 0
    private var chatCargado = false
    private var mensajeLeidoPendienteId: Int? = null

    fun cargarChat() {
        viewModelScope.launch {
            chatCargado = false
            ultimoMensajeId = 0

            _uiState.value =
                _uiState.value.copy(
                    cargando = true,
                    mensajes = emptyList(),
                    error = null
                )

            repository.obtenerChat()
                .onSuccess { respuesta ->
                    val mensajes = respuesta.mensajes
                    chatCargado = true

                    ultimoMensajeId =
                        mensajes.maxOfOrNull { it.id } ?: 0

                    val lecturaPendiente = mensajeLeidoPendienteId
                    val respuestaEsAnterior =
                        lecturaPendiente != null &&
                                (respuesta.ultimo_leido_message_id == null ||
                                        respuesta.ultimo_leido_message_id < lecturaPendiente)

                    _uiState.value =
                        if (respuestaEsAnterior) {
                            _uiState.value.copy(
                                cargando = false,
                                mensajes = mensajes,
                                personas = respuesta.personas,
                                personasEnLinea = respuesta.personas_en_linea,
                                usuariosEscribiendo = respuesta.usuarios_escribiendo,
                                error = null
                            )
                        } else {
                            _uiState.value.copy(
                                cargando = false,
                                mensajes = mensajes,
                                personas = respuesta.personas,
                                personasEnLinea = respuesta.personas_en_linea,
                                usuariosEscribiendo = respuesta.usuarios_escribiendo,
                                ultimoLeidoMessageId = respuesta.ultimo_leido_message_id,
                                primerNoLeidoId = respuesta.primer_no_leido_id,
                                mensajesNoLeidos = respuesta.mensajes_no_leidos,
                                error = null
                            )
                        }
                }
                .onFailure { error ->
                    _uiState.value =
                        _uiState.value.copy(
                            cargando = false,
                            error = error.message ?: "No se pudo cargar el Chat Vecinal."
                        )
                }
        }
    }

    fun marcarLeido(mensajeId: Int) {
        val estadoActual = _uiState.value
        val ultimoLeido = estadoActual.ultimoLeidoMessageId
        val pendiente = mensajeLeidoPendienteId

        if (ultimoLeido != null && mensajeId <= ultimoLeido) return
        if (pendiente != null && mensajeId <= pendiente) return

        mensajeLeidoPendienteId = mensajeId

        viewModelScope.launch {
            repository.marcarLeido(mensajeId)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        ultimoLeidoMessageId = mensajeId,
                        primerNoLeidoId = null,
                        mensajesNoLeidos = 0
                    )
                    mensajeLeidoPendienteId = null
                }
                .onFailure {
                    mensajeLeidoPendienteId = null
                }
        }
    }

    fun iniciarActualizacionAutomatica() {
        if (actualizacionJob?.isActive == true) return

        actualizacionJob =
            viewModelScope.launch {
                while (isActive) {
                    obtenerMensajesNuevos()
                    delay(3000)
                }
            }
    }

    fun detenerActualizacionAutomatica() {
        actualizacionJob?.cancel()
        actualizacionJob = null
    }

    private suspend fun obtenerMensajesNuevos() {
        if (!chatCargado) return

        repository.obtenerMensajesNuevos(afterId = ultimoMensajeId)
            .onSuccess { respuesta ->
                if (respuesta.mensajes.isNotEmpty()) {
                    val mensajesActuales = _uiState.value.mensajes
                    val idsActuales = mensajesActuales.map { it.id }.toSet()
                    val nuevos = respuesta.mensajes.filter { it.id !in idsActuales }

                    if (nuevos.isNotEmpty()) {
                        val mensajesActualizados = mensajesActuales + nuevos
                        ultimoMensajeId = mensajesActualizados.maxOfOrNull { it.id } ?: ultimoMensajeId

                        val lecturaPendiente = mensajeLeidoPendienteId
                        val respuestaEsAnterior =
                            lecturaPendiente != null &&
                                    (respuesta.ultimo_leido_message_id == null ||
                                            respuesta.ultimo_leido_message_id < lecturaPendiente)

                        _uiState.value =
                            if (respuestaEsAnterior) {
                                _uiState.value.copy(mensajes = mensajesActualizados)
                            } else {
                                _uiState.value.copy(
                                    mensajes = mensajesActualizados,
                                    ultimoLeidoMessageId = respuesta.ultimo_leido_message_id,
                                    primerNoLeidoId = respuesta.primer_no_leido_id,
                                    mensajesNoLeidos = respuesta.mensajes_no_leidos
                                )
                            }
                    }
                }

                val lecturaPendiente = mensajeLeidoPendienteId
                val respuestaEsAnterior =
                    lecturaPendiente != null &&
                            (respuesta.ultimo_leido_message_id == null ||
                                    respuesta.ultimo_leido_message_id < lecturaPendiente)

                _uiState.value =
                    if (respuestaEsAnterior) {
                        _uiState.value.copy(
                            personasEnLinea = respuesta.personas_en_linea,
                            usuariosEscribiendo = respuesta.usuarios_escribiendo,
                            error = null
                        )
                    } else {
                        _uiState.value.copy(
                            personasEnLinea = respuesta.personas_en_linea,
                            usuariosEscribiendo = respuesta.usuarios_escribiendo,
                            ultimoLeidoMessageId = respuesta.ultimo_leido_message_id,
                            primerNoLeidoId = respuesta.primer_no_leido_id,
                            mensajesNoLeidos = respuesta.mensajes_no_leidos,
                            error = null
                        )
                    }
            }
    }

    fun prepararRespuesta(mensaje: ChatMessage) {
        _uiState.value = _uiState.value.copy(mensajeRespondido = mensaje)
    }

    fun cancelarRespuesta() {
        _uiState.value = _uiState.value.copy(mensajeRespondido = null)
    }

    fun enviarMensaje(texto: String) {
        val mensaje = texto.trim()
        if (mensaje.isBlank() || _uiState.value.enviando || _uiState.value.esBloqueoPermanente) return

        val replyToId = _uiState.value.mensajeRespondido?.id

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(enviando = true, error = null, mensaje = null)

            repository.enviarMensaje(mensaje = mensaje, replyToId = replyToId)
                .onSuccess { respuesta ->
                    val mensajeEnviado = respuesta.mensaje
                    if (mensajeEnviado != null) {
                        val yaExiste = _uiState.value.mensajes.any { it.id == mensajeEnviado.id }
                        if (!yaExiste) {
                            _uiState.value = _uiState.value.copy(
                                mensajes = _uiState.value.mensajes + mensajeEnviado
                            )
                        }
                        ultimoMensajeId = maxOf(ultimoMensajeId, mensajeEnviado.id)
                    }

                    _uiState.value = _uiState.value.copy(
                        enviando = false,
                        mensajeRespondido = null,
                        mensaje = respuesta.message,
                        error = null
                    )
                }
                .onFailure { error ->
                    val zoeEx = error as? ZoeException
                    val mensajeError = error.message ?: "No se pudo enviar el mensaje."
                    val esBloqueo = zoeEx?.esBloqueoPermanente == true ||
                            zoeEx?.detalle == "bloqueo" ||
                            mensajeError.contains("bloqueada", ignoreCase = true) ||
                            mensajeError.contains("segundo strike", ignoreCase = true)

                    _uiState.value = _uiState.value.copy(
                        enviando = false,
                        error = mensajeError,
                        esBloqueoPermanente = esBloqueo
                    )
                }
        }
    }

    fun enviarMultimedia(uri: Uri, textoLeyenda: String, context: android.content.Context) {
        if (_uiState.value.enviando || _uiState.value.esBloqueoPermanente) return

        val replyToId = _uiState.value.mensajeRespondido?.id

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(enviando = true, error = null)

            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "upload_${System.currentTimeMillis()}.tmp")

                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("archivo", file.name, requestFile)

                val textoBody = textoLeyenda.toRequestBody("text/plain".toMediaTypeOrNull())
                val replyToBody = replyToId?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                repository.enviarChatVecinalConArchivo(body, textoBody, replyToBody)
                    .onSuccess { respuesta ->
                        val mensajeEnviado = respuesta.mensaje
                        if (mensajeEnviado != null) {
                            val yaExiste = _uiState.value.mensajes.any { it.id == mensajeEnviado.id }
                            if (!yaExiste) {
                                _uiState.value = _uiState.value.copy(
                                    mensajes = _uiState.value.mensajes + mensajeEnviado
                                )
                            }
                            ultimoMensajeId = maxOf(ultimoMensajeId, mensajeEnviado.id)
                        }

                        _uiState.value = _uiState.value.copy(
                            enviando = false,
                            mensajeRespondido = null,
                            error = null
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            enviando = false,
                            error = error.message ?: "No se pudo enviar el archivo multimedia."
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    enviando = false,
                    error = e.localizedMessage ?: "Error al procesar el archivo adjunto."
                )
            }
        }
    }

    fun reaccionar(mensajeId: Int, emoji: String) {
        if (_uiState.value.reaccionandoMensajes.contains(mensajeId)) return

        _uiState.value =
            _uiState.value.copy(
                reaccionandoMensajes = _uiState.value.reaccionandoMensajes + mensajeId
            )

        viewModelScope.launch {
            repository.reaccionar(chatMessageId = mensajeId, emoji = emoji)
                .onSuccess { respuesta ->
                    actualizarReaccionesMensaje(
                        mensajeId = mensajeId,
                        reacciones = respuesta.reacciones
                    )
                }
                .onFailure { error ->
                    _uiState.value =
                        _uiState.value.copy(
                            error = error.message ?: "No se pudo guardar la reacción."
                        )
                }

            // CORREGIDO: Se agregó el operador '-' para remover el mensajeId del Set
            _uiState.value =
                _uiState.value.copy(
                    reaccionandoMensajes = _uiState.value.reaccionandoMensajes - mensajeId
                )
        }
    }

    fun cargarReacciones(mensajeId: Int) {
        viewModelScope.launch {
            repository.obtenerReacciones(chatMessageId = mensajeId)
                .onSuccess { respuesta ->
                    actualizarReaccionesMensaje(
                        mensajeId = mensajeId,
                        reacciones = respuesta.reacciones
                    )
                }
                .onFailure { error ->
                    _uiState.value =
                        _uiState.value.copy(
                            error = error.message ?: "No se pudieron obtener las reacciones."
                        )
                }
        }
    }

    private fun actualizarReaccionesMensaje(
        mensajeId: Int,
        reacciones: List<com.sigefiv.app.data.model.ChatReaccion>
    ) {
        val mensajesActuales = _uiState.value.mensajes
        val mensajesActualizados = mensajesActuales.map { mensaje ->
            if (mensaje.id == mensajeId) {
                mensaje.copy(reacciones = reacciones)
            } else {
                mensaje
            }
        }

        _uiState.value =
            _uiState.value.copy(
                mensajes = mensajesActualizados,
                error = null
            )
    }

    fun actualizarPresencia() {
        viewModelScope.launch {
            repository.actualizarPresencia()
                .onSuccess { respuesta ->
                    _uiState.value =
                        _uiState.value.copy(
                            personas = respuesta.personas,
                            personasEnLinea = respuesta.personas_en_linea,
                            usuariosEscribiendo = respuesta.usuarios_escribiendo,
                            error = null
                        )
                }
                .onFailure { error ->
                    _uiState.value =
                        _uiState.value.copy(
                            error = error.message ?: "No se pudo actualizar la presencia."
                        )
                }
        }
    }

    fun actualizarEscribiendo(escribiendo: Boolean) {
        viewModelScope.launch {
            repository.actualizarEscribiendo(escribiendo = escribiendo)
                .onSuccess { respuesta ->
                    _uiState.value =
                        _uiState.value.copy(
                            personas = respuesta.personas,
                            personasEnLinea = respuesta.personas_en_linea
                        )
                }
        }
    }

    fun limpiarError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(mensaje = null)
    }

    override fun onCleared() {
        detenerActualizacionAutomatica()
        super.onCleared()
    }

    class Factory(
        private val repository: ChatRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>
        ): T {
            if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
                return ChatViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}