package com.leita.leita.controller.study.response

import com.leita.leita.controller.dto.BasePage

data class StudySessionsResponse(
    override val content: List<StudySessionResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<StudySessionResponse>

