package com.sigefiv.app

import android.content.Context
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.sigefiv.app.data.SessionManager
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.repository.FcmTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlinx.coroutines.launch

object FcmTokenManager {

    private const val TAG = "SIGEFIV_FCM"

    /**
     * Obtiene el token FCM y lo registra en Laravel
     * utilizando la sesión Sanctum del usuario.
     *
     * Debe ejecutarse después de que el usuario
     * haya iniciado sesión correctamente.
     */
    fun registrarToken(
        context: Context
    ) {

        val contexto = context.applicationContext

        kotlinx.coroutines.CoroutineScope(
            Dispatchers.IO
        ).launch {

            try {

                val sessionManager =
                    SessionManager(contexto)

                val tokenSanctum =
                    sessionManager.token.first()

                if (tokenSanctum.isNullOrBlank()) {

                    Log.d(
                        TAG,
                        "No hay sesión Sanctum. No se registra el token FCM."
                    )

                    return@launch
                }

                Log.d(
                    TAG,
                    "Sesión Sanctum encontrada. Obteniendo token FCM..."
                )

                FirebaseMessaging
                    .getInstance()
                    .token
                    .addOnCompleteListener { task ->

                        if (!task.isSuccessful) {

                            Log.e(
                                TAG,
                                "No se pudo obtener el token FCM.",
                                task.exception
                            )

                            return@addOnCompleteListener
                        }

                        val tokenFcm =
                            task.result

                        if (tokenFcm.isNullOrBlank()) {

                            Log.e(
                                TAG,
                                "Firebase devolvió un token FCM vacío."
                            )

                            return@addOnCompleteListener
                        }

                        kotlinx.coroutines.CoroutineScope(
                            Dispatchers.IO
                        ).launch {

                            try {

                                val api =
                                    ApiClient.create(contexto)

                                val repository =
                                    FcmTokenRepository(api)

                                val resultado =
                                    repository.registrarToken(
                                        tokenFcm
                                    )

                                resultado
                                    .onSuccess { respuesta ->

                                        Log.d(
                                            TAG,
                                            "========================================"
                                        )

                                        Log.d(
                                            TAG,
                                            "TOKEN FCM REGISTRADO EN SIGEFIV"
                                        )

                                        Log.d(
                                            TAG,
                                            "Token: $tokenFcm"
                                        )

                                        Log.d(
                                            TAG,
                                            "Mensaje: ${respuesta.message}"
                                        )

                                        Log.d(
                                            TAG,
                                            "========================================"
                                        )
                                    }
                                    .onFailure { error ->

                                        Log.e(
                                            TAG,
                                            "No se pudo registrar el token FCM en Laravel.",
                                            error
                                        )
                                    }

                            } catch (e: Exception) {

                                Log.e(
                                    TAG,
                                    "Error registrando el token FCM.",
                                    e
                                )
                            }
                        }
                    }

            } catch (e: Exception) {

                Log.e(
                    TAG,
                    "Error preparando el registro del token FCM.",
                    e
                )
            }
        }
    }


    /**
     * Desactiva el token FCM actual en Laravel.
     *
     * Esta versión es suspend para que el cierre de sesión
     * pueda esperar a que Laravel procese la petición antes
     * de eliminar la sesión Sanctum local.
     *
     * Devuelve true si el token fue desactivado correctamente.
     */
    suspend fun desactivarToken(
        context: Context
    ): Boolean {

        val contexto =
            context.applicationContext

        return try {

            val sessionManager =
                SessionManager(contexto)

            val tokenSanctum =
                sessionManager.token.first()

            if (tokenSanctum.isNullOrBlank()) {

                Log.d(
                    TAG,
                    "No hay sesión Sanctum. No se desactiva el token FCM."
                )

                return false
            }

            Log.d(
                TAG,
                "Sesión Sanctum encontrada. Obteniendo token FCM para desactivarlo..."
            )

            val tokenFcm =
                obtenerTokenFcm()

            if (tokenFcm.isNullOrBlank()) {

                Log.e(
                    TAG,
                    "No se pudo obtener un token FCM para desactivarlo."
                )

                return false
            }

            val resultado =
                withContext(Dispatchers.IO) {

                    val api =
                        ApiClient.create(contexto)

                    val repository =
                        FcmTokenRepository(api)

                    repository.desactivarToken(
                        tokenFcm
                    )
                }

            resultado
                .onSuccess { respuesta ->

                    Log.d(
                        TAG,
                        "========================================"
                    )

                    Log.d(
                        TAG,
                        "TOKEN FCM DESACTIVADO EN SIGEFIV"
                    )

                    Log.d(
                        TAG,
                        "Token: $tokenFcm"
                    )

                    Log.d(
                        TAG,
                        "Mensaje: ${respuesta.message}"
                    )

                    Log.d(
                        TAG,
                        "========================================"
                    )
                }
                .onFailure { error ->

                    Log.e(
                        TAG,
                        "No se pudo desactivar el token FCM en Laravel.",
                        error
                    )
                }

            resultado.isSuccess

        } catch (e: Exception) {

            Log.e(
                TAG,
                "Error desactivando el token FCM.",
                e
            )

            false
        }
    }


    /**
     * Obtiene el token FCM actual de Firebase.
     *
     * FirebaseMessaging.token utiliza un Task, por lo que
     * lo convertimos a una función suspend para poder
     * esperar correctamente el resultado.
     */
    private suspend fun obtenerTokenFcm(): String? {

        return suspendCancellableCoroutine { continuation ->

            FirebaseMessaging
                .getInstance()
                .token
                .addOnCompleteListener { task ->

                    if (!task.isSuccessful) {

                        Log.e(
                            TAG,
                            "No se pudo obtener el token FCM.",
                            task.exception
                        )

                        if (continuation.isActive) {
                            continuation.resume(null)
                        }

                        return@addOnCompleteListener
                    }

                    val token =
                        task.result

                    if (continuation.isActive) {
                        continuation.resume(token)
                    }
                }
        }
    }
}