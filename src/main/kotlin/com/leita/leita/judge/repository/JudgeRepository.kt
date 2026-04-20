package com.leita.leita.judge.repository

import com.leita.leita.judge.domain.Judge
import com.leita.leita.judge.domain.JudgeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface JudgeRepository : JpaRepository<Judge, Long> {
    fun findAllByProblemIdAndType(problemId: String, type: JudgeType): List<Judge>

    fun findAllByUserIdAndType(userId: Long, type: JudgeType): List<Judge>

    fun findAllByType(type: JudgeType): List<Judge>

    fun findByProblemIdAndUserId(problemId: String, userId: Long): Judge?

    fun findByProblemIdInAndUserIdAndResult(problemIds: Collection<String>, userId: Long, result: com.leita.leita.judge.domain.Result): List<Judge>

    fun findByProblemIdInAndUserIdAndType(problemIds: Collection<String>, userId: Long, type: JudgeType): List<Judge>

    @Query(
        value = """
            SELECT problem_id
            FROM judge
            WHERE type = 'SUBMIT'
            GROUP BY problem_id
            ORDER BY COUNT(*) DESC
            LIMIT :limit
        """,
        nativeQuery = true
    )
    fun findPopularProblemIdsAllTime(limit: Int): List<String>

    @Query(
        value = """
            SELECT problem_id
            FROM judge
            WHERE type = 'SUBMIT' AND created_at >= :since
            GROUP BY problem_id
            ORDER BY COUNT(*) DESC
            LIMIT :limit
        """,
        nativeQuery = true
    )
    fun findPopularProblemIdsSince(since: LocalDateTime, limit: Int): List<String>
}