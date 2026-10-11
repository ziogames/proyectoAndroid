package com.sigefiv.app.data.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject

data class ZoeConsultaRequest(
    val usuario_id: Int,
    val nombre_usuario: String,
    val mensaje: String
)

/**
 * Request exclusivo para el nuevo ZOE AI.
 *
 * No modifica el ZOE actual.
 */
data class ZoeAIConsultaRequest(
    val usuario_id: Int,
    val nombre_usuario: String,
    val mensaje: String
)

data class ZoeConsultaResponse(
    val success: Boolean = false,
    val respuesta: String? = null,
    val consulta: String? = null,
    val interpretacion: JsonObject? = null,
    val resultado: JsonElement? = null,
    val tipo: String? = null,
    val mensaje: String? = null,
    val message: String? = null,
    val estadistica: JsonElement? = null
)

data class ZoeVozResponse(
    val success: Boolean = false,
    val texto: String? = null,
    val message: String? = null
)