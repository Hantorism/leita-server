package com.leita.leita.study.repository

import com.leita.leita.study.domain.Study
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudyRepository: JpaRepository<Study, Long> {
    @EntityGraph(attributePaths = ["studyMembers", "studyMembers.user"])
    override fun findAll(pageable: org.springframework.data.domain.Pageable): org.springframework.data.domain.Page<Study>

    @EntityGraph(attributePaths = ["studyMembers", "studyMembers.user"])
    fun findDetailById(id: Long): Study?
}