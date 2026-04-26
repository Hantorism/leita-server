package com.leita.leita.auth.controller

import com.leita.leita.auth.dto.InfoResponse
import com.leita.leita.user.domain.User

class AuthMapper {
    companion object {
        fun toInfoResponse(user: User): InfoResponse {
            return InfoResponse(
                email = user.email,
                name = user.name,
                role = user.role,
                profileImage = user.profileImage,
                mainLanguage = user.mainLanguage,
                department = user.department,
                isGithubLinked = user.githubInfo != null,
                githubUserName = user.githubInfo?.githubUserName,
                githubRepository = user.githubInfo?.githubRepository
            )
        }
    }
}
