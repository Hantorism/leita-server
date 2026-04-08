package com.leita.leita.study.dto

import com.leita.leita.study.domain.AttendanceRecordStatus
import com.leita.leita.study.domain.AttendanceStatus
import java.time.LocalDateTime

data class AttendanceResponse(
    val id: Long,
    val studySessionId: Long,
    val openTime: LocalDateTime,
    val closeTime: LocalDateTime?,
    val lateThresholdMinutes: Int,
    val status: AttendanceStatus,
    val records: List<AttendanceRecordResponse>,
    val attendanceRate: AttendanceRateResponse
)

data class AttendanceRecordResponse(
    val id: Long,
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val status: AttendanceRecordStatus,
    val attendedAt: LocalDateTime?
)

data class AttendanceRateResponse(
    val total: Int,
    val present: Int,
    val late: Int,
    val absent: Int,
    val percentage: Double
)
