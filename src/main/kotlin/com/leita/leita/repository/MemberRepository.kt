package com.leita.leita.repository

import com.leita.leita.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MemberRepository : JpaRepository<User, Long> {
    @Query(
        "SELECT u FROM Study s JOIN s.members u WHERE s.id = :studyId",
        countQuery = "SELECT COUNT(u) FROM Study s JOIN s.members u WHERE s.id = :studyId"
    )
    fun findMembersByStudyId(studyId: Long, pageable: Pageable): Page<User>
}