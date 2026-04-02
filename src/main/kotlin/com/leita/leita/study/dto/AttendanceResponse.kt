package com.leita.leita.study.dto

import java.time.LocalDateTime

data class AttendanceResponse(
    val id: Long,
    val studySessionId: Long,
    val openTime: LocalDateTime,
    val closeTime: LocalDateTime?,
    val lateThresholdMinutes: Int,
    val status: String,
    val records: List<AttendanceRecordResponse>,
    val attendanceRate: AttendanceRateResponse
)

data class AttendanceRecordResponse(
    val id: Long,
    val userId: Long,
    val userName: String,
    val userEmail: String,
    val status: String,
    val attendedAt: LocalDateTime?
)

data class AttendanceRateResponse(
    val total: Int,
    val present: Int,
    val late: Int,
    val absent: Int,
    val percentage: Double
)
