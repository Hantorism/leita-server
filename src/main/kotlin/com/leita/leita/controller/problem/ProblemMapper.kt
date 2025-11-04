package com.leita.leita.controller.problem

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.controller.problem.response.ProblemDetailResponse
import com.leita.leita.controller.problem.response.ProblemsResponse
import com.leita.leita.domain.problem.Problem
import org.springframework.data.domain.Page

class ProblemMapper {
    companion object {
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
                problemId = problem.problemId,
                title = problem.title,
                authorName = problem.author.name,
                description = problem.description,
                limit = problem.limit,
                testCases = problem.testCases.map {
                    testCase -> TestCaseDto.fromDomain(testCase)
                },
                source = problem.source,
                solved = problem.solved,
                category = problem.category,
            )
        }
    }
}