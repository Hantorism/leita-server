package com.leita.leita.study.dto

import java.time.LocalDateTime

data class AssignmentResponse(
    val id: Long,
    val description: String?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val problemIds: List<String>
)
