package com.leita.leita.study.dto

import com.leita.leita.study.domain.AssignmentStatus

data class AssignmentDetailResponse(
    val id: Long,
    val studySessionId: Long,
    val status: AssignmentStatus?,
    val description: String?,
    val problemIds: List<Long>
)
