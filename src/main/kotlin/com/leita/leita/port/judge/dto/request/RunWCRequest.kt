package com.leita.leita.port.judge.dto.request

import com.leita.leita.domain.judge.Language
import com.leita.leita.domain.problem.TestCase

data class RunWCRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCase>
)