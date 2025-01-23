package com.leita.leita.controller.dto.auth.response

data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
)