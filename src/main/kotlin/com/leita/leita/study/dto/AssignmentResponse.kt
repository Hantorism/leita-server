package com.leita.leita.study.dto

data class AssignmentResponse(
    val id: Long,
    val studySessionId: Long,
    val title: String,
    val description: String?,
    val problemIds: List<Long>
)
