package com.leita.leita.port.github.dto

data class GithubCommitRequest(
    val message: String,
    val content: String,
    val branch: String = "main",
    val sha: String? = null
)