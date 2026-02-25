package com.leita.leita.util.google

import com.leita.leita.util.google.dto.OAuthUserInfo
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "googleOAuthClient", url = "\${external-server.google}")
interface GoogleOAuthClient {
    @GetMapping("/userinfo")
    fun getUserInfo(@RequestHeader("Authorization") token: String): OAuthUserInfo
}

