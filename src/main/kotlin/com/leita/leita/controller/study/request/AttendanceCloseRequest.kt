package com.leita.leita.controller.study.request

import java.time.LocalDateTime

data class AttendanceCloseRequest(
    val closeTime: LocalDateTime?
)
