package com.leita.leita.util.github.dto.request

data class GithubTreeRequest(
    val tree: List<TreeObject>,
    val base_tree: String? = null
)

data class TreeObject(
    val path: String,
    val mode: String = "100644",
    val type: String = "blob",
    val content: String? = null,
    val sha: String? = null
)
