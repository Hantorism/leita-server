package com.leita.leita.controller.dto.study.response

import com.leita.leita.controller.dto.BasePage
import com.leita.leita.domain.User

data class StudyPendingResponse (
    override val content: List<User>,
    override val currentPage: Int,
    override val totalPages: Int,
    override val totalElements: Long,
    override val size: Int
) : BasePage<User>