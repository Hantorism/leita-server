package com.leita.leita.controller.study.response

import java.time.LocalDate

data class StudyResponse(
    val id: Long,
    val title: String,
    val description: String,
    val requirement: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
)
