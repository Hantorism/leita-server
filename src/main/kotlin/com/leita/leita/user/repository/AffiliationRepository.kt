package com.leita.leita.user.repository

import com.leita.leita.user.domain.Affiliation
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AffiliationRepository : JpaRepository<Affiliation, Long> {
    fun findByEmailDomain(emailDomain: String): Affiliation?
    fun existsByName(name: String): Boolean
    fun existsByEmailDomain(emailDomain: String): Boolean
}
