
package com.sigefiv.app.data.api

import com.sigefiv.app.data.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val originalRequest = chain.request()

        /*
         * El login con Google obtiene una nueva sesión.
         *
         * No enviamos un Bearer anterior en esta petición.
         */
        val esLoginGoogle =
            originalRequest.url.encodedPath.endsWith("/auth/google")

        val token =
            if (esLoginGoogle) {
                null
            } else {
                runBlocking {
                    sessionManager.token.first()
                }
            }

        println(
            "SIGEFIV TOKEN: ${
                if (token.isNullOrBlank()) {
                    "VACIO"
                } else {
                    "EXISTE (${token.length} caracteres)"
                }
            }"
        )

        val request =
            originalRequest
                .newBuilder()
                .apply {

                    if (!token.isNullOrBlank() && !esLoginGoogle) {

                        addHeader(
                            "Authorization",
                            "Bearer $token"
                        )

                        println(
                            "SIGEFIV AUTH: Bearer enviado"
                        )

                    } else {

                        if (esLoginGoogle) {
                            println(
                                "SIGEFIV AUTH: Login Google - NO SE ENVIO TOKEN"
                            )
                        } else {
                            println(
                                "SIGEFIV AUTH: NO SE ENVIO TOKEN"
                            )
                        }
                    }

                    addHeader(
                        "Accept",
                        "application/json"
                    )
                }
                .build()

        println(
            "SIGEFIV URL: ${request.url}"
        )

        /*
         * Enviar la petición al servidor.
         *
         * Detectar errores de conexión.
         */
        val response: Response

        try {

            response =
                chain.proceed(request)

        } catch (e: IOException) {

            println(
                "SIGEFIV ERROR DE CONEXION: ${e.message}"
            )

            NetworkErrorManager.mostrarErrorConexion()

            throw e
        }

        /*
         * DIAGNÓSTICO
         */
        println(
            "SIGEFIV HTTP CODE: ${response.code}"
        )

        println(
            "SIGEFIV HTTP MESSAGE: ${response.message}"
        )

        println(
            "SIGEFIV RESPONSE URL: ${response.request.url}"
        )

        /*
         * Manejo de errores HTTP del servidor.
         *
         * No mostramos mensajes técnicos al usuario.
         */
        when (response.code) {

            500, 502, 503, 504 -> {

                println(
                    "SIGEFIV ERROR DEL SERVIDOR: ${response.code}"
                )

                NetworkErrorManager.mostrarErrorServidor()
            }
        }

        /*
         * Leer el cuerpo únicamente para diagnóstico.
         */
        if (esLoginGoogle || response.code >= 400) {

            try {

                val responseBody =
                    response.peekBody(1024 * 1024)

                println(
                    "SIGEFIV RESPONSE BODY: ${responseBody.string()}"
                )

            } catch (e: Exception) {

                println(
                    "SIGEFIV RESPONSE BODY ERROR: ${e.message}"
                )
            }
        }

        return response
    }
}