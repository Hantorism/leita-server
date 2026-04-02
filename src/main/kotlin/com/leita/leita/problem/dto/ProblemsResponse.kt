package com.leita.leita.problem.dto

import com.leita.leita.common.dto.BasePage

data class ProblemsResponse (
    override val content: List<ProblemDetailResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<ProblemDetailResponse>