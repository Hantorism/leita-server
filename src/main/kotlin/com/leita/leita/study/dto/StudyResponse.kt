package com.leita.leita.study.dto

import java.time.LocalDate

data class StudyResponse(
    val id: Long,
    val title: String,
    val description: String,
    val requirement: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isJoined: Boolean = false,
    val memberCount: Int = 0
)
