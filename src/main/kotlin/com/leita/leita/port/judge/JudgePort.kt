package com.leita.leita.port.judge

import com.leita.leita.controller.dto.problem.request.SubmitRequest

interface JudgePort {
    fun submit(problemId: Long, submitId: Long, request: SubmitRequest): Boolean
}