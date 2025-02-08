package com.leita.leita.service

import com.leita.leita.common.security.OAutheUserInfo
import com.leita.leita.common.security.SecurityRole
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.AuthMapper
import com.leita.leita.controller.dto.auth.request.*
import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.domain.User
import com.leita.leita.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val jwtUtils: JwtUtils,
    private val webClient: WebClient.Builder
) {
    fun oauth(request: OAuthRequest): JwtResponse {
        val userInfo = webClient.build()
            .get()
            .uri("https://www.googleapis.com/oauth2/v3/userinfo")
            .header("Authorization", "Bearer ${request.accessToken}")
            .retrieve()
            .bodyToMono(OAutheUserInfo::class.java)
            .block()!!

        isAjouEmail(userInfo.email)

        if(userRepository.findByEmail(userInfo.email) == null) {
            val user = User(
                email = userInfo.email,
                name = userInfo.name,
                profileImage = userInfo.picture,
                sub = userInfo.sub,
                role = SecurityRole.USER,
            )
            userRepository.save(user)
        }

        return jwtUtils.generateToken(userInfo.email)
    }

    fun info(): InfoResponse {
        val email: String = jwtUtils.extractEmail()
        val user = userRepository.findByEmail(email) ?: throw IllegalArgumentException("User not found")
        return AuthMapper.toInfoResponse(user)
    }

    private fun isAjouEmail(email: String) {
        if(!email.endsWith("@ajou.ac.kr")) {
            throw IllegalArgumentException("Use only Ajou Univ. email address")
        }
    }
}