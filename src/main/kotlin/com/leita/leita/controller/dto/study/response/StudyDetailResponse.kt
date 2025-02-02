package com.leita.leita.controller.dto.study.response

import com.leita.leita.domain.User

data class StudyDetailResponse (
    val admins: List<User>,
    val title: String,
    val description: String,
    val members: List<User>,
)