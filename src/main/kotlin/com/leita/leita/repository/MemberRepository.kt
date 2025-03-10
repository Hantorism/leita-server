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
        "SELECT u FROM User u JOIN study_members sp ON u.id = sp.user.id WHERE sp.study.id = :studyId",
        countQuery = "SELECT COUNT(u) FROM User u JOIN study_members sp ON u.id = sp.user.id WHERE sp.study.id = :studyId"
    )
    fun findMembersByStudyId(studyId: Long, pageable: Pageable): Page<User>
}