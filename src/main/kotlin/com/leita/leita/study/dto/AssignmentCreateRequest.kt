package com.leita.leita.study.dto

data class AssignmentCreateRequest(
    val title: String,
    val description: String?,
    val problemIds: List<Long>
)
