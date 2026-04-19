package com.leita.leita.judge.controller

import com.leita.leita.judge.dto.RunResponse
import com.leita.leita.judge.dto.SubmitResponse
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCResponse

import com.leita.leita.judge.dto.JudgeDetailResponse
import com.leita.leita.judge.domain.Judge

class JudgeMapper {
    companion object {
        fun toSubmitResponse(response: JudgeWCResponse): SubmitResponse {
            return SubmitResponse (
                    result = response.result,
                    error = response.error,
                )
        }

        fun toJudgeDetailResponse(judge: Judge): JudgeDetailResponse {
            return JudgeDetailResponse(
                id = judge.id,
                problemId = judge.problemId,
                result = judge.result,
                used = judge.used,
                sizeOfCode = judge.sizeOfCode,
                codeUrl = judge.codeUrl,
                createdAt = judge.createdAt
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
