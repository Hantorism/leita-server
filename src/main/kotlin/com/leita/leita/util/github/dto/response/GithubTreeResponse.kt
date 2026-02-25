package com.leita.leita.util.github.dto.response

data class GithubTreeResponse(
    val sha: String,
    val url: String,
    val tree: List<TreeEntry>,
    val truncated: Boolean
)

data class TreeEntry(
    val path: String,
    val mode: String,
    val type: String,
    val sha: String,
    val size: Int?,
    val url: String
)
