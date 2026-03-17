package com.leita.leita.controller.study.request

import java.time.LocalDateTime

data class AttendanceOpenRequest(
    val openTime: LocalDateTime?,
    val closeTime: LocalDateTime?,
    val lateThresholdMinutes: Int?
)
