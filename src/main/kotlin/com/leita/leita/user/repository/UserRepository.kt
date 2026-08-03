package com.leita.leita.user.repository

import com.leita.leita.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun findByAffiliationId(affiliationId: Long, pageable: Pageable): Page<User>
}