package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.judge.response.RunResponse
import com.leita.leita.controller.dto.judge.response.SubmitResponse
import com.leita.leita.util.judge.dto.response.JudgeWCResponse
import com.leita.leita.util.judge.dto.response.RunWCResponse

class JudgeMapper {
    companion object {
        fun toSubmitResponse(response: JudgeWCResponse): SubmitResponse {
            return SubmitResponse (
                    result = response.result,
                    error = response.error,
                )
        }

        fun toRunResponse(responses: List<RunWCResponse>): List<RunResponse> {
            return responses.map { response ->
                RunResponse(
                    result = response.result,
                    error = response.error,
                    output = response.output,
                )
            }
        }
    }
}