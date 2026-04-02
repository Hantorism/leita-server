package com.leita.leita.git.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubRefResponse(
    val ref: String,
    @JsonProperty("node_id")
    val nodeId: String,
    val url: String,
    val `object`: RefObject
)

data class RefObject(
    val sha: String,
    val type: String,
    val url: String
)
