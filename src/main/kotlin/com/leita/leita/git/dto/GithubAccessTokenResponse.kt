package com.leita.leita.git.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class GithubAccessTokenResponse(
    @field:JsonProperty("access_token")
    val accessToken: String,
    @field:JsonProperty("token_type")
    val tokenType: String,
    val scope: String
)