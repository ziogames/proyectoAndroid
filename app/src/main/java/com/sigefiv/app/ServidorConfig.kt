package com.sigefiv.app

import android.os.Build

object ServidorConfig {

    // Cambia a 'true' solo si necesitas probar offline con IP local
    private const val MODO_LOCAL = false
    //private val MODO_LOCAL: Boolean = BuildConfig.DEBUG
    private const val IP_PC = "192.168.1.49"
    private const val DOMINIO_PROD = "api.sigefiv.win"

    private fun esEmulador(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk", ignoreCase = true)
                || Build.MODEL.contains("Emulator", ignoreCase = true)
                || Build.MODEL.contains("Android SDK built for", ignoreCase = true)
                || Build.MANUFACTURER.contains("Genymotion", ignoreCase = true)
                || (Build.BRAND.startsWith("generic")
                && Build.DEVICE.startsWith("generic"))
    }

    private val hostLocal: String
        get() = if (esEmulador()) "10.0.2.2" else IP_PC

    // URL base raíz del servidor (sin el /api/)
    val BASE_URL: String
        get() = if (MODO_LOCAL) {
            "http://$hostLocal:8080"
        } else {
            "https://$DOMINIO_PROD"
        }

    // API Laravel
    val API_BASE_URL: String
        get() = "$BASE_URL/api/"

    // Gateway de voz ZOE
    val ZOE_WEBSOCKET_URL: String
        get() = if (MODO_LOCAL) {
            "ws://$hostLocal:8001/ws/zoe"
        } else {
            "wss://$DOMINIO_PROD/ws/zoe"
        }

    /**
     * Normaliza cualquier URL o ruta de archivo proveniente del backend:
     * - Convierte rutas relativas ("emprendimientos/...", "storage/...", etc.) a URLs absolutas válidas con BASE_URL.
     * - Asegura el prefijo "storage/" para archivos del disco público de Laravel sin duplicarlo.
     * - Reemplaza URLs absolutas que apunten a dominios locales obsoletos (localhost, 10.0.2.2 con o sin puerto).
     * - Devuelve URLs externas intactas si son de servicios terceros.
     */
    fun normalizarUrlArchivo(url: String?): String? {
        if (url.isNullOrBlank()) return null

        val urlLimpia = url.trim()

        // 1. Caso ruta relativa devuelta por Laravel
        if (!urlLimpia.startsWith("http://", ignoreCase = true) &&
            !urlLimpia.startsWith("https://", ignoreCase = true)
        ) {
            val path = urlLimpia.removePrefix("/")
            val rutaConStorage = if (
                path.startsWith("storage/") ||
                path.startsWith("documentos/") ||
                path.startsWith("images/")
            ) {
                path
            } else {
                "storage/$path"
            }
            return "$BASE_URL/$rutaConStorage"
        }

        // 2. Caso URL absoluta con localhost o 10.0.2.2 guardada en BD
        return urlLimpia
            .replace("http://localhost:8080", BASE_URL)
            .replace("https://localhost:8080", BASE_URL)
            .replace("http://10.0.2.2:8080", BASE_URL)
            .replace("https://10.0.2.2:8080", BASE_URL)
            .replace("http://localhost", BASE_URL)
            .replace("https://localhost", BASE_URL)
            .replace("http://10.0.2.2", BASE_URL)
            .replace("https://10.0.2.2", BASE_URL)
    }

    /**
     * Alias de normalizarUrlArchivo para mantener compatibilidad con Coil / Glide.
     */
    fun resolverUrlImagen(ruta: String?): String? = normalizarUrlArchivo(ruta)
}