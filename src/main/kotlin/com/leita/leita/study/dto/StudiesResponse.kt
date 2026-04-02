package com.leita.leita.study.dto

import com.leita.leita.common.dto.BasePage

data class StudiesResponse (
    override val content: List<StudyResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<StudyResponse>
