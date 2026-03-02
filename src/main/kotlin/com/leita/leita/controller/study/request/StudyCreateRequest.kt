package com.leita.leita.controller.study.request

data class StudyCreateRequest(
    val title: String,
    val description: String,
    val attendanceCheckRequired: Boolean,
    val assignmentRequired: Boolean,
    val requiredAttendanceCount: Int,
    val requiredAssignmentCount: Int
)
