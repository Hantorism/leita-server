package com.leita.leita.problem.repository

import com.leita.leita.problem.domain.Problem
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ProblemRepository : JpaRepository<Problem, Long> {
    @Query(
        value = """
            SELECT p.id, p.title, p.user_id, p.description_problem, p.description_input, p.description_output, 
                   p.limit_memory, p.limit_time, p.source, p.solved_success_count, p.solved_total_count, 
                   p.solved_rate, p.problem_id, p.created_at, p.updated_at
            FROM problem p
            LEFT JOIN (
                SELECT problem_id, MAX(CASE WHEN result = 'CORRECT' THEN 1 ELSE 0 END) AS is_solved
                FROM judge
                WHERE user_id = :userId
                GROUP BY problem_id
            ) j ON p.problem_id = j.problem_id
            WHERE
                (
                    :search IS NULL OR :search = '' OR
                    p.problem_id LIKE CONCAT('%', :search, '%') OR
                    p.title LIKE CONCAT('%', :search, '%')
                )
            AND
                (
                    :filter IS NULL OR
                    (:filter = 'SOLVED' AND j.is_solved = 1) OR
                    (:filter = 'UNSOLVED' AND (j.is_solved IS NULL OR j.is_solved = 0))
                )
            ORDER BY p.id DESC
        """,
        countQuery = """
            SELECT COUNT(*)
            FROM problem p
            LEFT JOIN (
                SELECT problem_id, MAX(CASE WHEN result = 'CORRECT' THEN 1 ELSE 0 END) AS is_solved
                FROM judge
                WHERE user_id = :userId
                GROUP BY problem_id
            ) j ON p.problem_id = j.problem_id
            WHERE
                (
                    :search IS NULL OR :search = '' OR
                    p.problem_id LIKE CONCAT('%', :search, '%') OR
                    p.title LIKE CONCAT('%', :search, '%')
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

    @EntityGraph(attributePaths = ["testCases"])
    fun findProblemByProblemId(problemId: String): Problem?

    fun findAllByProblemIdIn(problemIds: Collection<String>): List<Problem>

    fun existsProblemByProblemId(problemId: String): Boolean
    }