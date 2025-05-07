package com.leita.leita.port.judge.dto.response

import com.leita.leita.domain.judge.Result

data class RunWCResponse(
    val result: Result,
    val error: String,
    val output: String
)