package com.leita.leita.controller.study.request

data class AssignmentCreateRequest(
    val week: Int,
    val title: String,
    val description: String,
    val problemIds: List<Long>
)

