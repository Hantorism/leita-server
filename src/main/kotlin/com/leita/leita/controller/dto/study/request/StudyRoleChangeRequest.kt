package com.leita.leita.controller.dto.study.request

import com.leita.leita.domain.study.StudyRole

class StudyRoleChangeRequest(
    val email: String,
    val newRole: StudyRole
)
