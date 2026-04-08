package com.leita.leita.study.dto

import com.leita.leita.study.domain.AssignmentStatus
import com.leita.leita.study.domain.AttendanceRecordStatus
import java.time.LocalDateTime

data class StudyMemberStatusResponse(
    val user: UserBriefResponse,
    val sessions: List<SessionStatus>
)

data class SessionStatus(
    val sessionId: Long,
    val sessionTitle: String,
    val attendanceStatus: AttendanceRecordStatus?, 
    val assignmentStatus: AssignmentStatus?
)

data class StudyMemberAttendanceResponse(
    val user: UserBriefResponse,
    val attendances: List<AttendanceDetail>
)

data class AttendanceDetail(
    val sessionId: Long,
    val sessionTitle: String,
    val status: AttendanceRecordStatus?,
    val attendedAt: LocalDateTime?
)

data class StudyMemberAssignmentResponse(
    val user: UserBriefResponse,
    val assignments: List<AssignmentDetail>
)

data class AssignmentDetail(
    val sessionId: Long,
    val sessionTitle: String,
    val status: AssignmentStatus?,
    val solvedCount: Int,
    val totalCount: Int,
    val solvedProblemIds: List<Long>
)
