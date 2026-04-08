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
    val assignmentStatus: String? // AssignmentStatus name
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
    val status: String?, // AssignmentStatus name
    val solvedCount: Int,
    val totalCount: Int,
    val solvedProblemIds: List<Long>
)
