package com.leita.leita.port.github.dto

data class GithubFileResponse(
    val sha: String,
    val content: String?
)