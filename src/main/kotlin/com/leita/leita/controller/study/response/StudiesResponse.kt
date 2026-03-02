package com.leita.leita.controller.study.response

import com.leita.leita.controller.dto.BasePage

data class StudiesResponse (
    override val content: List<StudyResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<StudyResponse>
