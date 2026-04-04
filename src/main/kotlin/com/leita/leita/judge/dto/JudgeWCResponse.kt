package com.leita.leita.judge.dto

import com.leita.leita.judge.domain.Result

data class JudgeWCResponse(
    val submitId: Long,
    val result: Result,
    val error: String,
    val usedMemory: Long,
    val usedTime: Long
)

