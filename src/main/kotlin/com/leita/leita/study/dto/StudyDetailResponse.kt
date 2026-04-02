package com.leita.leita.study.dto

import java.time.LocalDate
import java.time.LocalDateTime

data class StudyDetailResponse(
    val id: Long,
    val title: String,
    val description: String,
    val requirement: String,
    val startDate: LocalDate,
    val endDate: LocalDate,

    val members: List<StudyMemberResponse>
)
