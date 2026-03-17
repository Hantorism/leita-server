package com.leita.leita.controller.study.response

import java.time.LocalDateTime

data class StudySessionResponse(
    val id: Long,
    val studyId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val attendanceStatus: String?,
    val assignmentCreated: Boolean
)
