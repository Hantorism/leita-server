package com.leita.leita.study.dto

import java.time.LocalDateTime

data class StudySessionCreateRequest(
    val studyId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)

