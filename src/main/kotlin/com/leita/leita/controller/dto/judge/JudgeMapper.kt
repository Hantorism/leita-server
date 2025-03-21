package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.judge.response.RunResponse
import com.leita.leita.controller.dto.judge.response.SubmitResponse
import com.leita.leita.domain.problem.TestCase
import com.leita.leita.port.judge.dto.response.RunWCResponse

class JudgeMapper {
    companion object {
        fun toSubmitResponse(isSubmit: Boolean): SubmitResponse {
            return SubmitResponse(
                isSubmit = isSubmit,
            )
        }

        fun toRunResponse(results: List<RunWCResponse>): List<RunResponse> {
            return results.map { result ->
                RunResponse(
                    result = result.result.message,
                    error = result.error,
                )
            }
        }
    }
}