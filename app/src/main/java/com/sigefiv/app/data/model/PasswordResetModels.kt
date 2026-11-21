package com.sigefiv.app.data.model

data class ForgotPasswordRequest(
    val email: String
)

data class ResetPasswordRequest(
    val email: String,
    val code: String,
    val password: String,
    val password_confirmation: String
)

data class SimpleApiResponse(
    val success: Boolean? = null,
    val message: String
)