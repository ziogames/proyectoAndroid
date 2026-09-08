package com.sigefiv.app.data.repository

import com.sigefiv.app.data.api.AuthApi
import com.sigefiv.app.data.model.CategoriasResponse

class CategoriasRepository(
    private val authApi: AuthApi
) {

    suspend fun obtenerCategorias(): CategoriasResponse {
        return authApi.categorias()
    }
}