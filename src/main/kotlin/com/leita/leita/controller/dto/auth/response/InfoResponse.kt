package com.leita.leita.controller.dto.auth.response

import com.leita.leita.common.security.SecurityRole

class InfoResponse(
    val email: String,
    val username: String,
    val role: SecurityRole,
)