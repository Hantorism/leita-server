package com.leita.leita.controller.study.response

import java.time.LocalDate
import java.time.LocalDateTime

data class StudyDetailResponse(
    val id: Long,
    val title: String,
    val description: String,
    val requirement: String,
    val startDate: LocalDate,
    val endDate: LocalDate,

    val attendanceRequired: Boolean,
    val assignmentRequired: Boolean,
    val requiredAttendanceCount: Int,
    val requiredAssignmentCount: Int,
    val members: List<StudyMemberResponse>
)
