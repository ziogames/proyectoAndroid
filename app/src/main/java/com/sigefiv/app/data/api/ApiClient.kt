package com.sigefiv.app.data.api

import android.content.Context
import com.sigefiv.app.data.SessionManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL =
      "http://10.0.2.2:8080/api/"

   // private const val BASE_URL =
     //   "https://api.sigefiv.win/api/"

    private fun createRetrofit(
        context: Context
    ): Retrofit {

        val sessionManager =
            SessionManager(context)

        val httpClient =
            OkHttpClient.Builder()
                .addInterceptor(
                    AuthInterceptor(
                        sessionManager
                    )
                )
                .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    /**
     * Cliente utilizado actualmente para autenticación.
     *
     * Se mantiene para no romper el login existente.
     */
    fun create(
        context: Context
    ): AuthApi {

        return createRetrofit(context)
            .create(AuthApi::class.java)
    }

    /**
     * Cliente para la API de Asambleas.
     */
    fun asambleaApi(
        context: Context
    ): AsambleaApi {

        return createRetrofit(context)
            .create(AsambleaApi::class.java)
    }

    /**
     * Cliente para la API del Chat Vecinal.
     */
    fun chatApi(
        context: Context
    ): ChatApi {

        return createRetrofit(context)
            .create(ChatApi::class.java)
    }

    /**
     * Cliente para la API de Usuarios.
     */
    fun usuarioApi(
        context: Context
    ): UsuarioApi {

        return createRetrofit(context)
            .create(UsuarioApi::class.java)
    }

    /**
     * Cliente para la API de Caja.
     */
    fun cajaApi(
        context: Context
    ): CajaApi {

        return createRetrofit(context)
            .create(CajaApi::class.java)
    }

    /**
     * Cliente para la API de Roles.
     */
    fun rolApi(
        context: Context
    ): RolApi {

        return createRetrofit(context)
            .create(RolApi::class.java)
    }
}