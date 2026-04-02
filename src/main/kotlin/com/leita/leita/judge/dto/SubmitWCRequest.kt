package com.leita.leita.judge.dto

import com.leita.leita.judge.domain.Language

data class SubmitWCRequest(
    val submitId: Long,
    val code: String,
    val language: Language
)

