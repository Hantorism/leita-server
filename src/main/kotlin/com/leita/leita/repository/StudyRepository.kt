package com.leita.leita.repository

import com.leita.leita.domain.Study
import com.leita.leita.domain.problem.Problem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface StudyRepository: JpaRepository<Study, Long> {
}