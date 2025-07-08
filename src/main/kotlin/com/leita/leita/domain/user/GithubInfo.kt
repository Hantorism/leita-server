package com.leita.leita.domain.user

import jakarta.persistence.Embeddable

@Embeddable
data class GithubInfo(
    val userName: String,
    val email: String,
    val repository: String? = null,
    val accessToken: String,
)
