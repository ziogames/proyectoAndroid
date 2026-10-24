
package com.sigefiv.app

import android.os.Build

object ServidorConfig {

    // ÚNICO LUGAR donde cambiar la IP del PC
    private const val IP_PC = "192.168.1.49"

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

    // El emulador accede al PC mediante 10.0.2.2
    private val host: String
        get() = if (esEmulador()) "10.0.2.2" else IP_PC

    // API Laravel
    val API_BASE_URL: String
        get() = "http://$host:8080/api/"

    // Gateway de voz ZOE
    val ZOE_WEBSOCKET_URL: String
        get() = "ws://$host:8001/ws/zoe"
}
