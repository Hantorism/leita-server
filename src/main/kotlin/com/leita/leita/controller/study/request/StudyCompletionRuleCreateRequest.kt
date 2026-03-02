package com.leita.leita.controller.study.request

data class StudyCompletionRuleCreateRequest(
    val requiredAttendanceCount: Int,
    val requiredAssignmentCount: Int,
    val attendanceCheckRequired: Boolean,
    val assignmentRequired: Boolean
)

