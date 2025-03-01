package com.leita.leita.port.submit

import com.leita.leita.controller.dto.submit.request.JudgeSubmitRequest
import com.leita.leita.controller.dto.submit.request.RunSubmitRequest

interface SubmitPort {
    fun judgeSubmit(problemId: Long, submitId: Long, request: JudgeSubmitRequest): Boolean
    fun runSubmit(problemId: Long, submitId: Long, request: RunSubmitRequest): Boolean
}