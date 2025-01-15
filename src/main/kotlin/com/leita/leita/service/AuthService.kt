package com.leita.leita.service

import com.leita.leita.common.security.SecurityRole
import com.leita.leita.common.security.jwt.JwtUtils
import com.leita.leita.controller.dto.auth.AuthMapper
import com.leita.leita.controller.dto.auth.request.LoginRequest
import com.leita.leita.controller.dto.auth.request.RegisterRequest
import com.leita.leita.controller.dto.auth.request.SendVerifyRequest
import com.leita.leita.controller.dto.auth.request.VerifyRequest
import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.domain.User
import com.leita.leita.port.cache.CachePort
import com.leita.leita.port.gmail.GmailPort
import com.leita.leita.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val cachePort: CachePort,
    private val gmailPort: GmailPort,
    private val jwtUtils: JwtUtils,
    private val passwordEncoder: PasswordEncoder
) {

    fun register(request: RegisterRequest): RegisterResponse {
        isAjouEmail(request.email)

        val user = User(
            email = request.email,
            password = passwordEncoder.encode(request.password),
            username = request.username,
            role = SecurityRole.USER,
        )

        if(userRepository.findByEmail(user.email) != null) {
            throw IllegalArgumentException("User already exists")
        }

        if(!cachePort.get("VERIFIED_EMAIL_"+request.email).equals("verified")) {
            throw IllegalArgumentException("Email not verified")
        }

        val savedUser = userRepository.save(user)
        cachePort.remove("VERIFIED_EMAIL_"+request.email)
        return AuthMapper.toRegisterResponse(savedUser)
    }

    fun login(request: LoginRequest): LoginResponse {
        isAjouEmail(request.email)

        val user = userRepository.findByEmail(request.email) ?: throw IllegalArgumentException("User not found")

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw Exception("User not found")
        }

        return jwtUtils.generateToken(user.email)
    }

    fun verify(request: VerifyRequest): VerifyResponse {
        isAjouEmail(request.email)

        if(!cachePort.get("EMAIL_"+request.email).equals(request.code)) {
            throw IllegalArgumentException("Not Verified")
        }

        cachePort.remove("EMAIL_"+request.email)
        cachePort.set("VERIFIED_EMAIL_"+request.email, "verified")
        return AuthMapper.toVerifyResponse(request.email)
    }

    fun sendVerify(request: SendVerifyRequest): SendVerifyResponse {
        isAjouEmail(request.email)
        val code = createCode()
        gmailPort.send(request.email, code)
        cachePort.set("EMAIL_"+request.email, code)
        return AuthMapper.toSendVerifyResponse(request.email)
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

    private fun createCode(): String {
        val random = Random()
        val code = (100000 + random.nextInt(900000)).toString()
        return code
    }
}