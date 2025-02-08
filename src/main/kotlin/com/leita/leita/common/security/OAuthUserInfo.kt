package com.leita.leita.common.security

data class OAutheUserInfo(
    val sub: String,
    val email: String,
    val emailVerified: Boolean,
    val familyName: String?,
    val givenName: String?,
    val name: String,
    val picture: String?
)