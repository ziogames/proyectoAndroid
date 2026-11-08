package com.sigefiv.app.data.repository

import android.content.Context
import com.sigefiv.app.data.api.ApiClient
import com.sigefiv.app.data.model.BitacoraResponse
import retrofit2.Response

class BitacoraRepository(private val context: Context) {

    suspend fun obtenerBitacora(
        buscar: String? = null,
        usuario: Long? = null,
        modulo: String? = null
    ): Response<BitacoraResponse> {
        return ApiClient.bitacoraApi(context).obtenerBitacora(
            buscar = buscar,
            usuario = usuario,
            modulo = modulo
        )
    }
}