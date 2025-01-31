package com.leita.leita.controller.dto.problem.response

import com.leita.leita.domain.problem.TestCase

data class ProblemDetailResponse (
    val memoryLimit: Long,
    val timeLimit: Long,
    val problem: String,
    val testCases: List<TestCase>,
)