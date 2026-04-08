package com.leita.leita.judge.repository

import com.leita.leita.judge.domain.Judge
import com.leita.leita.judge.domain.JudgeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface JudgeRepository: JpaRepository<Judge, Long> {
    fun findAllByProblemIdAndType(problemId: Long, type: JudgeType): List<Judge>
    fun findAllByUserIdAndType(userId: Long, type: JudgeType): List<Judge>
    fun findByProblemIdAndUserId(problemId: Long, userId: Long): Judge?
    fun findByProblemIdInAndUserIdAndResult(problemIds: Collection<Long>, userId: Long, result: com.leita.leita.judge.domain.Result): List<Judge>
    fun findByProblemIdInAndUserIdAndType(problemIds: Collection<Long>, userId: Long, type: JudgeType): List<Judge>
}