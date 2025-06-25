package com.leita.leita.controller.studyClass.response

import com.leita.leita.controller.dto.BasePage

data class StudyClassesResponse (
    override val content: List<StudyClassDetailResponse>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<StudyClassDetailResponse>