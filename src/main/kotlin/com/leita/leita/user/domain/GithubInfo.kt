package com.leita.leita.user.domain

import jakarta.persistence.Embeddable

@Embeddable
data class GithubInfo(
    var githubUserName: String? = null,
    var installationId: Long? = null,
)
