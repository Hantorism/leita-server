package com.leita.leita.controller.study.request

import com.leita.leita.domain.study.StudyMemberRole

data class StudyRoleChangeRequest(
    val email: String,
    val newRole: StudyMemberRole
)

