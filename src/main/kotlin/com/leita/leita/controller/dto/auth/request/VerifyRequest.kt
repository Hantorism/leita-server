package com.leita.leita.controller.dto.auth.request

data class VerifyRequest(
    val email: String,
    val code: String,
)