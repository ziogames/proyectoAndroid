package com.sigefiv.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SIGEFIVFirebaseMessagingService : FirebaseMessagingService() {

    companion object {

        private const val TAG = "SIGEFIV_FCM"

        private const val CHANNEL_ID =
            "sigefiv_notificaciones"

        private const val CHANNEL_NAME =
            "Notificaciones SIGEFIV"

        private const val CHANNEL_DESCRIPTION =
            "Notificaciones de SIGEFIV"

        private const val NOTIFICATION_ID =
            1001

        /*
         * Datos utilizados para abrir una pantalla
         * específica desde una notificación.
         */
        const val EXTRA_TIPO =
            "tipo"

        const val EXTRA_ASAMBLEA_ID =
            "asamblea_id"
    }

    /**
     * Se ejecuta cuando Firebase genera un nuevo token
     * o cuando el token actual cambia.
     */
    override fun onNewToken(token: String) {

        super.onNewToken(token)

        Log.d(
            TAG,
            "Nuevo token FCM generado:"
        )

        Log.d(
            TAG,
            token
        )

        /*
        |--------------------------------------------------------------------------
        | Registro del nuevo token
        |--------------------------------------------------------------------------
        |
        | El token se registra después del login mediante
        | FcmTokenManager.
        |
        |--------------------------------------------------------------------------
        */
    }

    /**
     * Se ejecuta cuando llega un mensaje FCM mientras
     * la aplicación está en primer plano.
     */
    override fun onMessageReceived(
        remoteMessage: RemoteMessage
    ) {

        super.onMessageReceived(
            remoteMessage
        )

        Log.d(
            TAG,
            "========================================"
        )

        Log.d(
            TAG,
            "NOTIFICACIÓN FCM RECIBIDA"
        )

        Log.d(
            TAG,
            "From: ${remoteMessage.from}"
        )

        Log.d(
            TAG,
            "Data: ${remoteMessage.data}"
        )

        Log.d(
            TAG,
            "========================================"
        )

        val titulo =
            remoteMessage.notification?.title
                ?: remoteMessage.data["titulo"]
                ?: "SIGEFIV"

        val mensaje =
            remoteMessage.notification?.body
                ?: remoteMessage.data["mensaje"]
                ?: "Tienes una nueva notificación."

        /*
         * Recuperamos los datos enviados por Laravel.
         */
        val tipo =
            remoteMessage.data["tipo"]

        val asambleaId =
            remoteMessage.data["asamblea_id"]

        crearCanalNotificaciones()

        mostrarNotificacion(
            titulo = titulo,
            mensaje = mensaje,
            tipo = tipo,
            asambleaId = asambleaId
        )
    }

    /**
     * Crea el canal de notificaciones requerido
     * por Android 8.0 o superior.
     */
    private fun crearCanalNotificaciones() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            val canal =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {

                    description =
                        CHANNEL_DESCRIPTION
                }

            val notificationManager =
                getSystemService(
                    NotificationManager::class.java
                )

            notificationManager
                .createNotificationChannel(
                    canal
                )
        }
    }

    /**
     * Muestra una notificación nativa de Android.
     *
     * Además de mostrar el mensaje, conserva
     * tipo y asamblea_id para que MainActivity
     * pueda decidir qué pantalla abrir cuando
     * el usuario toque la notificación.
     */
    private fun mostrarNotificacion(
        titulo: String,
        mensaje: String,
        tipo: String?,
        asambleaId: String?
    ) {

        val intent =
            Intent(
                this,
                MainActivity::class.java
            ).apply {

                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP

                /*
                 * Información enviada por Laravel.
                 */
                if (!tipo.isNullOrBlank()) {

                    putExtra(
                        EXTRA_TIPO,
                        tipo
                    )
                }

                if (!asambleaId.isNullOrBlank()) {

                    putExtra(
                        EXTRA_ASAMBLEA_ID,
                        asambleaId
                    )
                }
            }

        /*
         * Usamos un requestCode diferente para cada
         * asamblea para evitar que Android reutilice
         * accidentalmente un PendingIntent anterior.
         */
        val requestCode =
            asambleaId
                ?.toIntOrNull()
                ?: NOTIFICATION_ID

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(
                    R.drawable.ic_launcher_foreground
                )
                .setContentTitle(
                    titulo
                )
                .setContentText(
                    mensaje
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(mensaje)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_HIGH
                )
                .setAutoCancel(
                    true
                )
                .setContentIntent(
                    pendingIntent
                )
                .build()

        try {

            NotificationManagerCompat
                .from(this)
                .notify(
                    requestCode,
                    notification
                )

        } catch (e: SecurityException) {

            Log.e(
                TAG,
                "No se pudo mostrar la notificación. " +
                        "Probablemente falta el permiso POST_NOTIFICATIONS.",
                e
            )
        }
    }
}