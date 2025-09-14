package com.leita.leita.common.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "github")
data class GithubConfig(
    var appId: String = "",
    var privateKey: String = ""
)