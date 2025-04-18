package com.leita.leita.controller.dto.auth.response

import com.leita.leita.common.security.SecurityRole

data class InfoResponse(
    val email: String,
    val name: String,
    val role: SecurityRole,
)