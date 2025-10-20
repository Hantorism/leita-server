package com.leita.leita.port.github.dto.request

data class UpdateRefRequest(
    val sha: String,
    val force: Boolean = false
)
