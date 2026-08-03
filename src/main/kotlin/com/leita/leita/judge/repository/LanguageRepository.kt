package com.leita.leita.judge.repository

import com.leita.leita.judge.domain.Language
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LanguageRepository : JpaRepository<Language, Long> {
    fun findByCode(code: String): Language?
    fun existsByCode(code: String): Boolean
    fun existsByName(name: String): Boolean
}
