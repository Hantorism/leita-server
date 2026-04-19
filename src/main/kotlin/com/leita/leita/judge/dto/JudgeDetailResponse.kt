package com.leita.leita.judge.dto

import com.leita.leita.judge.domain.Result
import com.leita.leita.judge.domain.UsedInfo
import java.time.LocalDateTime

data class JudgeDetailResponse(
    val id: Long,
    val problemId: Long,
    val result: Result?,
    val used: UsedInfo?,
    val sizeOfCode: Long?,
    val codeUrl: String?,
    val createdAt: LocalDateTime
)
