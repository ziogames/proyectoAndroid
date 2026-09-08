package com.sigefiv.app.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta principal devuelta por el endpoint GET api/roles de Laravel.
 */
data class RolesResponse(
    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("roles")
    val roles: List<RolDetalle> = emptyList(),

    @SerializedName("message")
    val message: String? = null
)

/**
 * Respuesta devuelta al consultar un solo rol: GET api/roles/{id}.
 */
data class RolDetalleResponse(
    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("rol")
    val rol: RolDetalle? = null,

    @SerializedName("message")
    val message: String? = null
)

/**
 * Petición enviada para actualizar nombre y permisos: PUT api/roles/{id}.
 */
data class ActualizarRolRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("permissions")
    val permissions: List<String> = emptyList()
)

/**
 * Respuesta genérica devuelta al guardar o eliminar.
 */
data class BaseResponse(
    @SerializedName("success")
    val success: Boolean = true,

    @SerializedName("message")
    val message: String? = null
)

/**
 * Modelo de cada Rol con sus estadísticas y módulos.
 */
data class RolDetalle(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("users_count", alternate = ["cantidadUsuarios", "usuarios_count"])
    val cantidadUsuarios: Int = 0,

    @SerializedName("permissions_count", alternate = ["cantidadPermisos", "permisos_count"])
    val cantidadPermisos: Int = 0,

    @SerializedName("permisos_agrupados", alternate = ["permisosAgrupados"])
    val permisosAgrupados: Map<String, List<Permiso>> = emptyMap()
)

/**
 * Modelo para cada permiso individual dentro de un módulo/carpeta.
 */
data class Permiso(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("asignado", alternate = ["assigned"])
    val asignado: Boolean = false
)