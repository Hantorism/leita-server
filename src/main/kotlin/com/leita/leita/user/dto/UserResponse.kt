package com.leita.leita.user.dto

import com.leita.leita.user.domain.User
import com.leita.leita.common.security.SecurityRole

data class UserResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: SecurityRole,
    val affiliationName: String,
    val profileImage: String?,
    val department: String?
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                name = user.name,
                email = user.email,
                role = user.role,
                affiliationName = user.affiliation?.name ?: "지정 안 됨",
                profileImage = user.profileImage,
                department = user.department
            )
        }
    }
}
