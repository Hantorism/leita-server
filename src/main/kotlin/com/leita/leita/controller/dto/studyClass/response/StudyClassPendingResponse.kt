package com.leita.leita.controller.dto.studyClass.response

import com.leita.leita.controller.dto.BasePage
import com.leita.leita.domain.User

data class StudyClassPendingResponse (
    override val content: List<User>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<User>