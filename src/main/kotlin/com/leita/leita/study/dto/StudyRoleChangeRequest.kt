package com.leita.leita.study.dto

import com.leita.leita.study.domain.StudyMemberRole

data class StudyRoleChangeRequest(
    val email: String,
    val newRole: StudyMemberRole
)

