package com.leita.leita.controller.study.response

data class AssignmentDetailResponse(
    val id: Long,
    val studySessionId: Long,
    val week: Int,
    val title: String,
    val description: String,
    val problems: List<AssignmentProblemResponse>
)

data class AssignmentProblemResponse(
    val problemId: Long
)

