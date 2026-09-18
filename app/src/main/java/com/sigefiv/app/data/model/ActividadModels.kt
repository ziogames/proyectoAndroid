package com.sigefiv.app.data.model

import com.google.gson.annotations.SerializedName

data class ActividadResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("estadisticas")
    val estadisticas: EstadisticasActividad,

    @SerializedName("actividades")
    val actividades: ActividadesPaginadas
)

data class EstadisticasActividad(
    @SerializedName("total_actividades")
    val totalActividades: Int,

    @SerializedName("usuarios_activos")
    val usuariosActivos: Int,

    @SerializedName("actividades_hoy")
    val actividadesHoy: Int,

    @SerializedName("modulo_mas_utilizado")
    val moduloMasUtilizado: ModuloEstadistica?,

    @SerializedName("modulos")
    val modulos: List<ModuloEstadistica>,

    @SerializedName("usuarios")
    val usuarios: List<UsuarioEstadistica>
)

data class ModuloEstadistica(
    @SerializedName("modulo")
    val modulo: String,

    @SerializedName("total")
    val total: Int
)

data class UsuarioEstadistica(
    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("user")
    val usuario: UsuarioActividad?
)

data class UsuarioActividad(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val nombre: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("foto")
    val foto: String?,

    @SerializedName("google_id")
    val googleId: String?
)

data class ActividadesPaginadas(
    @SerializedName("current_page")
    val paginaActual: Int,

    @SerializedName("data")
    val datos: List<Actividad>,

    @SerializedName("last_page")
    val ultimaPagina: Int,

    @SerializedName("per_page")
    val porPagina: Int,

    @SerializedName("total")
    val total: Int,

    @SerializedName("next_page_url")
    val siguientePagina: String?,

    @SerializedName("prev_page_url")
    val paginaAnterior: String?
)

data class Actividad(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int?,

    @SerializedName("modulo")
    val modulo: String,

    @SerializedName("accion")
    val accion: String,

    @SerializedName("ruta")
    val ruta: String?,

    @SerializedName("ip")
    val ip: String?,

    @SerializedName("user_agent")
    val userAgent: String?,

    @SerializedName("created_at")
    val fechaCreacion: String,

    @SerializedName("user")
    val usuario: UsuarioActividad?
)