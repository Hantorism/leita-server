package com.leita.leita.port.github.dto.request

data class CreateCommitRequest(
    val message: String,
    val tree: String,
    val parents: List<String>,
    val author: Author? = null
)

data class Author(
    val name: String,
    val email: String
)
