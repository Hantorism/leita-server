package com.leita.leita.controller.study.request

import java.time.LocalDateTime

data class StudySessionUpdateRequest(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)

