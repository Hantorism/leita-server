package com.leita.leita.auth.dto

import com.leita.leita.common.security.SecurityRole

data class InfoResponse(
    val email: String,
    val name: String,
    val role: SecurityRole,
)