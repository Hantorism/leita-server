package com.leita.leita.study.dto

data class StudyCompletionRuleCreateRequest(
    val requiredAttendanceCount: Int,
    val requiredAssignmentCount: Int,
    val attendanceRequired: Boolean,
    val assignmentRequired: Boolean
)
