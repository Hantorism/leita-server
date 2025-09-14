package com.leita.leita.port.github.dto.request

data class GithubCommitRequest(
    val message: String,
    val content: String,
    val repo: String,
    val branch: String? = "main",
    val sha: String? = null,
    val committer: Committer? = null
) {
    data class Committer(
        val name: String?,
        val email: String?
    )
}