package com.leita.leita.repository

import com.leita.leita.domain.problem.Problem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProblemRepository: JpaRepository<Problem, Long> {
    @Query(
        value = """
        SELECT p.*
        FROM problem p
        LEFT JOIN (
            SELECT problem_id, MAX(result) as result
            FROM judge
            WHERE user_id = :userId
            GROUP BY problem_id
        ) j ON p.id = j.problem_id
        WHERE 
            (:search IS NULL OR 
            (
                (CAST(:search AS UNSIGNED) IS NOT NULL AND p.id = CAST(:search AS UNSIGNED)) OR 
                (CAST(:search AS UNSIGNED) IS NULL AND p.title LIKE %:search%)
            ))
        AND (
            :filter IS NULL OR 
            (:filter = 'SOLVED' AND j.result = 'CORRECT') OR
            (:filter = 'UNSOLVED' AND (j.result IS NULL OR j.result != 'CORRECT'))
        )
        ORDER BY p.id DESC
        LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
    """,
        nativeQuery = true
    )
    fun findProblemsByFilter(
        userId: Long,
        search: String?,
        filter: String?,
        pageable: Pageable
    ): Page<Problem>
}