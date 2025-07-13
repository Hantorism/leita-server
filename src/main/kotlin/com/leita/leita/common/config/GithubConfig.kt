package com.leita.leita.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "github")
data class GithubConfig(
    var clientId: String = "",
    var redirectUri: String = "",
    var baseUrl: String = "",
    var appId: String = "",
    var privateKey: String = ""
)