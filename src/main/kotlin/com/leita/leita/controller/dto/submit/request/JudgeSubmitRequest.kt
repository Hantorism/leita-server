package com.leita.leita.controller.dto.submit.request

import com.leita.leita.domain.enum.Language

data class JudgeSubmitRequest(
    val code: String,
    val language: Language
)