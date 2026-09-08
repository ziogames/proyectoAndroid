package com.sigefiv.app.data.api

import com.sigefiv.app.data.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

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
         * No debemos enviar un Bearer de una sesión anterior
         * en esta petición, porque podría provocar que Laravel
         * intente utilizar un token antiguo o inválido.
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

                    /*
                     * Para /auth/google NO enviamos Authorization.
                     *
                     * Para todas las demás peticiones se mantiene
                     * exactamente el comportamiento anterior.
                     */
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

        return chain.proceed(request)
    }
}