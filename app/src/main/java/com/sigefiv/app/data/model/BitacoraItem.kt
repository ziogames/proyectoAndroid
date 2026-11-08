package com.sigefiv.app.data.model

data class BitacoraItem(
    val id: Long,
    val fecha: String,
    val hora: String,
    val usuario: String,
    val email: String,
    val modulo: String,
    val accion: String,
    val descripcion: String,
    val ip: String,
    val color: String, // 'success', 'warning', 'danger', etc.
    val icono: String  // 'cil-plus', 'cil-pencil', etc.
)
