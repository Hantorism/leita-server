package com.leita.leita.study.controller

import com.leita.leita.study.dto.*
import com.leita.leita.study.domain.Assignment
import com.leita.leita.study.domain.Attendance
import com.leita.leita.study.domain.AttendanceRecord
import com.leita.leita.study.domain.AttendanceRecordStatus
import com.leita.leita.study.domain.StudySession
import org.springframework.data.domain.Page
import kotlin.math.round

class StudySessionMapper {
    companion object {
        fun toStudySessionsResponse(studySessions: Page<StudySession>): StudySessionsResponse {
            return StudySessionsResponse(
                content = studySessions.content.map(::toStudySessionResponse),
                currentPage = studySessions.number,
                totalPages = studySessions.totalPages,
                totalElements = studySessions.totalElements,
                size = studySessions.size
            )
        }

        fun toStudySessionResponse(studySession: StudySession): StudySessionResponse {
            return StudySessionResponse(
                id = studySession.id,
                studyId = studySession.studyId,
                title = studySession.title,
                description = studySession.description,
                startDateTime = studySession.startDateTime,
                endDateTime = studySession.endDateTime,
                attendanceStatus = studySession.attendances.maxByOrNull { it.openTime }?.status,
                assignmentCreated = studySession.getAssignment() != null
            )
        }

        fun toStudySessionDetailResponse(
            studySession: StudySession, 
            userId: Long? = null,
            assignmentProblems: List<AssignmentProblemStatus> = emptyList()
        ): StudySessionDetailResponse {
            return StudySessionDetailResponse(
                id = studySession.id,
                studyId = studySession.studyId,
                title = studySession.title,
                description = studySession.description,
                startDateTime = studySession.startDateTime,
                endDateTime = studySession.endDateTime,
                attendance = studySession.attendances.maxByOrNull { it.openTime }?.let(::toAttendanceResponse),
                assignment = studySession.getAssignment()?.let { toAssignmentDetailResponse(it, userId, assignmentProblems) }
            )
        }

        fun toAttendanceResponse(attendance: Attendance): AttendanceResponse {
            val presentCount = attendance.records.count { it.status == AttendanceRecordStatus.PRESENT }
            val lateCount = attendance.records.count { it.status == AttendanceRecordStatus.LATE }
            val absentCount = attendance.records.count { it.status == AttendanceRecordStatus.ABSENT }
            val totalCount = attendance.records.size
            val rawPercentage = if (totalCount == 0) 0.0 else ((presentCount + lateCount).toDouble() / totalCount) * 100
            val percentage = round(rawPercentage * 100) / 100

            return AttendanceResponse(
                id = attendance.id,
                studySessionId = attendance.studySession.id,
                openTime = attendance.openTime,
                closeTime = attendance.closeTime,
                lateThresholdMinutes = attendance.lateThresholdMinutes,
                status = attendance.status,
                records = attendance.records
                    .sortedWith(
                        compareByDescending<AttendanceRecord> { it.attendedAt }
                            .thenBy { it.user.name }
                    )
                    .map {
                        AttendanceRecordResponse(
                            id = it.id,
                            userId = it.user.id,
                            userName = it.user.name,
                            userEmail = it.user.email,
                            status = it.status,
                            attendedAt = it.attendedAt
                        )
                    },
                attendanceRate = AttendanceRateResponse(
                    total = totalCount,
                    present = presentCount,
                    late = lateCount,
                    absent = absentCount,
                    percentage = percentage
                )
            )
        }

        fun toAssignmentResponse(assignment: Assignment): AssignmentResponse {
            return AssignmentResponse(
                id = assignment.id,
                description = assignment.description,
                startDateTime = assignment.startDateTime,
                endDateTime = assignment.endDateTime,
                problemIds = assignment.problemIds.toList()
            )
        }

        fun toAssignmentDetailResponse(
            assignment: Assignment, 
            userId: Long? = null,
            problems: List<AssignmentProblemStatus> = emptyList()
        ): AssignmentDetailResponse {
            val record = userId?.let { uid -> assignment.records.find { it.user.id == uid } }
            return AssignmentDetailResponse(
                id = assignment.id,
                studySessionId = assignment.studySession.id,
                status = record?.status,
                description = assignment.description,
                problems = problems,
                startDateTime = assignment.startDateTime,
                endDateTime = assignment.endDateTime
            )
        }
    }
}
