package com.leita.leita.study.dto

import com.leita.leita.study.domain.StudyMemberRole

data class StudyRoleResponse(
    val role: StudyMemberRole? // Null if not associated with study
)
