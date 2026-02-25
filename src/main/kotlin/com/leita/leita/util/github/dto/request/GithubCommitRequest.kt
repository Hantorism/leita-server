package com.leita.leita.util.github.dto.request

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
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