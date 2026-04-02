package com.leita.leita.study.dto

import java.time.LocalDateTime

data class StudySessionCreateRequest(
    val studyId: Long,
    val title: String,
    val description: String?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime
)
