package com.leita.leita.controller.dto.auth.request

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
)