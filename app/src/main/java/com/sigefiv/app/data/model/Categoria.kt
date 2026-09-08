package com.sigefiv.app.data.model

data class Categoria(
    val id: Int,
    val codigo: String?,
    val nombre: String,
    val tipo: String,
    val icono: String?,
    val color: String?,
    val orden: Int?
)