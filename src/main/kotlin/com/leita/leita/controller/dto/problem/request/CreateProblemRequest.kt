package com.leita.leita.controller.dto.problem.request

import com.leita.leita.domain.problem.Description
import com.leita.leita.domain.problem.Limit
import com.leita.leita.domain.problem.TestCase

data class CreateProblemRequest(
    val title: String,
    val description: Description,
    val limit: Limit,
    val testCases: List<TestCase>,
    val source: String,
    val category: List<String>,
)