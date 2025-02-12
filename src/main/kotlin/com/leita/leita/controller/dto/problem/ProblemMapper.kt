package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.controller.dto.problem.response.ProblemDetailResponse
import com.leita.leita.domain.problem.Problem

class ProblemMapper {
    companion object {
        fun toProblemDetailResponse(problem: Problem): ProblemDetailResponse {
            return ProblemDetailResponse(
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

        fun toSubmitResponse(isSubmit: Boolean): SubmitResponse {
            return SubmitResponse(
                isSubmit = isSubmit,
            )
        }
    }
}