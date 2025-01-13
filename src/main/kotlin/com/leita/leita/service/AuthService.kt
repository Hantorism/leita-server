package com.leita.leita.service

import com.leita.leita.controller.dto.auth.AuthMapper
import com.leita.leita.controller.dto.auth.request.RegisterRequest
import com.leita.leita.controller.dto.auth.request.SendVerifyRequest
import com.leita.leita.controller.dto.auth.request.VerifyRequest
import com.leita.leita.controller.dto.auth.response.RegisterResponse
import com.leita.leita.controller.dto.auth.response.SendVerifyResponse
import com.leita.leita.controller.dto.auth.response.VerifyResponse
import com.leita.leita.domain.User
import com.leita.leita.port.cache.CachePort
import com.leita.leita.port.gmail.GmailPort
import com.leita.leita.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val cachePort: CachePort,
    private val gmailPort: GmailPort
) {

    fun register(request: RegisterRequest): RegisterResponse {
        isAjouEmail(request.email)

        val user = User.register(request)

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

    fun isAjouEmail(email: String) {
        if(email.endsWith("@ajou.ac.kr")) {
            throw IllegalArgumentException("Register only Ajou Univ. email address")
        }
    }

    fun createCode(): String {
        val random = Random()
        val code = (100000 + random.nextInt(900000)).toString()
        return code
    }
}