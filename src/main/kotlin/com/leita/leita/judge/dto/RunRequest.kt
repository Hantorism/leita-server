package com.leita.leita.judge.dto

import com.leita.leita.common.dto.TestCaseDto

data class RunRequest(
    val code: String,
    val language: String,
    val testCases: List<TestCaseDto>
)