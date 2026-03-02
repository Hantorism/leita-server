package com.leita.leita.controller.study.request

import java.time.LocalDateTime

data class AttendanceCheckCloseRequest(
    val closeTime: LocalDateTime? = null
)


