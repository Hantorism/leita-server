package com.leita.leita.controller.study.response

import java.time.LocalDateTime

data class StudyResponse(
    val id: Long,
    val title: String,
    val description: String,
    val requirement: String,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime,
    val attendanceRequired: Boolean,
    val assignmentRequired: Boolean,
    val requiredAttendanceCount: Int,
    val requiredAssignmentCount: Int
)
