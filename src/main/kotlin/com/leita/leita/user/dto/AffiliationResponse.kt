package com.leita.leita.user.dto

import com.leita.leita.user.domain.Affiliation

data class AffiliationResponse(
    val id: Long,
    val name: String,
    val emailDomain: String
) {
    companion object {
        fun from(affiliation: Affiliation): AffiliationResponse {
            return AffiliationResponse(
                id = affiliation.id,
                name = affiliation.name,
                emailDomain = affiliation.emailDomain
            )
        }
    }
}
