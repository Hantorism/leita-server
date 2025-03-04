package com.leita.leita.port.judge

import com.leita.leita.controller.dto.judge.request.SubmitRequest
import com.leita.leita.controller.dto.judge.request.RunRequest

interface JudgePort {
    fun submit(problemId: Long, submitId: Long, request: SubmitRequest): Boolean
    fun run(problemId: Long, submitId: Long, request: RunRequest): Boolean
}