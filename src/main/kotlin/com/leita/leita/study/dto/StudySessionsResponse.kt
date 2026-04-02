package com.leita.leita.study.dto

import com.leita.leita.common.dto.BasePage

data class StudySessionsResponse(
    override val content: List<StudySessionResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<StudySessionResponse>

