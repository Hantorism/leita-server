package com.leita.leita.repository

import com.leita.leita.domain.study.StudySession
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudySessionRepository: JpaRepository<StudySession, Long> {
    fun findByStudyId(studyId: Long, pageable: Pageable): Page<StudySession>

    fun findAllByStudyId(studyId: Long): List<StudySession>
}



