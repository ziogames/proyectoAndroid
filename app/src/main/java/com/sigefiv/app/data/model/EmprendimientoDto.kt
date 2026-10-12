
package com.sigefiv.app.data.model

import com.google.gson.annotations.SerializedName
import com.sigefiv.app.data.api.EmprendimientoCategoriaDto

data class EmprendimientoDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("user_id")
    val userId: Long,

    @SerializedName("categoria_id")
    val categoriaId: Long,

    @SerializedName("categoria")
    val categoria: EmprendimientoCategoriaDto? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("slug")
    val slug: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("show_whatsapp")
    val showWhatsapp: Boolean,

    @SerializedName("modality")
    val modality: String,

    @SerializedName("attention_zone")
    val attentionZone: String?,

    @SerializedName("schedule")
    val schedule: String?,

    @SerializedName("status")
    val status: String,

    @SerializedName("photos")
    val photos: List<EmprendimientoFotoDto>? = emptyList(),

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)

data class EmprendimientoFotoDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("emprendimiento_id")
    val emprendimientoId: Long,

    @SerializedName("path")
    val path: String,

    @SerializedName("is_primary")
    val isPrimary: Boolean
)
