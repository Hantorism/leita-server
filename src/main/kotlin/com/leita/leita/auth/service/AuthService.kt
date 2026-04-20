package com.leita.leita.auth.service

import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.auth.controller.AuthMapper
import com.leita.leita.auth.dto.OAuthRequest
import com.leita.leita.auth.dto.InfoResponse
import com.leita.leita.auth.dto.JwtResponse
import com.leita.leita.user.domain.User
import com.leita.leita.util.google.GoogleOAuthUtil
import com.leita.leita.user.repository.UserRepository
import org.springframework.stereotype.Service

import com.leita.leita.auth.dto.UpdateInfoRequest
import org.springframework.transaction.annotation.Transactional

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

    @Transactional
    fun updateInfo(request: UpdateInfoRequest): InfoResponse {
        val user = jwtUtils.extractUser()
        user.updateInfo(request.mainLanguage, request.department)
        userRepository.save(user)
        return AuthMapper.toInfoResponse(user)
    }

}