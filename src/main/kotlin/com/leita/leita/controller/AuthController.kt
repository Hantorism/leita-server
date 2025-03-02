package com.leita.leita.controller

import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.controller.dto.auth.request.*
import com.leita.leita.controller.dto.auth.response.*
import com.leita.leita.controller.dto.problem.response.SubmitResponse
import com.leita.leita.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/oauth")
    fun register(@RequestBody  request: OAuthRequest): ResponseEntity<BaseResponse<JwtResponse>> {
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
}