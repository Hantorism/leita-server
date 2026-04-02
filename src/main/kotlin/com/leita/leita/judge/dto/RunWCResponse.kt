package com.leita.leita.judge.dto

import com.leita.leita.judge.domain.Result

data class RunWCResponse(
    val result: Result,
    val error: String,
    val output: String
)

