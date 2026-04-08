package com.leita.leita.study.dto

import com.leita.leita.study.domain.AssignmentStatus

data class MemberAssignmentUpdateRequest(
    val status: AssignmentStatus
)
