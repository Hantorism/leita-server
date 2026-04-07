package com.leita.leita.study.dto

data class AssignmentCreateRequest(
    val description: String?,
    val problemIds: List<Long>
)
