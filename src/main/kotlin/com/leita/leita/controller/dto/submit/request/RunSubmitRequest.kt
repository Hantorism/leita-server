package com.leita.leita.controller.dto.submit.request

import com.leita.leita.domain.enum.Language
import com.leita.leita.domain.problem.TestCase

data class RunSubmitRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCase>
)