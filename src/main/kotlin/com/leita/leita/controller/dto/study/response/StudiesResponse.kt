package com.leita.leita.controller.dto.problem.response

import com.leita.leita.controller.dto.BasePage
import com.leita.leita.controller.dto.study.response.StudyDetailResponse

data class StudiesResponse (
    override val content: List<StudyDetailResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<StudyDetailResponse>