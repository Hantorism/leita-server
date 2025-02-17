package com.leita.leita.common.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val message: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)