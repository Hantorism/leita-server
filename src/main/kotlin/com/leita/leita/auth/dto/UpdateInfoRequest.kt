package com.leita.leita.auth.dto

data class UpdateInfoRequest(
    val name: String?,
    val profileImage: String?,
    val mainLanguage: String?,
    val department: String?
)
