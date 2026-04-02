package com.leita.leita.study.dto

import java.time.LocalDateTime

data class StudySessionUpdateRequest(
    val title: String,
    val description: String?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)
