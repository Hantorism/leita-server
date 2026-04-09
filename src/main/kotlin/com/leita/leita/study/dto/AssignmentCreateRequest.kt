package com.leita.leita.study.dto

import java.time.LocalDateTime

data class AssignmentCreateRequest(
    val description: String?,
    val problemIds: List<Long>,
    val startDateTime: LocalDateTime?,
    val endDateTime: LocalDateTime
)
