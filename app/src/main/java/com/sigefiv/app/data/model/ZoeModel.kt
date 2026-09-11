package com.sigefiv.app.data.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject

data class ZoeConsultaRequest(
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
    val message: String? = null
)