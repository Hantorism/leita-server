package com.leita.leita.repository

import com.leita.leita.domain.study.StudySession
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudySessionRepository: JpaRepository<StudySession, Long> {
    fun findByStudyIdOrderByStartDateTimeAsc(studyId: Long, pageable: Pageable): Page<StudySession>

    fun findAllByStudyIdOrderByStartDateTimeAsc(studyId: Long): List<StudySession>

    @EntityGraph(attributePaths = ["attendances", "attendances.records", "attendances.records.user", "assignments"])
    fun findDetailById(id: Long): StudySession?
}
