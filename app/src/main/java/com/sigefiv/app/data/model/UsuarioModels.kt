package com.sigefiv.app.data.model

import com.google.gson.annotations.SerializedName

data class UsuariosResponse(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("usuarios") val usuarios: List<UsuarioListado> = emptyList(),
    @SerializedName("roles") val roles: List<UsuarioRol> = emptyList(),
    @SerializedName("cargos") val cargos: List<CargoItem> = emptyList()
)

data class CargoItem(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String = ""
)

data class UsuarioListado(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String = "",
    @SerializedName("email") val email: String = "",
    @SerializedName("estado") val estado: String? = null,
    @SerializedName("cargo_id") val cargoId: Int? = null,
    @SerializedName("cargo") val cargo: CargoItem? = null,
    @SerializedName("recibir_notificaciones") val recibirNotificaciones: Boolean = false,
    @SerializedName("telefono") val telefono: String? = null,
    @SerializedName("dni") val dni: String? = null,
    @SerializedName("direccion") val direccion: String? = null,
    @SerializedName("foto") val foto: String? = null,
    @SerializedName("avatar") val avatar: String? = null,
    @SerializedName("ultimo_acceso") val ultimoAcceso: String? = null,
    @SerializedName("bienvenida_vista") val bienvenidaVista: Boolean = false,
    @SerializedName("roles") val roles: List<UsuarioRol> = emptyList(),
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
) {
    val rolPrincipal: String
        get() = roles.firstOrNull()?.name ?: "Sin Rol"

    val cargoNombre: String
        get() = cargo?.nombre?.takeIf { it.isNotBlank() } ?: "Vecino"

    val estaActivo: Boolean
        get() = estado?.equals("activo", ignoreCase = true) == true

    val estaBloqueado: Boolean
        get() = estado?.equals("bloqueado", ignoreCase = true) == true

    val estaPendiente: Boolean
        get() = estado?.equals("pendiente", ignoreCase = true) == true
}

data class UsuarioRol(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String? = null
)

data class CambiarEstadoResponse(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("message") val message: String? = null,
    @SerializedName("usuario") val usuario: UsuarioListado? = null
)

data class CrearUsuarioRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("cargo_id") val cargoId: Int?,
    @SerializedName("password") val password: String,
    @SerializedName("password_confirmation") val passwordConfirmation: String
)

data class CambiarRolRequest(
    @SerializedName("rol") val rol: String
)

data class CambiarEstadoRequest(
    @SerializedName("estado") val estado: String
)