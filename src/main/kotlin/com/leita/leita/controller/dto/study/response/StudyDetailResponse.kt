package com.leita.leita.controller.dto.study.response

import com.leita.leita.domain.User

data class StudyDetailResponse (
    val admins: MutableList<User>,
    val title: String,
    val description: String,
    val participants: MutableList<User>,
)