package com.leita.leita.controller.dto.judge.response

import com.leita.leita.domain.judge.Result

data class RunResponse(
    val result: Result,
    val error: String,
    val output: String,
)