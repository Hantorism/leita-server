package com.leita.leita.study.dto

import java.time.LocalDateTime

data class StudyMemberStatusResponse(
    val user: UserBriefResponse,
    val sessions: List<SessionStatus>
)

data class SessionStatus(
    val sessionId: Long,
    val sessionTitle: String,
    val attendanceStatus: String?, // AttendanceStatus name
    val assignmentStatus: Boolean? // All problems solved
)

data class StudyMemberAttendanceResponse(
    val user: UserBriefResponse,
    val attendances: List<AttendanceDetail>
)

data class AttendanceDetail(
    val sessionId: Long,
    val sessionTitle: String,
    val status: String?, // AttendanceRecordStatus name
    val attendedAt: LocalDateTime?
)

data class StudyMemberAssignmentResponse(
    val user: UserBriefResponse,
    val assignments: List<AssignmentDetail>
)

data class AssignmentDetail(
    val sessionId: Long,
    val sessionTitle: String,
    val solvedCount: Int,
    val totalCount: Int,
    val isCompleted: Boolean,
    val solvedProblemIds: List<Long>
)
