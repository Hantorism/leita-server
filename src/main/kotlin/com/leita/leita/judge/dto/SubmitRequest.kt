package com.leita.leita.judge.dto

import com.leita.leita.judge.domain.Language

data class SubmitRequest(
    val code: String,
    val language: Language
)