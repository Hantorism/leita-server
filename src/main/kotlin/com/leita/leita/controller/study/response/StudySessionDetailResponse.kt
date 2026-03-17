package com.leita.leita.controller.study.response

import java.time.LocalDateTime

data class StudySessionDetailResponse(
    val id: Long,
    val studyId: Long,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val attendance: AttendanceResponse?,
    val assignment: AssignmentDetailResponse?
)
