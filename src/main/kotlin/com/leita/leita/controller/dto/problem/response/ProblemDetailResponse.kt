package com.leita.leita.controller.dto.problem.response

import com.leita.leita.domain.problem.Description
import com.leita.leita.domain.problem.Limit
import com.leita.leita.domain.problem.Solved
import com.leita.leita.domain.problem.TestCase

data class ProblemDetailResponse (
    val title: String,
    val authorName: String,
    val description: Description,
    val limit: Limit,
    val testCases: List<TestCase>,
    val source: String,
    val solved: Solved,
    val category: List<String>,
)