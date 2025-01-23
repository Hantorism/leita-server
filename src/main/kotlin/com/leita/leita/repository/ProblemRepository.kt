package com.leita.leita.repository

import com.leita.leita.domain.problem.Problem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProblemRepository: JpaRepository<Problem, Long> {
}