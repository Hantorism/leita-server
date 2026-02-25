package com.leita.leita.util.google

import com.leita.leita.util.google.dto.OAuthUserInfo
import org.springframework.stereotype.Component

@Component
class GoogleOAuthUtil(private val googleOAuthClient: GoogleOAuthClient) {
    fun getUserInfo(accessToken: String): OAuthUserInfo {
        val wrappedAccessToken = "Bearer $accessToken"
        return googleOAuthClient.getUserInfo(wrappedAccessToken)
    }
}

