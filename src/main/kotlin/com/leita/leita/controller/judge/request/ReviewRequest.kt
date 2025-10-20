package com.leita.leita.controller.dto.judge.request

data class ReviewRequest(
    val submitId: Long,
    val description: String,
    val commitMessage: String,
    val repositoryName: String,
)