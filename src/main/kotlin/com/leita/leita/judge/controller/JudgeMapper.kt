package com.leita.leita.judge.controller

import com.leita.leita.judge.dto.RunResponse
import com.leita.leita.judge.dto.SubmitResponse
import com.leita.leita.judge.dto.JudgeWCResponse
import com.leita.leita.judge.dto.RunWCResponse

import com.leita.leita.judge.dto.JudgeDetailResponse
import com.leita.leita.judge.domain.Judge
import com.leita.leita.study.dto.UserBriefResponse

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
                createdAt = judge.createdAt,
                user = UserBriefResponse(
                    id = judge.user.id,
                    name = judge.user.name,
                    email = judge.user.email,
                    profileImage = judge.user.profileImage
                )
            )
        }

        fun toJudgeDetailResponses(judges: List<Judge>): List<JudgeDetailResponse> {
            return judges.map { toJudgeDetailResponse(it) }
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
