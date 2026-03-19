package com.leita.leita.controller.study.request

import java.time.LocalDateTime

data class StudySessionCreateRequest(
    val studyId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)

