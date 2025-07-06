package com.leita.leita.port.github.model

data class GithubRepository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val url: String,
    val isPrivate: Boolean
)