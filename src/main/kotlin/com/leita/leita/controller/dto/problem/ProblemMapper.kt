package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.controller.dto.problem.response.ProblemDetailResponse
import com.leita.leita.domain.problem.Problem

class ProblemMapper {
    companion object {
        fun toProblemDetailResponse(problem: Problem): ProblemDetailResponse {
            return ProblemDetailResponse(
                memoryLimit = problem.memoryLimit,
                timeLimit = problem.timeLimit,
                problem = problem.problem,
                testCases = problem.testCases
            )
        }

        fun toSubmitResponse(isSubmit: Boolean): SubmitResponse {
            return SubmitResponse(
                isSubmit = isSubmit,
            )
        }
    }
}