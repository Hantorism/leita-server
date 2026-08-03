package com.leita.leita.judge.dto

import com.leita.leita.common.dto.TestCaseDto

data class RunWCRequest(
    val code: String,
    val language: String,
    val testCases: List<TestCaseDto>,
    val memoryLimit: Long,
    val timeLimit: Long,
    val limit: LimitWCRequest
)
