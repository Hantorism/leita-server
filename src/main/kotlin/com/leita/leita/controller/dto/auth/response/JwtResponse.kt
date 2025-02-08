package com.leita.leita.controller.dto.auth.response

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
)