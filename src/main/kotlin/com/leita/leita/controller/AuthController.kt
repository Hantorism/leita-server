package com.leita.leita.controller

import com.leita.leita.controller.dto.auth.request.RegisterRequest
import com.leita.leita.controller.dto.auth.response.RegisterResponse
import com.leita.leita.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @GetMapping("/register")
    fun register(request: RegisterRequest): ResponseEntity<RegisterResponse> {
        val response = authService.register(request)
        return ResponseEntity.ok(response)
    }
}