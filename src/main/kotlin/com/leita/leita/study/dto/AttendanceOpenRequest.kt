package com.leita.leita.study.dto

import java.time.LocalDateTime

data class AttendanceOpenRequest(
    val openTime: LocalDateTime,
    val closeTime: LocalDateTime,
    val lateThresholdMinutes: Int
)
