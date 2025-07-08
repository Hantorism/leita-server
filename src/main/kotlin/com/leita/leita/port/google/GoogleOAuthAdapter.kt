package com.leita.leita.port.google

import com.leita.leita.port.google.dto.OAuthUserInfo
import org.springframework.stereotype.Component

@Component
class GoogleOAuthAdapter(private val googleOAuthClient: GoogleOAuthClient) : GoogleOAuthPort {
    override fun getUserInfo(accessToken: String): OAuthUserInfo {
        val wrappedAccessToken = "Bearer $accessToken"
        return googleOAuthClient.getUserInfo(wrappedAccessToken)
    }
}