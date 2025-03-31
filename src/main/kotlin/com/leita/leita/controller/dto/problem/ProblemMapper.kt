package com.leita.leita.controller.dto.problem

import com.leita.leita.controller.dto.problem.request.CreateProblemRequest
import com.leita.leita.controller.dto.problem.response.ProblemDetailResponse
import com.leita.leita.controller.dto.problem.response.ProblemsResponse
import com.leita.leita.domain.User
import com.leita.leita.domain.problem.Problem
import com.leita.leita.domain.problem.Solved
import org.springframework.data.domain.Page

class ProblemMapper {
    companion object {
        fun fromCreateProblemRequest(author: User, request: CreateProblemRequest): Problem {
            val problem = Problem(
                title = request.title,
                author,
                description = request.description,
                limit = request.limit,
                source = request.source,
                solved = Solved(0, 0.0),
                category = request.category
            )

            val testCases = request.testCases.map { it.createTestCase(problem) }
            return problem.addTestCases(testCases)
        }

        fun toProblemsResponse(problems: Page<Problem>): ProblemsResponse {
            return ProblemsResponse(
                content = problems.content.map { toProblemDetailResponse(it) },
                currentPage = problems.number,
                totalPages = problems.totalPages,
                totalElements = problems.totalElements,
                size = problems.size
            )
        }

        fun toProblemDetailResponse(problem: Problem): ProblemDetailResponse {
            return ProblemDetailResponse(
                problemId = problem.id,
                title = problem.title,
                authorName = problem.author.name,
                description = problem.description,
                limit = problem.limit,
                testCases = problem.testCases,
                source = problem.source,
                solved = problem.solved,
                category = problem.category,
            )
        }
    }
}