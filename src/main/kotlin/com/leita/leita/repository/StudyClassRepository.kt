package com.leita.leita.repository

import com.leita.leita.domain.study.StudyClass
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import org.springframework.data.jpa.repository.EntityGraph

@Repository
interface StudyClassRepository: JpaRepository<StudyClass, Long> {
    @EntityGraph(attributePaths = ["admins", "members", "pendings"])
    fun findDetailById(id: Long): StudyClass?
}