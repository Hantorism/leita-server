package com.leita.leita.study.dto

import com.leita.leita.study.domain.AttendanceStatus
import java.time.LocalDateTime

data class StudySessionResponse(
    val id: Long,
    val studyId: Long,
    val title: String,
    val description: String?,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val attendanceStatus: AttendanceStatus?,
    val assignmentCreated: Boolean
)
