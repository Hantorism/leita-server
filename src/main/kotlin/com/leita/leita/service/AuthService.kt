package com.leita.leita.service

import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.auth.AuthMapper
import com.leita.leita.controller.auth.request.OAuthRequest
import com.leita.leita.controller.auth.response.InfoResponse
import com.leita.leita.controller.auth.response.JwtResponse
import com.leita.leita.domain.user.User
import com.leita.leita.util.google.GoogleOAuthUtil
import com.leita.leita.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val googleOAuthUtil: GoogleOAuthUtil
) {
    fun oauth(request: OAuthRequest): JwtResponse {
        val userInfo = googleOAuthUtil.getUserInfo(request.accessToken)

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

}