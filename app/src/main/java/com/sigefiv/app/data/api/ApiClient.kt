package com.sigefiv.app.data.api

import android.content.Context
import com.sigefiv.app.ServidorConfig
import com.sigefiv.app.data.SessionManager
import okhttp3.ConnectionPool
import okhttp3.Dns
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.Inet4Address
import java.net.InetAddress
import java.util.concurrent.TimeUnit

object ApiClient {

    private val BASE_URL: String
        get() = ServidorConfig.API_BASE_URL

    @Volatile
    private var sharedHttpClient: OkHttpClient? = null

    @Volatile
    private var retrofitInstance: Retrofit? = null

    @Volatile
    private var lastBaseUrl: String? = null

    // Prioriza IPv4 para evitar bloqueos en celulares físicos con Wi-Fi / IPv6 parcial
    private val ipv4FirstDns = object : Dns {
        override fun lookup(hostname: String): List<InetAddress> {
            val addresses = Dns.SYSTEM.lookup(hostname)
            val ipv4 = addresses.filterIsInstance<Inet4Address>()
            return ipv4.ifEmpty { addresses }
        }
    }

    /**
     * Instancia única de OkHttpClient compartida por toda la aplicación.
     * Reutiliza el mismo túnel TLS/HTTPS aunque cambie el usuario autenticado.
     */
    fun getHttpClient(context: Context): OkHttpClient {
        return sharedHttpClient ?: synchronized(this) {
            sharedHttpClient ?: OkHttpClient.Builder()
                .dns(ipv4FirstDns)
                .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
                .addInterceptor(AuthInterceptor(SessionManager(context.applicationContext)))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
                .also { sharedHttpClient = it }
        }
    }

    private fun getRetrofit(context: Context): Retrofit {
        val currentUrl = BASE_URL
        val cached = retrofitInstance
        if (cached != null && lastBaseUrl == currentUrl) {
            return cached
        }

        return synchronized(this) {
            val recheck = retrofitInstance
            if (recheck != null && lastBaseUrl == currentUrl) {
                recheck
            } else {
                Retrofit.Builder()
                    .baseUrl(currentUrl)
                    .client(getHttpClient(context))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .also {
                        retrofitInstance = it
                        lastBaseUrl = currentUrl
                    }
            }
        }
    }

    fun create(context: Context): AuthApi =
        getRetrofit(context).create(AuthApi::class.java)

    fun asambleaApi(context: Context): AsambleaApi =
        getRetrofit(context).create(AsambleaApi::class.java)

    fun chatApi(context: Context): ChatApi =
        getRetrofit(context).create(ChatApi::class.java)

    fun usuarioApi(context: Context): UsuarioApi =
        getRetrofit(context).create(UsuarioApi::class.java)

    fun cajaApi(context: Context): CajaApi =
        getRetrofit(context).create(CajaApi::class.java)

    fun rolApi(context: Context): RolApi =
        getRetrofit(context).create(RolApi::class.java)

    fun actividadApi(context: Context): ActividadApi =
        getRetrofit(context).create(ActividadApi::class.java)

    fun emprendimientoApi(context: Context): EmprendimientoApi =
        getRetrofit(context).create(EmprendimientoApi::class.java)

    fun zoeApi(context: Context): ZoeApi =
        getRetrofit(context).create(ZoeApi::class.java)

    fun bitacoraApi(context: Context): ApiService =
        getRetrofit(context).create(ApiService::class.java)

    fun incidenciaApi(context: Context): IncidenciaApiService =
        getRetrofit(context).create(IncidenciaApiService::class.java)

    fun configuracionApi(context: Context): ConfiguracionApi =
        getRetrofit(context).create(ConfiguracionApi::class.java)

    fun reportesApi(context: Context): ReportesApiService =
        getRetrofit(context).create(ReportesApiService::class.java)
}