package com.leita.leita.repository

import com.leita.leita.domain.problem.Problem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProblemRepository : JpaRepository<Problem, Long> {
    @Query(
        value = """
            SELECT p.*
            FROM problem p
            LEFT JOIN (
                SELECT problem_id, MAX(CASE WHEN result = 'CORRECT' THEN 1 ELSE 0 END) AS is_solved
                FROM judge
                WHERE user_id = :userId
                GROUP BY problem_id
            ) j ON p.id = j.problem_id
            WHERE
                (
                    :search IS NULL OR
                    (
                        (:search REGEXP '^[0-9]+$' AND p.id = CAST(:search AS UNSIGNED)) OR
                        (NOT :search REGEXP '^[0-9]+$' AND p.title LIKE CONCAT('%', :search, '%'))
                    )
                )
            AND
                (
                    :filter IS NULL OR
                    (:filter = 'SOLVED' AND j.is_solved = 1) OR
                    (:filter = 'UNSOLVED' AND (j.is_solved IS NULL OR j.is_solved = 0))
                )
            ORDER BY p.id DESC
            LIMIT :#{#pageable.pageSize} OFFSET :#{#pageable.offset}
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM problem p
            LEFT JOIN (
                SELECT problem_id, MAX(CASE WHEN result = 'CORRECT' THEN 1 ELSE 0 END) AS is_solved
                FROM judge
                WHERE user_id = :userId
                GROUP BY problem_id
            ) j ON p.id = j.problem_id
            WHERE
                (
                    :search IS NULL OR
                    (
                        (:search REGEXP '^[0-9]+$' AND p.id = CAST(:search AS UNSIGNED)) OR
                        (NOT :search REGEXP '^[0-9]+$' AND p.title LIKE CONCAT('%', :search, '%'))
                    )
                )
            AND
                (
                    :filter IS NULL OR
                    (:filter = 'SOLVED' AND j.is_solved = 1) OR
                    (:filter = 'UNSOLVED' AND (j.is_solved IS NULL OR j.is_solved = 0))
                )
        """,
        nativeQuery = true
    )
    fun findProblemsByFilter(
        userId: Long?,
        search: String?,
        filter: String?,
        pageable: Pageable
    ): Page<Problem>
}