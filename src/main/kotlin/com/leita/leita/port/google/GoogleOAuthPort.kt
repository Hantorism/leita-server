package com.leita.leita.port.google

import com.leita.leita.port.google.dto.OAuthUserInfo

interface GoogleOAuthPort {
    fun getUserInfo(accessToken: String): OAuthUserInfo
}