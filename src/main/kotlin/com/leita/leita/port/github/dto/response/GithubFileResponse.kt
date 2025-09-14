package com.leita.leita.port.github.dto.response

data class GithubFileResponse(
    val sha: String,
    val content: String?
)