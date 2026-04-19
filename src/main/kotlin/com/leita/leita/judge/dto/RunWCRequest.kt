package com.leita.leita.judge.dto

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.judge.domain.Language

data class RunWCRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCaseDto>,
    val limit: LimitWCRequest
)
