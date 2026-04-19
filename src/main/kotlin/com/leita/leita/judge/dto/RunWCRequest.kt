package com.leita.leita.judge.dto

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.judge.domain.Language

data class RunWCRequest(
    val code: String,
    val language: Language,
    val testCases: List<TestCaseDto>,
    val memoryLimit: Long, // 기존 필드 복구
    val timeLimit: Long,   // 기존 필드 복구
    val limit: LimitWCRequest // 새로 추가된 객체
)
