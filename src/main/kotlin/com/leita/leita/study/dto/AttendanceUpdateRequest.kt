package com.leita.leita.study.dto

import com.leita.leita.study.domain.AttendanceStatus
import java.time.LocalDateTime

data class AttendanceUpdateRequest(
    val closeTime: LocalDateTime?,
    val lateThresholdMinutes: Int?,
    val status: AttendanceStatus?
)
