package com.sigefiv.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.ZoeConsultaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ZoeMensaje(
    val texto: String,
    val esUsuario: Boolean
)

class ZoeViewModel(
    context: Context
) : ViewModel() {

    private val api = ApiClient.create(context.applicationContext)

    private val _mensajes = MutableStateFlow(
        listOf(
            ZoeMensaje(
                texto = "Hola, soy ZOE. Estoy lista para ayudarte con la información de SIGEFIV.",
                esUsuario = false
            )
        )
    )
    val mensajes: StateFlow<List<ZoeMensaje>> = _mensajes.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun enviarConsulta(texto: String) {

        val consulta = texto.trim()

        if (consulta.isEmpty() || _cargando.value) return

        _error.value = null

        _mensajes.value = _mensajes.value +
                ZoeMensaje(
                    texto = consulta,
                    esUsuario = true
                )

        viewModelScope.launch {

            _cargando.value = true

            try {

                // Nuevo endpoint:
                // POST /api/zoe
                val respuesta = api.consultarZoeN8n(
                    ZoeConsultaRequest(
                        mensaje = consulta
                    )
                )

                if (respuesta.success) {

                    val mensaje = respuesta.respuesta
                        ?.trim()
                        ?.replace("**", "")
                        ?.replace("__", "")
                        ?.takeIf { it.isNotEmpty() }
                        ?: "ZOE recibió la consulta, pero no devolvió un mensaje."

                    _mensajes.value = _mensajes.value +
                            ZoeMensaje(
                                texto = mensaje,
                                esUsuario = false
                            )

                } else {

                    val mensaje = respuesta.message
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() }
                        ?: respuesta.mensaje
                            ?.trim()
                            ?.takeIf { it.isNotEmpty() }
                        ?: "No fue posible procesar la consulta."

                    _error.value = mensaje

                    _mensajes.value = _mensajes.value +
                            ZoeMensaje(
                                texto = mensaje,
                                esUsuario = false
                            )
                }

            } catch (e: Exception) {

                val mensaje = when {

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

                _error.value = mensaje

                _mensajes.value = _mensajes.value +
                        ZoeMensaje(
                            texto = mensaje,
                            esUsuario = false
                        )
            } finally {

                _cargando.value = false
            }
        }
    }

    fun limpiarError() {
        _error.value = null
    }
}