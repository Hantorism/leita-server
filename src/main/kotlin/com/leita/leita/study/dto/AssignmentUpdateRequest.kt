package com.leita.leita.study.dto

data class AssignmentUpdateRequest(
    val title: String,
    val description: String?,
    val problemIds: List<Long>
)

