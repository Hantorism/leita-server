package com.leita.leita.qna.repository

import com.leita.leita.qna.domain.Qna
import com.leita.leita.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface QnaRepository : JpaRepository<Qna, Long> {
    fun findByAuthor(author: User, pageable: Pageable): Page<Qna>
}
