package com.leita.leita.controller.dto.judge.response

import com.leita.leita.domain.judge.Result

data class SubmitResponse(
    val result: Result,
    val error: String,
)