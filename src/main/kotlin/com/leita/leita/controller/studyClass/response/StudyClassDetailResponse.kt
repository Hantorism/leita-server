package com.leita.leita.controller.studyClass.response

import com.leita.leita.domain.user.User

data class StudyClassDetailResponse (
    val id: Long,
    val title: String,
    val description: String,
    val requirement: String,
    val admins: List<User>,
    val members: List<User>,
    val pendings: List<User>,
)