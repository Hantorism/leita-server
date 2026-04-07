package com.leita.leita.study.controller

import com.leita.leita.study.dto.AssignmentDetailResponse
import com.leita.leita.study.dto.AssignmentResponse
import com.leita.leita.study.dto.AttendanceRateResponse
import com.leita.leita.study.dto.AttendanceRecordResponse
import com.leita.leita.study.dto.AttendanceResponse
import com.leita.leita.study.dto.StudySessionDetailResponse
import com.leita.leita.study.dto.StudySessionResponse
import com.leita.leita.study.dto.StudySessionsResponse
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
                attendanceStatus = studySession.attendances.maxByOrNull { it.openTime }?.status?.name,
                assignmentCreated = studySession.getAssignment() != null
            )
        }

        fun toStudySessionDetailResponse(studySession: StudySession): StudySessionDetailResponse {
            return StudySessionDetailResponse(
                id = studySession.id,
                studyId = studySession.studyId,
                title = studySession.title,
                description = studySession.description,
                startDateTime = studySession.startDateTime,
                endDateTime = studySession.endDateTime,
                attendance = studySession.attendances.maxByOrNull { it.openTime }?.let(::toAttendanceResponse),
                assignment = studySession.getAssignment()?.let(::toAssignmentDetailResponse)
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
                status = attendance.status.name,
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
                            status = it.status.name,
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
                studySessionId = assignment.studySession.id,
                description = assignment.description,
                problemIds = assignment.problemIds.toList()
            )
        }

        fun toAssignmentDetailResponse(assignment: Assignment): AssignmentDetailResponse {
            return AssignmentDetailResponse(
                id = assignment.id,
                studySessionId = assignment.studySession.id,
                description = assignment.description,
                problemIds = assignment.problemIds.toList()
            )
        }
    }
}
