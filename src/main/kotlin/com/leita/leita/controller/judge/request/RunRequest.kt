package com.leita.leita.controller.dto.judge.request

import com.leita.leita.domain.judge.Language
import com.leita.leita.domain.problem.TestCase

data class RunRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCase>
)