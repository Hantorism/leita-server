package com.leita.leita.service

import com.leita.leita.common.exception.CustomException
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.auth.AuthMapper
import com.leita.leita.controller.auth.request.OAuthRequest
import com.leita.leita.controller.auth.response.InfoResponse
import com.leita.leita.controller.auth.response.JwtResponse
import com.leita.leita.domain.user.User
import com.leita.leita.port.google.GoogleOAuthPort
import com.leita.leita.repository.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val googleOAuthPort: GoogleOAuthPort
) {
    fun oauth(request: OAuthRequest): JwtResponse {
        val userInfo = googleOAuthPort.getUserInfo(request.accessToken)

        isAjouEmail(userInfo.email)

        if(userRepository.findByEmail(userInfo.email) == null) {
            val user = User.oauthLogin(userInfo)
            userRepository.save(user)
        }

        return jwtUtils.generateToken(userInfo.email)
    }

    fun info(): InfoResponse {
        val user = jwtUtils.extractUser()
        return AuthMapper.toInfoResponse(user)
    }

    private fun isAjouEmail(email: String) {
        if(!email.endsWith("@ajou.ac.kr")) {
            throw CustomException("Use only Ajou Univ. email address", HttpStatus.BAD_REQUEST)
        }
    }
}