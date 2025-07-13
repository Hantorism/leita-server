package com.leita.leita.domain.user

import jakarta.persistence.Embeddable

@Embeddable
data class GithubInfo(
    val installationId: String,
    val userName: String,
)
