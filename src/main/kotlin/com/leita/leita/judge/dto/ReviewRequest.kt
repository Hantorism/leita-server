package com.leita.leita.judge.dto

data class ReviewRequest(
    val submitId: Long,
    val description: String,
    val commitMessage: String,
    val repositoryName: String,
)