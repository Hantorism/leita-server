package com.leita.leita.study.dto

import java.time.LocalDateTime

data class AttendanceCloseRequest(
    val closeTime: LocalDateTime?
)
