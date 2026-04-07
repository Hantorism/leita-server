package com.leita.leita.study.dto

data class AssignmentUpdateRequest(
    val description: String?,
    val problemIds: List<Long>
)
