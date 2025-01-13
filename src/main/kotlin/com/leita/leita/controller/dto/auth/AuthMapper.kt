package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.auth.response.RegisterResponse
import com.leita.leita.domain.User

class AuthMapper {
    companion object {
        fun toRegisterResponse(user: User): RegisterResponse {
            return RegisterResponse(user.username)
        }
    }
}