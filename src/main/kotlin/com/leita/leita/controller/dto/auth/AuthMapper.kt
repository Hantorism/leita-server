package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.auth.response.InfoResponse
import com.leita.leita.controller.dto.auth.response.JwtResponse
import com.leita.leita.domain.User

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