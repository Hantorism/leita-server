package com.leita.leita.util.github.dto.request

data class UpdateRefRequest(
    val sha: String,
    val force: Boolean = false
)
