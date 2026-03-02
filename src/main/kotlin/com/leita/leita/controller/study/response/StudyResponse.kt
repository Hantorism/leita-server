package com.leita.leita.controller.study.response

data class StudyResponse(
    val id: Long,
    val title: String,
    val description: String,
    val attendanceCheckRequired: Boolean,
    val assignmentRequired: Boolean,
    val requiredAttendanceCount: Int,
    val requiredAssignmentCount: Int
)

