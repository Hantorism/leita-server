package com.leita.leita.study.dto

import com.leita.leita.study.domain.StudyMemberRole
import java.time.LocalDateTime

data class StudyMemberResponse(
    val userId: Long,
    val name: String,
    val email: String,
    val role: StudyMemberRole,
    val joinedAt: LocalDateTime,
    val approvedAt: LocalDateTime?
)

