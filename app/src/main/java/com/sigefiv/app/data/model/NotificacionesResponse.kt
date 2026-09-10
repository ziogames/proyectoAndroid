package com.sigefiv.app.data.model

data class NotificacionesResponse(
    val success: Boolean,
    val data: List<Notificacion>,
    val total: Int = 0,
    val current_page: Int = 1,
    val last_page: Int = 1
)

data class NotificacionesData(
    val current_page: Int? = null,
    val data: List<Notificacion>,
    val first_page_url: String? = null,
    val from: Int? = null,
    val last_page: Int? = null,
    val last_page_url: String? = null,
    val next_page_url: String? = null,
    val path: String? = null,
    val per_page: Int? = null,
    val prev_page_url: String? = null,
    val to: Int? = null,
    val total: Int? = null
)