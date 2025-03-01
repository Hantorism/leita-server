package com.leita.leita.port.submit.dto.request

import com.leita.leita.domain.enum.Language
import com.leita.leita.domain.problem.TestCase

data class RunSubmitWCRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCase>
)