package com.leita.leita.common.dto

import com.leita.leita.problem.domain.TestCase

data class TestCaseDto(
        val input: String,
        val output: String,
        val isShow: Boolean
) {
    companion object {
        fun fromDomain(testCase: TestCase): TestCaseDto {
            return TestCaseDto(
                input = testCase.input,
                output = testCase.output,
                isShow = testCase.isShow
            )
        }
    }
}