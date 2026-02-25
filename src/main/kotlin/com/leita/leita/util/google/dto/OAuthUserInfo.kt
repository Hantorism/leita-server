package com.leita.leita.util.google.dto

data class OAuthUserInfo(
    val sub: String,
    val email: String,
    val emailVerified: Boolean,
    val familyName: String?,
    val givenName: String?,
    val name: String,
    val picture: String?
)

