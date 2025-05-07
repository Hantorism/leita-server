package com.leita.leita.controller.dto.study.request

import java.time.LocalDateTime

class StudyCreateRequest(
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
)
