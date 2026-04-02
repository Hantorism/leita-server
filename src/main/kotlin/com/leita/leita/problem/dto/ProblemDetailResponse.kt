package com.leita.leita.problem.dto

import com.leita.leita.common.dto.TestCaseDto
import com.leita.leita.problem.domain.Description
import com.leita.leita.problem.domain.Limit
import com.leita.leita.problem.domain.Solved

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