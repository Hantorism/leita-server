package com.leita.leita.controller.dto.study.request

import java.time.LocalDateTime

data class StudyUpdateRequest (
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
)