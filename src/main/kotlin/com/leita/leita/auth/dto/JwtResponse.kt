package com.leita.leita.auth.dto

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
)