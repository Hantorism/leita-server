package com.leita.leita.repository

import com.leita.leita.domain.judge.Judge
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JudgeRepository: JpaRepository<Judge, Long> {
}