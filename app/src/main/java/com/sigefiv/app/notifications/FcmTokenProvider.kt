package com.sigefiv.app.notifications

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FcmTokenProvider {

    suspend fun obtenerToken(): String =
        suspendCancellableCoroutine { continuation ->

            FirebaseMessaging
                .getInstance()
                .token
                .addOnSuccessListener { token ->
                    continuation.resume(token)
                }
                .addOnFailureListener { error ->
                    continuation.resumeWithException(error)
                }
        }
}