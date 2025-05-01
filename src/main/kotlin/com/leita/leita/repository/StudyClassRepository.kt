package com.leita.leita.repository

import com.leita.leita.domain.study.StudyClass
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudyClassRepository: JpaRepository<StudyClass, Long> {
}