package com.sigefiv.app

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging

object FirebaseTokenTest {

    private const val TAG = "SIGEFIV_FCM"

    fun obtenerToken() {

        FirebaseMessaging.getInstance()
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

                val token = task.result

                Log.d(
                    TAG,
                    "========================================"
                )

                Log.d(
                    TAG,
                    "TOKEN FCM DE SIGEFIV:"
                )

                Log.d(
                    TAG,
                    token
                )

                Log.d(
                    TAG,
                    "========================================"
                )
            }
    }
}