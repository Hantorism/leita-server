package com.leita.leita.problem.dto

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.problem.domain.Description
import com.leita.leita.problem.domain.Limit

data class CreateProblemRequest(
    val title: String,
    val description: Description,
    val limit: Limit,
    val testCases: List<TestCaseDto>,
    val source: String,
    val category: List<String>,
)