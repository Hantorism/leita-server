package com.leita.leita.controller.study.request

data class AssignmentCreateRequest(
    val title: String,
    val description: String?,
    val problemIds: List<Long>
)
