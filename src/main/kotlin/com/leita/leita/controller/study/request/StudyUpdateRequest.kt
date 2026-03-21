package com.leita.leita.controller.study.request

import java.time.LocalDate
import java.time.LocalDateTime

data class StudyUpdateRequest(
    val title: String,
    val description: String,
    val requirement: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
