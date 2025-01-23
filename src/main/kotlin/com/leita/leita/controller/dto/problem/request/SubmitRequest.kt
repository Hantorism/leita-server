package com.leita.leita.controller.dto.auth.request

import com.leita.leita.domain.enum.Language

data class SubmitRequest(
    val problemId: Long,
    val code: String,
    val language: Language
)