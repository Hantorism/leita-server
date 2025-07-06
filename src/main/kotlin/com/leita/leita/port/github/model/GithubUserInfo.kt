package com.leita.leita.port.github.model
data class GithubUserInfo(
    val id: Long,
    val login: String,
    val name: String?,
    val email: String?
)