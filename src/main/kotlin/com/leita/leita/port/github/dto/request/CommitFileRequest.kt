package com.leita.leita.port.github.dto.request

data class CommitFileRequest(
    val message: String,
    val content: String,
    val branch: String = "main",
    val sha: String? = null
)