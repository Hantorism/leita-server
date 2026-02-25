package com.leita.leita.util.judge.dto.request

import com.leita.leita.domain.judge.Language

data class SubmitWCRequest(
    val submitId: Long,
    val code: String,
    val language: Language
)

