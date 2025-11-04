package com.leita.leita.controller.problem.response

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.domain.problem.Description
import com.leita.leita.domain.problem.Limit
import com.leita.leita.domain.problem.Solved

data class ProblemDetailResponse (
    val problemId: Long,
    val title: String,
    val authorName: String,
    val description: Description,
    val limit: Limit,
    val testCases: List<TestCaseDto>,
    val source: String,
    val solved: Solved,
    val category: List<String>,
)