package com.sigefiv.app.data.api

import com.sigefiv.app.data.model.FiltrosReporteResponse
import com.sigefiv.app.data.model.ResumenReporteResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ReportesApiService {

    @GET("reportes/filtros")
    suspend fun obtenerFiltros(): Response<FiltrosReporteResponse>

    @GET("reportes/resumen")
    suspend fun obtenerResumen(
        @Query("anio") anio: Int,
        @Query("mes") mes: Int? = null,
        @Query("reporte") reporte: String = "estado"
    ): Response<ResumenReporteResponse>

    @Streaming
    @GET("reportes/pdf")
    suspend fun descargarPdf(
        @Query("anio") anio: Int,
        @Query("mes") mes: Int? = null
    ): Response<ResponseBody>

    @Streaming
    @GET("reportes/excel")
    suspend fun descargarExcel(
        @Query("anio") anio: Int,
        @Query("mes") mes: Int? = null
    ): Response<ResponseBody>
}