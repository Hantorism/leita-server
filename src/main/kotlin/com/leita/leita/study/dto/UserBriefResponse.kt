package com.leita.leita.study.dto

data class UserBriefResponse(
    val id: Long,
    val name: String,
    val email: String,
    val profileImage: String?
)
