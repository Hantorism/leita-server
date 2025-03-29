package com.leita.leita.repository

import com.leita.leita.domain.judge.Judge
import com.leita.leita.domain.judge.JudgeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JudgeRepository: JpaRepository<Judge, Long> {
    fun findAllByProblemIdAndType(problemId: Long, type: JudgeType): List<Judge>
    fun findAllByUserIdAndType(userId: Long, type: JudgeType): List<Judge>
}