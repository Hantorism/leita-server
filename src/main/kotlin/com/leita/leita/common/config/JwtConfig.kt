package com.leita.leita.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.jwt")
data class JwtConfig(
    var secret: String = "",
    var accessTokenExpiration: String = "",
    var refreshTokenExpiration: String = ""
)