package com.leita.leita.port.judge

import com.leita.leita.common.exception.CustomException
import com.leita.leita.controller.dto.judge.request.SubmitRequest
import com.leita.leita.controller.dto.judge.request.RunRequest
import com.leita.leita.port.judge.dto.request.SubmitWCRequest
import com.leita.leita.port.judge.dto.request.RunWCRequest
import com.leita.leita.port.judge.dto.response.JudgeWCResponse
import com.leita.leita.port.judge.dto.response.RunWCResponse
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class JudgeAdapter(private val judgeClient: JudgeClient) : JudgePort {

    @Async
    override fun submit(problemId: Long, submitId: Long, request: SubmitRequest): JudgeWCResponse {
        try {
            val submitRequest = SubmitWCRequest(
                submitId,
                code = request.code,
                language = request.language,
            )
            println(submitRequest)

            return judgeClient.submit(submitRequest)
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @Async
    override fun run(problemId: Long, submitId: Long, request: RunRequest): List<RunWCResponse> {
        try {
            val runRequest = RunWCRequest(
                code = request.code,
                language = request.language,
                testCases = request.testCases,
            )

            return judgeClient.run(runRequest)
        } catch (ex: Exception) {
            println(ex.message)
            throw CustomException("제출 실패", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}