package com.leita.leita.port.judge

import com.leita.leita.controller.dto.auth.request.SubmitRequest

interface JudgePort {
    fun submit(request: SubmitRequest): Boolean
}