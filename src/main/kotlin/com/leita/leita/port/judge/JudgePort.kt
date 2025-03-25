package com.leita.leita.port.judge

import com.leita.leita.controller.dto.judge.request.SubmitRequest
import com.leita.leita.controller.dto.judge.request.RunRequest
import com.leita.leita.port.judge.dto.response.JudgeWCResponse

interface JudgePort {
    fun submit(problemId: Long, submitId: Long, request: SubmitRequest): JudgeWCResponse
    fun run(problemId: Long, submitId: Long, request: RunRequest): List<JudgeWCResponse>
}