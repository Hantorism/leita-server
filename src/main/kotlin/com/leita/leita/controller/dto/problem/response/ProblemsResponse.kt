package com.leita.leita.controller.dto.problem.response

import com.leita.leita.controller.dto.BasePage

data class ProblemsResponse (
    override val content: List<ProblemDetailResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<ProblemDetailResponse>