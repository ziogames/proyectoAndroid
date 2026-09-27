package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.MovimientoRequest
import com.sigefiv.app.data.model.MovimientoResponse
import com.sigefiv.app.data.model.MovimientosResponse
import retrofit2.Response
import android.net.Uri
import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class MovimientosRepository(
    private val authApi: AuthApi,
    private val context: Context
) {

    suspend fun obtenerMovimientos(
        limite: Int? = null
    ): MovimientosResponse {
        return authApi.movimientos(limite)
    }
    suspend fun obtenerMovimientoPorId(
        id: Int
    ): MovimientoResponse {
        return authApi.obtenerMovimientoPorId(id)
    }

    suspend fun crearMovimiento(
        request: MovimientoRequest,
        comprobanteUri: Uri?
    ): MovimientoResponse {

        val texto = "text/plain".toMediaType()

        val fecha = request.fecha
            .toRequestBody(texto)

        val categoriaId = request.categoria_id
            .toString()
            .toRequestBody(texto)

        val concepto = request.concepto
            .toRequestBody(texto)

        val persona = request.persona
            ?.toRequestBody(texto)

        val formaPago = request.forma_pago
            .toRequestBody(texto)

        val monto = request.monto
            .toString()
            .toRequestBody(texto)

        val referencia = request.referencia
            ?.toRequestBody(texto)

        val observaciones = request.observaciones
            ?.toRequestBody(texto)

        val comprobante = comprobanteUri?.let { uri ->

            val resolver = context.contentResolver

            val bytes = resolver
                .openInputStream(uri)
                ?.use { it.readBytes() }

            bytes?.let {
                val body = it.toRequestBody(
                    "image/*".toMediaType()
                )

                MultipartBody.Part.createFormData(
                    "comprobante",
                    "comprobante.jpg",
                    body
                )
            }
        }

        return authApi.crearMovimiento(
            fecha = fecha,
            categoriaId = categoriaId,
            concepto = concepto,
            persona = persona,
            formaPago = formaPago,
            monto = monto,
            referencia = referencia,
            observaciones = observaciones,
            comprobante = comprobante
        )
    }

    suspend fun actualizarMovimiento(
        id: Int,
        request: MovimientoRequest
    ): MovimientoResponse {
        return authApi.actualizarMovimiento(id, request)
    }

    suspend fun eliminarMovimiento(
        id: Int
    ): Response<Unit> {
        return authApi.eliminarMovimiento(id)
    }
}
