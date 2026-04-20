package com.leita.leita.auth.controller

import com.leita.leita.auth.dto.OAuthRequest
import com.leita.leita.auth.dto.InfoResponse
import com.leita.leita.auth.dto.JwtResponse
import com.leita.leita.common.dto.BaseResponse
import com.leita.leita.auth.service.AuthService
import org.springframework.http.ResponseEntity
import com.leita.leita.auth.dto.UpdateInfoRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/oauth")
    fun register(@RequestBody request: OAuthRequest): ResponseEntity<BaseResponse<JwtResponse>> {
        val response = authService.oauth(request)
        val wrappedResponse: BaseResponse<JwtResponse> = BaseResponse("로그인 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @GetMapping("/info")
    fun info(): ResponseEntity<BaseResponse<InfoResponse>> {
        val response = authService.info()
        val wrappedResponse: BaseResponse<InfoResponse> = BaseResponse("유저 정보", response)
        return ResponseEntity.ok(wrappedResponse)
    }

    @PatchMapping("/info")
    fun updateInfo(@RequestBody request: UpdateInfoRequest): ResponseEntity<BaseResponse<InfoResponse>> {
        val response = authService.updateInfo(request)
        val wrappedResponse: BaseResponse<InfoResponse> = BaseResponse("유저 정보 수정 완료", response)
        return ResponseEntity.ok(wrappedResponse)
    }
}