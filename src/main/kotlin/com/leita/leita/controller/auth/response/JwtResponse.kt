package com.leita.leita.controller.auth.response

data class JwtResponse(
    val accessToken: String,
    val refreshToken: String,
)