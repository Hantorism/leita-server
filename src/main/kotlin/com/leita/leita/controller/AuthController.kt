package com.leita.leita.controller

import com.leita.leita.controller.dto.auth.request.*
import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val response = authService.register(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/verify")
    fun verify(request: VerifyRequest): ResponseEntity<VerifyResponse> {
        val response = authService.verify(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/send-verify")
    fun sendVerify(request: SendVerifyRequest): ResponseEntity<SendVerifyResponse> {
        val response = authService.sendVerify(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/login")
    fun register(request: LoginRequest): ResponseEntity<LoginResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/info")
    fun info(): ResponseEntity<InfoResponse> {
        val response = authService.info()
        return ResponseEntity.ok(response)
    }
}