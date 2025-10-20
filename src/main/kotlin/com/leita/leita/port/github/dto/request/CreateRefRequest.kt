package com.leita.leita.port.github.dto.request

data class CreateRefRequest(
    val ref: String,
    val sha: String
)