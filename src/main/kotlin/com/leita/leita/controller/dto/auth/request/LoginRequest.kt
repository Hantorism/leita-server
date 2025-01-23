package com.leita.leita.controller.dto.auth.request

data class LoginRequest(
    val email: String,
    val password: String,
)