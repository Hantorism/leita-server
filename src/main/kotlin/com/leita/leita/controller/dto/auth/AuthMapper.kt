package com.leita.leita.controller.dto.auth

import com.leita.leita.controller.dto.auth.response.InfoResponse
import com.leita.leita.controller.dto.auth.response.RegisterResponse
import com.leita.leita.controller.dto.auth.response.SendVerifyResponse
import com.leita.leita.controller.dto.auth.response.VerifyResponse
import com.leita.leita.domain.User

class AuthMapper {
    companion object {
        fun toRegisterResponse(user: User): RegisterResponse {
            return RegisterResponse(user.username)
        }

        fun toSendVerifyResponse(email: String): SendVerifyResponse {
            return SendVerifyResponse(email)
        }

        fun toVerifyResponse(email: String): VerifyResponse {
            return VerifyResponse(email)
        }

        fun toInfoResponse(user: User): InfoResponse {
            return InfoResponse(
                email = user.email,
                username = user.username,
                role = user.role,
            )
        }
    }
}