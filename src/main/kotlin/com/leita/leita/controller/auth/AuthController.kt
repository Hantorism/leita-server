package com.leita.leita.controller.auth

import com.leita.leita.controller.auth.request.OAuthRequest
import com.leita.leita.controller.auth.response.InfoResponse
import com.leita.leita.controller.auth.response.JwtResponse
import com.leita.leita.controller.dto.BaseResponse
import com.leita.leita.service.AuthService

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