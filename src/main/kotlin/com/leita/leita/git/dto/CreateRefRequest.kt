package com.leita.leita.git.dto

data class CreateRefRequest(
    val ref: String,
    val sha: String
)