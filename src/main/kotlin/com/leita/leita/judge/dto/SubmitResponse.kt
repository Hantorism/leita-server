package com.leita.leita.judge.dto

import com.leita.leita.judge.domain.Result

data class SubmitResponse(
    val result: Result,
    val error: String,
)