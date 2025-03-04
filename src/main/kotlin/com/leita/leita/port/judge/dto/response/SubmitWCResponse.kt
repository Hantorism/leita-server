package com.leita.leita.port.judge.dto.response

import com.leita.leita.domain.judge.Result

data class SubmitWCResponse(
    val isSuccessful: Boolean,
    val result: Result,
    val error: String
)