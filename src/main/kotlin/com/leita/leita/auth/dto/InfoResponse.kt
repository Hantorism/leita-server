package com.leita.leita.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.leita.leita.common.security.SecurityRole

data class InfoResponse(
    val email: String,
    val name: String,
    val role: SecurityRole,
    val profileImage: String?,
    val mainLanguage: String?,
    val department: String?,
    val isGithubLinked: Boolean,
    @get:JsonProperty("githubUserName")
    val githubUserName: String?
)
