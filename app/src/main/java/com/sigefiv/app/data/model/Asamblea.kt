package com.sigefiv.app.data.model

data class AsambleaResponse(
    val success: Boolean,
    val message: String? = null,
    val asambleas: List<Asamblea> = emptyList()
)

data class AsambleaDetalleResponse(
    val success: Boolean,
    val message: String? = null,
    val asamblea: Asamblea? = null
)

data class Asamblea(
    val id: Int,
    val tipo: String? = null,
    val titulo: String? = null,
    val convoca: String? = null,
    val sector: String? = null,
    val grupo: String? = null,
    val manzana: String? = null,
    val lote: String? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val primera_citacion: String? = null,
    val segunda_citacion: String? = null,
    val lugar: String? = null,
    val descripcion: String? = null,
    val importancia: String? = null,
    val plantilla_citacion: Int? = null,
    val estado: String? = null,
    val created_by: Int? = null,
    val alerta_enviada: Boolean = false,
    val alerta_enviada_at: String? = null,
    val creador: AsambleaCreador? = null,
    val agendas: List<AsambleaAgenda> = emptyList(),
    val created_at: String? = null,
    val updated_at: String? = null
)

data class AsambleaCreador(
    val id: Int,
    val name: String? = null,
    val email: String? = null
)

data class AsambleaAgenda(
    val id: Int,
    val numero: Int,
    val descripcion: String? = null
)

/*
|--------------------------------------------------------------------------
| REQUEST PARA CREAR ASAMBLEA
|--------------------------------------------------------------------------
*/

data class CrearAsambleaRequest(
    val tipo: String,
    val titulo: String,
    val convoca: String,
    val sector: String? = null,
    val grupo: String? = null,
    val manzana: String? = null,
    val lote: String? = null,
    val fecha: String,
    val hora: String? = null,
    val primera_citacion: String,
    val segunda_citacion: String? = null,
    val lugar: String,
    val descripcion: String? = null,
    val importancia: String,
    val agenda: List<String> = emptyList(),
    val plantilla_citacion: Int
)