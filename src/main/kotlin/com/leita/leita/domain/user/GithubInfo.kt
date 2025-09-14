package com.leita.leita.domain.user

import jakarta.persistence.Embeddable

@Embeddable
data class GithubInfo(
    var installationId: Long,
)
