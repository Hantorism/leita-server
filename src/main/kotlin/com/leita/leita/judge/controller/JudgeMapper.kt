package com.leita.leita.judge.controller

import com.leita.leita.judge.dto.RunResponse
import com.leita.leita.judge.dto.SubmitResponse
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCResponse

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
