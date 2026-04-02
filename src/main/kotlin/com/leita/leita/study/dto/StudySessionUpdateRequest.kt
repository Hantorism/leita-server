package com.leita.leita.study.dto

import java.time.LocalDateTime

data class StudySessionUpdateRequest(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)

