package com.leita.leita.port.judge.dto.request

import com.leita.leita.domain.enum.Language

data class JudgeSubmitRequest(
    val submitId: Long,
    val code: String,
    val language: Language
)