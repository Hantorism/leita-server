package com.leita.leita.controller.study.response

import com.leita.leita.domain.study.StudyMemberRole
import java.time.LocalDateTime

data class StudyMemberResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val role: StudyMemberRole,
    val joinedAt: LocalDateTime,
    val approvedAt: LocalDateTime?
)

