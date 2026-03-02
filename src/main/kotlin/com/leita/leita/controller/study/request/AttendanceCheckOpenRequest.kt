package com.leita.leita.controller.study.request

import java.time.LocalDateTime

data class AttendanceCheckOpenRequest(
    val lateThresholdMinutes: Int? = 10,
    val openTime: LocalDateTime? = null,
    val closeTime: LocalDateTime? = null
)


