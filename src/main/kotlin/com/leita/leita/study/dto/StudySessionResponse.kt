package com.leita.leita.study.dto

import java.time.LocalDateTime

data class StudySessionResponse(
    val id: Long,
    val studyId: Long,
    val title: String,
    val description: String?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val attendanceStatus: String?,
    val assignmentCreated: Boolean
)
