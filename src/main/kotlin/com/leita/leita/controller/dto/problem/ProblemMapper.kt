package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.auth.response.*
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
    }
}