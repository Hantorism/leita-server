package com.leita.leita.port.submit.dto.request

import com.leita.leita.domain.enum.Language

data class JudgeSubmitWCRequest(
    val submitId: Long,
    val code: String,
    val language: Language
)