package com.leita.leita.port.github.model

data class FileExistenceResult(
    val exists: Boolean,
    val sha: String?
)