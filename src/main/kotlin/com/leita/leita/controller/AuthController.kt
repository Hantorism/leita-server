package com.leita.leita.controller

import com.leita.leita.controller.dto.auth.request.*
import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/oauth")
    fun register(@RequestBody  request: OAuthRequest): ResponseEntity<JwtResponse> {
        val response = authService.oauth(request)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/info")
    fun info(): ResponseEntity<InfoResponse> {
        val response = authService.info()
        return ResponseEntity.ok(response)
    }
}