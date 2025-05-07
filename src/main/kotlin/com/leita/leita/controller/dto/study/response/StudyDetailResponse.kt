package com.leita.leita.controller.dto.study.response

import com.leita.leita.domain.User
import java.time.LocalDateTime

data class StudyDetailResponse (
    val id: Long,
    val participants: List<User>,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime,
    val studyClassId: Long,
)