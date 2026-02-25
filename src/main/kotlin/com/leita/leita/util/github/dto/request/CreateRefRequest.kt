package com.leita.leita.util.github.dto.request

data class CreateRefRequest(
    val ref: String,
    val sha: String
)