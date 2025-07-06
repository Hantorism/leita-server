package com.leita.leita.port.github.dto

data class GithubRepositoryResponse(
    val id: Long,
    val name: String,
    val full_name: String,
    val description: String?,
    val html_url: String,
    val private: Boolean
)