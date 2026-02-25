package com.leita.leita.util.judge.dto.response

import com.leita.leita.domain.judge.Result

data class JudgeWCResponse(
    val result: Result,
    val error: String,
    val usedMemory: Long,
    val usedTime: Long
)

