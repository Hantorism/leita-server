package com.leita.leita.git.dto

data class UpdateRefRequest(
    val sha: String,
    val force: Boolean = false
)
