package com.leita.leita.study.dto

data class AssignmentDetailResponse(
    val id: Long,
    val studySessionId: Long,
    val status: String?, // AssignmentStatus name
    val description: String?,
    val problemIds: List<Long>
)
