package com.leita.leita.controller.problem.request

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.domain.problem.Description
import com.leita.leita.domain.problem.Limit

data class CreateProblemRequest(
    val title: String,
    val description: Description,
    val limit: Limit,
    val testCases: List<TestCaseDto>,
    val source: String,
    val category: List<String>,
)