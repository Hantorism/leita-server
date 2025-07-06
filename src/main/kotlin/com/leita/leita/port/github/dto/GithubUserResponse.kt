package com.leita.leita.port.github.dto

data class GithubUserResponse(
    val id: Long,
    val login: String,
    val name: String?,
    val email: String?
)