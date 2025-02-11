package com.leita.leita.controller.dto.problem.request

import com.leita.leita.domain.enum.Language

data class SubmitRequest(
    val code: String,
    val language: Language
)