package com.leita.leita.util.judge.dto.request

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.domain.judge.Language

data class RunWCRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCaseDto>
)

