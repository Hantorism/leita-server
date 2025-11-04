package com.leita.leita.controller.dto.judge.request

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.domain.judge.Language

data class RunRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCaseDto>
)