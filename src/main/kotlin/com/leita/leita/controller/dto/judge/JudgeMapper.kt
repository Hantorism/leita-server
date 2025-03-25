package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.judge.response.RunResponse
import com.leita.leita.controller.dto.judge.response.SubmitResponse
import com.leita.leita.port.judge.dto.response.JudgeWCResponse

class JudgeMapper {
    companion object {
        fun toSubmitResponse(result: JudgeWCResponse): SubmitResponse {
            return SubmitResponse (
                    result = result.result.message,
                    error = result.error,
                )
        }

        fun toRunResponse(results: List<JudgeWCResponse>): List<RunResponse> {
            return results.map { result ->
                RunResponse(
                    result = result.result.message,
                    error = result.error,
                )
            }
        }
    }
}