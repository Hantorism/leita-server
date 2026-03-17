package com.leita.leita.controller.study.request

data class AssignmentUpdateRequest(
    val title: String,
    val description: String?,
    val problemIds: List<Long>
)

