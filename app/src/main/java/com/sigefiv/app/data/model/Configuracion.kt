package com.sigefiv.app.data.model

data class Configuracion(
    val id: Int = 1,
    val nombre_sistema: String? = null,
    val organizacion: String? = null,
    val ruc: String? = null,
    val direccion: String? = null,
    val telefono: String? = null,
    val correo: String? = null,
    val web: String? = null,
    val facebook: String? = null,
    val instagram: String? = null,
    val logo: String? = null,
    val color_principal: String? = null,
    // Directiva
    val presidente: String? = null,
    val tesorero: String? = null,
    val secretario: String? = null,
    // Moneda y Contabilidad
    val moneda: String? = "Soles",
    val simbolo_moneda: String? = "S/",
    val decimales: Int = 2,
    val zona_horaria: String? = "America/Lima",
    val anio_inicio: Int? = 2026,
    val mes_inicio: Int? = 1,
    val saldo_apertura: Double? = 0.0,
    val contabilidad_iniciada: Boolean = true,
    // Seguridad
    val bitacora_activa: Boolean = true,
    val sesion_unica: Boolean = false,
    val bloqueo_intentos: Boolean = true,
    val expirar_password: Boolean = false,
    val intentos_login: Int = 5,
    val tiempo_sesion: Int = 120,
    val longitud_password: Int = 8
)