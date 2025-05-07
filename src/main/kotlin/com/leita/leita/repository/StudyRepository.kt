package com.leita.leita.repository

import com.leita.leita.domain.study.Study
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudyRepository: JpaRepository<Study, Long> {
    fun findStudyByStudyClassId(studyClassId: Long, pageable: Pageable): Page<Study>
}