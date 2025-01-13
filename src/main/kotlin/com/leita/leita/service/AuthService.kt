package com.leita.leita.service

import com.leita.leita.controller.dto.auth.AuthMapper
import com.leita.leita.domain.User
import com.leita.leita.controller.dto.auth.request.RegisterRequest
import com.leita.leita.controller.dto.auth.response.RegisterResponse
import com.leita.leita.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
) {

    fun register(request: RegisterRequest): RegisterResponse {

        if(request.email.endsWith("@ajou.ac.kr")) {
            throw IllegalArgumentException("Register only Ajou Univ. email address")
        }

        val user = User.register(request)

        if(userRepository.findByEmail(user.email) != null) {
            throw IllegalArgumentException("User already exists")
        }

        val savedUser = userRepository.save(user)
        return AuthMapper.toRegisterResponse(savedUser)
    }
}