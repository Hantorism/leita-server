package com.leita.leita.controller.auth

import com.leita.leita.controller.auth.response.InfoResponse
import com.leita.leita.domain.user.User

class AuthMapper {
    companion object {
        fun toInfoResponse(user: User): InfoResponse {
            return InfoResponse(
                email = user.email,
                name = user.name,
                role = user.role,
            )
        }
    }
}