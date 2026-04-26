package com.leita.leita.study.dto

data class StudyCompletionResponse(
    val hasRequirement: Boolean,
    val requirement: String?,
    val attendanceThreshold: Int,
    val assignmentThreshold: Int,
    val memberCompletions: List<MemberCompletionResponse>
)

data class MemberCompletionResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val attendanceCount: Int,
    val totalSessions: Int,
    val attendanceRate: Int,
    val completedAssignments: Int,
    val totalAssignments: Int,
    val assignmentRate: Int,
    val isCompleted: Boolean
)
