package com.sigefiv.app.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val token: String?,
    val token_type: String?,
    val usuario: Usuario?
)

data class Usuario(
    val id: Int,
    val name: String,
    val email: String,
    val telefono: String?,
    val dni: String?,
    val direccion: String?,
    val foto: String?,
    val rol: String?,
    val permisos: List<String> = emptyList(),
    val metodo_acceso: String? = null,
    val estado: String? = null,
    @SerializedName("bienvenida_vista")
    val bienvenidaVista: Boolean = false
)